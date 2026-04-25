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

package com.tomkeuper.bedwars.api.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Get Chat Methods
 */
public interface IChat {

    /**
     * Get Player prefix
     * @param p player from which to take the prefix
     */
    String getPrefix(Player p);

    /**
     * Get Player suffix
     * @param p player from which to take the suffix
     */
    String getSuffix(Player p);

    /**
     * Sends a message to a player with adventure minimessage formating
     * @param player the player to receive the message
     * @param msg the message that will be sent to the player
     */
    void sendMessage(Player player, String msg);

    /**
     * Parse legacy and minimessage string to Component
     * @param msg the message to be parsed
     * @return the parsed Component
     */
    Component parseMiniMessage(String msg);

    /**
     * Parse placeholders, legacy and minimessage string to Component
     * @param player the player to get placeholders for
     * @param msg the message to be parsed
     * @return the parsed Component
     */
    Component parsePlaceholders(Player player, String msg);

}
