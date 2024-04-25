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

package com.tomkeuper.bedwars.api.items.handlers;

import org.bukkit.inventory.ItemStack;

/**
 * Interface for permanent items.
 */
public interface IPermanentItem {
    /**
     * Get the handler for this permanent item.
     *
     * @return The handler for this permanent item.
     */
    IPermanentItemHandler getHandler();

    /**
     * Set the handler for this permanent item.
     *
     * @param handler The handler for this permanent item.
     */
    void setHandler(IPermanentItemHandler handler);

    /**
     * Set the item stack for this permanent item.
     *
     * @param item The item stack for this permanent item.
     */
    void setItem(ItemStack item);

    /**
     * Get the item stack for this permanent item.
     *
     * @return The item stack for this permanent item.
     */
    ItemStack getItem();

    /**
     * Set the slot for this permanent item.
     *
     * @param slot The slot for this permanent item.
     */
    void setSlot(int slot);

    /**
     * Get the slot for this permanent item.
     *
     * @return The slot for this permanent item.
     */
    int getSlot();

    /**
     * Set the identifier for this permanent item.
     *
     * @param identifier The identifier for this permanent item.
     */
    void setIdentifier(String identifier);

    /**
     * Get the identifier for this permanent item.
     *
     * @return The identifier for this permanent item.
     */
    String getIdentifier();
}
