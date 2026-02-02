package com.tomkeuper.bedwars.arena.feature;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
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
import org.bukkit.block.Block;
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
    // We use these maps to keep track of holograms and which team owns each chest
    private final Map<IArena, List<IHologram>> arenaHolograms = new HashMap<>();
    private final Map<IArena, Map<Location, ITeam>> teamChests = new HashMap<>();
    private final IHologramManager hologramManager;
    // Some old material names changed in newer Minecraft versions
    private static final Map<String, String> OLD_MATERIAL_NAMES = new HashMap<>();

    private ResourceChestFeature() {
        this.blockedItems = BedWars.config.getYml()
                .getStringList("click-in-chest-to-deposit-feature.blocked-items")
                .stream()
                .map(String::toUpperCase)
                .map(itemName -> OLD_MATERIAL_NAMES.getOrDefault(itemName, itemName))
                .map(Material::valueOf)
                .collect(Collectors.toSet());

        this.hologramManager = BedWars.getAPI().getHologramsUtil();
        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);
        try {
            Sounds.addDefSound("ChestOpen",
                    BedWars.getForCurrentVersion("CHEST_OPEN", "BLOCK_CHEST_OPEN", "BLOCK_CHEST_OPEN"));
        } catch (Exception e) {
            BedWars.plugin.getLogger().info("Using default sound configuration for ChestOpen");
        }
    }

    public static void init() {
        if (BedWars.config.getBoolean("click-in-chest-to-deposit-feature.enable") && instance == null) {
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
            if (BedWars.config.getBoolean("resource-chest.hologram.enabled")) {
                loadAllChunks(arena);
                Bukkit.getScheduler().runTaskLater(BedWars.plugin,
                        () -> createAllHolograms(arena), 20L);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeftClickChest(PlayerInteractEvent e) {
        // Check if the player is in an arena
        IArena arena = Arena.getArenaByPlayer(e.getPlayer());
        if (arena == null) return;

        // We only care about left clicks
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) return;

        // Check if they clicked a chest or ender chest
        boolean normalChest = clickedBlock.getType() == Material.CHEST;
        boolean enderChest = clickedBlock.getType() == Material.ENDER_CHEST;
        if (!normalChest && !enderChest) return;

        Player player = e.getPlayer();
        ITeam playerTeam = arena.getTeam(player);
        if (playerTeam == null) return;

        // If it's a team chest, make sure the player is allowed to use it
        if (normalChest && !canUseThisChest(arena, player, clickedBlock.getLocation())) {
            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_BLOCKED_ITEM)
                    .replace("{item}", "this team chest");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            e.setCancelled(true);
            return;
        }

        // If holograms are on, make sure there's one above this chest
        if (BedWars.config.getBoolean("resource-chest.hologram.enabled")) {
            makeHologramIfNeeded(arena, clickedBlock.getLocation());
        }

        // Get what the player is holding
        ItemStack itemInHand = e.getItem();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;

        // Check if this item can be deposited
        if (blockedItems.contains(itemInHand.getType())
                || BedWars.nms.isTool(itemInHand)
                || BedWars.nms.getCustomData(itemInHand).equalsIgnoreCase("DEFAULT_ITEM")) {

            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_BLOCKED_ITEM)
                    .replace("{item}", itemInHand.getType().name().toLowerCase());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return;
        }

        // Get the chest's inventory (or ender chest)
        Inventory chestInventory = normalChest
                ? ((Chest) clickedBlock.getState()).getBlockInventory()
                : player.getEnderChest();

        // Check if there's any space left
        if (chestInventory.firstEmpty() == -1) {
            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_FULL);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return;
        }

        // Try to put the items in the chest
        int howManyDeposited = tryDepositItems(player, itemInHand, chestInventory);

        if (howManyDeposited > 0) {
            // If it's a normal chest, remember that this team owns it now
            if (normalChest) {
                markChestAsOwned(arena, clickedBlock.getLocation(), playerTeam);
            }

            // If they deposited a sword, give them the default one back
            if (itemInHand.getType().name().contains("SWORD")) {
                playerTeam.defaultSword(player, true);
            }

            // Tell the player it worked
            String chestType = enderChest ? "ender chest" : "team chest";
            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_DEPOSITED)
                    .replace("{amount}", String.valueOf(howManyDeposited))
                    .replace("{item}", makeNamePretty(itemInHand.getType()))
                    .replace("{chest}", chestType);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

            Sounds.playSound("ChestOpen", player);

            tellOtherPlugins(player, arena, itemInHand.clone(), chestInventory, enderChest);
        }
    }

    private boolean canUseThisChest(IArena arena, Player player, Location chestLoc) {
        Map<Location, ITeam> chestsInArena = teamChests.get(arena);
        if (chestsInArena == null) return true;

        ITeam chestOwner = chestsInArena.get(chestLoc);
        if (chestOwner == null) return true;

        ITeam playerTeam = arena.getTeam(player);
        return playerTeam != null && playerTeam.equals(chestOwner);
    }

    private void markChestAsOwned(IArena arena, Location chestLoc, ITeam team) {
        Map<Location, ITeam> chestsInArena = teamChests.get(arena);
        if (chestsInArena != null && !chestsInArena.containsKey(chestLoc)) {
            chestsInArena.put(chestLoc, team);
        }
    }

    private int tryDepositItems(Player player, ItemStack hand, Inventory chest) {
        ItemStack itemsToStore = hand.clone();
        Map<Integer, ItemStack> leftoverItems = chest.addItem(itemsToStore);

        int triedToAdd = itemsToStore.getAmount();
        int couldntFit = leftoverItems.values().stream()
                .mapToInt(ItemStack::getAmount)
                .sum();
        int actuallyAdded = triedToAdd - couldntFit;

        if (actuallyAdded <= 0) {
            String message = Language.getMsg(player, Messages.RESOURCE_CHEST_FULL);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return 0;
        }

        ItemStack toRemove = hand.clone();
        toRemove.setAmount(actuallyAdded);
        player.getInventory().removeItem(toRemove);

        leftoverItems.values().forEach(item -> player.getInventory().addItem(item));

        return actuallyAdded;
    }
    private String makeNamePretty(Material material) {
        String rawName = material.name().replace('_', ' ').toLowerCase();
        return Arrays.stream(rawName.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
    private void tellOtherPlugins(Player player, IArena arena, ItemStack item,
                                  Inventory inv, boolean isEnder) {
        Bukkit.getPluginManager().callEvent(
                new PlayerItemDepositEvent(player, arena, item, inv, isEnder));
    }
    private void loadAllChunks(IArena arena) {
        for (ITeam team : arena.getTeams()) {
            if (team.getSpawn() != null) {
                team.getSpawn().getChunk().load(true);
            }
            if (team.getShop() != null) {
                team.getShop().getChunk().load(true);
            }
            if (team.getTeamUpgrades() != null) {
                team.getTeamUpgrades().getChunk().load(true);
            }
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
        Location holoPosition = chestLoc.clone().add(0.5, 1.2, 0.5);

        IHologram hologram = hologramManager.createHologram(
                arena.getPlayers(),
                holoPosition,
                "&e&l⚡ STORAGE CHEST",
                "&7Left-click to deposit"
        );

        List<IHologram> holosInArena = arenaHolograms.get(arena);
        if (holosInArena != null) {
            holosInArena.add(hologram);
        }
    }

    private void makeHologramIfNeeded(IArena arena, Location chestLoc) {
        List<IHologram> holosInArena = arenaHolograms.get(arena);
        if (holosInArena == null) return;
        for (IHologram holo : holosInArena) {
            if (holo.getLocation().distance(chestLoc) < 2.0) {
                return; // We found one, no need to make another
            }
        }

        makeHologramForChest(arena, chestLoc);
    }

    public List<IHologram> getArenaHolograms(IArena arena) {
        return arenaHolograms.getOrDefault(arena, new ArrayList<>());
    }

    public void removeArenaHolograms(IArena arena) {
        List<IHologram> holosInArena = arenaHolograms.get(arena);
        if (holosInArena != null) {
            holosInArena.forEach(IHologram::remove);
            arenaHolograms.remove(arena);
        }
    }

    public List<IHologram> getHologramsNearLocation(IArena arena, Location spot, double howClose) {
        List<IHologram> nearby = new ArrayList<>();
        List<IHologram> allHolos = arenaHolograms.get(arena);

        if (allHolos != null) {
            for (IHologram holo : allHolos) {
                if (holo.getLocation().distance(spot) <= howClose) {
                    nearby.add(holo);
                }
            }
        }

        return nearby;
    }

    public void updateChestHologram(IArena arena, Location chestLoc,
                                    String topLine, String bottomLine) {
        for (IHologram holo : getHologramsNearLocation(arena, chestLoc, 2.0)) {
            if (holo.getLines().size() >= 2) {
                holo.setLine(0, topLine, true);
                holo.setLine(1, bottomLine, true);
            }
        }
    }

    public static ResourceChestFeature getInstance() {
        return instance;
    }

    // Old Minecraft versions used different names for some materials
    static {
        OLD_MATERIAL_NAMES.put("WOODEN_SWORD", "WOOD_SWORD");
        OLD_MATERIAL_NAMES.put("WOODEN_PICKAXE", "WOOD_PICKAXE");
        OLD_MATERIAL_NAMES.put("WOODEN_AXE", "WOOD_AXE");
        OLD_MATERIAL_NAMES.put("WOODEN_SHOVEL", "WOOD_SPADE");
        OLD_MATERIAL_NAMES.put("WOODEN_HOE", "WOOD_HOE");
    }
}