/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
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
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.tomkeuper.bedwars.api;

import com.tomkeuper.bedwars.api.addon.IAddonManager;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.shop.IContentTier;
import com.tomkeuper.bedwars.api.chat.IChat;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import com.tomkeuper.bedwars.api.database.IDatabase;
import com.tomkeuper.bedwars.api.economy.IEconomy;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.SupportPAPI;
import com.tomkeuper.bedwars.api.levels.Level;
import com.tomkeuper.bedwars.api.party.Party;
import com.tomkeuper.bedwars.api.server.ISetupSession;
import com.tomkeuper.bedwars.api.server.RestoreAdapter;
import com.tomkeuper.bedwars.api.server.ServerType;
import com.tomkeuper.bedwars.api.server.VersionSupport;
import com.tomkeuper.bedwars.api.shop.IPlayerQuickBuyCache;
import com.tomkeuper.bedwars.api.shop.IShopCache;
import com.tomkeuper.bedwars.api.shop.IShopManager;
import com.tomkeuper.bedwars.api.sidebar.IScoreboardService;
import com.tomkeuper.bedwars.api.upgrades.MenuContent;
import com.tomkeuper.bedwars.api.upgrades.UpgradesIndex;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

@SuppressWarnings("unused")
public interface BedWars {
    /**
     * Get stats utils.
     */
    IStats getStatsUtil();

    /**
     * Get addon util
     */
    IAddonManager getAddonsUtil();

    interface IStats {
        /**
         * Get the player's first play date.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The timestamp of the player's first play date.
         */
        Timestamp getPlayerFirstPlay(UUID p);

        /**
         * Get the player's last play date.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The timestamp of the player's last play date.
         */
        Timestamp getPlayerLastPlay(UUID p);

        /**
         * Get the player's total wins.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The total number of wins for the player.
         */
        int getPlayerWins(UUID p);

        /**
         * Get the player's regular kills.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The number of regular kills for the player.
         */
        int getPlayerKills(UUID p);

        /**
         * Get the player's total kills.
         * Regular kills + final kills.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The total number of kills for the player.
         */
        int getPlayerTotalKills(UUID p);

        /**
         * Get the player's total final kills.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The total number of final kills for the player.
         */
        int getPlayerFinalKills(UUID p);

        /**
         * Get the player's total looses.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The total number of losses for the player.
         */
        int getPlayerLoses(UUID p);

        /**
         * Get the player's total deaths.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The total number of deaths for the player.
         */
        int getPlayerDeaths(UUID p);

        /**
         * Get the player's total final deaths.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The total number of final deaths for the player.
         */
        int getPlayerFinalDeaths(UUID p);

        /**
         * Get the player's beds destroyed.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The number of beds destroyed by the player.
         */
        int getPlayerBedsDestroyed(UUID p);

        /**
         * Get the player's games played.
         * You get data from the local cache.
         *
         * @param p The UUID of the player.
         * @return The number of games played by the player.
         */
        int getPlayerGamesPlayed(UUID p);
    }

    /**
     * Get afk system methods. It will only work if the game is started.
     */
    AFKUtil getAFKUtil();

    /**
     * Utility interface for managing AFK (Away From Keyboard) status of players in the bed wars mini-game.
     */
    interface AFKUtil {
        /**
         * Checks if a player is marked as AFK (Away From Keyboard).
         *
         * @param player the player to check
         * @return true if the player is AFK, false otherwise
         */
        boolean isPlayerAFK(Player player);

        /**
         * Sets the AFK (Away From Keyboard) status of a player.
         *
         * @param player the player to set AFK status for
         * @param value  true to mark the player as AFK, false to mark them as not AFK
         */
        void setPlayerAFK(Player player, boolean value);

        /**
         * Gets the amount of time in seconds that a player has been AFK (Away From Keyboard).
         *
         * @param player the player to get AFK time for
         * @return the number of seconds since the player became AFK, or 0 if they are not AFK
         */
        @SuppressWarnings("unused")
        int getPlayerTimeAFK(Player player);
    }


    /**
     * Retrieves the ArenaUtil interface for managing arenas in the bed wars mini-game.
     *
     * @return the ArenaUtil interface
     */
    ArenaUtil getArenaUtil();

    /**
     * The ArenaUtil interface provides utility methods for managing arenas in the bed wars mini-game.
     */
    public interface ArenaUtil {

        /**
         * Checks if an arena can be auto-scaled.
         *
         * @param arenaName the name of the arena
         * @return true if the arena can be auto-scaled, false otherwise
         */
        boolean canAutoScale(String arenaName);

        /**
         * Adds a custom arena to the enable queue.
         *
         * @param a the arena to add
         */
        @SuppressWarnings("unused")
        void addToEnableQueue(IArena a);

        /**
         * Removes an arena from the enable queue.
         *
         * @param a the arena to remove
         */
        void removeFromEnableQueue(IArena a);

        /**
         * Checks if a player is currently playing in an arena.
         *
         * @param p the player
         * @return true if the player is playing, false otherwise
         */
        boolean isPlaying(Player p);

        /**
         * Checks if a player is currently spectating in an arena.
         *
         * @param p the player
         * @return true if the player is spectating, false otherwise
         */
        boolean isSpectating(Player p);

        /**
         * Loads an arena and adds it to the enable queue.
         *
         * @param worldName the name of the arena world
         * @param sender    the player or console who triggered the load (can be null)
         */
        void loadArena(String worldName, @Nullable CommandSender sender);

        /**
         * Sets the number of games before the server restarts.
         * This is only applicable in a BUNGEE server type.
         *
         * @param games the number of games before restart
         */
        void setGamesBeforeRestart(int games);

        /**
         * Gets the number of games before the server restarts.
         * This is only applicable in a BUNGEE server type.
         *
         * @return the number of games before restart
         */
        int getGamesBeforeRestart();

        /**
         * Retrieves the arena where the player is located (either as a spectator or a player).
         *
         * @param player the player
         * @return the arena where the player is located, or null if not in an arena
         */
        IArena getArenaByPlayer(Player player);

        /**
         * Sets the arena for a player if the player is in the specified arena.
         *
         * @param p     the player
         * @param arena the arena
         */
        void setArenaByPlayer(Player p, IArena arena);

        /**
         * Removes the association of an arena with a player.
         *
         * @param p the player
         * @param a the arena
         */
        @SuppressWarnings("unused")
        void removeArenaByPlayer(Player p, IArena a);

        /**
         * Retrieves an arena by its world name.
         *
         * @param worldName the world name of the arena
         * @return the arena with the specified world name, or null if not found
         */
        IArena getArenaByName(String worldName);

        /**
         * Retrieves an arena by its identifier.
         *
         * @param identifier the identifier of the arena
         * @return the arena with the specified identifier, or null if not found
         */
        IArena getArenaByIdentifier(String identifier);

        /**
         * Sets the arena using its world name.
         *
         * @param arena the arena to set
         */
        void setArenaByName(IArena arena);

        /**
         * Removes the association of an arena with its world name.
         *
         * @param worldName the world name of the arena to remove
         */
        @SuppressWarnings("unused")
        void removeArenaByName(String worldName);

        /**
         * Retrieves a list of all arenas.
         *
         * @return the list of arenas
         */
        LinkedList<IArena> getArenas();

        /**
         * Checks if a player has VIP join access.
         *
         * @param p the player
         * @return true if the player has VIP join access, false otherwise
         */
        boolean vipJoin(Player p);

        /**
         * Retrieves the number of players in a specific group.
         *
         * @param group the group name
         * @return the number of players in the group
         */
        int getPlayers(String group);

        /**
         * Adds a player to the most filled arena.
         * Checks if the player is the party owner first.
         *
         * @param p the player to add
         * @return true if the player joined an arena, false otherwise
         */
        @SuppressWarnings("unused")
        boolean joinRandomArena(Player p);

        /**
         * Adds a player to the most filled arena from a specific group.
         *
         * @param p     the player to add
         * @param group the group name
         * @return true if the player joined an arena, false otherwise
         */
        boolean joinRandomFromGroup(Player p, String group);

        /**
         * Retrieves the enable queue of arenas.
         *
         * @return the enable queue
         */
        LinkedList<IArena> getEnableQueue();

        /**
         * Gives the lobby items to the player.
         * This clears the player's inventory before giving the items.
         * (Not used for BUNGEE serverType)
         *
         * @param p the player
         */
        void sendLobbyCommandItems(Player p);
    }

    Configs getConfigs();

    interface Configs {
        /**
         * Get plugin main configuration.
         */
        ConfigManager getMainConfig();

        /**
         * Get signs configuration.
         */
        @SuppressWarnings("unused")
        ConfigManager getSignsConfig();

        /**
         * Get generators configuration.
         */
        @SuppressWarnings("unused")
        ConfigManager getGeneratorsConfig();

        /**
         * Get shop configuration.
         */
        ConfigManager getShopConfig();

        /**
         * Get upgrades configuration.
         */
        @SuppressWarnings("unused")
        ConfigManager getUpgradesConfig();
    }

    /**
     * Get shop util.
     */
    ShopUtil getShopUtil();
    /**
     * The ShopUtil interface provides utility methods for managing player's money, currency, and purchases in the shop.
     */
    public interface ShopUtil {

        /**
         * Retrieves the amount of money a player has in the specified currency.
         *
         * @param player    the player
         * @param currency  the currency material
         * @return the amount of money the player has
         */
        int calculateMoney(Player player, Material currency);

        /**
         * Retrieves the currency material based on its name.
         *
         * @param currency  the currency name
         * @return the currency material, or {@link Material#AIR} if it is a vault currency
         */
        Material getCurrency(String currency);

        /**
         * Retrieves the color associated with the specified currency material.
         *
         * @param currency  the currency material
         * @return the color associated with the currency
         */
        ChatColor getCurrencyColor(Material currency);

        /**
         * Retrieves the currency message path for the specified content tier.
         *
         * @param contentTier  the content tier
         * @return the currency message path
         */
        String getCurrencyMsgPath(IContentTier contentTier);

        /**
         * Retrieves the Roman numeral representation of the specified integer.
         *
         * @param n  the integer value (1-10)
         * @return the Roman numeral representation
         */
        String getRomanNumber(int n);

        /**
         * Takes the specified amount of money from the player in the specified currency.
         *
         * @param player    the player
         * @param currency  the currency material
         * @param amount    the amount of money to take
         */
        void takeMoney(Player player, Material currency, int amount);

        /**
         * Retrieves the shop manager instance.
         *
         * @return the shop manager
         */
        IShopManager getShopManager();

        /**
         * Retrieves the shop cache instance.
         *
         * @return the shop cache
         */
        IShopCache getShopCache();

        /**
         * Retrieves the player quick buy cache instance.
         *
         * @return the player quick buy cache
         */
        IPlayerQuickBuyCache getPlayerQuickBuyCache();
    }

    /**
     * Get shop util.
     */
    @SuppressWarnings("unused")
    TeamUpgradesUtil getTeamUpgradesUtil();

    /**
     * An implementation of the TeamUpgradesUtil interface that provides access to team upgrade-related functionalities.
     * This implementation uses the UpgradesManager class to interact with team upgrades.
     */
    interface TeamUpgradesUtil {
        /**
         * Checks if a player is currently watching the team upgrades GUI.
         *
         * @param player The player to check.
         * @return {@code true} if the player is watching the team upgrades GUI, {@code false} otherwise.
         */
        @SuppressWarnings("unused")
        boolean isWatchingGUI(Player player);

        /**
         * Sets a player to be watching the team upgrades GUI.
         *
         * @param player The player to set.
         */
        void setWatchingGUI(Player player);

        /**
         * Removes the watching status for team upgrades from a player.
         *
         * @param uuid The UUID of the player.
         */
        void removeWatchingUpgrades(UUID uuid);

        /**
         * Retrieves the total number of upgrade tiers available for an arena.
         *
         * @param arena The arena to get the upgrade tiers for.
         * @return The total number of upgrade tiers for the specified arena.
         */
        int getTotalUpgradeTiers(IArena arena);

        /**
         * Sets a custom upgrade menu for an arena.
         *
         * @param arena The arena to set the custom menu for.
         * @param menu  The custom upgrade menu.
         */
        void setCustomMenuForArena(IArena arena, UpgradesIndex menu);

        /**
         * Retrieves the upgrade menu for an arena.
         *
         * @param arena The arena to get the upgrade menu for.
         * @return {@link UpgradesIndex} The upgrade menu for the specified arena.
         */
        UpgradesIndex getMenuForArena(IArena arena);

        /**
         * Check if is upgradable item.
         * Used in inventory click.
         *
         * @param item item to be checked.
         * @return {@link MenuContent} null if isn't an element.
         */
        MenuContent getMenuContent(ItemStack item);

        /**
         * Get menu content by identifier.
         *
         * @param identifier menu identifier to be checked.
         * @return {@link MenuContent} null if not found.
         */
        MenuContent getMenuContent(String identifier);

        /**
         * Get a map of menu contents by their identifiers.
         *
         * @return A {@link HashMap} containing menu identifiers as keys and corresponding {@link MenuContent} objects as values.
         */
        HashMap<String, MenuContent> getMenuContentByName();
    }

    /**
     * Get the utility class for managing levels in BedWars.
     *
     * @return the {@link Level} utility class
     */
    Level getLevelsUtil();

    /**
     * Get the utility class for managing parties in BedWars.
     *
     * @return the {@link Party} utility class
     */
    Party getPartyUtil();

    /**
     * Get the active setup session for a player.
     *
     * @param player the UUID of the player
     * @return the active setup session for the player, or null if no session was found
     */
    ISetupSession getSetupSession(UUID player);

    /**
     * Check if a player is currently in a setup session.
     *
     * @param player the UUID of the player
     * @return true if the player is in a setup session, false otherwise
     */
    boolean isInSetupSession(UUID player);

    /**
     * Get the server type of the BedWars plugin.
     *
     * @return the server type
     */
    ServerType getServerType();

    /**
     * Get the language ISO code for a player.
     *
     * @param p the player
     * @return the language ISO code of the player
     */
    @SuppressWarnings("unused")
    String getLangIso(Player p);

    /**
     * Get the main BedWars command.
     *
     * @return the main command for BedWars
     */
    ParentCommand getBedWarsCommand();

    /**
     * Get the restore adapter used by the BedWars plugin.
     *
     * @return the restore adapter
     */
    RestoreAdapter getRestoreAdapter();

    /**
     * Set a custom restore adapter for the BedWars plugin.
     *
     * @param restoreAdapter the custom restore adapter to set
     * @throws IllegalAccessError if arenas are not unloaded when changing the adapter
     */
    void setRestoreAdapter(RestoreAdapter restoreAdapter) throws IllegalAccessError;

    /**
     * Set a custom party adapter for the BedWars plugin.
     *
     * @param partyAdapter the custom party adapter to set
     * @throws IllegalAccessError if the party adapter is null or already set to the default adapter
     */
    void setPartyAdapter(Party partyAdapter);

    /**
     * Get the version support utility for BedWars.
     *
     * @return the version support utility
     */
    VersionSupport getVersionSupport();

    /**
     * Get the Support PlaceholderAPI utility for BedWars.
     *
     * @return the SupportPAPI utility
     */
    SupportPAPI getSupportPapi();

    /**
     * Get the default language used by the BedWars plugin.
     *
     * @return the default language
     */
    Language getDefaultLang();

    /**
     * Get the name of the lobby world in BedWars.
     *
     * @return the name of the lobby world
     */
    String getLobbyWorld();

    /**
     * Get the appropriate version string based on the server's current version.
     *
     * @param v18 the string for version 1.8 - 1.11
     * @param v12 the string for version 1.12
     * @param v13 the string for version 1.13 and newer
     * @return the appropriate version string based on the server's version
     */
    String getForCurrentVersion(String v18, String v12, String v13);

    /**
     * Set the level adapter used by the BedWars plugin.
     *
     * @param level the level adapter to set
     */
    @SuppressWarnings("unused")
    void setLevelAdapter(Level level);

    /**
     * Check if auto-scaling is enabled in BedWars.
     *
     * @return true if auto-scaling is enabled, false otherwise
     */
    boolean isAutoScale();

    /**
     * Get the language by its ISO code.
     *
     * @param isoCode the ISO code of the language
     * @return the language corresponding to the ISO code
     */
    @SuppressWarnings("unused")
    Language getLanguageByIso(String isoCode);

    /**
     * Get the language used by a player.
     *
     * @param player the player
     * @return the language used by the player
     */
    Language getPlayerLanguage(Player player);

    /**
     * Get the path to the addons folder in the BedWars plugin.
     *
     * @return the addons folder path
     */
    @SuppressWarnings("unused")
    File getAddonsPath();

    /**
     * Check if the BedWars plugin is currently shutting down.
     *
     * @return true if the plugin is shutting down, false otherwise
     */
    boolean isShuttingDown();

    /**
     * Get the utility class for managing scoreboards in BedWars.
     *
     * @return the {@link ScoreboardUtil} utility class
     */
    ScoreboardUtil getScoreboardUtil();

    /**
     * The utility interface for managing scoreboards in BedWars.
     */
    interface ScoreboardUtil {

        /**
         * Remove the BedWars sidebar from the player's scoreboard.
         *
         * @param player the player to remove the scoreboard from
         */
        void removePlayerScoreboard(Player player);

        /**
         * Give the player a scoreboard based on the plugin configuration.
         *
         * @param player the player to give the scoreboard to
         * @param delay  true if there should be a 5-second delay, false otherwise
         */
        void givePlayerScoreboard(Player player, boolean delay);
    }


    /**
     * Get the scoreboard manager used by the BedWars plugin.
     *
     * @return the scoreboard manager
     */
    IScoreboardService getScoreboardManager();

    /**
     * Get the economy utility class used by BedWars.
     *
     * @return the {@link IEconomy} utility class
     */
    IEconomy getEconomyUtil();

    /**
     * Get the chat utility class used by BedWars.
     *
     * @return the {@link IChat} utility class
     */
    IChat getChatUtil();

    /**
     * Set the remote database implementation for BedWars.
     *
     * @param database the remote database implementation to set
     */
    void setRemoteDatabase(IDatabase database);

    /**
     * Get the remote database implementation used by BedWars.
     *
     * @return the remote database implementation
     */
    IDatabase getRemoteDatabase();

    /**
     * Set the economy adapter used by BedWars.
     *
     * @param economyAdapter the economy adapter to set
     */
    void setEconomyAdapter(IEconomy economyAdapter);

}
