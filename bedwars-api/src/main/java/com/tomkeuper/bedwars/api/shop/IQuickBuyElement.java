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

package com.tomkeuper.bedwars.api.shop;

import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;

/**
 * Represents an element in the quick buy menu, containing a slot, category content, and load status.
 */
public interface IQuickBuyElement {

    /**
     * Check if the quick buy element is loaded.
     *
     * @return true if the element is loaded, false otherwise.
     */
    boolean isLoaded();

    /**
     * Get the slot of the quick buy element.
     *
     * @return The slot of the element.
     */
    int getSlot();

    /**
     * Get the category content associated with the quick buy element.
     *
     * @return The category content of the element.
     */
    ICategoryContent getCategoryContent();
}

