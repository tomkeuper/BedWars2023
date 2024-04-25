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
import org.bukkit.inventory.ItemStack;

/**
 * Represents a quick buy button.
 */
public interface IQuickBuyButton {

    /**
     * Get the ItemStack of the quick buy button for a specific player.
     *
     * @param player The player for whom to retrieve the button.
     * @return The ItemStack of the quick buy button.
     */
    ItemStack getItemStack(Player player);

    /**
     * Get the slot of the quick buy button in the inventory.
     *
     * @return The slot of the quick buy button.
     */
    int getSlot();
}

