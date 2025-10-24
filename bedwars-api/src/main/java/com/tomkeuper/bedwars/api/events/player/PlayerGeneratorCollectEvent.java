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

import com.tomkeuper.bedwars.api.arena.IArena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Getter
public class PlayerGeneratorCollectEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * -- GETTER --
     *  Get the player
     */
    private final Player player;
    /**
     * -- GETTER --
     *  Get the item entity involved
     */
    private final Item item;
    private final IArena arena;
    /**
     * -- GETTER --
     *  Get the amount of items involved
     */
    private final int amount;
    /**
     * -- GETTER --
     *  Cancel this event
     */
    @Setter
    private boolean cancelled = false;

    /**
     * Triggered when players collect from generators.
     * This is not hired when player will receive items in inv from gen-split feature. This feature can be disabled in bw config.
     */
    public PlayerGeneratorCollectEvent(Player player, Item item, IArena arena, int amount) {
        this.player = player;
        this.item = item;
        this.arena = arena;
        this.amount = amount;
    }

    /**
     * Get the itemStack involved
     */
    public ItemStack getItemStack() {
        return item.getItemStack();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
