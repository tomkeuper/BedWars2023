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

package com.tomkeuper.bedwars.database;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.database.IDatabase;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.shop.IQuickBuyElement;
import com.tomkeuper.bedwars.api.stats.IPlayerStats;
import com.tomkeuper.bedwars.stats.PlayerStats;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class H2 implements IDatabase {

    private String url;

    private Connection connection;

    public H2() {
        File folder = new File(BedWars.plugin.getDataFolder() + "/Cache");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                BedWars.plugin.getLogger().severe("Could not create /Cache folder!");
            }
        }
        File dataFolder = new File(folder.getPath() + "/player_data.db");

        if (!dataFolder.exists()) {
            try {
                if (!dataFolder.createNewFile()) {
                    BedWars.plugin.getLogger().severe("Could not create /Cache/player_data.db file!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        this.url = "jdbc:h2:" + dataFolder;
        try {
            Class.forName("com.tomkeuper.bedwars.libs.h2.Driver");
            DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            if (e instanceof ClassNotFoundException) {
                BedWars.plugin.getLogger().severe("Could Not Find H2 Driver on your system!");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        String sql;
        try {
            checkConnection();

            sql = "CREATE TABLE IF NOT EXISTS GLOBAL_STATS (ID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "NAME VARCHAR(200), UUID VARCHAR(36), FIRST_PLAY TIMESTAMP NULL DEFAULT NULL, " +
                    "LAST_PLAY TIMESTAMP DEFAULT NULL, WINS INTEGER(10), KILLS INTEGER(10), " +
                    "FINAL_KILLS INTEGER(10), LOSES INTEGER(10), DEATHS INTEGER(10), FINAL_DEATHS INTEGER(10), BEDS_DESTROYED INTEGER(10), GAMES_PLAYED INTEGER(10));";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
            try (Statement st = connection.createStatement()) {
                sql = "CREATE TABLE IF NOT EXISTS QUICK_BUY (UUID VARCHAR(36) PRIMARY KEY, " +
                        "SLOT_19 VARCHAR(200), SLOT_20 VARCHAR(200), SLOT_21 VARCHAR(200), SLOT_22 VARCHAR(200), SLOT_23 VARCHAR(200), SLOT_24 VARCHAR(200), SLOT_25 VARCHAR(200)," +
                        "SLOT_28 VARCHAR(200), SLOT_29 VARCHAR(200), SLOT_30 VARCHAR(200), SLOT_31 VARCHAR(200), SLOT_32 VARCHAR(200), SLOT_33 VARCHAR(200), SLOT_34 VARCHAR(200)," +
                        "SLOT_37 VARCHAR(200), SLOT_38 VARCHAR(200), SLOT_39 VARCHAR(200), SLOT_40 VARCHAR(200), SLOT_41 VARCHAR(200), SLOT_42 VARCHAR(200), SLOT_43 VARCHAR(200));";
                st.executeUpdate(sql);
            }
            try (Statement st = connection.createStatement()) {
                sql = "CREATE TABLE IF NOT EXISTS PLAYER_LEVELS (id INTEGER PRIMARY KEY AUTO_INCREMENT, UUID VARCHAR(200), " +
                        "LEVEL INTEGER, XP INTEGER, NAME VARCHAR(200), NEXT_COST INTEGER);";
                st.executeUpdate(sql);
            }
            try (Statement st = connection.createStatement()) {
                sql = "CREATE TABLE IF NOT EXISTS PLAYER_LANGUAGE (id INTEGER PRIMARY KEY AUTO_INCREMENT, UUID VARCHAR(200), " +
                        "iso VARCHAR(200));";
                st.executeUpdate(sql);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasStats(UUID uuid) {
        String sql = "SELECT UUID FROM GLOBAL_STATS WHERE UUID = ?;";
        try {
            checkConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void saveStats(IPlayerStats stats) {
        String sql;
        try {
            checkConnection();

            if (hasStats(stats.getUuid())) {
                sql = "UPDATE GLOBAL_STATS SET last_play=?, wins=?, kills=?, final_kills=?, looses=?, deaths=?, final_deaths=?, beds_destroyed=?, games_played=?, NAME=? WHERE UUID = ?;";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setTimestamp(1, Timestamp.from(stats.getLastPlay()));
                    statement.setInt(2, stats.getWins());
                    statement.setInt(3, stats.getKills());
                    statement.setInt(4, stats.getFinalKills());
                    statement.setInt(5, stats.getLosses());
                    statement.setInt(6, stats.getDeaths());
                    statement.setInt(7, stats.getFinalDeaths());
                    statement.setInt(8, stats.getBedsDestroyed());
                    statement.setInt(9, stats.getGamesPlayed());
                    statement.setString(10, stats.getName());
                    statement.setString(11, stats.getUuid().toString());
                    statement.executeUpdate();
                }
            } else {
                sql = "INSERT INTO GLOBAL_STATS (Name, UUID, first_play, last_play, wins, kills, final_kills, looses, deaths, final_deaths, beds_destroyed, games_played) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, stats.getName());
                    statement.setString(2, stats.getUuid().toString());
                    statement.setTimestamp(3, Timestamp.from(stats.getFirstPlay()));
                    statement.setTimestamp(4, Timestamp.from(stats.getLastPlay()));
                    statement.setInt(5, stats.getWins());
                    statement.setInt(6, stats.getKills());
                    statement.setInt(7, stats.getFinalKills());
                    statement.setInt(8, stats.getLosses());
                    statement.setInt(9, stats.getDeaths());
                    statement.setInt(10, stats.getFinalDeaths());
                    statement.setInt(11, stats.getBedsDestroyed());
                    statement.setInt(12, stats.getGamesPlayed());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IPlayerStats fetchStats(UUID uuid) {
        IPlayerStats stats = new PlayerStats(uuid);
        String sql = "SELECT * FROM GLOBAL_STATS WHERE UUID = ?;";
        try {
            checkConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        stats.setFirstPlay(result.getTimestamp("first_play").toInstant());
                        stats.setLastPlay(result.getTimestamp("last_play").toInstant());
                        stats.setWins(result.getInt("wins"));
                        stats.setKills(result.getInt("kills"));
                        stats.setFinalKills(result.getInt("final_kills"));
                        stats.setLosses(result.getInt("looses"));
                        stats.setDeaths(result.getInt("deaths"));
                        stats.setFinalDeaths(result.getInt("final_deaths"));
                        stats.setBedsDestroyed(result.getInt("beds_destroyed"));
                        stats.setGamesPlayed(result.getInt("games_played"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    @Override
    public void saveCustomStat(String columnName, UUID player, Object value, String dataType) {
        String sql;
        checkCustomColumnExists(columnName, dataType);
        try {
            checkConnection();

            if (hasStats(player)) {
                sql = "UPDATE GLOBAL_STATS SET "+columnName+"=? WHERE UUID = ?;";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, value);
                    statement.setString(2, player.toString());
                    statement.executeUpdate();
                }
            } else {
                sql = "INSERT INTO GLOBAL_STATS (UUID, "+columnName+") VALUES (?, ?);";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, player.toString());
                    statement.setObject(2, value);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkCustomColumnExists(String columnName, String dataType){
        BedWars.debug("checkCustomColumnExists(String columnName, String dataType)");
        String sql = "SHOW COLUMNS FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?;";
        try {
            checkConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, columnName);
                try (ResultSet result = statement.executeQuery()){
                    if (!result.next()){
                        BedWars.debug("Creating column: " + columnName);
                        sql = "ALTER TABLE GLOBAL_STATS ADD COLUMN " +columnName+ " " + dataType;
                        try (PreparedStatement statement1 = connection.prepareStatement(sql)){
                            statement1.executeUpdate();
                        }
                    }
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getCustomStat(String columnName, UUID player) {
        String sql = "SELECT " + columnName + " FROM GLOBAL_STATS WHERE UUID = ?;";
        try {
            checkConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, player.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getObject(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getQuickBuySlots(UUID p, int slot) {
        String result = "";
        try {
            checkConnection();

            try (PreparedStatement ps = connection.prepareStatement("SELECT SLOT_" + slot + " FROM QUICK_BUY WHERE UUID = ?;")) {
                ps.setString(1, p.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result = rs.getString("SLOT_" + slot);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean hasQuickBuy(UUID uuid) {
        try {
            checkConnection();

            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("SELECT UUID FROM QUICK_BUY WHERE UUID = '" + uuid.toString() + "';")) {
                    if (rs.next()) {
                        rs.close();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getColumn(UUID player, String column) {
        String sql = "SELECT ? FROM GLOBAL_STATS WHERE UUID = ?;";
        try {
            checkConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, column);
                statement.setString(2, player.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getInt(column);
                    }
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
        return 0;
    }

    @Override
    public Object[] getLevelData(UUID player) {
        Object[] r = new Object[]{1, 0, "", 0};
        try {
            checkConnection();

            try (PreparedStatement ps = connection.prepareStatement("SELECT LEVEL, XP, NAME, NEXT_COST FROM PLAYER_LEVELS WHERE UUID = ?;")) {
                ps.setString(1, player.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        r[0] = rs.getInt("LEVEL");
                        r[1] = rs.getInt("XP");
                        r[2] = rs.getString("NAME");
                        r[3] = rs.getInt("NEXT_COST");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public void setLevelData(UUID player, int level, int xp, String displayName, int nextCost) {
        try {
            checkConnection();

            try (PreparedStatement pss = connection.prepareStatement("SELECT UUID from PLAYER_LEVELS WHERE UUID = ?;")) {
                pss.setString(1, player.toString());
                try (ResultSet rs = pss.executeQuery()) {
                    if (!rs.next()) {
                        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO PLAYER_LEVELS (UUID, LEVEL, XP, NAME, NEXT_COST) VALUES (?, ?, ?, ?, ?);")) {
                            //ps.setInt(1, 0);
                            ps.setString(1, player.toString());
                            ps.setInt(2, level);
                            ps.setInt(3, xp);
                            ps.setString(4, displayName);
                            ps.setInt(5, nextCost);
                            ps.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement ps = displayName == null ? connection.prepareStatement("UPDATE PLAYER_LEVELS SET LEVEL=?, XP=? WHERE UUID = '" + player.toString() + "';") : connection.prepareStatement("UPDATE PLAYER_LEVELS SET LEVEL=?, XP=?, NAME=?, NEXT_COST=? WHERE UUID = '" + player.toString() + "';")) {
                            ps.setInt(1, level);
                            ps.setInt(2, xp);
                            if (displayName != null) {
                                ps.setString(3, displayName);
                                ps.setInt(4, nextCost);
                            }
                            ps.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLanguage(UUID player, String iso) {
        try {
            checkConnection();

            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("SELECT iso FROM PLAYER_LANGUAGE WHERE UUID = '" + player.toString() + "';")) {
                    if (rs.next()) {
                        try (Statement st = connection.createStatement()) {
                            st.executeUpdate("UPDATE PLAYER_LANGUAGE SET iso='" + iso + "' WHERE UUID = '" + player.toString() + "';");
                        }
                    } else {
                        try (PreparedStatement st = connection.prepareStatement("INSERT INTO PLAYER_LANGUAGE (UUID, iso) VALUES (?, ?);")) {
                            st.setString(1, player.toString());
                            st.setString(2, iso);
                            st.execute();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLanguage(UUID player) {
        String iso = Language.getDefaultLanguage().getIso();
        try {
            checkConnection();

            try (PreparedStatement ps = connection.prepareStatement("SELECT iso FROM PLAYER_LANGUAGE WHERE UUID = ?;")) {
                ps.setString(1, player.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        iso = rs.getString("iso");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return iso;
    }

    @Override
    public void pushQuickBuyChanges(HashMap<Integer, String> updateSlots, UUID uuid, List<IQuickBuyElement> elements) {
        if (updateSlots.isEmpty()) return;
        boolean hasQuick;
        if (!(hasQuick = hasQuickBuy(uuid))) {
            for (IQuickBuyElement element : elements) {
                if (!updateSlots.containsKey(element.getSlot())) {
                    updateSlots.put(element.getSlot(), element.getCategoryContent().getIdentifier());
                }
            }
        }
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        int i = 0;
        if (hasQuick) {
            for (Map.Entry<Integer, String> entry : updateSlots.entrySet()) {
                i++;
                columns.append("SLOT_").append(entry.getKey()).append("=?");
                if (i != updateSlots.size()) {
                    columns.append(", ");
                }
            }
        } else {
            for (Map.Entry<Integer, String> entry : updateSlots.entrySet()) {
                i++;
                columns.append("SLOT_").append(entry.getKey());
                values.append("?");
                if (i != updateSlots.size()) {
                    columns.append(", ");
                    values.append(", ");
                }
            }
        }
        String sql = hasQuick ? "UPDATE QUICK_BUY SET " + columns + " WHERE UUID=?;" : "INSERT INTO QUICK_BUY (UUID," + columns + ") VALUES (?," + values + ");";
        try {
            checkConnection();

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int index = hasQuick ? 0 : 1;
                for (int key : updateSlots.keySet()) {
                    index++;
                    String identifier = updateSlots.get(key);
                    ps.setString(index, identifier.trim().isEmpty() ? null : identifier);
                }
                ps.setString(hasQuick ? updateSlots.size()+1 : 1, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<Integer, String> getQuickBuySlots(UUID uuid, int[] slot) {
        HashMap<Integer, String> results = new HashMap<>();
        if (slot.length == 0) {
            return results;
        }
        try {
            checkConnection();

            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUICK_BUY WHERE UUID = ?;")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        for (int i : slot) {
                            String id = rs.getString("SLOT_" + i);
                            if (null != id && !id.isEmpty()) {
                                results.put(i, id);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void checkConnection() throws SQLException {
        boolean renew = false;

        if (this.connection == null)
            renew = true;
        else
        if (this.connection.isClosed())
            renew = true;

        if (renew)
            this.connection = DriverManager.getConnection(url);
    }

}