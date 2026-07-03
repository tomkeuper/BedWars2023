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

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks recently played arenas per player.
 * Data is persisted in the H2 database so it survives server restarts.
 * Entries older than EXPIRY_MS (30 minutes) are ignored and cleaned up.
 */
public class RecentlyPlayedTracker {

    // How long (ms) an arena is considered "recently played" — 30 minutes
    private static final long EXPIRY_MS = 30 * 60 * 1000L;

    // In-memory cache: playerUUID -> { arenaName -> timestamp }
    private static final Map<UUID, Map<String, Long>> recentMap = new HashMap<>();

    // ── Database init ─────────────────────────────────────────────────────────

    /**
     * Call once on plugin enable (after H2 is ready) to create the table
     * and load non-expired rows into the in-memory cache.
     */
    public static void init() {
        try (Connection conn = getConnection()) {
            // Create table if not exists
            String create = "CREATE TABLE IF NOT EXISTS RECENTLY_PLAYED (" +
                    "UUID VARCHAR(36) NOT NULL, " +
                    "ARENA_NAME VARCHAR(200) NOT NULL, " +
                    "PLAYED_AT BIGINT NOT NULL, " +
                    "PRIMARY KEY (UUID, ARENA_NAME));";
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(create);
            }

            // Load non-expired entries into memory
            long cutoff = System.currentTimeMillis() - EXPIRY_MS;
            String select = "SELECT UUID, ARENA_NAME, PLAYED_AT FROM RECENTLY_PLAYED WHERE PLAYED_AT > ?;";
            try (PreparedStatement ps = conn.prepareStatement(select)) {
                ps.setLong(1, cutoff);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("UUID"));
                        String arena = rs.getString("ARENA_NAME");
                        long ts = rs.getLong("PLAYED_AT");
                        recentMap.computeIfAbsent(uuid, k -> new HashMap<>()).put(arena, ts);
                    }
                }
            }

            // Delete expired rows from DB while we're at it
            String delete = "DELETE FROM RECENTLY_PLAYED WHERE PLAYED_AT <= ?;";
            try (PreparedStatement ps = conn.prepareStatement(delete)) {
                ps.setLong(1, cutoff);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            BedWars.plugin.getLogger().severe("[RecentlyPlayedTracker] Failed to init table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Record that a player left an arena. Updates memory + DB.
     */
    public static void record(UUID playerUUID, String arenaName) {
        long now = System.currentTimeMillis();
        recentMap.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(arenaName, now);

        // Persist to H2 (ensure table exists first in case init() wasn't called yet)
        BedWars.plugin.getServer().getScheduler().runTaskAsynchronously(BedWars.plugin, () -> {
            try (Connection conn = getConnection()) {
                // Auto-create table if missing (safety net)
                String create = "CREATE TABLE IF NOT EXISTS RECENTLY_PLAYED (" +
                        "UUID VARCHAR(36) NOT NULL, " +
                        "ARENA_NAME VARCHAR(200) NOT NULL, " +
                        "PLAYED_AT BIGINT NOT NULL, " +
                        "PRIMARY KEY (UUID, ARENA_NAME));";
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate(create);
                }

                String upsert = "MERGE INTO RECENTLY_PLAYED (UUID, ARENA_NAME, PLAYED_AT) KEY (UUID, ARENA_NAME) VALUES (?, ?, ?);";
                try (PreparedStatement ps = conn.prepareStatement(upsert)) {
                    ps.setString(1, playerUUID.toString());
                    ps.setString(2, arenaName);
                    ps.setLong(3, now);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                BedWars.plugin.getLogger().severe("[RecentlyPlayedTracker] Failed to save record: " + e.getMessage());
            }
        });
    }

    /**
     * Returns true if the player played in this arena within EXPIRY_MS.
     */
    public static boolean wasRecentlyPlayed(UUID playerUUID, String arenaName) {
        Map<String, Long> map = recentMap.get(playerUUID);
        if (map == null) return false;
        Long ts = map.get(arenaName);
        if (ts == null) return false;
        if (System.currentTimeMillis() - ts > EXPIRY_MS) {
            map.remove(arenaName);
            return false;
        }
        return true;
    }

    /**
     * Loads a player's recent arenas from H2 into memory.
     * Called when cache is empty (e.g. after server restart or disconnect).
     */
    public static void loadFromDatabase(UUID playerUUID) {
        if (recentMap.containsKey(playerUUID)) return;
        try (Connection conn = getConnection()) {
            long cutoff = System.currentTimeMillis() - EXPIRY_MS;
            String select = "SELECT ARENA_NAME, PLAYED_AT FROM RECENTLY_PLAYED WHERE UUID = ? AND PLAYED_AT > ?;";
            try (PreparedStatement ps = conn.prepareStatement(select)) {
                ps.setString(1, playerUUID.toString());
                ps.setLong(2, cutoff);
                try (ResultSet rs = ps.executeQuery()) {
                    Map<String, Long> map = new HashMap<>();
                    while (rs.next()) {
                        map.put(rs.getString("ARENA_NAME"), rs.getLong("PLAYED_AT"));
                    }
                    if (!map.isEmpty()) {
                        recentMap.put(playerUUID, map);
                    }
                }
            }
        } catch (SQLException e) {
            BedWars.plugin.getLogger().severe("[RecentlyPlayedTracker] Failed to load from DB: " + e.getMessage());
        }
    }

    /**
     * Returns all non-expired arena names recently played by this player.
     * Auto-loads from H2 if not in memory (handles reconnects/restarts).
     */
    public static Set<String> getRecentArenas(UUID playerUUID) {
        // Load from DB if not cached (player reconnected after restart)
        if (!recentMap.containsKey(playerUUID)) {
            loadFromDatabase(playerUUID);
        }
        Map<String, Long> map = recentMap.get(playerUUID);
        if (map == null) return new HashSet<>();

        long now = System.currentTimeMillis();

        // Remove expired from memory and collect valid names
        Set<String> expired = new HashSet<>();
        Set<String> result  = new HashSet<>();
        for (Map.Entry<String, Long> e : map.entrySet()) {
            if (now - e.getValue() > EXPIRY_MS) {
                expired.add(e.getKey());
            } else {
                result.add(e.getKey());
            }
        }

        // Clean expired from memory + DB
        if (!expired.isEmpty()) {
            expired.forEach(map::remove);
            BedWars.plugin.getServer().getScheduler().runTaskAsynchronously(BedWars.plugin, () -> {
                try (Connection conn = getConnection()) {
                    long cutoff = System.currentTimeMillis() - EXPIRY_MS;
                    String del = "DELETE FROM RECENTLY_PLAYED WHERE UUID = ? AND PLAYED_AT <= ?;";
                    try (PreparedStatement ps = conn.prepareStatement(del)) {
                        ps.setString(1, playerUUID.toString());
                        ps.setLong(2, cutoff);
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        }

        return result;
    }

    /**
     * Remove all in-memory data for a player on disconnect.
     * DB data is kept until it expires naturally.
     */
    public static void clear(UUID playerUUID) {
        recentMap.remove(playerUUID);
    }

    // ── Internal helper ───────────────────────────────────────────────────────

    /**
     * Borrows a connection from the same H2 file used by the plugin.
     */
    private static Connection getConnection() throws SQLException {
        String url = "jdbc:h2:" + BedWars.plugin.getDataFolder().getAbsolutePath()
                + java.io.File.separator + "Cache"
                + java.io.File.separator + "player_data.h2"
                + ";TRACE_LEVEL_FILE=0";
        return DriverManager.getConnection(url);
    }
}