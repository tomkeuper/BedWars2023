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

package com.tomkeuper.bedwars.api.items.handlers;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Interface for handling permanent lobby items.
 */
public interface IPermanentItemHandler {

    /**
     * Handles the use of a permanent item by a player in an arena.
     *
     * @param player The player who used the item.
     * @param arena  The arena where the item was used.
     * @param item   The permanent item that was used.
     */
    void handleUse(Player player, IArena arena, IPermanentItem item);

    /**
     * Checks if the permanent item should be visible to the player in the arena.
     *
     * @param player The player viewing the item.
     * @param arena  The arena where the item is located.
     * @return True if the lobby item should be visible; false otherwise.
     */
    boolean isVisible(Player player, IArena arena);

    /**
     * Gets the ID of the permanent item handler.
     *
     * @return The ID of the handler.
     */
    String getId();

    /**
     * Gets the plugin associated with the permanent item handler.
     *
     * @return The plugin associated with the handler.
     */
    Plugin getPlugin();

    /**
     * Gets the type of the permanent item handler.
     *
     * @return The type of the handler.
     */
    HandlerType getType();

    /**
     * Checks if the permanent item handler is registered.
     *
     * @return True if the handler is registered; false otherwise.
     */
    boolean isRegistered();
}
