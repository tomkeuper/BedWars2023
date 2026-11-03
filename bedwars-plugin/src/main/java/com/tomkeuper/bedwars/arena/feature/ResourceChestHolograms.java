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
import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ResourceChestHolograms {

    private static final Map<IArena, List<ChestHolo>> BY_ARENA = new HashMap<>();
    private static final Map<IArena, BukkitTask> PENDING = new HashMap<>();

    private ResourceChestHolograms() {
    }

    public static void start(IArena arena) {
        if (arena == null || !enabled()) return;
        cancelPending(arena);
        PENDING.put(arena, Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
            PENDING.remove(arena);
            if (arena.getStatus() != GameState.playing || arena.getWorld() == null) return;
            spawn(arena);
        }, 20L));
    }

    public static void clear(IArena arena) {
        if (arena == null) return;
        cancelPending(arena);
        List<ChestHolo> chests = BY_ARENA.remove(arena);
        if (chests == null) return;
        for (ChestHolo chest : chests) chest.remove();
    }

    public static void feedback(IArena arena, Block block, ItemStack item, int amount, boolean full) {
        if (!enabled()) return;
        ChestHolo chest = find(arena, block);
        if (chest == null) return;
        String path = full ? Messages.RESOURCE_CHEST_HOLOGRAM_FULL : Messages.RESOURCE_CHEST_HOLOGRAM_DEPOSIT;
        for (String iso : new ArrayList<>(chest.holograms.keySet())) {
            Language language = Language.getLang(iso);
            show(chest, iso, message(language, path, amount, item), null);
        }
        if (chest.reset != null) chest.reset.cancel();
        chest.reset = Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
            chest.reset = null;
            if (!BY_ARENA.containsKey(arena)) return;
            for (String iso : new ArrayList<>(chest.holograms.keySet())) {
                show(chest, iso, idle(Language.getLang(iso)), null);
            }
        }, 40L);
    }

    public static void addViewer(IArena arena, Player player) {
        if (!enabled()) return;
        List<ChestHolo> chests = BY_ARENA.get(arena);
        if (chests == null || player == null || !player.isOnline()) return;
        Language language = Language.getPlayerLanguage(player);
        String iso = language.getIso();
        List<Player> alone = new ArrayList<>();
        alone.add(player);
        for (ChestHolo chest : chests) {
            IHologram hologram = chest.holograms.get(iso);
            if (hologram == null) {
                show(chest, iso, idle(language), alone);
                continue;
            }
            if (hologram.getPlayers().contains(player)) continue;
            hologram.addPlayer(player);
            for (IHoloLine line : hologram.getLines()) line.reveal(player);
            hologram.update(player);
        }
    }

    public static void removeViewer(IArena arena, Player player) {
        List<ChestHolo> chests = BY_ARENA.get(arena);
        if (chests == null || player == null) return;
        for (ChestHolo chest : chests) {
            for (IHologram hologram : chest.holograms.values()) {
                if (hologram.getPlayers().contains(player)) hologram.removePlayer(player);
            }
        }
    }

    public static void refresh(IArena arena, Player player) {
        if (!enabled()) return;
        List<ChestHolo> chests = BY_ARENA.get(arena);
        if (chests == null || player == null || !player.isOnline()) return;
        String iso = Language.getPlayerLanguage(player).getIso();
        for (ChestHolo chest : chests) {
            IHologram hologram = chest.holograms.get(iso);
            if (hologram == null) continue;
            Location location = hologram.getLocation();
            if (location.getWorld() == null || !location.getWorld().equals(player.getWorld())) continue;
            if (!hologram.getPlayers().contains(player)) {
                hologram.addPlayer(player);
                for (IHoloLine line : hologram.getLines()) line.reveal(player);
            }
            if (player.getLocation().distance(location) > BedWars.hologramUpdateDistance) hologram.update(player);
        }
    }

    private static void spawn(IArena arena) {
        List<ChestHolo> previous = BY_ARENA.remove(arena);
        if (previous != null) {
            for (ChestHolo chest : previous) chest.remove();
        }
        List<ChestHolo> chests = new ArrayList<>();
        BY_ARENA.put(arena, chests);
        List<Block> blocks = new ArrayList<>();
        for (ITeam team : arena.getTeams()) {
            for (Block block : team.getTeamChests().getAll()) {
                if (isChest(block.getType())) blocks.add(block);
            }
        }
        for (Block block : blocks) chests.add(new ChestHolo(block));
        for (Language language : Language.getLanguages()) {
            List<Player> players = viewers(arena, language);
            if (players.isEmpty()) continue;
            for (ChestHolo chest : chests) show(chest, language.getIso(), idle(language), players);
        }
    }

    private static void show(ChestHolo chest, String iso, String[] lines, List<Player> players) {
        if (lines.length == 0) return;
        IHologram previous = chest.holograms.remove(iso);
        if (previous != null) {
            if (players == null) players = new ArrayList<>(previous.getPlayers());
            previous.remove();
        }
        if (players == null) players = new ArrayList<>();
        players.removeIf(player -> player == null || !player.isOnline());
        IHologram hologram = BedWars.nms.createHologram(players, chest.block.getLocation().clone().add(0.5, yOffset(), 0.5), lines);
        hologram.setGap(0.3);
        chest.holograms.put(iso, hologram);
    }

    private static boolean enabled() {
        return BedWars.config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_ENABLED);
    }

    private static double yOffset() {
        return BedWars.config.getDouble(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_Y_OFFSET);
    }

    private static void cancelPending(IArena arena) {
        BukkitTask task = PENDING.remove(arena);
        if (task != null) task.cancel();
    }

    private static ChestHolo find(IArena arena, Block block) {
        List<ChestHolo> chests = BY_ARENA.get(arena);
        if (chests == null || block == null) return null;
        for (ChestHolo chest : chests) {
            if (chest.block.equals(block)) return chest;
        }
        return null;
    }

    private static List<Player> viewers(IArena arena, Language language) {
        List<Player> players = new ArrayList<>();
        for (Player player : arena.getPlayers()) {
            if (speaks(player, language)) players.add(player);
        }
        for (Player player : arena.getSpectators()) {
            if (speaks(player, language)) players.add(player);
        }
        return players;
    }

    private static boolean speaks(Player player, Language language) {
        return player != null && player.isOnline() && Language.getPlayerLanguage(player).getIso().equals(language.getIso());
    }

    private static String[] idle(Language language) {
        return language.l(Messages.RESOURCE_CHEST_HOLOGRAM).toArray(new String[0]);
    }

    private static String[] message(Language language, String path, int amount, ItemStack item) {
        String name = itemName(language, item, amount);
        String line = language.m(path)
                .replace("%bw_amount%", Integer.toString(amount))
                .replace("%bw_item%", name)
                .replace("%bw_meaning%", name)
                .replace("%bw_material%", item == null ? "" : item.getType().name());
        return new String[]{line};
    }

    private static String itemName(Language language, ItemStack item, int amount) {
        if (item == null || item.getType() == Material.AIR) return "";
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName() && !meta.getDisplayName().isEmpty()) return meta.getDisplayName();
        }
        String meaning = meaning(item.getType(), amount == 1);
        if (meaning != null) return language.m(meaning);
        String[] parts = item.getType().name().toLowerCase(Locale.ROOT).split("_");
        StringBuilder name = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (name.length() > 0) name.append(' ');
            name.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return name.toString();
    }

    private static String meaning(Material type, boolean singular) {
        switch (type) {
            case IRON_INGOT:
                return singular ? Messages.MEANING_IRON_SINGULAR : Messages.MEANING_IRON_PLURAL;
            case GOLD_INGOT:
                return singular ? Messages.MEANING_GOLD_SINGULAR : Messages.MEANING_GOLD_PLURAL;
            case DIAMOND:
                return singular ? Messages.MEANING_DIAMOND_SINGULAR : Messages.MEANING_DIAMOND_PLURAL;
            case EMERALD:
                return singular ? Messages.MEANING_EMERALD_SINGULAR : Messages.MEANING_EMERALD_PLURAL;
            default:
                return null;
        }
    }

    private static boolean isChest(Material type) {
        return type == Material.CHEST || type == Material.ENDER_CHEST;
    }

    private static final class ChestHolo {
        private final Block block;
        private final Map<String, IHologram> holograms = new HashMap<>();
        private BukkitTask reset;

        private ChestHolo(Block block) {
            this.block = block;
        }

        private void remove() {
            if (reset != null) reset.cancel();
            for (IHologram hologram : holograms.values()) hologram.remove();
            holograms.clear();
        }
    }
}
