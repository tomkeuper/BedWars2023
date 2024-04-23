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

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"DuplicatedCode", "CallToPrintStackTrace", "SqlSourceToSinkFlow", "ResultOfMethodCallIgnored"})
public class SQLite implements IDatabase {

  private String url;

  private Connection connection;

  public SQLite() {
    File folder = new File(BedWars.plugin.getDataFolder() + "/Cache");
    if (!folder.exists()) {
      if (!folder.mkdir()) {
        BedWars.plugin.getLogger().severe("Could not create /Cache folder!");
      }
    }
    File dataFolder = new File(folder.getPath() + "/player_data.db");

    //TODO Remove check in V2.0
    if (dataFolder.getPath().equals(folder.getPath() + "/shop.db")) {
      dataFolder.renameTo(new File(folder.getPath() + "/player_data.db"));
    }
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
    this.url = "jdbc:sqlite:" + dataFolder;
    try {
      Class.forName("org.sqlite.JDBC");
      DriverManager.getConnection(url);
    } catch (SQLException | ClassNotFoundException e) {
      if (e instanceof ClassNotFoundException) {
        BedWars.plugin.getLogger().severe("Could not find SQLite Driver on your system!");
      }
      e.printStackTrace();
    }
  }

  @Override
  public void init() {
    String sql;
    try {
      checkConnection();

      if (!tableExists("stats")) {
        sql = "CREATE TABLE IF NOT EXISTS stats (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name VARCHAR(200), uuid VARCHAR(36), first_play TIMESTAMP NULL DEFAULT NULL, " +
            "last_play TIMESTAMP DEFAULT NULL);";
        try (Statement statement = connection.createStatement()) {
          statement.executeUpdate(sql);
        }
      }

      // Creates the columns for stats table if the stats table is not populated correctly
      updateStatsTable();

      try (Statement st = connection.createStatement()) {
        sql = "CREATE TABLE IF NOT EXISTS quick_buy_2 (uuid VARCHAR(36) PRIMARY KEY, " +
            "slot_19 VARCHAR(200), slot_20 VARCHAR(200), slot_21 VARCHAR(200), slot_22 VARCHAR(200), slot_23 VARCHAR(200), slot_24 VARCHAR(200), slot_25 VARCHAR(200)," +
            "slot_28 VARCHAR(200), slot_29 VARCHAR(200), slot_30 VARCHAR(200), slot_31 VARCHAR(200), slot_32 VARCHAR(200), slot_33 VARCHAR(200), slot_34 VARCHAR(200)," +
            "slot_37 VARCHAR(200), slot_38 VARCHAR(200), slot_39 VARCHAR(200), slot_40 VARCHAR(200), slot_41 VARCHAR(200), slot_42 VARCHAR(200), slot_43 VARCHAR(200));";
        st.executeUpdate(sql);
      }
      try (Statement st = connection.createStatement()) {
        sql = "CREATE TABLE IF NOT EXISTS player_levels (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR(200), " +
            "level INTEGER, xp INTEGER, name VARCHAR(200), next_cost INTEGER);";
        st.executeUpdate(sql);
      }
      try (Statement st = connection.createStatement()) {
        sql = "CREATE TABLE IF NOT EXISTS  player_language (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR(200), " +
            "iso VARCHAR(200));";
        st.executeUpdate(sql);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean hasStats(UUID uuid) {
    String sql = "SELECT uuid FROM stats WHERE uuid = ?;";
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
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
    try {
      checkConnection();

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
    try {
      checkConnection();

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

  public void checkCustomColumnExists(String columnName, String dataType) {
    String sql = "PRAGMA table_info(stats)";
    try {
      checkConnection();

      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        ResultSet resultSet = statement.executeQuery();
        boolean columnExists = false;
        while (resultSet.next()) {
          String existingColumnName = resultSet.getString("name");
          if (existingColumnName.equalsIgnoreCase(columnName)) {
            columnExists = true;
            break;
          }
        }
        if (!columnExists) {
          sql = "ALTER TABLE stats ADD COLUMN " + columnName + " " + dataType;
          try (PreparedStatement statement1 = connection.prepareStatement(sql)) {
            statement1.executeUpdate();
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Object getCustomStat(String columnName, UUID player) {
    String sql = "SELECT " + columnName + " FROM stats WHERE uuid = ?;";
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

      try (PreparedStatement ps = connection.prepareStatement("SELECT slot_" + slot + " FROM quick_buy_2 WHERE uuid = ?;")) {
        ps.setString(1, p.toString());
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            result = rs.getString("slot_" + slot);
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
        try (ResultSet rs = statement.executeQuery("SELECT uuid FROM quick_buy_2 WHERE uuid = '" + uuid.toString() + "';")) {
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

  @SuppressWarnings("unused")
  @Override
  public int getColumn(UUID player, String column) {
    String sql = "SELECT ? FROM stats WHERE uuid = ?;";
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
    } catch (SQLException ex) {
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

      try (PreparedStatement ps = connection.prepareStatement("SELECT level, xp, name, next_cost FROM player_levels WHERE uuid = ?;")) {
        ps.setString(1, player.toString());
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            r[0] = rs.getInt("level");
            r[1] = rs.getInt("xp");
            r[2] = rs.getString("name");
            r[3] = rs.getInt("next_cost");
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

      try (PreparedStatement pss = connection.prepareStatement("SELECT uuid from player_levels WHERE uuid = ?;")) {
        pss.setString(1, player.toString());
        try (ResultSet rs = pss.executeQuery()) {
          if (!rs.next()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO player_levels (uuid, level, xp, name, next_cost) VALUES (?, ?, ?, ?, ?);")) {
              //ps.setInt(1, 0);
              ps.setString(1, player.toString());
              ps.setInt(2, level);
              ps.setInt(3, xp);
              ps.setString(4, displayName);
              ps.setInt(5, nextCost);
              ps.executeUpdate();
            }
          } else {
            try (PreparedStatement ps = displayName == null ? connection.prepareStatement("UPDATE player_levels SET level=?, xp=? WHERE uuid = '" + player.toString() + "';") : connection.prepareStatement("UPDATE player_levels SET level=?, xp=?, name=?, next_cost=? WHERE uuid = '" + player.toString() + "';")) {
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
        try (ResultSet rs = statement.executeQuery("SELECT iso FROM player_language WHERE uuid = '" + player.toString() + "';")) {
          if (rs.next()) {
            try (Statement st = connection.createStatement()) {
              st.executeUpdate("UPDATE player_language SET iso='" + iso + "' WHERE uuid = '" + player.toString() + "';");
            }
          } else {
            try (PreparedStatement st = connection.prepareStatement("INSERT INTO player_language (uuid, iso) VALUES (?, ?);")) {
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

      try (PreparedStatement ps = connection.prepareStatement("SELECT iso FROM player_language WHERE uuid = ?;")) {
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
    try {
      checkConnection();

      try (PreparedStatement ps = connection.prepareStatement(sql)) {
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

  @Override
  public HashMap<Integer, String> getQuickBuySlots(UUID uuid, int[] slot) {
    HashMap<Integer, String> results = new HashMap<>();
    if (slot.length == 0) {
      return results;
    }
    try {
      checkConnection();

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

  private void checkConnection() throws SQLException {
    boolean renew = false;

    if (this.connection == null)
      renew = true;
    else if (this.connection.isClosed())
      renew = true;

    if (renew)
      this.connection = DriverManager.getConnection(url);
  }


  private List<String> getArenaGroups() {
    return BedWars.config.getYml().getStringList(ConfigPath.GENERAL_CONFIGURATION_ARENA_GROUPS);
  }

  public boolean tableExists(String tableName) {
    try {
      checkConnection();
      boolean found = false;
      DatabaseMetaData databaseMetaData = connection.getMetaData();
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
      checkCustomColumnExists("wins." + arenaGroup, "INTEGER(10)");
      checkCustomColumnExists("kills." + arenaGroup, "INTEGER(10)");
      checkCustomColumnExists("final_kills." + arenaGroup, "INTEGER(10)");
      checkCustomColumnExists("looses." + arenaGroup, "INTEGER(10)");
      checkCustomColumnExists("final_deaths." + arenaGroup, "INTEGER(10)");
      checkCustomColumnExists("beds_destroyed." + arenaGroup, "INTEGER(10)");
      checkCustomColumnExists("games_played." + arenaGroup, "INTEGER(10)");
    }
  }
}