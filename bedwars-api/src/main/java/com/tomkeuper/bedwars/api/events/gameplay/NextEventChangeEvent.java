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
import com.tomkeuper.bedwars.api.arena.NextEvent;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class NextEventChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * -- GETTER --
     *  Get the arena where happened.
     *
     * @return the arena where happened.
     */
    private IArena arena;
    /**
     * -- GETTER --
     *  Get the event that is coming.
     *
     * @return upcoming event.
     */
    private NextEvent newEvent, /**
     * -- GETTER --
     *  Get the event that happened in this moment.
     *
     * @return the event that just happened.
     */
            oldEvent;

    public NextEventChangeEvent(IArena arena, NextEvent newEvent, NextEvent oldEvent) {
        this.arena = arena;
        this.oldEvent = oldEvent;
        this.newEvent = newEvent;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
