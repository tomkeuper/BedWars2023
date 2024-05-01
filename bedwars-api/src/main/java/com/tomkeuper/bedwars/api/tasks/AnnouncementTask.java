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

package com.tomkeuper.bedwars.api.tasks;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * The AnnouncementTask interface represents a task for announcing messages to players in an arena.
 * It provides methods to load and add messages for players, retrieve the associated arena, Bukkit task, task ID, and cancel the task.
 */
public interface AnnouncementTask {

    /**
     * Get the arena associated with this announcement task.
     *
     * @return The arena associated with the task.
     */
    @SuppressWarnings("unused")
    IArena getArena();

    /**
     * Get the Bukkit task associated with this announcement task.
     *
     * @return The Bukkit task associated with the task.
     */
    @SuppressWarnings("unused")
    BukkitTask getBukkitTask();

    /**
     * Get the task ID of the Bukkit task.
     *
     * @return The task ID of the Bukkit task.
     */
    @SuppressWarnings("unused")
    int getTask();

    /**
     * Cancel the announcement task.
     */
    void cancel();

    /**
     * Load messages for a specific player from a given message path.
     *
     * @param p    The player to load the messages for.
     * @param path The message path to load the messages from.
     */
    @SuppressWarnings("unused")
    void loadMessagesForPlayer(Player p, String path);

    /**
     * Add a message for a specific player.
     *
     * @param p       The player to add the message for.
     * @param message The message to add.
     */
    @SuppressWarnings("unused")
    void addMessageForPlayer(Player p, String message);

    /**
     * Add a list of messages for a specific player.
     *
     * @param p        The player to add the messages for.
     * @param messages The list of messages to add.
     */
    @SuppressWarnings("unused")
    void addMessagesForPlayer(Player p, List<String> messages);
}
