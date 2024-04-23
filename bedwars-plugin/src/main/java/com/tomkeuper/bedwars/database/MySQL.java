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
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.database.IDatabase;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.shop.IQuickBuyElement;
import com.tomkeuper.bedwars.api.stats.IPlayerStats;
import com.tomkeuper.bedwars.stats.PlayerStats;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.tomkeuper.bedwars.BedWars.config;

@SuppressWarnings({"WeakerAccess", "DuplicatedCode", "CallToPrintStackTrace", "SqlSourceToSinkFlow"})
public class MySQL implements IDatabase {

    private HikariDataSource dataSource;
    private final String host;
    private final String database;
    private final String user;
    private final String pass;
    private final int port;
    private final boolean ssl;
    private final boolean certificateVerification;
    private final int poolSize;
    private final int maxLifetime;

    /**
     * Create new MySQL connection.
     */
    public MySQL() {
        this.host = config.getYml().getString(ConfigPath.GENERAL_CONFIGURATION_DATABASE_HOST);
        this.database = config.getYml().getString(ConfigPath.GENERAL_CONFIGURATION_DATABASE_DATABASE);
        this.user = config.getYml().getString(ConfigPath.GENERAL_CONFIGURATION_DATABASE_USER);
        this.pass = config.getYml().getString(ConfigPath.GENERAL_CONFIGURATION_DATABASE_PASS);
        this.port = config.getYml().getInt(ConfigPath.GENERAL_CONFIGURATION_DATABASE_PORT);
        this.ssl = config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_DATABASE_SSL);
        this.certificateVerification = config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_DATABASE_VERIFY_CERT);
        this.poolSize = config.getYml().getInt(ConfigPath.GENERAL_CONFIGURATION_DATABASE_POOL_SIZE);
        this.maxLifetime = config.getYml().getInt(ConfigPath.GENERAL_CONFIGURATION_DATABASE_MAX_LIFETIME);
    }

    /**
     * Creates the SQL connection pool and tries to connect.
     *
     * @return true if connected successfully.
     */
    public boolean connect() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setPoolName("BedWars2023MySQLPool");

        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);

        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);

        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification) {
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        }

        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");

        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Recover if connection gets interrupted
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        dataSource = new HikariDataSource(hikariConfig);

        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean hasStats(UUID uuid) {
        String sql = "SELECT uuid FROM stats WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
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
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            String sql;

            if (!tableExists("stats")) {
                 sql = "CREATE TABLE IF NOT EXISTS stats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(200), uuid VARCHAR(200), first_play TIMESTAMP NULL DEFAULT NULL, " +
                        "last_play TIMESTAMP NULL DEFAULT NULL);";
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                }
            }
            // Creates the columns for stats table if the stats table is not populated correctly
            updateStatsTable();

            sql = "CREATE TABLE IF NOT EXISTS quick_buy_2 (uuid VARCHAR(36) PRIMARY KEY, " +
                    "slot_19 VARCHAR(200), slot_20 VARCHAR(200), slot_21 VARCHAR(200), slot_22 VARCHAR(200), slot_23 VARCHAR(200), slot_24 VARCHAR(200), slot_25 VARCHAR(200)," +
                    "slot_28 VARCHAR(200), slot_29 VARCHAR(200), slot_30 VARCHAR(200), slot_31 VARCHAR(200), slot_32 VARCHAR(200), slot_33 VARCHAR(200), slot_34 VARCHAR(200)," +
                    "slot_37 VARCHAR(200), slot_38 VARCHAR(200), slot_39 VARCHAR(200), slot_40 VARCHAR(200), slot_41 VARCHAR(200), slot_42 VARCHAR(200), slot_43 VARCHAR(200));";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }

            sql = "CREATE TABLE IF NOT EXISTS player_levels (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(200), " +
                    "level INT(200), xp INT(200), name VARCHAR(200) CHARACTER SET utf8, next_cost INT(200));";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }

            sql = "CREATE TABLE IF NOT EXISTS player_language (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(200), iso VARCHAR(200));";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveStats(IPlayerStats stats) {
        updateStatsTable();
        String sql;
        try {
            if (hasStats(stats.getUuid())) {
                StringBuilder sqlBuilder = new StringBuilder();
                for (String arenaGroup : getArenaGroups()) {
                    sqlBuilder.append("wins.").append(arenaGroup).append("=?, ");
                    sqlBuilder.append("kills.").append(arenaGroup).append("=?, ");
                    sqlBuilder.append("final_kills.").append(arenaGroup).append("=?, ");
                    sqlBuilder.append("looses.").append(arenaGroup).append("=?, ");
                    sqlBuilder.append("deaths.").append(arenaGroup).append("=?, ");
                    sqlBuilder.append("final_deaths.").append(arenaGroup).append("=?, ");
                    sqlBuilder.append("beds_destroyed.").append(arenaGroup).append("=?, ");
                    sqlBuilder.append("games_played.").append(arenaGroup).append("=?, ");
                }
                // Deletes the last ", "
                sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
                sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
                sql = "UPDATE stats SET name=?, last_play=?, " + sqlBuilder + " WHERE uuid = ?;";
                try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
                    statement.setString(1, stats.getName());
                    statement.setTimestamp(2, Timestamp.from(stats.getLastPlay()));
                    int currentParameterIndex = 3;
                    // This loop will follow the same order as the above loop to generate the sql statement
                    for (String arenaGroup : getArenaGroups()) {
                        statement.setInt(currentParameterIndex, stats.getWins(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getKills(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getFinalKills(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getLosses(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getDeaths(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getFinalDeaths(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getBedsDestroyed(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getGamesPlayed(arenaGroup));
                        currentParameterIndex++;
                    }
                    statement.setString(currentParameterIndex, stats.getUuid().toString());
                    statement.executeUpdate();
                }
            } else {
                StringBuilder sqlBuilder = new StringBuilder();
                StringBuilder values = new StringBuilder();
                for (String arenaGroup : getArenaGroups()) {
                    sqlBuilder.append("wins.").append(arenaGroup).append(", ");
                    sqlBuilder.append("kills.").append(arenaGroup).append(", ");
                    sqlBuilder.append("final_kills.").append(arenaGroup).append(", ");
                    sqlBuilder.append("looses.").append(arenaGroup).append(", ");
                    sqlBuilder.append("deaths.").append(arenaGroup).append(", ");
                    sqlBuilder.append("final_deaths.").append(arenaGroup).append(", ");
                    sqlBuilder.append("beds_destroyed.").append(arenaGroup).append(", ");
                    sqlBuilder.append("games_played.").append(arenaGroup).append(", ");
                }
                int valueCount = 8 * getArenaGroups().size() + 2;
                values.append("?, ".repeat(Math.max(0, valueCount - 1))).append("?");
                // Deletes the last ", "
                sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
                sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
                sql = "INSERT INTO stats (name, uuid, first_play, last_play, " + sqlBuilder + ") VALUES(" + values + ");";
                try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
                    statement.setString(1, stats.getName());
                    statement.setString(2, stats.getUuid().toString());
                    statement.setTimestamp(3, Timestamp.from(stats.getFirstPlay()));
                    statement.setTimestamp(4, Timestamp.from(stats.getLastPlay()));
                    int currentParameterIndex = 5;
                    // This loop will follow the same order as the above loop to generate the sql statement
                    for (String arenaGroup : getArenaGroups()) {
                        statement.setInt(currentParameterIndex, stats.getWins(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getKills(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getFinalKills(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getLosses(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getDeaths(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getFinalDeaths(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getBedsDestroyed(arenaGroup));
                        currentParameterIndex++;
                        statement.setInt(currentParameterIndex, stats.getGamesPlayed(arenaGroup));
                        currentParameterIndex++;
                    }
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
        String sql = "SELECT * FROM stats WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        stats.setFirstPlay(result.getTimestamp("first_play").toInstant());
                        stats.setLastPlay(result.getTimestamp("last_play").toInstant());
                        for (String arenaGroup : getArenaGroups()) {
                            stats.setWins(arenaGroup, result.getInt("wins." + arenaGroup));
                            stats.setKills(arenaGroup, result.getInt("kills." + arenaGroup));
                            stats.setFinalKills(arenaGroup, result.getInt("final_kills." + arenaGroup));
                            stats.setLosses(arenaGroup, result.getInt("looses." + arenaGroup));
                            stats.setDeaths(arenaGroup, result.getInt("deaths." + arenaGroup));
                            stats.setFinalDeaths(arenaGroup, result.getInt("final_deaths." + arenaGroup));
                            stats.setBedsDestroyed(arenaGroup, result.getInt("beds_destroyed." + arenaGroup));
                            stats.setGamesPlayed(arenaGroup, result.getInt("games_played." + arenaGroup));
                        }
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
        try (Connection connection = dataSource.getConnection()) {
            if (hasStats(player)) {
                sql = "UPDATE stats SET " + columnName + "=? WHERE uuid = ?;";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, value);
                    statement.setString(2, player.toString());
                    statement.executeUpdate();
                }
            } else {
                sql = "INSERT INTO stats (uuid, " + columnName + ") VALUES (?, ?);";
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

    @Override
    public Object getCustomStat(String columnName, UUID player) {
        String sql = "SELECT " + columnName + " FROM stats WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
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

    public void checkCustomColumnExists(String columnName, String dataType) {
        String sql = "SHOW COLUMNS FROM stats LIKE ?";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, columnName);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        sql = "ALTER TABLE stats ADD COLUMN " + columnName + " " + dataType;
                        try (PreparedStatement statement1 = connection.prepareStatement(sql)) {
                            statement1.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getQuickBuySlots(UUID uuid, int slot) {
        String sql = "SELECT slot_" + slot + " FROM quick_buy_2 WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public HashMap<Integer, String> getQuickBuySlots(UUID uuid, int[] slot) {
        HashMap<Integer, String> results = new HashMap<>();
        if (slot.length == 0) {
            return results;
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM quick_buy_2 WHERE uuid = ?;")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        for (int i : slot) {
                            String id = rs.getString("slot_" + i);
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

    @Override
    public boolean hasQuickBuy(UUID uuid) {
        String sql = "SELECT uuid FROM quick_buy_2 WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
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
    public int getColumn(UUID player, String column) {
        String sql = "SELECT ? FROM stats WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, column);
                statement.setString(2, player.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getInt(column);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
        return 0;
    }

    @Override
    public Object[] getLevelData(UUID uuid) {
        String sql = "SELECT level, xp, name, next_cost FROM player_levels WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return new Object[]{
                                result.getInt(1),
                                result.getInt(2),
                                result.getString(3),
                                result.getInt(4)
                        };
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Object[]{1, 0, "", 0};
    }

    @Override
    public void setLevelData(UUID uuid, int level, int xp, String displayName, int nextCost) {
        String sql = "SELECT uuid from player_levels WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        sql = "INSERT INTO player_levels VALUES (?, ?, ?, ?, ?, ?);";
                        try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                            statement2.setInt(1, 0);
                            statement2.setString(2, uuid.toString());
                            statement2.setInt(3, level);
                            statement2.setInt(4, xp);
                            statement2.setString(5, displayName);
                            statement2.setInt(6, nextCost);
                            statement2.executeUpdate();
                        }
                    } else {
                        if (displayName == null) {
                            sql = "UPDATE player_levels SET level=?, xp=? WHERE uuid = ?;";
                        } else {
                            sql = "UPDATE player_levels SET level=?, xp=?, name=?, next_cost=? WHERE uuid = ?;";
                        }
                        try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                            statement2.setInt(1, level);
                            statement2.setInt(2, xp);
                            if (displayName != null) {
                                statement2.setString(3, displayName);
                                statement2.setInt(4, nextCost);
                                statement2.setString(5, uuid.toString());
                            } else {
                                statement2.setString(3, uuid.toString());
                            }
                            statement2.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLanguage(UUID uuid, String iso) {
        String sql = "SELECT iso FROM player_language WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        sql = "UPDATE player_language SET iso = ? WHERE uuid = ?;";
                        try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                            statement2.setString(1, iso);
                            statement2.setString(2, uuid.toString());
                            statement2.executeUpdate();
                        }
                    } else {
                        sql = "INSERT INTO player_language VALUES (0, ?, ?);";
                        try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                            statement2.setString(1, uuid.toString());
                            statement2.setString(2, iso);
                            statement2.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLanguage(UUID uuid) {
        String sql = "SELECT iso FROM player_language WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getString(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Language.getDefaultLanguage().getIso();
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
                columns.append("slot_").append(entry.getKey()).append("=?");
                if (i != updateSlots.size()) {
                    columns.append(", ");
                }
            }
        } else {
            for (Map.Entry<Integer, String> entry : updateSlots.entrySet()) {
                i++;
                columns.append("slot_").append(entry.getKey());
                values.append("?");
                if (i != updateSlots.size()) {
                    columns.append(", ");
                    values.append(", ");
                }
            }
        }
        String sql = hasQuick ? "UPDATE quick_buy_2 SET " + columns + " WHERE uuid=?;" : "INSERT INTO quick_buy_2 (uuid," + columns + ") VALUES (?," + values + ");";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int index = hasQuick ? 0 : 1;
                for (int key : updateSlots.keySet()) {
                    index++;
                    String identifier = updateSlots.get(key);
                    ps.setString(index, identifier.trim().isEmpty() ? null : identifier);
                }
                ps.setString(hasQuick ? updateSlots.size() + 1 : 1, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> getArenaGroups() {
        return BedWars.config.getYml().getStringList(ConfigPath.GENERAL_CONFIGURATION_ARENA_GROUPS);
    }

    public boolean tableExists(String tableName) {
        try {
            boolean found = false;
            DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();
            ResultSet rs = databaseMetaData.getTables(null, null, tableName, null);
            while (rs.next()) {
                String name = rs.getString("TABLE_NAME");
                if (tableName.equals(name)) {
                    found = true;
                    break;
                }
            }
            return found;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStatsTable() {
        for (String arenaGroup : getArenaGroups()) {
            checkCustomColumnExists("wins." + arenaGroup, "INT(200)");
            checkCustomColumnExists("kills." + arenaGroup, "INT(200)");
            checkCustomColumnExists("final_kills." + arenaGroup, "INT(200)");
            checkCustomColumnExists("looses." + arenaGroup, "INT(200)");
            checkCustomColumnExists("final_deaths." + arenaGroup, "INT(200)");
            checkCustomColumnExists("beds_destroyed." + arenaGroup, "INT(200)");
            checkCustomColumnExists("games_played." + arenaGroup, "INT(200)");
        }
    }
}
