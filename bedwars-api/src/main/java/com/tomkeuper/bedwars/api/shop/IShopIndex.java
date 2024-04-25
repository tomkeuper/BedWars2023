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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents a shop index.
 */
public interface IShopIndex {

    /**
     * Get the message path for the shop inventory name.
     *
     * @return The message path.
     */
    String getNamePath();

    /**
     * Get the size of the shop inventory.
     *
     * @return The inventory size.
     */
    int getInvSize();

    /**
     * Open this shop to a player
     *
     * @param callEvent     true if you want to call the shop open event
     * @param quickBuyCache the player cache regarding his preferences
     * @param player        target player
     */
    void open(Player player, IPlayerQuickBuyCache quickBuyCache, boolean callEvent);

    /**
     * Get the list of shop categories in the index.
     *
     * @return The list of shop categories.
     */
    List<IShopCategory> getCategoryList();

    /**
     * Get the quick buy button for the shop index.
     *
     * @return The quick buy button.
     */
    IQuickBuyButton getQuickBuyButton();

    /**
     * Add a separator between categories and items in the shop index.
     *
     * @param player The player.
     * @param inv    The shop inventory.
     */
    void addSeparator(Player player, Inventory inv);

    /**
     * Get the item stack representing the selected category indicator.
     *
     * @param player The player.
     * @return The item stack representing the selected category.
     */
    ItemStack getSelectedItem(Player player);

    /**
     * Add a shop category to the index.
     *
     * @param sc The shop category to add.
     */
    void addShopCategory(IShopCategory sc);
}

