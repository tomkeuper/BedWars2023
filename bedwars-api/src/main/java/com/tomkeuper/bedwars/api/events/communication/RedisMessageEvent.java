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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
