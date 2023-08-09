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

package com.tomkeuper.bedwars.api.events.player;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents an event that is triggered when a player joins an arena as a player or spectator.
 * This event is not triggered for players who died and become spectators; listen to the kill event for this purpose.
 */
public class PlayerJoinArenaEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final boolean spectator;
    private boolean cancelled = false;
    private final IArena arena;
    private String message;

    /**
     * Constructs a PlayerJoinArenaEvent.
     *
     * @param arena The arena the player is joining.
     * @param player The player joining the arena.
     * @param spectator Whether the player is joining as a spectator.
     */
    public PlayerJoinArenaEvent(IArena arena, Player player, boolean spectator) {
        this.arena = arena;
        this.player = player;
        this.spectator = spectator;
    }

    /**
     * Get the arena where the event occurred.
     *
     * @return The arena instance.
     */
    public IArena getArena() {
        return arena;
    }

    /**
     * Get the player who is joining the arena.
     *
     * @return The player instance.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Check if the player is joining the arena as a spectator.
     *
     * @return true if the player is joining as a spectator, false if as a regular player.
     */
    public boolean isSpectator() {
        return spectator;
    }

    /**
     * Check if the event is cancelled.
     *
     * @return true if the event is cancelled, false otherwise.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancel the event if needed.
     *
     * @param cancelled true to cancel the event, false otherwise.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Set a custom join message for the event.
     *
     * @param message The custom join message.
     */
    @SuppressWarnings("unused")
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the custom join message associated with the event.
     *
     * @return The custom join message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the list of handlers for the event.
     *
     * @return The list of event handlers.
     */
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Get the list of handlers for the event.
     *
     * @return The list of event handlers.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
