package com.tomkeuper.bedwars.api.events.communication;

import com.google.gson.JsonObject;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RedisMessageEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final JsonObject message;
    private final String AddonName;

    /**
     * Create a new RedisMessageEvent
     *
     * @param message   the message that was sent
     * @param addonName the name of the addon that sent the message
     */
    public RedisMessageEvent(JsonObject message, String addonName) {
        this.message = message;
        AddonName = addonName;
    }

    /**
     * @return the message that was sent
     */
    @NotNull
    @SuppressWarnings("unused")
    public JsonObject getMessage() {
        return message;
    }

    /**
     * @return the name of the addon that sent the message
     */
    @NotNull
    @SuppressWarnings("unused")
    public String getAddonName() {
        return AddonName;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
