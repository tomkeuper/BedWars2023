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
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class GameEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * -- GETTER --
     *  Get a list of winners including eliminated teammates
     */
    private List<UUID> winners;
    /**
     * -- GETTER --
     *  Get a list with people who played and didn't win.
     *  This includes people who leaved the game etc.
     */
    private List<UUID> losers;
    /**
     * -- GETTER --
     *  Get a list of winners.
     *  Teammates killed by final kill excluded.
     */
    private List<UUID> aliveWinners;
    /**
     * -- GETTER --
     *  Get the winner team
     */
    private ITeam teamWinner;
    /**
     * -- GETTER --
     *  Get the arena
     */
    private IArena arena;

    /**
     * Triggered when the game ends.
     */
    public GameEndEvent(IArena arena, List<UUID> winners, List<UUID> losers, ITeam teamWinner, List<UUID> aliveWinners) {
        this.winners = new ArrayList<>(winners);
        this.arena = arena;
        this.losers = new ArrayList<>(losers);
        this.teamWinner = teamWinner;
        this.aliveWinners = new ArrayList<>(aliveWinners);
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
