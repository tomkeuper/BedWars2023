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

package com.tomkeuper.bedwars.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerMoneyGainEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;
    private int amount;
    private MoneySource moneySource;
    private boolean cancelled = false;

    /**
     * Called when a player receives money.
     * This only works when an economy system is found.
     *
     * @param player   - target player.
     * @param amount   - amount of xp.
     * @param moneySource - where did the player receive money from.
     */
    public PlayerMoneyGainEvent(Player player, int amount, MoneySource moneySource) {
        this.player = player;
        this.amount = amount;
        this.moneySource = moneySource;
    }

    /**
     * Get the player that have received money.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the amount of money received.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Set a custom amount on the money received
     * This can be used for boosters
     * @param amount - amount of xp
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Get xp source
     */
    public MoneySource getMoneySource() {
        return moneySource;
    }

    /**
     * Lets you know why did the player received money.
     */
    public enum MoneySource {
        PER_MINUTE, PER_TEAMMATE, GAME_WIN, BED_DESTROYED, FINAL_KILL, REGULAR_KILL, OTHER
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Check if event was cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancel event
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
