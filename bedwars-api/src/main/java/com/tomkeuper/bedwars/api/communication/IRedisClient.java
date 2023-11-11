package com.tomkeuper.bedwars.api.communication;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public interface IRedisClient {
    void sendMessage(@NotNull JsonObject message, @NotNull String addonIdentifier);
}
