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
import org.bukkit.scheduler.BukkitTask;

/**
 * The StartingTask interface represents a task that handles the starting process of a game in a BedWars arena.
 */
public interface StartingTask {

    /**
     * Get the countdown value for the starting process.
     *
     * @return The countdown value.
     */
    int getCountdown();

    /**
     * Set the countdown value for the starting process.
     *
     * @param countdown The countdown value to set.
     */
    void setCountdown(int countdown);

    /**
     * Get the arena associated with the task.
     *
     * @return The arena object.
     */
    IArena getArena();

    /**
     * Get the task ID of the task.
     *
     * @return The task ID.
     */
    int getTask();

    /**
     * Get the BukkitTask associated with the task.
     *
     * @return The BukkitTask object.
     */
    BukkitTask getBukkitTask();

    /**
     * Cancel the task.
     */
    void cancel();
}

