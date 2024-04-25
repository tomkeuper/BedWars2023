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

import org.bukkit.entity.Player;

/**
 * Represents a cached item in the shop, keeping track of the associated category content and player's tier.
 */
public interface ICachedItem {

    /**
     * Update the item in the specified slot for the given player.
     *
     * @param slot The slot to update the item in.
     * @param p    The player for whom to update the item.
     */
    void updateItem(int slot, Player p);

    /**
     * Get the tier of the cached item.
     *
     * @return The tier of the cached item.
     */
    int getTier();

    /**
     * Upgrade the cached item in the specified slot.
     *
     * @param slot The slot of the item to upgrade.
     */
    void upgrade(int slot);

}
