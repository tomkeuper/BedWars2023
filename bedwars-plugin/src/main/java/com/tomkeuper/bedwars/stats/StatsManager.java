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
import com.tomkeuper.bedwars.api.stats.IPlayerStats;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsManager {

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

    @NotNull
    public IPlayerStats get(UUID uuid) {
        IPlayerStats playerStats = stats.get(uuid);
        if (playerStats == null) {
            throw new IllegalStateException("Trying to get stats data of an unloaded player!");
        }
        return playerStats;
    }

    @Nullable
    public IPlayerStats getUnsafe(UUID uuid) {
        return stats.get(uuid);
    }

    /**
     * Register listeners related to stats cache.
     */
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new StatsListener(), BedWars.plugin);
    }
}
