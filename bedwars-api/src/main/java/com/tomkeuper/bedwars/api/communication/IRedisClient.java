package com.tomkeuper.bedwars.api.communication;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public interface IRedisClient {
    void sendMessage(@NotNull JsonObject message, @NotNull String addonIdentifier);

    /**
     * Retrieve the data associated with a specific identifier from the Redis database.
     *
     * @param redisSettingIdentifier the identifier of the setting to be checked.
     * @return the data as a string associated with the specified identifier
     */
    String retrieveSetting(@NotNull String redisSettingIdentifier);

    /**
     * Store the settings in the Redis database.
     * Allowing for easy access to the settings across the network.
     * Can be retrieved on the proxy server.
     *
     * @param redisSettingIdentifier the identifier of the setting to be checked.
     * @param setting the setting to be stored.
     */
    public void storeSettings(String redisSettingIdentifier, String setting);
}
