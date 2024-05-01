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
