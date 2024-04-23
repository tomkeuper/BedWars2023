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

import com.tomkeuper.bedwars.api.stats.IPlayerStats;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStats implements IPlayerStats {

    private final UUID uuid;

    private String name;
    private Instant firstPlay;
    private Instant lastPlay;
    private final Map<String, Integer> wins;
    private final Map<String, Integer> kills;
    private final Map<String, Integer> finalKills;
    private final Map<String, Integer> losses;
    private final Map<String, Integer> deaths ;
    private final Map<String, Integer> finalDeaths;
    private final Map<String, Integer> bedsDestroyed;
    private final Map<String, Integer> gamesPlayed;

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.wins = new HashMap<>();
        this.kills = new HashMap<>();
        this.finalKills = new HashMap<>();
        this.losses = new HashMap<>();
        this.deaths = new HashMap<>();
        this.finalDeaths = new HashMap<>();
        this.bedsDestroyed = new HashMap<>();
        this.gamesPlayed = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Instant getFirstPlay() {
        return firstPlay;
    }

    @Override
    public void setFirstPlay(Instant firstPlay) {
        this.firstPlay = firstPlay;
    }

    @Override
    public Instant getLastPlay() {
        return lastPlay;
    }

    @Override
    public void setLastPlay(Instant lastPlay) {
        this.lastPlay = lastPlay;
    }

    @Override
    public int getWins() {
        return wins.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public int getWins(String group) {
        return wins.getOrDefault(group, 0);
    }

    @Override
    public void setWins(String group, int value) {
        if (wins.containsKey(group)) {
            wins.replace(group, value);
        } else {
            wins.put(group, value);
        }
    }

    @Override
    public int getKills() {
        return kills.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public int getKills(String group) {
        return kills.getOrDefault(group, 0);
    }

    @Override
    public void setKills(String group, int value) {
        if (kills.containsKey(group)) {
            kills.replace(group, value);
        } else {
            kills.put(group, value);
        }
    }

    @Override
    public int getFinalKills() {
        return finalKills.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public int getFinalKills(String group) {
        return finalKills.getOrDefault(group, 0);
    }

    @Override
    public void setFinalKills(String group, int value) {
        if (finalKills.containsKey(group)) {
            finalKills.replace(group, value);
        } else {
            finalKills.put(group, value);
        }
    }

    @Override
    public int getLosses() {
        return losses.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public int getLosses(String group) {
        return losses.getOrDefault(group, 0);
    }

    @Override
    public void setLosses(String group, int value) {
        if (losses.containsKey(group)) {
            losses.replace(group, value);
        } else {
            losses.put(group, value);
        }
    }

    public int getDeaths() {
        return deaths.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public int getDeaths(String group) {
        return deaths.getOrDefault(group, 0);
    }

    @Override
    public void setDeaths(String group, int value) {
        if (deaths.containsKey(group)) {
            deaths.replace(group, value);
        } else {
            deaths.put(group, value);
        }
    }

    @Override
    public int getFinalDeaths() {
        return finalDeaths.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public int getFinalDeaths(String group) {
        return finalDeaths.getOrDefault(group, 0);
    }

    @Override
    public void setFinalDeaths(String group, int value) {
        if (finalDeaths.containsKey(group)) {
            finalDeaths.replace(group, value);
        } else {
            finalDeaths.put(group, value);
        }
    }

    @Override
    public int getBedsDestroyed() {
        return bedsDestroyed.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public int getBedsDestroyed(String group) {
        return bedsDestroyed.getOrDefault(group, 0);
    }

    @Override
    public void setBedsDestroyed(String group, int value) {
        if (bedsDestroyed.containsKey(group)) {
            bedsDestroyed.replace(group, value);
        } else {
            bedsDestroyed.put(group, value);
        }
    }

    @Override
    public int getGamesPlayed() {
        return gamesPlayed.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public int getGamesPlayed(String group) {
        return gamesPlayed.getOrDefault(group, 0);
    }

    @Override
    public void setGamesPlayed(String group, int value) {
        if (gamesPlayed.containsKey(group)) {
            gamesPlayed.replace(group, value);
        } else {
            gamesPlayed.put(group, value);
        }
    }

    public int getTotalKills() {
        return getKills() + getFinalKills();
    }

    @Override
    public int getTotalKills(String group) {
        return getKills(group) + getFinalKills();
    }
}
