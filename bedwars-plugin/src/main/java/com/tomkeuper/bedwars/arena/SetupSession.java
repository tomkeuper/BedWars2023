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

package com.tomkeuper.bedwars.arena;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.team.TeamColor;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.server.SetupSessionCloseEvent;
import com.tomkeuper.bedwars.api.events.server.SetupSessionStartEvent;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.server.ISetupSession;
import com.tomkeuper.bedwars.api.server.ServerType;
import com.tomkeuper.bedwars.api.server.SetupType;
import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import com.tomkeuper.bedwars.configuration.ArenaConfig;
import com.tomkeuper.bedwars.support.paper.PaperSupport;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static com.tomkeuper.bedwars.BedWars.config;
import static com.tomkeuper.bedwars.BedWars.plugin;

public class SetupSession implements ISetupSession {

    private static List<SetupSession> setupSessions = new ArrayList<>();

    private Player player;
    private String worldName;
    private SetupType setupType;
    private ArenaConfig cm;
    private boolean started = false;
    private boolean autoCreatedEmerald = false;
    private boolean autoCreatedDiamond = false;
    private List<Location> skipAutoCreateGen = new ArrayList<>();
    private Map<String, IHologram> shopHologramsPerTeam = new HashMap<>();
    private Map<String, IHologram> upgradeHologramsPerTeam = new HashMap<>();
    private Map<String, IHologram> bedHologramsPerTeam = new HashMap<>();
    private Map<String, IHologram> spawnHologramsPerTeam = new HashMap<>();
    private Map<Location, IHologram> generatorHologramsPerTeam = new HashMap<>();
    private Map<String, IHologram> killDropsHologramsPerTeam = new HashMap<>();

    public SetupSession(Player player, String worldName) {
        this.player = player;
        this.worldName = worldName;
        getSetupSessions().add(this);
        openGUI(player);
    }

    public void setSetupType(SetupType setupType) {
        this.setupType = setupType;
    }

    @SuppressWarnings("WeakerAccess")
    public static List<SetupSession> getSetupSessions() {
        return setupSessions;
    }

    /**
     * Gets the setup type gui inv name
     */
    public static String getInvName() {
        return "§8Choose a setup method";
    }

    /**
     * Get advanced type item slot
     */
    public static int getAdvancedSlot() {
        return 5;
    }

    /**
     * Get assisted type item slot
     */
    public static int getAssistedSlot() {
        return 3;
    }

    public SetupType getSetupType() {
        return setupType;
    }

    public Player getPlayer() {
        return player;
    }

    public String getWorldName() {
        return worldName;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isStarted() {
        return started;
    }

    /**
     * Start setup session, loadStructure world etc
     *
     * @return return is broken. do not use it.
     */
    public boolean startSetup() {
        getPlayer().sendMessage("§6 ▪ §7Loading " + getWorldName());
        cm = new ArenaConfig(BedWars.plugin, getWorldName(), plugin.getDataFolder().getPath() + "/Arenas");
        BedWars.getAPI().getRestoreAdapter().onSetupSessionStart(this);
        return true;
    }

    private static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, getInvName());
        ItemStack assisted = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta am = assisted.getItemMeta();
        am.setDisplayName("§e§lASSISTED SETUP");
        am.setLore(Arrays.asList("", "§aEasy and quick setup!", "§7For beginners and lazy staff :D", "", "§3Reduced options."));
        assisted.setItemMeta(am);
        inv.setItem(getAssistedSlot(), assisted);

        ItemStack advanced = new ItemStack(Material.REDSTONE);
        ItemMeta amm = advanced.getItemMeta();
        amm.setDisplayName("§c§lADVANCED SETUP");
        amm.setLore(Arrays.asList("", "§aDetailed setup!", "§7For experienced staff :D", "", "§3Advanced options."));
        advanced.setItemMeta(amm);
        inv.setItem(getAdvancedSlot(), advanced);

        player.openInventory(inv);
    }

    /**
     * Cancel setup
     */
    public void cancel() {
        getSetupSessions().remove(this);
        if (isStarted()) {
            player.sendMessage("§6 ▪ §7" + getWorldName() + " setup cancelled!");
            done();
        }
    }

    /**
     * End setup session
     */
    public void done() {
        BedWars.getAPI().getRestoreAdapter().onSetupSessionClose(this);
        getSetupSessions().remove(this);
        if (BedWars.getServerType() != ServerType.BUNGEE) {
            try {
                PaperSupport.teleportC(getPlayer(), config.getConfigLoc("lobbyLoc"), PlayerTeleportEvent.TeleportCause.PLUGIN);
            } catch (Exception ex) {
                PaperSupport.teleportC(getPlayer(), Bukkit.getWorlds().get(0).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
        getPlayer().removePotionEffect(PotionEffectType.SPEED);
        if (BedWars.getServerType() == ServerType.MULTIARENA) Arena.sendLobbyCommandItems(getPlayer());
        Bukkit.getPluginManager().callEvent(new SetupSessionCloseEvent(this));
    }

    /**
     * Check if a player is in setup session
     */
    public static boolean isInSetupSession(UUID player) {
        for (SetupSession ss : getSetupSessions()) {
            if (ss.getPlayer().getUniqueId().equals(player)) return true;
        }
        return false;
    }

    /**
     * Get a player session
     */
    public static SetupSession getSession(UUID p) {
        for (SetupSession ss : getSetupSessions()) {
            if (ss.getPlayer().getUniqueId().equals(p)) return ss;
        }
        return null;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * Get arena configuration
     */
    public ArenaConfig getConfig() {
        return cm;
    }

    @Override
    public void teleportPlayer() {
        player.getInventory().clear();
        PaperSupport.teleport(player, Bukkit.getWorld(getWorldName()).getSpawnLocation());
        player.setGameMode(GameMode.CREATIVE);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            player.setAllowFlight(true);
            player.setFlying(true);
        }, 5L);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        player.sendMessage("\n" + ChatColor.WHITE + "\n");

        for (int x = 0; x < 10; x++) {
            getPlayer().sendMessage(" ");
        }
        player.sendMessage(ChatColor.GREEN + "You were teleported to the " + ChatColor.GOLD + getWorldName() + ChatColor.GREEN + "'s spawn.");
        if (getSetupType() == SetupType.ASSISTED && getConfig().getYml().get("waiting.Loc") == null) {
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "Hello " + player.getDisplayName() + "!");
            player.sendMessage(ChatColor.WHITE + "Please set the waiting spawn.");
            player.sendMessage(ChatColor.WHITE + "It is the place where players will wait the game to start.");
            player.spigot().sendMessage(Misc.msgHoverClick(ChatColor.BLUE + "     ▪     " + ChatColor.GOLD + "CLICK HERE TO SET THE WAITING LOBBY    " + ChatColor.BLUE + " ▪", ChatColor.LIGHT_PURPLE + "Click to set the waiting spawn.", "/" + BedWars.mainCmd + " setWaitingSpawn", ClickEvent.Action.RUN_COMMAND));
            player.spigot().sendMessage(MainCommand.createTC(ChatColor.YELLOW + "Or type: " + ChatColor.GRAY + "/" + BedWars.mainCmd + " to see the command list.", "/" + BedWars.mainCmd + "", ChatColor.WHITE + "Show commands list."));
        } else {
            Bukkit.dispatchCommand(player, BedWars.mainCmd + " cmds");
        }

        World w = Bukkit.getWorld(getWorldName());
        Bukkit.getScheduler().runTaskLater(plugin, () -> w.getEntities().stream()
                .filter(e -> e.getType() != EntityType.PLAYER).filter(e -> e.getType() != EntityType.PAINTING)
                .filter(e -> e.getType() != EntityType.ITEM_FRAME).forEach(Entity::remove), 30L);
        w.setAutoSave(false);
        w.setGameRuleValue("doMobSpawning", "false");
        Bukkit.getPluginManager().callEvent(new SetupSessionStartEvent(this));
        setStarted(true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (String team : getTeams()) {
                for (String gen : new String[]{"Iron", "Gold", "Emerald"}) {
                    if (getConfig().getYml().get("Team." + team + "." + gen) != null) {
                        for (String loc : getConfig().getList("Team." + team + "." + gen)) {
                            createGeneratorHologram(player, getConfig().convertStringToArenaLocation(loc), team, gen);
                        }
                    }
                }
                if (getConfig().getYml().get("Team." + team + ".Spawn") != null) {
                    createSpawnHologram(player, getConfig().getArenaLoc("Team." + team + ".Spawn"), team);
                }
                if (getConfig().getYml().get("Team." + team + ".Bed") != null) {
                    createBedHologram(player, getConfig().getArenaLoc("Team." + team + ".Bed"), team);
                }
                if (getConfig().getYml().get("Team." + team + ".Shop") != null) {
                    createShopHologram(player, getConfig().getArenaLoc("Team." + team + ".Shop"), team);
                }
                if (getConfig().getYml().get("Team." + team + ".Upgrade") != null) {
                    createUpgradeHologram(player, getConfig().getArenaLoc("Team." + team + ".Upgrade"), team);
                }
                if (getConfig().getYml().get("Team." + team + "." + ConfigPath.ARENA_TEAM_KILL_DROPS_LOC) != null) {
                    createKillDropsHologram(player, getConfig().getArenaLoc("Team." + team + "." + ConfigPath.ARENA_TEAM_KILL_DROPS_LOC), team);
                }
            }

            for (String type : new String[]{"Emerald", "Diamond"}) {
                if (getConfig().getYml().get("generator." + type) != null) {
                    for (String loc : getConfig().getList("generator." + type)) {
                        createGeneratorHologram(player, getConfig().convertStringToArenaLocation(loc), null, type);
                    }
                }
            }
        }, 90L);
    }

    @Override
    public void close() {
        cancel();
    }

    public List<Location> getSkipAutoCreateGen() {
        return new ArrayList<>(skipAutoCreateGen);
    }

    public void addSkipAutoCreateGen(Location location) {
        skipAutoCreateGen.add(location);
    }

    public void setAutoCreatedEmerald(boolean autoCreatedEmerald) {
        this.autoCreatedEmerald = autoCreatedEmerald;
    }

    public boolean isAutoCreatedEmerald() {
        return autoCreatedEmerald;
    }

    public void setAutoCreatedDiamond(boolean autoCreatedDiamond) {
        this.autoCreatedDiamond = autoCreatedDiamond;
    }

    public boolean isAutoCreatedDiamond() {
        return autoCreatedDiamond;
    }

    public String getPrefix() {
        return ChatColor.GREEN + "[" + getWorldName() + ChatColor.GREEN + "] " + ChatColor.GOLD;
    }

    /**
     * Get a team color.
     *
     * @param team team name.
     * @return team color.
     */
    public ChatColor getTeamColor(String team) {
        return TeamColor.getChatColor(getConfig().getString("Team." + team + ".Color"));
    }

    /**
     * Show available teams.
     */
    public void displayAvailableTeams() {
        if (getConfig().getYml().get("Team") != null) {
            getPlayer().sendMessage(getPrefix() + "Available teams: ");
            for (String team : Objects.requireNonNull(getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                getPlayer().sendMessage(getPrefix() + TeamColor.getChatColor(Objects.requireNonNull(getConfig().getYml().getString("Team." + team + ".Color"))) + team);
            }
        }
    }

    /**
     * Get nearest team name.
     *
     * @return empty if not found.
     */
    public String getNearestTeam() {
        String foundTeam = "";
        ConfigurationSection cs = getConfig().getYml().getConfigurationSection("Team");
        if (cs == null) return foundTeam;
        double distance = 100;
        for (String team : cs.getKeys(false)) {
            if (getConfig().getYml().get("Team." + team + ".Spawn") == null) continue;
            double dis = getConfig().getArenaLoc("Team." + team + ".Spawn").distance(getPlayer().getLocation());
            if (dis <= getConfig().getInt(ConfigPath.ARENA_ISLAND_RADIUS)) {
                if (dis < distance) {
                    distance = dis;
                    foundTeam = team;
                }
            }
        }
        return foundTeam;
    }

    public String dot() {
        return ChatColor.BLUE + " " + '▪' + " " + ChatColor.GRAY + "/" + BedWars.mainCmd + " ";
    }

    public List<String> getTeams() {
        if (getConfig().getYml().get("Team") == null) return new ArrayList<>();
        return new ArrayList<>(getConfig().getYml().getConfigurationSection("Team").getKeys(false));
    }

    public void createBedHologram(Player p, Location loc, String team) {
        manageHologram(
                bedHologramsPerTeam, p, loc, team, getTeamColor(team) + team + " " + ChatColor.GOLD + "BED SET"
        );
    }

    public void createSpawnHologram(Player p, Location loc, String team) {
        manageHologram(
                spawnHologramsPerTeam, p, loc, team, getTeamColor(team) + team + " " + ChatColor.GOLD + "SPAWN SET"
        );
    }

    public void createShopHologram(Player p, Location loc, String team) {
        manageHologram(
                shopHologramsPerTeam, p, loc, team, getTeamColor(team) + team + " " + ChatColor.GOLD + "SHOP SET"
        );
    }

    public void createUpgradeHologram(Player p, Location loc, String team) {
        manageHologram(
                upgradeHologramsPerTeam, p, loc, team, getTeamColor(team) + team + " " + ChatColor.GOLD + "UPGRADE SET"
        );
    }

    public void createKillDropsHologram(Player p, Location loc, String team) {
        manageHologram(
                killDropsHologramsPerTeam, p, loc, team, getTeamColor(team) + team + " " + ChatColor.GOLD + "KILL DROPS SET"
        );
    }

    public void createGeneratorHologram(Player p, Location loc, String team, String type) {
        String message = team != null
                ? getTeamColor(team) + team + " " + ChatColor.GOLD + type + " GENERATOR SET"
                : ChatColor.GOLD + type + " GENERATOR SET";

        manageHologramForLocation(generatorHologramsPerTeam, p, loc, message);
    }

    public void removeBedHologram(String team) {
        if (bedHologramsPerTeam.containsKey(team)) {
            bedHologramsPerTeam.get(team).remove();
            bedHologramsPerTeam.remove(team);
        }
    }

    public void removeSpawnHologram(String team) {
        if (spawnHologramsPerTeam.containsKey(team)) {
            spawnHologramsPerTeam.get(team).remove();
            spawnHologramsPerTeam.remove(team);
        }
    }

    public void removeShopHologram(String team) {
        if (shopHologramsPerTeam.containsKey(team)) {
            shopHologramsPerTeam.get(team).remove();
            shopHologramsPerTeam.remove(team);
        }
    }

    public void removeUpgradeHologram(String team) {
        if (upgradeHologramsPerTeam.containsKey(team)) {
            upgradeHologramsPerTeam.get(team).remove();
            upgradeHologramsPerTeam.remove(team);
        }
    }

    public void removeKillDropsHologram(String team) {
        if (killDropsHologramsPerTeam.containsKey(team)) {
            killDropsHologramsPerTeam.get(team).remove();
            killDropsHologramsPerTeam.remove(team);
        }
    }

    public void removeGeneratorHologram(Location loc) {
        // Find the hologram using the helper method
        IHologram hologram = getHologramForLocation(generatorHologramsPerTeam, loc);

        if (hologram != null) {
            // Remove the hologram from the game
            hologram.remove();

            // Remove the entry from the generator hologram map
            generatorHologramsPerTeam.entrySet().removeIf(entry -> isSameBlockLocation(entry.getKey(), loc));
        }
    }

    public void removeGeneratorHologramLineContainingType(Location loc, GeneratorType type) {
        IHologram hologram = getHologramForLocation(generatorHologramsPerTeam, loc);

        if (hologram != null) {
            BedWars.debug("Removing line containing " + type + " from hologram at " + loc);
            hologram.removeLineContaining(type.toString()); // Remove the line containing the type
            hologram.update();
        } else {
            BedWars.debug("Hologram not found for location: " + loc);
        }
    }

    private void manageHologram(Map<String, IHologram> teamHologramsMap, Player player, Location location, String team, String displayText) {
        // Make sure team name in map is always lowercase
        String lowerTeam = team != null ? team.toLowerCase() : null;

        // Check and remove existing hologram for the team
        if (teamHologramsMap.containsKey(lowerTeam)) {
            teamHologramsMap.get(lowerTeam).remove();
        }

        teamHologramsMap.put(lowerTeam, BedWars.nms.createHologram(player, location, displayText));
    }

    private void manageHologramForLocation(Map<Location, IHologram> locationHologramsMap, Player player, Location location, String displayText) {
        // Get the hologram using the new utility method
        IHologram hologram = getHologramForLocation(locationHologramsMap, location);

        if (hologram != null) {
            hologram.addLine(displayText); // Update existing hologram
        } else {
            locationHologramsMap.put(location, BedWars.nms.createHologram(player, location, displayText)); // Create a new hologram
        }
    }

    private IHologram getHologramForLocation(Map<Location, IHologram> locationHologramsMap, Location location) {
        // Debugging: Log the map and input location for analysis
        BedWars.debug("Looking for hologram at location: " + location + " in map with " + locationHologramsMap.size() + " entries");

        // Stream to find the existing location by comparing x, y, z, and world
        IHologram hologram = locationHologramsMap.entrySet().stream()
                .filter(entry -> isSameBlockLocation(entry.getKey(), location))
                .map(Map.Entry::getValue) // Get the IHologram related to the matching location
                .findFirst()
                .orElse(null); // Return null if no match is found

        if (hologram == null) {
            // Debugging: Log all entries for failed matches
            locationHologramsMap.forEach((key, value) -> BedWars.debug("Map entry: Location=" + key + ", IHologram=" + value));
            BedWars.debug("No hologram found for location: " + location);
        } else {
            BedWars.debug("Found hologram at location: " + location);
        }

        return hologram;
    }

    private boolean isSameBlockLocation(Location loc1, Location loc2) {
        if (loc1.getWorld() == null || loc2.getWorld() == null) return false;

        // Compare block-level coordinates and world
        return loc1.getWorld().getName().equals(loc2.getWorld().getName()) &&
                loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }
}
