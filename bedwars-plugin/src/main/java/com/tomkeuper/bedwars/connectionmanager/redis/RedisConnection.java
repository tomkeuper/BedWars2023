package com.tomkeuper.bedwars.connectionmanager.redis;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedisConnection {

    JedisPool pool;

    public RedisConnection() {
        JedisPoolConfig config = new JedisPoolConfig();
        pool = new JedisPool(config, BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_HOST),
                BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PORT),0,
                BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PASSWORD));

        // Clean up any instances that might still be in the database.
        cleanupRedisEntries();
    }

    public void cleanupRedisEntries(){
        try (Jedis jedis = pool.getResource()) {
            // Get all keys starting with the server identifier.
            Set<String> keys = jedis.keys("bwa-" + BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SERVER_ID)+"*");

            for (String key : keys) {
                jedis.del(key);
                BedWars.debug("Deleted arena redis with key: " + key);
            }
        } catch (Exception ignored) {
        }
    }

    public void cleanupRedisEntry(IArena a){
        try (Jedis jedis = pool.getResource()) {
            String key = "bwa-" + BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SERVER_ID) + "-" + a.getWorldName();
            jedis.del(key);
            BedWars.debug("Deleted arena redis with key: " + key);
        } catch (Exception ignored) {
        }
    }

    /**
     *
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean storeArenaInformation(IArena a) {
        if (a == null) return false;
        if (a.getWorldName() == null) return false;

        try (Jedis jedis = pool.getResource()) {
            // Create a map for the arena information.
            Map<String, String> arenaInfoMap = new HashMap<>();
            arenaInfoMap.put("server_name", BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SERVER_ID));
            arenaInfoMap.put("arena_name", a.getArenaName());
            arenaInfoMap.put("arena_identifier", a.getWorldName());
            arenaInfoMap.put("arena_status", a.getStatus().toString().toUpperCase());
            arenaInfoMap.put("arena_current_players", String.valueOf(a.getPlayers().size()));
            arenaInfoMap.put("arena_max_players", String.valueOf(a.getMaxPlayers()));
            arenaInfoMap.put("arena_max_in_team", String.valueOf(a.getMaxInTeam()));
            arenaInfoMap.put("arena_group", a.getGroup().toUpperCase());
            arenaInfoMap.put("allow_spectate", String.valueOf(a.isAllowSpectate()));

            // Store the map as a hash table in Redis.
            String key = "bwa-" + BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SERVER_ID) + "-" + a.getWorldName();
            jedis.hset(key, arenaInfoMap);

            BedWars.debug("Storing arena info for: " + a.getArenaName() + " - " + arenaInfoMap);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Send arena data to the lobbies.
     */
    public void sendMessage(String message) {
        if (message == null) return;
        if (message.isEmpty()) return;

        BedWars.debug("sending redis message: " +  message);
    }

    public void close(){
        pool.close();
    }

}
