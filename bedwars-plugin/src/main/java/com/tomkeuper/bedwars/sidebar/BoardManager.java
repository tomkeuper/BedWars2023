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

package com.tomkeuper.bedwars.sidebar;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.server.ServerType;
import com.tomkeuper.bedwars.api.sidebar.IScoreboardService;
import com.tomkeuper.bedwars.api.tasks.PlayingTask;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.levels.internal.PlayerLevel;
import lombok.Getter;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BossBarManager;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;
import me.neznamy.tab.api.event.plugin.TabLoadEvent;
import me.neznamy.tab.api.nametag.NameTagManager;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import me.neznamy.tab.api.tablist.TabListFormatManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tomkeuper.bedwars.api.language.Language.getMsg;

public class BoardManager implements IScoreboardService {
    private static ScoreboardManager scoreboardManager;
    private static TabListFormatManager tabListFormatManager;
    private static PlaceholderManager placeholderManager;
    private static NameTagManager nameTagManager;
    @Getter
    private static BoardManager instance;
    private final HashMap<TabPlayer, Integer> tabPlayersPrefix = new HashMap<>();
    private final HashMap<TabPlayer, Integer> tabPlayersSuffix = new HashMap<>();
    private final HashMap<TabPlayer, Integer> headPlayersPrefix = new HashMap<>();
    private final HashMap<TabPlayer, Integer> headPlayersSuffix = new HashMap<>();
    private final HashMap<TabPlayer, Integer> tabPlayersTitle = new HashMap<>();

    public final Map<UUID, TabPlayer> tabPlayerCache = new ConcurrentHashMap<>();

    public TabPlayer getTabPlayer(@NotNull Player player) {
        return tabPlayerCache.computeIfAbsent(
                player.getUniqueId(),
                uuid -> TabAPI.getInstance().getPlayer(uuid)
        );
    }

    public static boolean init() {
        if (TabAPI.getInstance().getScoreboardManager() == null) return false;
        if (instance == null) {
            instance = new BoardManager();
            instance.registerPlaceholders();
            instance.registerLoadEvent();
            instance.registerLobbyScoreboards();
            Bukkit.getPluginManager().registerEvents(new BoardListener(), BedWars.plugin);
            // Also watch TAB enable events to refresh references after its reload

            Objects.requireNonNull(TabAPI.getInstance().getEventBus()).register(TabLoadEvent.class, event -> {
                BoardManager.refreshTabManagers();
                BedWars.plugin.getLogger().info("[BoardManager] TAB plugin enabled; refreshed TAB managers.");
                instance.registerPlaceholders();
                instance.registerLobbyScoreboards();
                for (IArena arena : Arena.getArenas()) {
                    arena.registerScoreboards();
                }
            });
        }
        return instance != null;
    }

    // Ensure our cached managers point to the current TAB instances
    private static void refreshTabManagers() {
        try {
            var api = TabAPI.getInstance();
            scoreboardManager = api.getScoreboardManager();
            tabListFormatManager = api.getTabListFormatManager();
            placeholderManager = api.getPlaceholderManager();
            nameTagManager = api.getNameTagManager();
        } catch (Throwable t) {
            BedWars.plugin.getLogger().severe("[BoardManager] Failed to refresh TAB managers: " + t.getMessage());
        }
    }

    public void registerLoadEvent() {
        Objects.requireNonNull(TabAPI.getInstance().getEventBus()).register(PlayerLoadEvent.class, event -> {
            if (BedWars.getServerType() == ServerType.SHARED && !((Player) event.getPlayer().getPlayer()).getWorld().getName().equalsIgnoreCase(BedWars.getLobbyWorld())) {
                return;
            }
            Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
                IArena arena = Arena.getArenaByPlayer((Player) event.getPlayer().getPlayer());
                BoardManager.getInstance().giveTabFeatures((Player) event.getPlayer().getPlayer(), arena, false);
            }, 5); // Give time for player to be put in arena player list.
        });
    }

    public void registerLobbyScoreboards() {
        if (!BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_USE_LOBBY_SIDEBAR)) return;
        BedWars.debug("Registering lobby scoreboards...");

        for (Language language : Language.getLanguages()) {
            List<String> lines = language.l(Messages.SCOREBOARD_LOBBY);
            lines.replaceAll(s -> s.isEmpty() ? " " : s); // TAB doesn't display empty lines, we need to replace them with spaces
            scoreboardManager.createScoreboard("bw_lobby_" + language.getIso(), "%bw_scoreboard_title%", lines.subList(1, lines.size()));
        }
    }

    public List<Scoreboard> registerArenaScoreboards(IArena arena) {
        // Technically it's possible to have per arena scoreboards. Future feature?
        // TODO fix issue with scoreboard overwrites
        BedWars.debug("Registering scoreboard for arena: " + arena.getDisplayName());
        List<Scoreboard> scoreboards = new ArrayList<>();
        for (Language language : Language.getLanguages()) {
            List<String> waiting = getScoreboardLines(arena, language, "waiting", Messages.SCOREBOARD_DEFAULT_WAITING);
            String scoreboardWaitingName = "bw_" + arena.getGroup() + "_waiting_" + language.getIso();
            if (!scoreboardManager.getRegisteredScoreboards().containsKey(scoreboardWaitingName)) {
                scoreboards.add(scoreboardManager.createScoreboard(scoreboardWaitingName, "%bw_scoreboard_title%", waiting.subList(1, waiting.size())));
            }

            List<String> starting = getScoreboardLines(arena, language, "starting", Messages.SCOREBOARD_DEFAULT_STARTING);
            String scoreboardStartingName = "bw_" + arena.getGroup() + "_starting_" + language.getIso();
            if (!scoreboardManager.getRegisteredScoreboards().containsKey(scoreboardStartingName)) {
                scoreboards.add(scoreboardManager.createScoreboard(scoreboardStartingName,"%bw_scoreboard_title%", starting.subList(1, starting.size())));
            }

            List<String> playing = getScoreboardLines(arena, language, "playing", Messages.SCOREBOARD_DEFAULT_PLAYING);
            String scoreboardPlayingName = "bw_" + arena.getGroup() + "_playing_" + language.getIso();
            if (!scoreboardManager.getRegisteredScoreboards().containsKey(scoreboardPlayingName)) {
                scoreboards.add(scoreboardManager.createScoreboard(scoreboardPlayingName,"%bw_scoreboard_title%", playing.subList(1, playing.size())));
            }
        }
        return scoreboards;
    }

    private List<String> getScoreboardLines(IArena arena, Language language, String phase, String path){
        List<String> lines = Language.getScoreboard(language, "scoreboard." + arena.getGroup() + "." + phase, path);
        lines.replaceAll(s -> s.isEmpty() ? " " : s); // TAB doesn't display empty lines, we need to replace them with spaces
        return lines;
    }

    private SimpleDateFormat getDateFormat(Player player){
        return new SimpleDateFormat(getMsg(player, Messages.FORMATTING_SCOREBOARD_DATE));
    }

    private SimpleDateFormat getNextEventDateFormat(Player player){
        SimpleDateFormat nextEventDateFormat = new SimpleDateFormat(getMsg(player, Messages.FORMATTING_SCOREBOARD_NEXEVENT_TIMER));
        nextEventDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return nextEventDateFormat;
    }

    private BoardManager() {
        scoreboardManager = TabAPI.getInstance().getScoreboardManager();
        tabListFormatManager = TabAPI.getInstance().getTabListFormatManager();
        placeholderManager = TabAPI.getInstance().getPlaceholderManager();
        nameTagManager = TabAPI.getInstance().getNameTagManager();
        BossBarManager bossBarManager = TabAPI.getInstance().getBossBarManager();
        if (bossBarManager == null) BedWars.plugin.getLogger().warning("BossBar is disabled in TAB config! Please enable it there.\n Make sure to remove the ServerInfo default config if you want to use dragon boss-bars");
        if (tabListFormatManager == null) BedWars.plugin.getLogger().warning("TabList Format Manager is null! You dont have the tablist-name-formatting enabled in TAB config.\nWithout this feature the plugin will NOT work properly");
    }

    private void registerPlaceholders(){
        BedWars.debug("Registering TAB placeholders...");

        int placeholderRefresh = BedWars.config.getInt(ConfigPath.SB_CONFIG_SIDEBAR_PLACEHOLDERS_REFRESH_INTERVAL);
        if (placeholderRefresh < 50) {
            BedWars.plugin.getLogger().warning("Placeholder refresh interval is set to `" + placeholderRefresh + "` but cannot be lower than 50! Overriding to 100 now...");
            BedWars.config.set(ConfigPath.SB_CONFIG_SIDEBAR_PLACEHOLDERS_REFRESH_INTERVAL, 100);
            placeholderRefresh = 100;
        }

        int PrefixRefresh = BedWars.config.getInt(ConfigPath.SB_CONFIG_SIDEBAR_PREFIX_REFRESH_INTERVAL);
        if (PrefixRefresh < 50) {
            BedWars.plugin.getLogger().warning("Prefix Suffix refresh interval is set to `" + PrefixRefresh + "` but cannot be lower than 50! Overriding to 100 now...");
            BedWars.config.set(ConfigPath.SB_CONFIG_SIDEBAR_PREFIX_REFRESH_INTERVAL, 100);
            PrefixRefresh = 100;
        }

        int SuffixRefresh = BedWars.config.getInt(ConfigPath.SB_CONFIG_SIDEBAR_SUFFIX_REFRESH_INTERVAL);
        if (SuffixRefresh < 50) {
            BedWars.plugin.getLogger().warning("Prefix Suffix refresh interval is set to `" + SuffixRefresh + "` but cannot be lower than 50! Overriding to 100 now...");
            BedWars.config.set(ConfigPath.SB_CONFIG_SIDEBAR_SUFFIX_REFRESH_INTERVAL, 100);
            SuffixRefresh = 100;
        }

        int titleRefresh = BedWars.config.getInt(ConfigPath.SB_CONFIG_SIDEBAR_TITLE_REFRESH_INTERVAL);
        if (titleRefresh < 50) {
            BedWars.plugin.getLogger().warning("Scoreboard title refresh interval is set to `" + titleRefresh + "` but cannot be lower than 50! Overriding to 100 now...");
            BedWars.config.set(ConfigPath.SB_CONFIG_SIDEBAR_TITLE_REFRESH_INTERVAL, 100);
            titleRefresh = 100;
        }

        placeholderManager.registerPlayerPlaceholder("%bw_v_prefix%",placeholderRefresh, player -> BedWars.getChatSupport().getPrefix((Player) player.getPlayer()));
        placeholderManager.registerPlayerPlaceholder("%bw_v_suffix%", placeholderRefresh, player -> BedWars.getChatSupport().getSuffix((Player) player.getPlayer()));
        placeholderManager.registerPlayerPlaceholder("%bw_playername%", placeholderRefresh, TabPlayer::getName);
        placeholderManager.registerPlayerPlaceholder("%bw_player%", placeholderRefresh, player -> ((Player) player.getPlayer()).getDisplayName());
        placeholderManager.registerPlayerPlaceholder("%bw_money%", placeholderRefresh, player -> String.valueOf(BedWars.getEconomy().getMoney((Player) player.getPlayer())));
        placeholderManager.registerServerPlaceholder("%bw_server_ip%", placeholderRefresh, () -> BedWars.config.getString(ConfigPath.GENERAL_CONFIG_PLACEHOLDERS_REPLACEMENTS_SERVER_IP));
        placeholderManager.registerServerPlaceholder("%bw_version%", placeholderRefresh, () -> BedWars.plugin.getDescription().getVersion());
        placeholderManager.registerServerPlaceholder("%bw_server_id%", placeholderRefresh, () -> BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SERVER_ID));
        placeholderManager.registerPlayerPlaceholder("%bw_date%", placeholderRefresh, player -> getDateFormat((Player) player.getPlayer()).format(new Date(System.currentTimeMillis())));
        placeholderManager.registerPlayerPlaceholder("%bw_progress%", placeholderRefresh, player -> PlayerLevel.getLevelByPlayer(player.getUniqueId()).getProgress());
        placeholderManager.registerPlayerPlaceholder("%bw_level%", placeholderRefresh, player -> PlayerLevel.getLevelByPlayer(player.getUniqueId()).getLevelName());
        placeholderManager.registerPlayerPlaceholder("%bw_level_unformatted%", placeholderRefresh, player -> String.valueOf(PlayerLevel.getLevelByPlayer(player.getUniqueId()).getLevel()));
        placeholderManager.registerPlayerPlaceholder("%bw_current_xp%", placeholderRefresh, player -> PlayerLevel.getLevelByPlayer(player.getUniqueId()).getFormattedCurrentXp());
        placeholderManager.registerPlayerPlaceholder("%bw_required_xp%", placeholderRefresh, player -> PlayerLevel.getLevelByPlayer(player.getUniqueId()).getFormattedRequiredXp());
        placeholderManager.registerPlayerPlaceholder("%bw_map%", placeholderRefresh, player -> Arena.getArenaByPlayer((Player) player.getPlayer()) == null ? "" : Arena.getArenaByPlayer((Player) player.getPlayer()).getDisplayName());
        placeholderManager.registerPlayerPlaceholder("%bw_map_name%", placeholderRefresh, player -> Arena.getArenaByPlayer((Player) player.getPlayer()) == null ? "" : Arena.getArenaByPlayer((Player) player.getPlayer()).getArenaName());
        placeholderManager.registerPlayerPlaceholder("%bw_group%", placeholderRefresh, player -> Arena.getArenaByPlayer((Player) player.getPlayer()) == null ? "" : Arena.getArenaByPlayer((Player) player.getPlayer()).getDisplayGroup((Player) player.getPlayer()));
        placeholderManager.registerPlayerPlaceholder("%bw_kills%", placeholderRefresh, player -> {
            if (null != Arena.getArenaByPlayer((Player) player.getPlayer())) return String.valueOf(Arena.getArenaByPlayer((Player) player.getPlayer()).getPlayerKills((Player) player.getPlayer(), false));
            return String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getKills());
        });
        placeholderManager.registerPlayerPlaceholder("%bw_total_kills%", placeholderRefresh, player -> {
            if (null != Arena.getArenaByPlayer((Player) player.getPlayer())) return String.valueOf(Arena.getArenaByPlayer((Player) player.getPlayer()).getPlayerTotalKills((Player) player.getPlayer()));
            return String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getTotalKills());
        });
        placeholderManager.registerPlayerPlaceholder("%bw_final_kills%", placeholderRefresh, player -> {
            if (null != Arena.getArenaByPlayer((Player) player.getPlayer())) return String.valueOf(Arena.getArenaByPlayer((Player) player.getPlayer()).getPlayerKills((Player) player.getPlayer(), true));
            return String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getFinalKills());
        });
        placeholderManager.registerPlayerPlaceholder("%bw_beds%", placeholderRefresh, player -> {
            if (null != Arena.getArenaByPlayer((Player) player.getPlayer())) return String.valueOf(Arena.getArenaByPlayer((Player) player.getPlayer()).getPlayerBedsDestroyed((Player) player.getPlayer()));
            return String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getBedsDestroyed());
        });
        placeholderManager.registerPlayerPlaceholder("%bw_deaths%", placeholderRefresh, player -> {
            if (null != Arena.getArenaByPlayer((Player) player.getPlayer())) return String.valueOf(Arena.getArenaByPlayer((Player) player.getPlayer()).getPlayerDeaths((Player) player.getPlayer(), false));
            return String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getDeaths());
        });
        placeholderManager.registerPlayerPlaceholder("%bw_final_deaths%", placeholderRefresh, player -> String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getFinalDeaths()));
        placeholderManager.registerPlayerPlaceholder("%bw_wins%", placeholderRefresh, player -> String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getWins()));
        placeholderManager.registerPlayerPlaceholder("%bw_losses%", placeholderRefresh, player -> String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getLosses()));
        placeholderManager.registerPlayerPlaceholder("%bw_games_played%", placeholderRefresh, player -> String.valueOf(BedWars.getStatsManager().get(player.getUniqueId()).getGamesPlayed()));
        placeholderManager.registerPlayerPlaceholder("%bw_next_event%", placeholderRefresh, player -> getNextEventName((Player) player.getPlayer()));
        placeholderManager.registerPlayerPlaceholder("%bw_on%", placeholderRefresh, player -> String.valueOf(getOnlinePlayers((Player) player.getPlayer())));
        placeholderManager.registerPlayerPlaceholder("%bw_max%", placeholderRefresh, player -> Arena.getArenaByPlayer((Player) player.getPlayer()) == null ? "" : String.valueOf(Arena.getArenaByPlayer((Player) player.getPlayer()).getMaxPlayers()));
        placeholderManager.registerPlayerPlaceholder("%bw_time%", placeholderRefresh, tabPlayer -> {
            Player player = (Player) tabPlayer.getPlayer();
            Arena arena = (Arena) Arena.getArenaByPlayer(player);
            if (null == arena) return "";
            if (arena.getStatus() == GameState.playing || arena.getStatus() == GameState.restarting) {
                return getNextEventTime(arena, player);
            } else if (arena.getStatus() == GameState.starting) {
                if (arena.getStartingTask() != null) {
                    return String.valueOf(arena.getStartingTask().getCountdown()+1);
                }
            }
            return getNextEventDateFormat(player).format(new Date(System.currentTimeMillis()));
        });

        placeholderManager.registerPlayerPlaceholder("%bw_arena_status%",  placeholderRefresh, tabPlayer -> {
            Player player = (Player) tabPlayer.getPlayer();
            IArena arena = Arena.getArenaByPlayer(player);
            if (null == arena) return "";
            return arena.getStatus().toString();
        });
        placeholderManager.registerPlayerPlaceholder("%bw_team%", placeholderRefresh, tabPlayer -> {
            Player player = (Player) tabPlayer.getPlayer();
            IArena arena = Arena.getArenaByPlayer(player);
            return null == arena ? "" : null == arena.getTeam(player) ? "" : arena.getTeam(player).getColor().chat() + arena.getTeam(player).getDisplayName(Language.getPlayerLanguage(player));
        });
        placeholderManager.registerPlayerPlaceholder("%bw_team_letter%", placeholderRefresh, tabPlayer -> {
            Player player = (Player) tabPlayer.getPlayer();
            IArena arena = Arena.getArenaByPlayer(player);
            return null == arena ? "" : null == arena.getTeam(player) ? "" :  (arena.getTeam(player).getDisplayName(Language.getPlayerLanguage(player)).substring(0, 1));
        });
        placeholderManager.registerPlayerPlaceholder("%bw_team_color%", placeholderRefresh, tabPlayer -> {
            Player player = (Player) tabPlayer.getPlayer();
            IArena arena = Arena.getArenaByPlayer(player);
            return null == arena ? "" : null == arena.getTeam(player) ? ""  : String.valueOf(arena.getTeam(player).getColor().chat());
        });

        placeholderManager.registerPlayerPlaceholder("%bw_prefix_tab%", PrefixRefresh, this::getPrefixTab);
        placeholderManager.registerPlayerPlaceholder("%bw_suffix_tab%", SuffixRefresh, this::getSuffixTab);
        placeholderManager.registerPlayerPlaceholder("%bw_prefix_head%", PrefixRefresh, this::getPrefixHead);
        placeholderManager.registerPlayerPlaceholder("%bw_suffix_head%", SuffixRefresh, this::getSuffixHead);

        placeholderManager.registerPlayerPlaceholder("%bw_scoreboard_title%", titleRefresh, tabPlayer -> {
            Player player = (Player) tabPlayer.getPlayer();
            IArena arena = Arena.getArenaByPlayer(player);
            int i = tabPlayersTitle.getOrDefault(tabPlayer,0);
            // set sidebar lines based on game state or lobby
            List<String> lines = null;
            String titleLine;
            if (null == arena) {
                if (player.getWorld().getName().equalsIgnoreCase(BedWars.getLobbyWorld())) {
                    lines = Language.getList(player, Messages.SCOREBOARD_LOBBY);
                }
            } else {
                if (arena.getStatus() == GameState.waiting) {
                    lines = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".waiting", Messages.SCOREBOARD_DEFAULT_WAITING);
                } else if (arena.getStatus() == GameState.starting) {
                    lines = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".starting", Messages.SCOREBOARD_DEFAULT_STARTING);
                } else if (arena.getStatus() == GameState.playing || arena.getStatus() == GameState.restarting) {
                    lines = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".playing", Messages.SCOREBOARD_DEFAULT_PLAYING);
                }
            }

            assert lines != null;
            titleLine = lines.get(0);
            String[] titleArray = titleLine.split(",");

            if (i+1 >= titleArray.length){
                tabPlayersTitle.put(tabPlayer,0);
                i = 0;
            } else {
                tabPlayersTitle.put(tabPlayer,i+1);
            }
            String title = titleArray[i];
            return null ==  title? "" : title;
        });

        // register arena placeholders
        PlaceholderManager pm = TabAPI.getInstance().getPlaceholderManager();
        for (int i = 1; i <= 32; i++) {
            int finalI = i;
            pm.registerPlayerPlaceholder("%bw_team_"+ i +"%", 50, player -> getTeamPlaceholder((Player) player.getPlayer(), finalI));
        }
    }
    @Override
    public void giveTabFeatures(@NotNull Player player, @Nullable IArena arena, boolean delay) {
        Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
            String arenaDisplayname = (arena != null) ? arena.getDisplayName() : "null";
            BedWars.debug("giveTabFeatures() player: " + player.getDisplayName() + " arena: " + arenaDisplayname);

            // Check if sidebar should be used based on arena and configuration
            if ((arena == null && !BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_USE_LOBBY_SIDEBAR))
                    || (arena != null && !BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_USE_GAME_SIDEBAR))) {
                return;
            }

            TabPlayer tabPlayer = getTabPlayer(player);

            if (nameTagManager == null) {
                BedWars.plugin.getLogger().severe("An error occurred while giving Tab Features to player, TAB nameTagManager is null!");
                return;
            }
            if (tabPlayer == null){
                BedWars.plugin.getLogger().severe("An error occurred while giving Tab Features to player, TAB tabPlayer is null!");
                return;
            }

            String scoreboardName = null;
            GameState arenaStatus = (arena != null) ? arena.getStatus() : null;
            Language playerLanguage = Language.getPlayerLanguage(player);

            // Set scoreboard name and temporary group based on arena status
            if (arenaStatus == null) {
                if (BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_USE_LOBBY_SIDEBAR)) scoreboardName = "bw_lobby_" + playerLanguage.getIso();
                tabPlayer.setTemporaryGroup(null); // Clear temporary group to allow TAB to use permission-based groups
            } else {
                String temporaryGroup = null;
                switch (arenaStatus) {
                    case waiting:
                        scoreboardName = "bw_" + arena.getGroup() + "_waiting_" + playerLanguage.getIso();
                        break;
                    case starting:
                        scoreboardName = "bw_" + arena.getGroup() + "_starting_" + playerLanguage.getIso();
                        break;
                    case playing:
                    case restarting:
                        scoreboardName = "bw_" + arena.getGroup() + "_playing_" + playerLanguage.getIso();
                        temporaryGroup = arena.getTeam(player) != null ? arena.getTeam(player).getName() : "";
                        break;
                    default:
                        scoreboardName = "bw_lobby_" + playerLanguage.getIso();
                }
                if (temporaryGroup != null) tabPlayer.setTemporaryGroup(temporaryGroup);
            }

            // Set below name health if enabled in config

            if (scoreboardName != null) {
                Scoreboard scoreboard = scoreboardManager.getRegisteredScoreboards().get(scoreboardName);
                scoreboardManager.showScoreboard(tabPlayer, scoreboard);
            }

            setHeaderFooter(tabPlayer, arena);

            if (BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_NAME_FORMATTING_ENABLED)){
                tabListFormatManager.setPrefix(tabPlayer, "%bw_prefix_tab%");
                tabListFormatManager.setSuffix(tabPlayer, "%bw_suffix_tab%");
            }

            nameTagManager.setPrefix(tabPlayer, "%bw_prefix_head%");
            nameTagManager.setSuffix(tabPlayer, "%bw_suffix_head%");

            tabListFormatManager.setName(tabPlayer,BedWars.config.getString(ConfigPath.SB_CONFIG_SIDEBAR_PLAYER_NAME));

        }, delay ? 5 : 0);
    }

    /**
     * Get the Placeholder string for a given team
     *
     * @param player Target player for localization
     * @param teamNumber number of team in array
     * @return formatted placeholder string with status. Can be NULL if no arena is found
     */
    private String getTeamPlaceholder(Player player, int teamNumber){
        Arena arena = (Arena) Arena.getArenaByPlayer(player);
        if (arena == null) return null;
        Language language = Language.getPlayerLanguage(player);
        String genericTeamFormat = language.m(Messages.FORMATTING_SCOREBOARD_TEAM_GENERIC);
        ITeam team;
        try {
            team = arena.getTeams().get(teamNumber-1);
        } catch (IndexOutOfBoundsException ignored){
            return null;
        }
        String teamName = team.getDisplayName(language);
        if (arena.getTeams().size() >= teamNumber) {
            return genericTeamFormat
                    .replace("%bw_team_letter%", String.valueOf(teamName.length() != 0 ? teamName.charAt(0) : ""))
                    .replace("%bw_team_color%", team.getColor().chat().toString())
                    .replace("%bw_team_name%", teamName)
                    .replace("%bw_team_status%", getTeamStatus(team, player));
        } else {
            // skip line
            return null;
        }
    }

    /**
     * Get the current status of a team. Alive/Dead/Num of players alive.
     *
     * @param currentTeam Target team to process
     * @param player Target player for localization
     * @return team status string
     */
    private String getTeamStatus(ITeam currentTeam, Player player){
        String result;
        if (currentTeam.isBedDestroyed()) {
            if (currentTeam.getSize() > 0) {
                result = getMsg(player, Messages.FORMATTING_SCOREBOARD_BED_DESTROYED)
                        .replace("%bw_players_remaining%", String.valueOf(currentTeam.getSize()));
            } else {
                result = getMsg(player, Messages.FORMATTING_SCOREBOARD_TEAM_ELIMINATED);
            }
        } else {
            result = getMsg(player, Messages.FORMATTING_SCOREBOARD_TEAM_ALIVE);
        }
        if (currentTeam.isMember(player)) {
            result += getMsg(player, Messages.FORMATTING_SCOREBOARD_YOUR_TEAM);
        }
        return result;
    }

    @Override
    public void remove(@NotNull Player player) {
        if (Objects.requireNonNull(getTabPlayer(player)).isLoaded())
            scoreboardManager.resetScoreboard(Objects.requireNonNull(getTabPlayer(player)));
    }

    public void cleanupPlayer(@NotNull Player player) {
        TabPlayer tabPlayer = getTabPlayer(player);
        if (tabPlayer == null) return;

        // Reset all prefixes and suffixes
        tabPlayersPrefix.remove(tabPlayer);
        tabPlayersSuffix.remove(tabPlayer);
        headPlayersPrefix.remove(tabPlayer);
        headPlayersSuffix.remove(tabPlayer);
        tabPlayersTitle.remove(tabPlayer);

        // Reset scoreboard
        scoreboardManager.resetScoreboard(tabPlayer);
    }

    public String getPrefixTab(TabPlayer tabPlayer) {
        return getPrefix(tabPlayer, "Tab");
    }

    public String getSuffixTab(TabPlayer tabPlayer) {
        return getSuffix(tabPlayer, "Tab");
    }

    public String getPrefixHead(TabPlayer tabPlayer) {
        return getPrefix(tabPlayer, "Head");
    }

    public String getSuffixHead(TabPlayer tabPlayer) {
        return getSuffix(tabPlayer, "Head");
    }

    public String getPrefix(TabPlayer tabPlayer, String type) {
        Player player = (Player) tabPlayer.getPlayer();
        IArena arena = Arena.getArenaByPlayer(player);
        if (type.equalsIgnoreCase("tab")) {
            int currentIndex = tabPlayersPrefix.getOrDefault(tabPlayer, 0);
            List<String> fixList;

            if (arena == null) {
                fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_LOBBY);
            } else {
                GameState arenaStatus = arena.getStatus();

                if (arena.isSpectator(player)) {
                    fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_SPECTATOR);
                } else {
                    switch (arenaStatus) {
                        case playing:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_PLAYING);
                            break;
                        case waiting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_WAITING);
                            break;
                        case starting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_STARTING);
                            break;
                        case restarting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_RESTARTING);
                            break;
                        default:
                            BedWars.debug("Unhandled game state for BedWars prefix");
                            fixList = Collections.singletonList("");
                            break;
                    }
                }
            }

            return getString(tabPlayer, currentIndex, fixList, tabPlayersPrefix);

        } else {
            int currentIndex = headPlayersPrefix.getOrDefault(tabPlayer, 0);
            List<String> fixList;

            if (arena == null) {
                fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_LOBBY);
            } else {
                GameState arenaStatus = arena.getStatus();

                if (arena.isSpectator(player)) {
                    fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_SPECTATOR);
                } else {
                    switch (arenaStatus) {
                        case playing:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_PLAYING);
                            break;
                        case waiting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_WAITING);
                            break;
                        case starting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_STARTING);
                            break;
                        case restarting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_RESTARTING);
                            break;
                        default:
                            BedWars.debug("Unhandled game state for BedWars prefix");
                            fixList = Collections.singletonList("");
                            break;
                    }
                }
            }

            return getString(tabPlayer, currentIndex, fixList, headPlayersPrefix);
        }
    }

    @NotNull
    private String getString(TabPlayer tabPlayer, int currentIndex, List<String> fixList, HashMap<TabPlayer, Integer> tabPlayersPrefix) {
        String prefix;
        if (currentIndex + 1 >= fixList.size()) {
            tabPlayersPrefix.put(tabPlayer, 0);
            currentIndex = 0;
        } else {
            tabPlayersPrefix.put(tabPlayer, currentIndex + 1);
        }

        prefix = (fixList.isEmpty()) ? null : fixList.get(currentIndex);
        return (prefix == null) ? "" : prefix;
    }

    public String getSuffix(TabPlayer tabPlayer, String type) {
        Player player = (Player) tabPlayer.getPlayer();
        IArena arena = Arena.getArenaByPlayer(player);
        if (type.equalsIgnoreCase("tab")) {
            int currentIndex = tabPlayersSuffix.getOrDefault(tabPlayer, 0);
            List<String> fixList;

            if (arena == null) {
                fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_LOBBY);
            } else {
                GameState arenaStatus = arena.getStatus();

                if (arena.isSpectator(player)) {
                    fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_SPECTATOR);
                } else {
                    switch (arenaStatus) {
                        case playing:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_PLAYING);
                            break;
                        case waiting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_WAITING);
                            break;
                        case starting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_STARTING);
                            break;
                        case restarting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_RESTARTING);
                            break;
                        default:
                            BedWars.debug("Unhandled game state for BedWars suffix");
                            fixList = Collections.singletonList("");
                            break;
                    }
                }
            }

            return getString(tabPlayer, currentIndex, fixList, tabPlayersSuffix);
        } else {
            int currentIndex = headPlayersSuffix.getOrDefault(tabPlayer, 0);
            List<String> fixList;

            if (arena == null) {
                fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_LOBBY);
            } else {
                GameState arenaStatus = arena.getStatus();

                if (arena.isSpectator(player)) {
                    fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_SPECTATOR);
                } else {
                    switch (arenaStatus) {
                        case playing:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_PLAYING);
                            break;
                        case waiting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_WAITING);
                            break;
                        case starting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_STARTING);
                            break;
                        case restarting:
                            fixList = Language.getList(player, Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_RESTARTING);
                            break;
                        default:
                            BedWars.debug("Unhandled game state for BedWars suffix");
                            fixList = Collections.singletonList("");
                            break;
                    }
                }
            }

            return getString(tabPlayer, currentIndex, fixList, headPlayersSuffix);
        }
    }

    @NotNull
    private String getNextEventName(Player player) {
        IArena arena = Arena.getArenaByPlayer(player);
        if (arena == null) return "-";
        String st = "-";
        switch (arena.getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
                st = getMsg(player, Messages.NEXT_EVENT_EMERALD_UPGRADE_II);
                break;
            case EMERALD_GENERATOR_TIER_III:
                st = getMsg(player, Messages.NEXT_EVENT_EMERALD_UPGRADE_III);
                break;
            case DIAMOND_GENERATOR_TIER_II:
                st = getMsg(player, Messages.NEXT_EVENT_DIAMOND_UPGRADE_II);
                break;
            case DIAMOND_GENERATOR_TIER_III:
                st = getMsg(player, Messages.NEXT_EVENT_DIAMOND_UPGRADE_III);
                break;
            case GAME_END:
                st = getMsg(player, Messages.NEXT_EVENT_GAME_END);
                break;
            case BEDS_DESTROY:
                st = getMsg(player, Messages.NEXT_EVENT_BEDS_DESTROY);
                break;
            case ENDER_DRAGON:
                st = getMsg(player, Messages.NEXT_EVENT_DRAGON_SPAWN);
                break;
        }

        return st;
    }

    @NotNull
    private String getNextEventTime(Arena arena, Player player) {
        if (arena == null) return getNextEventDateFormat(player).format((0L));
        long time = 0L;
        PlayingTask playingTask = arena.getPlayingTask();
        switch (arena.getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
            case EMERALD_GENERATOR_TIER_III:
                time = (arena.upgradeEmeraldsCount) * 1000L;
                break;
            case DIAMOND_GENERATOR_TIER_II:
            case DIAMOND_GENERATOR_TIER_III:
                time = (arena.upgradeDiamondsCount) * 1000L;
                break;
            case GAME_END:
                if (null == playingTask) {
                    break;
                }
                time = (playingTask.getGameEndCountdown()) * 1000L;
                break;
            case BEDS_DESTROY:
                if (null == playingTask) {
                    break;
                }
                time = (arena.getPlayingTask().getBedsDestroyCountdown()) * 1000L;
                break;
            case ENDER_DRAGON:
                if (null == playingTask) {
                    break;
                }
                time = (arena.getPlayingTask().getDragonSpawnCountdown()) * 1000L;
                break;
        }
        return getNextEventDateFormat(player).format(new Date(time));
    }

    private int getOnlinePlayers(Player player){
        IArena arena = Arena.getArenaByPlayer(player);
        if (arena == null) return Bukkit.getOnlinePlayers().size();
        return arena.getPlayers().size();
    }

    private void setHeaderFooter(TabPlayer player, IArena arena) {
        if (TabAPI.getInstance().getHeaderFooterManager() == null) return;
        if (isTabFormattingDisabled(arena)) {
            return;
        }
        Language lang = Language.getPlayerLanguage((Player) player.getPlayer());

        if (null == arena) {
            Objects.requireNonNull(TabAPI.getInstance().getHeaderFooterManager()).setHeaderAndFooter(
                    player, lang.m(Messages.FORMATTING_SIDEBAR_TAB_HEADER_LOBBY),
                    lang.m(Messages.FORMATTING_SIDEBAR_TAB_FOOTER_LOBBY)
            );
            return;
        }
        if (arena.isSpectator((Player) player.getPlayer())) {
            Objects.requireNonNull(TabAPI.getInstance().getHeaderFooterManager()).setHeaderAndFooter(
                    player, lang.m(Messages.FORMATTING_SIDEBAR_TAB_HEADER_SPECTATOR),
                    lang.m(Messages.FORMATTING_SIDEBAR_TAB_FOOTER_SPECTATOR)
            );
            return;
        }

        String headerPath = null;
        String footerPath = null;

        switch (arena.getStatus()) {
            case waiting:
                headerPath = Messages.FORMATTING_SIDEBAR_TAB_HEADER_WAITING;
                footerPath = Messages.FORMATTING_SIDEBAR_TAB_FOOTER_WAITING;
                break;
            case starting:
                headerPath = Messages.FORMATTING_SIDEBAR_TAB_HEADER_STARTING;
                footerPath = Messages.FORMATTING_SIDEBAR_TAB_FOOTER_STARTING;
                break;
            case playing:
                headerPath = Messages.FORMATTING_SIDEBAR_TAB_HEADER_PLAYING;
                footerPath = Messages.FORMATTING_SIDEBAR_TAB_FOOTER_PLAYING;
                break;
            case restarting:
                headerPath = Messages.FORMATTING_SIDEBAR_TAB_HEADER_RESTARTING;
                footerPath = Messages.FORMATTING_SIDEBAR_TAB_FOOTER_RESTARTING;
                break;
        }

        Objects.requireNonNull(TabAPI.getInstance().getHeaderFooterManager()).setHeaderAndFooter(
                player, lang.m(headerPath),
                lang.m(footerPath)
        );
    }

    /**
     * @return true if tab formatting is disabled for current sidebar/ arena stage
     */
    @Override
    public boolean isTabFormattingDisabled(IArena arena) {
        if (null == arena) {

            if (BedWars.getServerType() == ServerType.SHARED) {
                if (BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_LIST_FORMAT_LOBBY) &&
                        !BedWars.config.getLobbyWorldName().trim().isEmpty()) {

                    World lobby = Bukkit.getWorld(BedWars.config.getLobbyWorldName());
                    return null != lobby;
                }
            }

            return !BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_LIST_FORMAT_LOBBY);
        }
        // if tab formatting is disabled in game
        if (arena.getStatus() == GameState.playing && BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_LIST_FORMAT_PLAYING)) {
            return false;
        }

        // if tab formatting is disabled in starting
        if (arena.getStatus() == GameState.starting && BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_LIST_FORMAT_STARTING)) {
            return false;
        }

        // if tab formatting is disabled in waiting
        if (arena.getStatus() == GameState.waiting && BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_LIST_FORMAT_WAITING)) {
            return false;
        }

        // if tab formatting is disabled in restarting
        return arena.getStatus() != GameState.restarting || !BedWars.config.getBoolean(ConfigPath.SB_CONFIG_SIDEBAR_LIST_FORMAT_RESTARTING);
    }

    @Override
    public @Nullable Scoreboard getScoreboard(@NotNull Player player) {
        return scoreboardManager.getActiveScoreboard(Objects.requireNonNull(getTabPlayer(player)));
    }
}