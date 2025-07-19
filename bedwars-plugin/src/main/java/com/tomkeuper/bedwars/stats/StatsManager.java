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

package com.tomkeuper.bedwars.stats;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.player.PlayerStatChangeEvent;
import com.tomkeuper.bedwars.api.stats.IPlayerStats;
import com.tomkeuper.bedwars.api.stats.IStatsManager;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsManager implements IStatsManager {

    private final Map<UUID, IPlayerStats> stats = new ConcurrentHashMap<>();

    public StatsManager() {
        registerListeners();
    }

    public void remove(UUID uuid) {
        stats.remove(uuid);
    }

    public void put(UUID uuid, IPlayerStats playerStats) {
        stats.put(uuid, playerStats);
    }

    @Override
    public @NotNull IPlayerStats get(UUID uuid) {
        IPlayerStats playerStats = stats.get(uuid);
        if (playerStats == null) {
            throw new IllegalStateException("Trying to get stats data of an unloaded player!");
        }
        return playerStats;
    }

    @Override
    public @Nullable IPlayerStats getUnsafe(UUID uuid) {
        return stats.get(uuid);
    }

    @Override
    public void addPlayerKill(Player player) {
        IPlayerStats playerStats = get(player.getUniqueId());
        IArena arena = Arena.getArenaByPlayer(player);
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, arena, PlayerStatChangeEvent.StatType.KILLS);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        playerStats.setKills(playerStats.getKills() + 1);
    }

    @Override
    public void addFinalKill(Player player) {
        IPlayerStats playerStats = get(player.getUniqueId());
        IArena arena = Arena.getArenaByPlayer(player);
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, arena, PlayerStatChangeEvent.StatType.FINAL_KILLS);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        playerStats.setFinalKills(playerStats.getFinalKills() + 1);
    }

    @Override
    public void addPlayerDeath(Player player) {
        IPlayerStats playerStats = get(player.getUniqueId());
        IArena arena = Arena.getArenaByPlayer(player);
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, arena, PlayerStatChangeEvent.StatType.DEATHS);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        playerStats.setDeaths(playerStats.getDeaths() + 1);
    }

    @Override
    public void addFinalDeath(Player player) {
        IPlayerStats playerStats = get(player.getUniqueId());
        IArena arena = Arena.getArenaByPlayer(player);
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, arena, PlayerStatChangeEvent.StatType.FINAL_DEATHS);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        playerStats.setFinalDeaths(playerStats.getFinalDeaths() + 1);
    }

    @Override
    public void addBedBreak(Player player) {
        IPlayerStats playerStats = get(player.getUniqueId());
        IArena arena = Arena.getArenaByPlayer(player);
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, arena, PlayerStatChangeEvent.StatType.BEDS_DESTROYED);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        playerStats.setBedsDestroyed(playerStats.getBedsDestroyed() + 1);
    }

    @Override
    public void addWin(Player player) {
        IPlayerStats playerStats = get(player.getUniqueId());
        IArena arena = Arena.getArenaByPlayer(player);
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, arena, PlayerStatChangeEvent.StatType.WINS);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        playerStats.setWins(playerStats.getWins() + 1);
    }

    @Override
    public void addLoss(Player player) {
        IPlayerStats playerStats = get(player.getUniqueId());
        IArena arena = Arena.getArenaByPlayer(player);
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, arena, PlayerStatChangeEvent.StatType.LOSSES);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        playerStats.setLosses(playerStats.getLosses() + 1);
    }

    @Override
    public void addGamesPlayed(Player player) {
        IPlayerStats playerStats = get(player.getUniqueId());
        IArena arena = Arena.getArenaByPlayer(player);
        PlayerStatChangeEvent ev = new PlayerStatChangeEvent(player, arena, PlayerStatChangeEvent.StatType.GAMES_PLAYED);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        playerStats.setGamesPlayed(playerStats.getGamesPlayed() + 1);
    }

    /**
     * Register listeners related to stats cache.
     */
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new StatsListener(), BedWars.plugin);
    }
}
