/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
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
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.tomkeuper.bedwars.api.tasks;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.scheduler.BukkitTask;

/**
 * The PlayingTask interface represents a task that is executed during the gameplay of a BedWars mini-game.
 * It provides methods to access the associated arena, Bukkit task, task ID, countdowns, and to cancel the task.
 */
public interface PlayingTask {

    /**
     * Get the arena associated with the playing task.
     *
     * @return The arena object.
     */
    IArena getArena();

    /**
     * Get the Bukkit task associated with the playing task.
     *
     * @return The BukkitTask object representing the task.
     */
    BukkitTask getBukkitTask();

    /**
     * Get the task ID of the playing task.
     *
     * @return The task ID.
     */
    int getTask();

    /**
     * Get the countdown value for destroying beds in the game.
     *
     * @return The countdown value for beds destruction.
     */
    int getBedsDestroyCountdown();

    /**
     * Get the countdown value for spawning the ender dragon in the game.
     *
     * @return The countdown value for ender dragon spawn.
     */
    int getDragonSpawnCountdown();

    /**
     * Get the countdown value for the end of the game.
     *
     * @return The countdown value for game end.
     */
    int getGameEndCountdown();

    /**
     * Cancel the playing task and associated tasks.
     */
    void cancel();
}