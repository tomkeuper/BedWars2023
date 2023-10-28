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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisConnection {

    private final String channel;
    private final JedisPool dataPool;
    private final JedisPool subscriptionPool;
    private final RedisPubSubListener redisPubSubListener;

    private final ExecutorService listenerPool = Executors.newCachedThreadPool();

    public RedisConnection() {
        JedisPoolConfig config = new JedisPoolConfig();
        dataPool = new JedisPool(config, BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_HOST),
                BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PORT),0,
                BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PASSWORD));

        // Need a new pool for the subscriptions since they will allow only `(P|S)SUBSCRIBE / (P|S)UNSUBSCRIBE / PING / QUIT / RESET` commands while being subscribed.
        subscriptionPool = new JedisPool(config, BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_HOST),
                BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PORT),0,
                BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PASSWORD));

        this.channel = BedWars.config.getYml().getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_CHANNEL);

        // Clean up any instances that might still be in the database.
        cleanupRedisEntries();

        redisPubSubListener = new RedisPubSubListener(channel);
    }

    public boolean connect(){
        try {
            listenerPool.execute(() -> {
                BedWars.debug("Subscribing to redis channel: " + channel);
                try (final Jedis listenerConnection = subscriptionPool.getResource()){
                    listenerConnection.subscribe(redisPubSubListener, channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                 * Since Jedis PubSub channel listener is thread-blocking,
                 * we can shut down thread when the pub-sub listener stops
                 * or fails.
                 */
                BedWars.debug("Unsubscribing from redis channel: " + channel);
                listenerPool.shutdown();
            });
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }


    public void cleanupRedisEntries(){
        try (Jedis jedis = dataPool.getResource()) {
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
        try (Jedis jedis = dataPool.getResource()) {
            String key = "bwa-" + BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SERVER_ID) + "-" + a.getWorldName();
            jedis.del(key);
            BedWars.debug("Deleted arena redis with key: " + key);
        } catch (Exception ignored) {
        }
    }

    /**
     * Stores information about the arena in the data store.
     * The information includes various details such as the server name, arena name, identifier, status, current players,
     * maximum players, maximum players in a team, group, and whether spectating is allowed.
     *
     * @param a The IArena object for which the information needs to be stored.
     * @return True if the arena information is successfully stored, otherwise false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean storeArenaInformation(IArena a) {
        if (a == null) return false;
        if (a.getWorldName() == null) return false;

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

        try (Jedis jedis = dataPool.getResource()) {
            // Store the map as a hash table in Redis using a separate connection
            String key = "bwa-" + BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SERVER_ID) + "-" + a.getWorldName();
            jedis.hmset(key, arenaInfoMap);

            BedWars.debug("Storing arena info for: " + a.getArenaName() + " - " + arenaInfoMap);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     */
    public void sendMessage(String message) {
        if (message == null) return;
        if (message.isEmpty()) return;

        //TODO: Send message to redis channel.

        BedWars.debug("sending redis message: " +  message);
    }

    public void close(){
        BedWars.debug("Closing redis connections...");
        redisPubSubListener.unsubscribe();
        dataPool.close();
    }

}
