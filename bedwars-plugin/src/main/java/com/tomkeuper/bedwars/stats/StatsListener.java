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

package com.tomkeuper.bedwars.stats;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerBedBreakEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerStatChangeEvent;
import com.tomkeuper.bedwars.api.stats.IPlayerStats;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.UUID;

public class StatsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // Do nothing if login fails
            return;
        }
        IPlayerStats stats = BedWars.getRemoteDatabase().fetchStats(event.getUniqueId());
        stats.setName(event.getName());
        BedWars.getStatsManager().put(event.getUniqueId(), stats);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            // Prevent memory leak if login fails
            BedWars.getStatsManager().remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onBedBreak(PlayerBedBreakEvent event) {
        IPlayerStats stats = BedWars.getStatsManager().get(event.getPlayer().getUniqueId());
        //store beds destroyed
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(event.getPlayer(), event.getArena(), PlayerStatChangeEvent.StatType.BEDS_DESTROYED);
        Bukkit.getPluginManager().callEvent(ev); //call player stat change event for bed destroyer (bed destroy)
        if (!ev.isCancelled()) {
            stats.setBedsDestroyed(stats.getBedsDestroyed() + 1);
        }
    }

    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        IPlayerStats victimStats = BedWars.getStatsManager().get(event.getVictim().getUniqueId());
        // If killer is not null and not equal to victim
        IPlayerStats killerStats = !event.getVictim().equals(event.getKiller()) ?
                (event.getKiller() == null ? null : BedWars.getStatsManager().getUnsafe(event.getKiller().getUniqueId())) : null;

        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(event.getKiller(), event.getArena(), PlayerStatChangeEvent.StatType.KILLS);
        PlayerStatChangeEvent ev1 = new PlayerStatChangeEvent(event.getKiller(), event.getArena(), PlayerStatChangeEvent.StatType.FINAL_KILLS);
        PlayerStatChangeEvent ev2 = new PlayerStatChangeEvent(event.getVictim(), event.getArena(), PlayerStatChangeEvent.StatType.FINAL_DEATHS);
        PlayerStatChangeEvent ev3 = new PlayerStatChangeEvent(event.getVictim(), event.getArena(), PlayerStatChangeEvent.StatType.LOSSES);
        PlayerStatChangeEvent ev4 = new PlayerStatChangeEvent(event.getVictim(), event.getArena(), PlayerStatChangeEvent.StatType.GAMES_PLAYED);
        PlayerStatChangeEvent ev5 = new PlayerStatChangeEvent(event.getVictim(), event.getArena(), PlayerStatChangeEvent.StatType.DEATHS);
        String arenaGroup = event.getArena().getGroup();
        if (event.getCause().isFinalKill()) {
            Bukkit.getPluginManager().callEvent(ev2); //call player stat change event for victim (final deaths)
            if (!ev2.isCancelled()) {
                //store final deaths
                victimStats.setFinalDeaths(victimStats.getFinalDeaths() + 1);
            }

            Bukkit.getPluginManager().callEvent(ev3); //call player stat change event for victim (losses)
            if (!ev3.isCancelled()) {
                //store losses
                victimStats.setLosses(victimStats.getLosses() + 1);
            }

            Bukkit.getPluginManager().callEvent(ev4); //call player stat change event for victim (games played)
            if (!ev4.isCancelled()) {
                //store games played
                victimStats.setGamesPlayed(victimStats.getGamesPlayed() + 1);
            }

            Bukkit.getPluginManager().callEvent(ev1); //call player stat change event for killer
            if (!ev1.isCancelled()) {
                //store final kills
                if (killerStats != null) killerStats.setFinalKills(killerStats.getFinalKills() + 1);
            }
        } else {
            Bukkit.getPluginManager().callEvent(ev5); //call player stat change event for victim (deaths)
            if (!ev5.isCancelled()) {
                //store deaths
                victimStats.setDeaths(victimStats.getDeaths() + 1);
            }

            Bukkit.getPluginManager().callEvent(ev); //call player stat change event for killer (kills)
            if (ev.isCancelled()) {
                //store kills
                if (killerStats != null) killerStats.setKills(killerStats.getKills() + 1);
            }
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        for (UUID uuid : event.getWinners()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (!player.isOnline()) continue;

            IPlayerStats stats = BedWars.getStatsManager().get(uuid);
            String arenaGroup = event.getArena().getGroup();

            PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, event.getArena(), PlayerStatChangeEvent.StatType.WINS);
            PlayerStatChangeEvent ev1 = new PlayerStatChangeEvent(player, event.getArena(), PlayerStatChangeEvent.StatType.GAMES_PLAYED);

            Bukkit.getPluginManager().callEvent(ev); //call player stat change event for winners (wins)
            if (!ev.isCancelled()) {
                // store wins even if is in another game because he assisted this team
                // the ones who abandoned are already removed from the winners list
                stats.setWins(stats.getWins() + 1);
            }

            // store games played
            // give if he remained in this arena till the end even if was eliminated
            // for those who left games played are updated in arena leave listener
            IArena playerArena = Arena.getArenaByPlayer(player);
            if (playerArena != null && playerArena.equals(event.getArena())) {
                Bukkit.getPluginManager().callEvent(ev1); //call player stat change event for winners (games played)
                if (!ev1.isCancelled()) {
                    stats.setGamesPlayed(stats.getGamesPlayed() + 1);
                }
            }
        }
    }

    @EventHandler
    public void onArenaLeave(PlayerLeaveArenaEvent event) {
        final Player player = event.getPlayer();

        ITeam team = event.getArena().getExTeam(player.getUniqueId());
        if (team == null) {
            return; // The player didn't play this game
        }

        if (event.getArena().getStatus() == GameState.starting || event.getArena().getStatus() == GameState.waiting) {
            return; // Game didn't start
        }

        IPlayerStats playerStats = BedWars.getStatsManager().get(player.getUniqueId());
        // sometimes can be null due to scheduling delays
        if (playerStats == null) return;

        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, event.getArena(), PlayerStatChangeEvent.StatType.FIRST_PLAY);
        PlayerStatChangeEvent ev1 = new PlayerStatChangeEvent(player, event.getArena(), PlayerStatChangeEvent.StatType.LAST_PLAY);
        PlayerStatChangeEvent ev2 = new PlayerStatChangeEvent(player, event.getArena(), PlayerStatChangeEvent.StatType.FINAL_DEATHS);
        PlayerStatChangeEvent ev3 = new PlayerStatChangeEvent(player, event.getArena(), PlayerStatChangeEvent.StatType.LOSSES);

        // Update last play and first play (if required)
        Instant now = Instant.now();
        Bukkit.getPluginManager().callEvent(ev1); //call player stat change event for player left arena (last play)
        if (!ev1.isCancelled()) {
            playerStats.setLastPlay(now);
        }
        if (playerStats.getFirstPlay() == null) {
            Bukkit.getPluginManager().callEvent(ev); //call player stat change event for player that left (first play)
            if (!ev.isCancelled()) {
                playerStats.setFirstPlay(now);
            }
        }

        // Check quit abuse
        if (event.getArena().getStatus() == GameState.playing) {
            // Only if the player left the arena while the game was running
            String arenaGroup = event.getArena().getGroup();
            if (team.isBedDestroyed()) {
                // Only if the team had the bed destroyed

                // Punish player if bed is destroyed and he disconnects without getting killed
                // if he is not in the spectators list it means he did not pass trough player kill event and he did not receive
                // the penalty bellow.
                if (event.getArena().isPlayer(player)) {
                    Bukkit.getPluginManager().callEvent(ev2); //call player stat change event for player that left (final deaths)
                    if (!ev2.isCancelled()) {
                        playerStats.setFinalDeaths(playerStats.getFinalDeaths() + 1);
                    }
                    Bukkit.getPluginManager().callEvent(ev3); //call player stat change event for player that left (losses)
                    if (!ev3.isCancelled()) {
                        playerStats.setLosses(playerStats.getLosses() + 1);
                    }
                }

                // Reward attacker
                // if attacker is not null it means the victim did pvp log out
                Player damager = event.getLastDamager();
                ITeam killerTeam = event.getArena().getTeam(damager);

                PlayerStatChangeEvent ev4 = new PlayerStatChangeEvent(damager, event.getArena(), PlayerStatChangeEvent.StatType.FINAL_KILLS);

                if (damager != null && event.getArena().isPlayer(damager) && killerTeam != null) {
                    Bukkit.getPluginManager().callEvent(ev4); //call player stat change event for damager
                    if (!ev4.isCancelled()) {
                        IPlayerStats damagerStats = BedWars.getStatsManager().get(damager.getUniqueId());
                        damagerStats.setFinalKills(damagerStats.getFinalKills() + 1);
                        event.getArena().addPlayerKill(damager, true, player);
                    }
                }
            } else {
                // Prevent pvp log out abuse
                Player damager = event.getLastDamager();
                ITeam killerTeam = event.getArena().getTeam(damager);

                PlayerStatChangeEvent ev5 = new PlayerStatChangeEvent(player, event.getArena(), PlayerStatChangeEvent.StatType.DEATHS);
                PlayerStatChangeEvent ev6 = new PlayerStatChangeEvent(damager, event.getArena(), PlayerStatChangeEvent.StatType.KILLS);

                // Killer is null if he already received kill point.
                // LastHit damager is set to null at PlayerDeathEvent so this part is not duplicated for sure.
                // damager is not null if the victim disconnected during pvp only.
                if (event.getLastDamager() != null && event.getArena().isPlayer(damager) && killerTeam != null) {
                    // Punish player
                    Bukkit.getPluginManager().callEvent(ev5); //call player stat change event for player that died
                    if (!ev5.isCancelled()) {
                        playerStats.setDeaths(playerStats.getDeaths() + 1);
                    }
                    event.getArena().addPlayerDeath(player);

                    // Reward attacker
                    event.getArena().addPlayerKill(damager, false, player);
                    Bukkit.getPluginManager().callEvent(ev6); //call player stat change event for damager
                    if (!ev6.isCancelled()) {
                        IPlayerStats damagerStats = BedWars.getStatsManager().get(damager.getUniqueId());
                        damagerStats.setKills(damagerStats.getKills() + 1);
                    }
                }
            }
        }

        //save or replace stats for player
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.plugin, () -> BedWars.getRemoteDatabase().saveStats(playerStats));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        BedWars.getStatsManager().remove(event.getPlayer().getUniqueId());
    }
}
