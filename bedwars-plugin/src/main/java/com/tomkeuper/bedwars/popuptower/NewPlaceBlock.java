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

package com.tomkeuper.bedwars.popuptower;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.TeamColor;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.gameplay.PopUpTowerBuildEvent;
import com.tomkeuper.bedwars.api.region.Region;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class NewPlaceBlock {
    public NewPlaceBlock(Block b, String xyz, TeamColor color, Player p, boolean ladder, int ladderdata) {
        IArena a = Arena.getArenaByPlayer(p);
        if (a == null) {
            return;
        }
        int x = Integer.parseInt(xyz.split(", ")[0]);
        int y = Integer.parseInt(xyz.split(", ")[1]);
        int z = Integer.parseInt(xyz.split(", ")[2]);
        if (b.getRelative(x, y, z).getLocation().getBlockY() >= a.getConfig().getInt(ConfigPath.ARENA_CONFIGURATION_MAX_BUILD_Y)) {
            return;
        }
        if (b.getRelative(x, y, z).getType().equals(Material.AIR)) {
            for (Region r : Arena.getArenaByPlayer(p).getRegionsList())
                if (r.isInRegion(b.getRelative(x, y, z).getLocation()))
                    return;

            IArena arena = Arena.getArenaByPlayer(p);
            Block block;

            if (!ladder)
                block = BedWars.nms.placeTowerBlocks(b, arena, color, x, y, z);
            else
                block = BedWars.nms.placeLadder(b, x, y, z, arena, ladderdata);

            PopUpTowerBuildEvent event = new PopUpTowerBuildEvent(color, arena, block);
            Bukkit.getPluginManager().callEvent(event);
        }

    }
}
