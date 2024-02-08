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

package com.tomkeuper.bedwars;

import com.tomkeuper.bedwars.api.addon.IAddonManager;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.shop.IContentTier;
import com.tomkeuper.bedwars.api.chat.IChat;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.communication.IRedisClient;
import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import com.tomkeuper.bedwars.api.economy.IEconomy;
import com.tomkeuper.bedwars.api.database.IDatabase;
import com.tomkeuper.bedwars.api.events.player.PlayerAfkEvent;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItem;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItemHandler;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.SupportPAPI;
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
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.SetupSession;
import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import com.tomkeuper.bedwars.shop.main.CategoryContent;
import com.tomkeuper.bedwars.sidebar.BoardManager;
import com.tomkeuper.bedwars.stats.StatsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;

public class API implements com.tomkeuper.bedwars.api.BedWars {

    private static RestoreAdapter restoreAdapter;
    private final AFKUtil afkSystem = new AFKUtil() {
        private final HashMap<UUID, Integer> afkPlayers = new HashMap<>();

        @SuppressWarnings("unused")
        @Override
        public boolean isPlayerAFK(Player player) {
            return afkPlayers.containsKey(player.getUniqueId());
        }

        @SuppressWarnings("unused")
        @Override
        public void setPlayerAFK(Player player, boolean value) {
            if (value) {
                if (!afkPlayers.containsKey(player.getUniqueId())) {
                    afkPlayers.put(player.getUniqueId(), Arena.afkCheck.get(player.getUniqueId()));
                    Bukkit.getPluginManager().callEvent(new PlayerAfkEvent(player, PlayerAfkEvent.AFKType.START));
                }
            } else {
                if (afkPlayers.containsKey(player.getUniqueId())) {
                    afkPlayers.remove(player.getUniqueId());
                    Bukkit.getPluginManager().callEvent(new PlayerAfkEvent(player, PlayerAfkEvent.AFKType.END));
                }
                Arena.afkCheck.remove(player.getUniqueId());
            }
        }

        @SuppressWarnings("unused")
        @Override
        public int getPlayerTimeAFK(Player player) {
            return afkPlayers.getOrDefault(player.getUniqueId(), 0);
        }
    };

    private final ArenaUtil arenaUtil = new ArenaUtil() {
        @SuppressWarnings("unused")
        @Override
        public boolean canAutoScale(String arenaName) {
            return Arena.canAutoScale(arenaName);
        }

        @SuppressWarnings("unused")
        @Override
        public void addToEnableQueue(IArena a) {
            Arena.addToEnableQueue(a);
        }

        @SuppressWarnings("unused")
        @Override
        public void removeFromEnableQueue(IArena a) {
            Arena.removeFromEnableQueue(a);
        }

        @SuppressWarnings("unused")
        @Override
        public boolean isPlaying(Player p) {
            return Arena.isInArena(p) && Arena.getArenaByPlayer(p).isPlayer(p);
        }

        @SuppressWarnings("unused")
        @Override
        public boolean isSpectating(Player p) {
            return Arena.isInArena(p) && Arena.getArenaByPlayer(p).isSpectator(p);
        }

        @SuppressWarnings("unused")
        @Override
        public void loadArena(String worldName, @Nullable CommandSender sender) {
            new Arena(worldName, sender);
        }

        @SuppressWarnings("unused")
        @Override
        public void setGamesBeforeRestart(int games) {
            Arena.setGamesBeforeRestart(games);
        }

        @SuppressWarnings("unused")
        @Override
        public int getGamesBeforeRestart() {
            return Arena.getGamesBeforeRestart();
        }

        @SuppressWarnings("unused")
        @Override
        public IArena getArenaByPlayer(Player player) {
            return Arena.getArenaByPlayer(player);
        }

        @SuppressWarnings("unused")
        @Override
        public void setArenaByPlayer(Player p, IArena arena) {
            Arena.setArenaByPlayer(p, arena);
        }

        @SuppressWarnings("unused")
        @Override
        public void removeArenaByPlayer(Player p, IArena a) {
            Arena.removeArenaByPlayer(p, a);
        }

        @SuppressWarnings("unused")
        @Override
        public IArena getArenaByName(String worldName) {
            return Arena.getArenaByName(worldName);
        }

        @SuppressWarnings("unused")
        @Override
        public IArena getArenaByIdentifier(String worldName) {
            return Arena.getArenaByIdentifier(worldName);
        }

        @SuppressWarnings("unused")
        @Override
        public void setArenaByName(IArena arena) {
            Arena.setArenaByName(arena);
        }

        @SuppressWarnings("unused")
        @Override
        public void removeArenaByName(String worldName) {
            Arena.removeArenaByName(worldName);
        }

        @SuppressWarnings("unused")
        @Override
        public LinkedList<IArena> getArenas() {
            return Arena.getArenas();
        }

        @SuppressWarnings("unused")
        @Override
        public boolean vipJoin(Player p) {
            return Arena.isVip(p);
        }

        @SuppressWarnings("unused")
        @Override
        public int getPlayers(String group) {
            return Arena.getPlayers(group);
        }

        @SuppressWarnings("unused")
        @Override
        public boolean joinRandomArena(Player p) {
            return Arena.joinRandomArena(p);
        }

        @SuppressWarnings("unused")
        @Override
        public boolean joinRandomFromGroup(Player p, String group) {
            return Arena.joinRandomFromGroup(p, group);
        }

        @SuppressWarnings("unused")
        @Override
        public LinkedList<IArena> getEnableQueue() {
            return Arena.getEnableQueue();
        }

        @SuppressWarnings("unused")
        @Override
        public void sendLobbyCommandItems(Player p) {
            Arena.sendLobbyCommandItems(p);
        }
    };

    private final Configs configs = new Configs() {
        @SuppressWarnings("unused")
        @Override
        public ConfigManager getMainConfig() {
            return BedWars.config;
        }

        @SuppressWarnings("unused")
        @Override
        public ConfigManager getSignsConfig() {
            return BedWars.signs;
        }

        @SuppressWarnings("unused")
        @Override
        public ConfigManager getGeneratorsConfig() {
            return BedWars.generators;
        }

        @SuppressWarnings("unused")
        @Override
        public ConfigManager getShopConfig() {
            return BedWars.shop;
        }

        @SuppressWarnings("unused")
        @Override
        public ConfigManager getUpgradesConfig() {
            return BedWars.getUpgradeManager().getConfiguration();
        }
    };

    private final ShopUtil shopUtil = new ShopUtil() {
        @SuppressWarnings("unused")
        @Override
        public int calculateMoney(Player player, Material currency) {
            return CategoryContent.calculateMoney(player, currency);
        }

        @SuppressWarnings("unused")
        @Override
        public Material getCurrency(String currency) {
            return CategoryContent.getCurrency(currency);
        }

        @SuppressWarnings("unused")
        @Override
        public ChatColor getCurrencyColor(Material currency) {
            return CategoryContent.getCurrencyColor(currency);
        }

        @SuppressWarnings("unused")
        @Override
        public String getCurrencyMsgPath(IContentTier contentTier) {
            return CategoryContent.getCurrencyMsgPath(contentTier);
        }

        @SuppressWarnings("unused")
        @Override
        public String getRomanNumber(int n) {
            return CategoryContent.getRomanNumber(n);
        }

        @SuppressWarnings("unused")
        @Override
        public void takeMoney(Player player, Material currency, int amount) {
            CategoryContent.takeMoney(player, currency, amount);
        }

        @SuppressWarnings("unused")
        @Override
        public IShopManager getShopManager() {
            return BedWars.shop;
        }

        @SuppressWarnings("unused")
        @Override
        public IShopCache getShopCache() {
            return BedWars.shopCache;
        }

        @SuppressWarnings("unused")
        @Override
        public IPlayerQuickBuyCache getPlayerQuickBuyCache() {
            return BedWars.playerQuickBuyCache;
        }
    };

    @SuppressWarnings("unused")
    @Override
    public IStats getStatsUtil() {
        return StatsAPI.getInstance();
    }

    @SuppressWarnings("unused")
    @Override
    public IAddonManager getAddonsUtil() {
        return BedWars.addonManager;
    }

    @SuppressWarnings("unused")
    @Override
    public AFKUtil getAFKUtil() {
        return afkSystem;
    }

    @SuppressWarnings("unused")
    @Override
    public ArenaUtil getArenaUtil() {
        return arenaUtil;
    }

    @SuppressWarnings("unused")
    @Override
    public Configs getConfigs() {
        return configs;
    }

    @SuppressWarnings("unused")
    @Override
    public ShopUtil getShopUtil() {
        return shopUtil;
    }

    private final TeamUpgradesUtil teamUpgradesUtil = new TeamUpgradesUtil() {
        @SuppressWarnings("unused")
        @Override
        public boolean isWatchingGUI(Player player) {
            return BedWars.getUpgradeManager().isWatchingUpgrades(player.getUniqueId());
        }

        @SuppressWarnings("unused")
        @Override
        public void setWatchingGUI(Player player) {
            BedWars.getUpgradeManager().setWatchingUpgrades(player.getUniqueId());
        }

        @SuppressWarnings("unused")
        @Override
        public void removeWatchingUpgrades(UUID uuid) {
            BedWars.getUpgradeManager().removeWatchingUpgrades(uuid);
        }

        @SuppressWarnings("unused")
        @Override
        public int getTotalUpgradeTiers(IArena arena) {
            return BedWars.getUpgradeManager().getMenuForArena(arena).countTiers();
        }

        @SuppressWarnings("unused")
        @Override
        public void setCustomMenuForArena(IArena arena, UpgradesIndex menu) {
            BedWars.getUpgradeManager().setCustomMenuForArena(arena, menu);
        }

        @SuppressWarnings("unused")
        @Override
        public UpgradesIndex getMenuForArena(IArena arena) {
            return BedWars.getUpgradeManager().getMenuForArena(arena);
        }

        @SuppressWarnings("unused")
        @Override
        public MenuContent getMenuContent(ItemStack item) {
            return BedWars.getUpgradeManager().getMenuContent(item);
        }

        @SuppressWarnings("unused")
        @Override
        public MenuContent getMenuContent(String identifier){
            return BedWars.getUpgradeManager().getMenuContent(identifier);
        }

        @SuppressWarnings("unused")
        @Override
        public HashMap<String, MenuContent> getMenuContentByName() {
            return BedWars.getUpgradeManager().menuContentByName();
        }
    };

    @SuppressWarnings("unused")
    @Override
    public TeamUpgradesUtil getTeamUpgradesUtil() {
        return teamUpgradesUtil;
    }

    @SuppressWarnings("unused")
    @Override
    public com.tomkeuper.bedwars.api.levels.Level getLevelsUtil() {
        return BedWars.getLevelSupport();
    }

    @SuppressWarnings("unused")
    @Override
    public Party getPartyUtil() {
        return BedWars.getPartyManager();
    }

    @SuppressWarnings("unused")
    @Override
    public ISetupSession getSetupSession(UUID player) {
        return SetupSession.getSession(player);
    }

    @SuppressWarnings("unused")
    @Override
    public boolean isInSetupSession(UUID player) {
        return SetupSession.isInSetupSession(player);
    }

    @SuppressWarnings("unused")
    @Override
    public ServerType getServerType() {
        return BedWars.getServerType();
    }

    @SuppressWarnings("unused")
    @Override
    public ParentCommand getBedWarsCommand() {
        return MainCommand.getInstance();
    }

    @SuppressWarnings("unused")
    @Override
    public RestoreAdapter getRestoreAdapter() {
        return restoreAdapter;
    }

    @SuppressWarnings("unused")
    @Override
    public void setRestoreAdapter(RestoreAdapter adapter) throws IllegalAccessError {
        if (!Arena.getArenas().isEmpty()) {
            throw new IllegalAccessError("Arenas must be unloaded when changing the adapter");
        }
        restoreAdapter = adapter;
        if (adapter.getOwner() != null) {
            if (adapter.getOwner() != BedWars.plugin) {
                BedWars.plugin.getLogger().log(Level.WARNING, adapter.getOwner().getName() + " changed the restore system to its own adapter.");
            }
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void setPartyAdapter(Party partyAdapter) throws IllegalAccessError {
        if (partyAdapter == null) return;
        if (partyAdapter.equals(BedWars.getPartyManager())) return;
        BedWars.setPartyManager(partyAdapter);
        BedWars.plugin.getLogger().log(Level.WARNING,  "One of your plugins changed the Party adapter to: " + partyAdapter.getClass().getName());
    }

    @SuppressWarnings("unused")
    @Override
    public VersionSupport getVersionSupport() {
        return BedWars.nms;
    }

    @SuppressWarnings("unused")
    @Override
    public SupportPAPI getSupportPapi() {
        return com.tomkeuper.bedwars.support.papi.SupportPAPI.getSupportPAPI();
    }

    @SuppressWarnings("unused")
    @Override
    public Language getDefaultLang() {
        return Language.getDefaultLanguage();
    }

    @SuppressWarnings("unused")
    @Override
    public String getLobbyWorld() {
        return BedWars.getLobbyWorld();
    }

    @SuppressWarnings("unused")
    @Override
    public String getForCurrentVersion(String v18, String v12, String v13) {
        return BedWars.getForCurrentVersion(v18, v12, v13);
    }

    @SuppressWarnings("unused")
    @Override
    public void setLevelAdapter(com.tomkeuper.bedwars.api.levels.Level level) {
        BedWars.setLevelAdapter(level);
    }

    @SuppressWarnings("unused")
    @Override
    public boolean isAutoScale() {
        return BedWars.autoscale;
    }

    @SuppressWarnings("unused")
    @Override
    public Language getLanguageByIso(String isoCode) {
        return Language.getLang(isoCode);
    }

    @SuppressWarnings("unused")
    @Override
    public Language getPlayerLanguage(Player player) {
        return Language.getPlayerLanguage(player);
    }

    @SuppressWarnings("unused")
    @Override
    public String getLangIso(Player p) {
        return Language.getPlayerLanguage(p).getIso();
    }

    @SuppressWarnings("unused")
    @Override
    public File getAddonsPath() {
        return new File(BedWars.plugin.getDataFolder(), "Addons");
    }


    private static final ScoreboardUtil scoreboardUtil = new ScoreboardUtil() {

        @SuppressWarnings("unused")
        @Override
        public void removePlayerScoreboard(Player player) {
            BoardManager.getInstance().remove(player);
        }

        @SuppressWarnings("unused")
        @Override
        public void givePlayerScoreboard(@NotNull Player player, boolean delay) {
            BoardManager.getInstance().giveTabFeatures(player, Arena.getArenaByPlayer(player), delay);
        }
    };

    @SuppressWarnings("unused")
    @Override
    public ScoreboardUtil getScoreboardUtil() {
        return scoreboardUtil;
    }

    @SuppressWarnings("unused")
    @Override
    public boolean isShuttingDown() {
        return BedWars.isShuttingDown();
    }

    @SuppressWarnings("unused")
    @Override
    public IScoreboardService getScoreboardManager() {
        return BoardManager.getInstance();
    }

    @SuppressWarnings("unused")
    @Override
    public IEconomy getEconomyUtil() {
        return BedWars.getEconomy();
    }

    @SuppressWarnings("unused")
    @Override
    public IChat getChatUtil() {
        return BedWars.getChatSupport();
    }

    @SuppressWarnings("unused")
    @Override
    public void setRemoteDatabase(IDatabase database) {
        BedWars.setRemoteDatabase(database);
    }
    @SuppressWarnings("unused")
    @Override
    public IDatabase getRemoteDatabase() {
        return BedWars.getRemoteDatabase();
    }

    @SuppressWarnings("unused")
    @Override
    public void setEconomyAdapter(IEconomy economyAdapter) {
        BedWars.setEconomy(economyAdapter);
    }

    @SuppressWarnings("unused")
    @Override
    public IRedisClient getRedisClient() {
        return BedWars.getRedisConnection();
    }

    @Override
    public ItemUtil getItemUtil() {
        return itemUtil;
    }

    private final ItemUtil itemUtil = new ItemUtil() {
        @SuppressWarnings("unused")
        @Override
        public Collection<IPermanentItem> getLobbyItems() {
            return BedWars.getLobbyItems();
        }

        @Override
        public Collection<IPermanentItem> getSpectatorItems() {
            return BedWars.getSpectatorItems();
        }

        @Override
        public Collection<IPermanentItem> getPreGameItems() {
            return BedWars.getPreGameItems();
        }

        @SuppressWarnings("unused")
        @Override
        public boolean registerLobbyItemHandler(IPermanentItemHandler handler, Plugin plugin) {
            return false;
        }

    };

}
