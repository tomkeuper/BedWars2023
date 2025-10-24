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

package com.tomkeuper.bedwars.api.events.gameplay;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.TeamColor;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class EggBridgeBuildEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * -- GETTER --
     *  Get the block's team color
     */
    private final TeamColor teamColor;
    /**
     * -- GETTER --
     *  Get the arena
     */
    private final IArena arena;
    /**
     * -- GETTER --
     *  Get the built block
     */
    private final Block block;

    /**
     * Called when the eggBridge is building another block
     * Called when an egg bridge is building another block
     */
    public EggBridgeBuildEvent(TeamColor teamColor, IArena arena, Block block) {
        this.teamColor = teamColor;
        this.arena = arena;
        this.block = block;
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
