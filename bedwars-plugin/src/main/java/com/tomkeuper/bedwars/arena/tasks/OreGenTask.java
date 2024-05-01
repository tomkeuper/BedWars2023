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

package com.tomkeuper.bedwars.arena.tasks;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;


public class OreGenTask implements Runnable{
    Arena arena;
    private BukkitTask task;
    public OreGenTask(Arena arena) {
        this.arena = arena;
        this.task = Bukkit.getScheduler().runTaskTimer(BedWars.plugin, this, 0, 5L);
    }

    @Override
    public void run() {
        for (ITeam t : getArena().getTeams()) {
            // spawn items
            for (IGenerator o : t.getGenerators()) {
                o.spawn();
            }
        }

        /* SPAWN ITEMS */
        for (IGenerator o : getArena().getOreGenerators()) {
            o.spawn();
        }
    }

    public void cancel() {
        task.cancel();
    }
    public Arena getArena() {
        return arena;
    }

}
