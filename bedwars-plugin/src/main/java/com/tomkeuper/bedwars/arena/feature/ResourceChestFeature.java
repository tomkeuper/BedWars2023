package com.tomkeuper.bedwars.arena.feature;


import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.Igram;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerItemDepositEvent;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.configuration.Sounds;
import org.bukkit.*;
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
    private final Set<Material> blocked;
    private final Map<IArena, List<Igram>> holoMap = new HashMap<>();
    private final Map<IArena, Map<Location, ITeam>> chestOwnership = new HashMap<>();
    private static final Map<String, String> LEGACY_MATERIALS = new HashMap<>();
    static {
        LEGACY_MATERIALS.put("WOODEN_SWORD", "WOOD_SWORD");
        LEGACY_MATERIALS.put("WOODEN_PICKAXE", "WOOD_PICKAXE");
        LEGACY_MATERIALS.put("WOODEN_AXE", "WOOD_AXE");
        LEGACY_MATERIALS.put("WOODEN_SHOVEL", "WOOD_SPADE"); // in 1.12, shovel was SPADE
        LEGACY_MATERIALS.put("WOODEN_HOE", "WOOD_HOE");
    }

    private ResourceChestFeature() {
        this.blocked = BedWars.config.getYml()
                .getStringList(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_BLOCKED)
                .stream()
                .map(String::toUpperCase)
                .map(name -> LEGACY_MATERIALS.getOrDefault(name, name))  // Map 1.13+ names to 1.12 names
                .map(Material::valueOf)
                .collect(Collectors.toSet());
        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);


        // Add the chest open sound
        try {
            Sounds.addDefSound("ChestOpen", BedWars.getForCurrentVersion("CHEST_OPEN", "BLOCK_CHEST_OPEN", "BLOCK_CHEST_OPEN"));
        } catch (Exception e) {
            // If addDefSound doesn't work, the sound should be configured in sounds.yml
            BedWars.plugin.getLogger().info("Using default sound configuration for ChestOpen");
        }
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
            if (holoMap.containsKey(arena)) {
                holoMap.get(arena).forEach(Igram::remove);
            }
            holoMap.remove(arena);
            chestOwnership.remove(arena);
        } else if (e.getNewState() == GameState.playing) {
            holoMap.put(arena, new ArrayList<>());
            chestOwnership.put(arena, new HashMap<>());

            // Check if holograms are enabled in config
            if (BedWars.config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_ENABLED)) {
                // Force load all chunks in the arena first
                loadArenaChunks(arena);

                // Small delay to ensure chunks are fully loaded
                Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
                    createHologramsForArena(arena);
                }, 20L); // 1 second delay
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeftClickChest(PlayerInteractEvent e) {
        IArena arena = Arena.getArenaByPlayer(e.getPlayer());
        if (arena == null || e.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        boolean isChest = block.getType() == Material.CHEST;
        boolean isEnderChest = block.getType() == Material.ENDER_CHEST;
        if (!isChest && !isEnderChest) return;

        Player player = e.getPlayer();
        ITeam playerTeam = arena.getTeam(player);
        if (playerTeam == null) return;

        // Check team ownership for regular chests
        if (isChest && !canPlayerAccessChest(arena, player, block.getLocation())) {
            String raw = Language.getMsg(player, Messages.RESOURCE_CHEST_BLOCKED_ITEM)
                    .replace("{item}", "this team chest");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', raw));
            e.setCancelled(true);
            return;
        }

        // Create hologram for this chest if it doesn't exist and holograms are enabled
        if (BedWars.config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_ENABLED)) {
            ensureHologramExists(arena, block.getLocation());
        }

        ItemStack hand = e.getItem();
        if (hand == null || hand.getType() == Material.AIR) return;

        if (blocked.contains(hand.getType())
                || BedWars.nms.isTool(hand)
                || BedWars.nms.getCustomData(hand).equalsIgnoreCase("DEFAULT_ITEM")) {
            String raw = Language.getMsg(player, Messages.RESOURCE_CHEST_BLOCKED_ITEM)
                    .replace("{item}", hand.getType().name().toLowerCase());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', raw));
            return;
        }

        Inventory inventory = isChest
                ? ((Chest) block.getState()).getBlockInventory()
                : player.getEnderChest();

        if (inventory.firstEmpty() == -1) {
            String raw = Language.getMsg(player, Messages.RESOURCE_CHEST_FULL);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', raw));
            return;
        }

        int inserted = safeDeposit(player, hand, inventory);
        if (inserted <= 0) return;

        // Set ownership for the chest if it's a regular chest and first time being used
        if (isChest) {
            setChestOwnership(arena, block.getLocation(), playerTeam);
        }

        if (hand.getType().name().contains("SWORD")) {
            playerTeam.defaultSword(player, true);
        }

        String chestType = isEnderChest ? "ender chest" : "team chest";
        String raw = Language.getMsg(player, Messages.RESOURCE_CHEST_DEPOSITED)
                .replace("{amount}", String.valueOf(inserted))
                .replace("{item}", prettyName(hand.getType()))
                .replace("{chest}", chestType);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', raw));
        Sounds.playSound("ChestOpen", player);
        callEvent(player, arena, hand.clone(), inventory, isEnderChest);
    }

    private boolean canPlayerAccessChest(IArena arena, Player player, Location chestLocation) {
        Map<Location, ITeam> arenaChests = chestOwnership.get(arena);
        if (arenaChests == null) return true;

        ITeam chestOwner = arenaChests.get(chestLocation);
        if (chestOwner == null) return true; // Chest has no owner yet

        ITeam playerTeam = arena.getTeam(player);
        return playerTeam != null && playerTeam.equals(chestOwner);
    }

    private void setChestOwnership(IArena arena, Location chestLocation, ITeam team) {
        Map<Location, ITeam> arenaChests = chestOwnership.get(arena);
        if (arenaChests != null && !arenaChests.containsKey(chestLocation)) {
            arenaChests.put(chestLocation, team);
        }
    }

    private int safeDeposit(Player player, ItemStack hand, Inventory inventory) {
        ItemStack toStore = hand.clone();
        Map<Integer, ItemStack> leftovers = inventory.addItem(toStore);

        int attempted = toStore.getAmount();
        int notInserted = leftovers.values().stream().mapToInt(ItemStack::getAmount).sum();
        int inserted = attempted - notInserted;

        if (inserted <= 0) {
            String raw = Language.getMsg(player, Messages.RESOURCE_CHEST_FULL);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', raw));
            return 0;
        }

        ItemStack toRemove = hand.clone();
        toRemove.setAmount(inserted);
        player.getInventory().removeItem(toRemove);

        leftovers.values().forEach(player.getInventory()::addItem);
        return inserted;
    }

    private String prettyName(Material mat) {
        String raw = mat.name().replace('_', ' ').toLowerCase();
        return Arrays.stream(raw.split(" "))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));
    }

    private void callEvent(Player player, IArena arena, ItemStack item, Inventory inventory, boolean isEnderChest) {
        Bukkit.getPluginManager().callEvent(new PlayerItemDepositEvent(player, arena, item, inventory, isEnderChest));
    }

    private void loadArenaChunks(IArena arena) {
        // Force load chunks for each team's island
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

        // Also load spawn area chunks
        if (arena.getSpectatorLocation() != null) {
            arena.getSpectatorLocation().getChunk().load(true);
        }
    }

    private void createHologramsForArena(IArena arena) {
        // Scan all loaded chunks again after forcing chunk loading
        for (Chunk chunk : arena.getWorld().getLoadedChunks()) {
            for (BlockState state : chunk.getTileEntities()) {
                if (state.getType() == Material.CHEST || state.getType() == Material.ENDER_CHEST) {
                    // Use the API to create hologram
                    Igram holo = BedWars.getAPI().getmama().createHologram(state.getLocation());
                    holo.createResourceChestHologram(state.getLocation());
                    holoMap.get(arena).add(holo);
                }
            }
        }
    }

    private void ensureHologramExists(IArena arena, Location chestLocation) {
        List<Igram> holograms = holoMap.get(arena);
        if (holograms == null) return;

        // Check if hologram already exists for this location
        for (Igram holo : holograms) {
            if (!holo.isRemoved() && holo.getLocation().distance(chestLocation) < 2.0) {
                return; // Hologram already exists
            }
        }

        // Create new hologram for this chest using the API
        Igram holo = BedWars.getAPI().getmama().createHologram(chestLocation);
        holo.createResourceChestHologram(chestLocation);
        holograms.add(holo);
    }

    // Additional utility methods for API integration

    /**
     * Get all holograms for a specific arena
     * @param arena The arena to get holograms for
     * @return List of holograms in the arena
     */
    public List<Igram> getArenaHolograms(IArena arena) {
        return holoMap.getOrDefault(arena, new ArrayList<>());
    }

    /**
     * Remove all holograms for a specific arena
     * @param arena The arena to remove holograms from
     */
    public void removeArenaHolograms(IArena arena) {
        List<Igram> holograms = holoMap.get(arena);
        if (holograms != null) {
            holograms.forEach(Igram::remove);
            holoMap.remove(arena);
        }
    }

    /**
     * Get holograms near a specific location
     * @param location The center location
     * @param radius The search radius
     * @return List of holograms within the radius
     */
    public List<Igram> getHologramsNearLocation(Location location, double radius) {
        return BedWars.getAPI().getmama().getHologramsInRadius(location, radius);
    }

    /**
     * Update hologram text for a specific chest
     * @param chestLocation The chest location
     * @param titleText New title text
     * @param subtitleText New subtitle text
     */
    public void updateChestHologram(Location chestLocation, String titleText, String subtitleText) {
        List<Igram> nearby = getHologramsNearLocation(chestLocation, 2.0);
        for (Igram holo : nearby) {
            if (holo.getLines().size() >= 2) {
                holo.updateLine(0, titleText);
                holo.updateLine(1, subtitleText);
            }
        }
    }

    /**
     * Get the singleton instance
     * @return The ResourceChestFeature instance
     */
    public static ResourceChestFeature getInstance() {
        return instance;
    }
}