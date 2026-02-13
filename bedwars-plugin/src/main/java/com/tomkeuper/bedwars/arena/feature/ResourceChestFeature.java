/*
 * BedWars2023 - A bed wars mini-game.
 * Copyright (C) 2024 Tomas Keuper
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: contact@fyreblox.com
 */

package com.tomkeuper.bedwars.arena.feature;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerItemDepositEvent;
import com.tomkeuper.bedwars.api.hologram.IHologramManager;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.configuration.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ResourceChestFeature implements Listener {

    private static ResourceChestFeature instance;

    private final Set<Material> blockedItems;
    private final Map<IArena, List<IHologram>> arenaHolograms = new HashMap<>();
    private final Map<IArena, Map<Location, ITeam>> teamChests = new HashMap<>();
    private final IHologramManager hologramManager;

    private static final Map<String, String> OLD_MATERIAL_NAMES = new HashMap<>();

    private ResourceChestFeature() {
        this.blockedItems = BedWars.config.getYml()
                .getStringList(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_BLOCKED)
                .stream()
                .map(String::toUpperCase)
                .map(name -> OLD_MATERIAL_NAMES.getOrDefault(name, name))
                .map(Material::valueOf)
                .collect(Collectors.toSet());

        this.hologramManager = BedWars.getAPI().getHologramsUtil();
        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);
    }

    public static void init() {
        if (BedWars.config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_ENABLED)
                && instance == null) {
            instance = new ResourceChestFeature();
        }
    }

    @EventHandler
    public void onArenaStateChange(GameStateChangeEvent e) {
        IArena arena = e.getArena();

        if (e.getNewState() == GameState.restarting) {
            if (arenaHolograms.containsKey(arena)) {
                arenaHolograms.get(arena).forEach(IHologram::remove);
            }
            arenaHolograms.remove(arena);
            teamChests.remove(arena);

        } else if (e.getNewState() == GameState.playing) {
            arenaHolograms.put(arena, new ArrayList<>());
            teamChests.put(arena, new HashMap<>());

            if (BedWars.config.getBoolean(
                    ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_ENABLED)) {

                loadAllChunks(arena);

                Bukkit.getScheduler().runTaskLater(
                        BedWars.plugin,
                        () -> createAllHolograms(arena),
                        20L
                );
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeftClickChest(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;

        IArena arena = Arena.getArenaByPlayer(e.getPlayer());
        if (arena == null) return;

        boolean normalChest = e.getClickedBlock().getType() == Material.CHEST;
        boolean enderChest = e.getClickedBlock().getType() == Material.ENDER_CHEST;
        if (!normalChest && !enderChest) return;

        Player player = e.getPlayer();
        ITeam playerTeam = arena.getTeam(player);
        if (playerTeam == null) return;

        if (normalChest && !canUseThisChest(arena, player, e.getClickedBlock().getLocation())) {
            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_BLOCKED_ITEM)
                    .replace("%item%", "this team chest");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            e.setCancelled(true);
            return;
        }

        if (BedWars.config.getBoolean(
                ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_ENABLED)) {
            makeHologramIfNeeded(arena, e.getClickedBlock().getLocation());
        }

        ItemStack itemInHand = e.getItem();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;

        String customData = BedWars.nms.getCustomData(itemInHand);

        if (blockedItems.contains(itemInHand.getType())
                || BedWars.nms.isTool(itemInHand)
                || (customData != null && customData.equalsIgnoreCase("DEFAULT_ITEM"))) {

            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_BLOCKED_ITEM)
                    .replace("%item%", itemInHand.getType().name().toLowerCase());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return;
        }

        Inventory chestInventory = normalChest
                ? ((Chest) e.getClickedBlock().getState()).getBlockInventory()
                : player.getEnderChest();

        if (chestInventory.firstEmpty() == -1) {
            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_FULL);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return;
        }

        int deposited = tryDepositItems(player, itemInHand, chestInventory);

        if (deposited > 0) {
            if (normalChest) {
                markChestAsOwned(arena, e.getClickedBlock().getLocation(), playerTeam);
            }

            if (itemInHand.getType().name().contains("SWORD")) {
                playerTeam.defaultSword(player, true);
            }

            String chestType = enderChest ? "ender chest" : "team chest";
            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_DEPOSITED)
                    .replace("%amount%", String.valueOf(deposited))
                    .replace("%item%", makeNamePretty(itemInHand.getType()))
                    .replace("%chest%", chestType);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            Sounds.playSound("chest-open", player);

            Bukkit.getPluginManager().callEvent(
                    new PlayerItemDepositEvent(
                            player,
                            arena,
                            itemInHand.clone(),
                            chestInventory,
                            enderChest ? Material.ENDER_CHEST : Material.CHEST
                    )
            );
        }
    }

    private String makeNamePretty(Material material) {
        return Arrays.stream(material.name().toLowerCase().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    private boolean canUseThisChest(IArena arena, Player player, Location chestLoc) {
        Map<Location, ITeam> chestsInArena = teamChests.get(arena);
        if (chestsInArena == null) return true;

        ITeam owner = chestsInArena.get(chestLoc);
        if (owner == null) return true;

        ITeam playerTeam = arena.getTeam(player);
        return playerTeam != null && playerTeam.equals(owner);
    }

    private void markChestAsOwned(IArena arena, Location chestLoc, ITeam team) {
        Map<Location, ITeam> chestsInArena = teamChests.get(arena);
        if (chestsInArena != null && !chestsInArena.containsKey(chestLoc)) {
            chestsInArena.put(chestLoc, team);
        }
    }

    private int tryDepositItems(Player player, ItemStack hand, Inventory chest) {
        ItemStack clone = hand.clone();
        Map<Integer, ItemStack> leftovers = chest.addItem(clone);

        int tried = clone.getAmount();
        int couldntFit = leftovers.values().stream()
                .mapToInt(ItemStack::getAmount)
                .sum();

        int added = tried - couldntFit;
        if (added <= 0) {
            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_FULL);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return 0;
        }

        ItemStack toRemove = hand.clone();
        toRemove.setAmount(added);
        player.getInventory().removeItem(toRemove);

        leftovers.values().forEach(item -> player.getInventory().addItem(item));
        return added;
    }

    private void loadAllChunks(IArena arena) {
        for (ITeam team : arena.getTeams()) {
            if (team.getSpawn() != null) team.getSpawn().getChunk().load(true);
            if (team.getShop() != null) team.getShop().getChunk().load(true);
            if (team.getTeamUpgrades() != null) team.getTeamUpgrades().getChunk().load(true);
        }

        if (arena.getSpectatorLocation() != null) {
            arena.getSpectatorLocation().getChunk().load(true);
        }
    }

    private void createAllHolograms(IArena arena) {
        for (Chunk chunk : arena.getWorld().getLoadedChunks()) {
            for (BlockState block : chunk.getTileEntities()) {
                if (block.getType() == Material.CHEST || block.getType() == Material.ENDER_CHEST) {
                    makeHologramForChest(arena, block.getLocation());
                }
            }
        }
    }

    private void makeHologramForChest(IArena arena, Location chestLoc) {
        double x = BedWars.config.getYml().getDouble(
                ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_X_OFFSET, 0.5);

        double y = BedWars.config.getYml().getDouble(
                ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_Y_OFFSET, 1.5);

        double z = BedWars.config.getYml().getDouble(
                ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_Z_OFFSET, 0.5);

        double spacing = BedWars.config.getYml().getDouble(
                ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_SPACING, 0.25);

        String title = BedWars.config.getYml().getString(
                ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_TITLE,
                "&e&l⚡ STORAGE CHEST");

        String subtitle = BedWars.config.getYml().getString(
                ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_SUBTITLE,
                "&7Left-click to deposit");

        Location pos = chestLoc.clone().add(x, y, z);

        IHologram holo = hologramManager.createHologram(
                arena.getPlayers(),
                pos,
                ChatColor.translateAlternateColorCodes('&', title),
                ChatColor.translateAlternateColorCodes('&', subtitle)
        );

        holo.setGap(spacing);
        arenaHolograms.get(arena).add(holo);
    }

    private void makeHologramIfNeeded(IArena arena, Location chestLoc) {
        List<IHologram> holos = arenaHolograms.get(arena);
        if (holos == null) return;

        for (IHologram holo : holos) {
            if (holo.getLocation().distance(chestLoc) < 2.0) return;
        }

        makeHologramForChest(arena, chestLoc);
    }


}
