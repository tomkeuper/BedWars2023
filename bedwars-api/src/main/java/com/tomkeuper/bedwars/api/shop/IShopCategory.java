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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents a shop category.
 */
public interface IShopCategory {

    /**
     * Get the slot of the category in the shop index.
     *
     * @return The slot number.
     */
    int getSlot();

    /**
     * Get the name of the category.
     *
     * @return The category name.
     */
    String getName();

    /**
     * Check if the category is loaded.
     *
     * @return true if the category is loaded, false otherwise.
     */
    boolean isLoaded();

    /**
     * Get the list of category contents in this category.
     *
     * @return The list of category contents.
     */
    List<ICategoryContent> getCategoryContentList();

    /**
     * Get a category content by its identifier.
     *
     * @param identifier The identifier of the category content.
     * @param shopIndex  The shop index containing the category.
     * @return The category content, or null if not found.
     */
    ICategoryContent getCategoryContent(String identifier, IShopIndex shopIndex);

    /**
     * Get the category item stack in the player's language.
     *
     * @param player The player.
     * @return The item stack representing the category.
     */
    ItemStack getItemStack(Player player);

    /**
     * Open the category for a player in the shop index.
     *
     * @param player    The player.
     * @param index     The shop index.
     * @param shopCache The shop cache.
     */
    void open(Player player, IShopIndex index, IShopCache shopCache);
}

