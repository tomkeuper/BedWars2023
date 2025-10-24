package com.tomkeuper.bedwars.levels.internal;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.events.player.*;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.configuration.LevelsConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelListeners implements Listener {

    public static LevelListeners instance;

    private final Map<UUID, PlayerGameStats> gameStats = new HashMap<>();
    private final Map<IArena, Boolean> firstBloodGiven = new HashMap<>();

    public LevelListeners() {
        instance = this;
    }

    /* --------------------------------------------------------
     * Player Join / Quit Events
     * -------------------------------------------------------- */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        new PlayerLevel(uuid, 1, 0);

        // Load level data async
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.plugin, () -> {
            Object[] levelData = BedWars.getRemoteDatabase().getLevelData(uuid);
            PlayerLevel.getLevelByPlayer(uuid)
                    .lazyLoad((Integer) levelData[0], (Integer) levelData[1]);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.plugin, () -> {
            PlayerLevel pl = PlayerLevel.getLevelByPlayer(uuid);
            if (pl != null) pl.destroy();
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArenaLeave(PlayerLeaveArenaEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        gameStats.remove(uuid);

        // Save player level async
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.plugin, () -> {
            PlayerLevel pl = PlayerLevel.getLevelByPlayer(uuid);
            if (pl != null) pl.updateDatabase();
        });
    }

    /* --------------------------------------------------------
     * Game End Event
     * -------------------------------------------------------- */
    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        IArena arena = e.getArena();

        // Winners
        for (UUID uuid : e.getWinners()) {
            giveGameWinXP(uuid, arena);
        }

        // Losers (team bonus only)
        for (UUID uuid : e.getLosers()) {
            givePerTeammateXP(uuid, arena);
        }

        // Cleanup
        e.getWinners().forEach(gameStats::remove);
        e.getLosers().forEach(gameStats::remove);
        firstBloodGiven.remove(arena);
    }

    private void giveGameWinXP(UUID uuid, IArena arena) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        PlayerLevel pl = PlayerLevel.getLevelByPlayer(uuid);
        if (pl == null) return;

        ITeam team = arena.getTeam(player);
        PlayerGameStats stats = getOrCreateStats(uuid);

        // Game win XP
        int winXP = LevelsConfig.levels.getInt("xp-rewards.game-win");
        if (winXP > 0) {
            pl.addXp(winXP, PlayerXpGainEvent.XpSource.GAME_WIN);
            player.sendMessage(Language.getMsg(player, Messages.XP_REWARD_WIN)
                    .replace("{xp}", String.valueOf(winXP)));
        }

        // Per teammate XP
        if (team != null && team.getMembersCache().size() > 1) {
            int teammateXP = LevelsConfig.levels.getInt("xp-rewards.per-teammate") * team.getMembersCache().size();
            if (teammateXP > 0) {
                pl.addXp(teammateXP, PlayerXpGainEvent.XpSource.PER_TEAMMATE);
                player.sendMessage(Language.getMsg(player, Messages.XP_REWARD_PER_TEAMMATE)
                        .replace("{xp}", String.valueOf(teammateXP)));
            }
        }

        // No-death bonus (no void deaths)

        // Flawless victory
        if (team != null && team.getMembersCache().size() == team.getMembers().size()) {
            int flawlessXP = LevelsConfig.levels.getInt("xp-rewards.flawless-victory");
            if (flawlessXP > 0) {
                pl.addXp(flawlessXP, PlayerXpGainEvent.XpSource.FLAWLESS_VICTORY);
                player.sendMessage(Language.getMsg(player, "xp-reward-flawless")
                        .replace("{xp}", String.valueOf(flawlessXP)));
            }
        }
    }

    private void givePerTeammateXP(UUID uuid, IArena arena) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        PlayerLevel pl = PlayerLevel.getLevelByPlayer(uuid);
        if (pl == null) return;

        ITeam team = arena.getTeam(player);
        if (team != null && team.getMembersCache().size() > 1) {
            int teammateXP = LevelsConfig.levels.getInt("xp-rewards.per-teammate") * team.getMembersCache().size();
            if (teammateXP > 0) {
                pl.addXp(teammateXP, PlayerXpGainEvent.XpSource.PER_TEAMMATE);
                player.sendMessage(Language.getMsg(player, Messages.XP_REWARD_PER_TEAMMATE)
                        .replace("{xp}", String.valueOf(teammateXP)));
            }
        }
    }

    /* --------------------------------------------------------
     * Bed Destroy Event
     * -------------------------------------------------------- */
    @EventHandler
    public void onBedBreak(PlayerBedBreakEvent e) {
        Player player = e.getPlayer();
        if (player == null) return;

        PlayerLevel pl = PlayerLevel.getLevelByPlayer(player.getUniqueId());
        if (pl == null) return;

        PlayerGameStats stats = getOrCreateStats(player.getUniqueId());

        // Bed destroyed XP
        int bedXP = LevelsConfig.levels.getInt("xp-rewards.bed-destroyed");
        if (bedXP > 0) {
            pl.addXp(bedXP, PlayerXpGainEvent.XpSource.BED_DESTROYED);
            player.sendMessage(Language.getMsg(player, Messages.XP_REWARD_BED_DESTROY)
                    .replace("{xp}", String.valueOf(bedXP)));
        }

        // First bed destroyed bonus
        if (stats.bedsDestroyed == 0) {
            int firstBedXP = LevelsConfig.levels.getInt("xp-rewards.first-bed-destroyed");
            if (firstBedXP > 0) {
                pl.addXp(firstBedXP, PlayerXpGainEvent.XpSource.FIRST_BED_DESTROYED);
                player.sendMessage(Language.getMsg(player, "xp-reward-first-bed")
                        .replace("{xp}", String.valueOf(firstBedXP)));
            }
        }
        stats.bedsDestroyed++;
    }

    /* --------------------------------------------------------
     * Kill Event
     * -------------------------------------------------------- */
    @EventHandler
    public void onKill(PlayerKillEvent e) {
        Player killer = e.getKiller();
        Player victim = e.getVictim();
        if (killer == null || victim == null || killer.equals(victim)) return;

        IArena arena = e.getArena();
        PlayerGameStats killerStats = getOrCreateStats(killer.getUniqueId());
        PlayerGameStats victimStats = getOrCreateStats(victim.getUniqueId());
        // XP for kills
        int finalKillXP = LevelsConfig.levels.getInt("xp-rewards.final-kill");
        int regularKillXP = LevelsConfig.levels.getInt("xp-rewards.regular-kill");

        if (e.getCause().isFinalKill()) {
            if (finalKillXP > 0) {
                PlayerLevel.getLevelByPlayer(killer.getUniqueId())
                        .addXp(finalKillXP, PlayerXpGainEvent.XpSource.FINAL_KILL);
                killer.sendMessage(Language.getMsg(killer, Messages.XP_REWARD_FINAL_KILL)
                        .replace("{xp}", String.valueOf(finalKillXP)));
            }
            killerStats.finalKills++;
        } else {
            if (regularKillXP > 0) {
                PlayerLevel.getLevelByPlayer(killer.getUniqueId())
                        .addXp(regularKillXP, PlayerXpGainEvent.XpSource.REGULAR_KILL);
                killer.sendMessage(Language.getMsg(killer, Messages.XP_REWARD_REGULAR_KILL)
                        .replace("{xp}", String.valueOf(regularKillXP)));
            }
            killerStats.regularKills++;
        }

        // First Blood
        if (!firstBloodGiven.getOrDefault(arena, false)) {
            int fbXP = LevelsConfig.levels.getInt("xp-rewards.first-blood");
            if (fbXP > 0) {
                PlayerLevel.getLevelByPlayer(killer.getUniqueId())
                        .addXp(fbXP, PlayerXpGainEvent.XpSource.FIRST_BLOOD);
                killer.sendMessage(Language.getMsg(killer, "xp-reward-first-blood")
                        .replace("{xp}", String.valueOf(fbXP)));
                firstBloodGiven.put(arena, true);
            }
        }

        // Kill streak bonus (every 5 kills)
        int totalKills = killerStats.regularKills + killerStats.finalKills;
        if (totalKills > 0 && totalKills % 5 == 0) {
            int streakXP = LevelsConfig.levels.getInt("xp-rewards.kill-streak-" + totalKills);
            if (streakXP > 0) {
                PlayerLevel.getLevelByPlayer(killer.getUniqueId())
                        .addXp(streakXP, PlayerXpGainEvent.XpSource.KILL_STREAK);
                killer.sendMessage(Language.getMsg(killer, "xp-reward-kill-streak")
                        .replace("{xp}", String.valueOf(streakXP))
                        .replace("{streak}", String.valueOf(totalKills)));
            }
        }

        // Team eliminated
        ITeam victimTeam = arena.getTeam(victim);
        if (victimTeam != null && e.getCause().isFinalKill()) {
            boolean eliminated = victimTeam.getMembersCache().stream()
                    .noneMatch(member -> !member.equals(victim) && arena.isPlayer(member));

            if (eliminated) {
                int teamElimXP = LevelsConfig.levels.getInt("xp-rewards.team-eliminated");
                if (teamElimXP > 0) {
                    PlayerLevel.getLevelByPlayer(killer.getUniqueId())
                            .addXp(teamElimXP, PlayerXpGainEvent.XpSource.TEAM_ELIMINATED);
                    killer.sendMessage(Language.getMsg(killer, "xp-reward-team-eliminated")
                            .replace("{xp}", String.valueOf(teamElimXP)));
                }
            }
        }
    }

    /* --------------------------------------------------------
     * Internal Helper Classes
     * -------------------------------------------------------- */
    private PlayerGameStats getOrCreateStats(UUID uuid) {
        return gameStats.computeIfAbsent(uuid, k -> new PlayerGameStats());
    }

    private static class PlayerGameStats {
        int regularKills = 0;
        int finalKills = 0;
        int deaths = 0;
        int bedsDestroyed = 0;
    }
}
