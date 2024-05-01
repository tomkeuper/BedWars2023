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

import java.util.UUID;

/**
 * Represents a cache for shop items and player tiers.
 */
public interface IShopCache {

    /**
     * Get the weight assigned to a specific shop category.
     *
     * @param sc The shop category.
     * @return The weight assigned to the category.
     */
    byte getCategoryWeight(IShopCategory sc);

    /**
     * Get the tier of a shop item based on its identifier.
     *
     * @param identifier The identifier of the shop item.
     * @return The tier of the item.
     */
    int getContentTier(String identifier);

    /**
     * Check if the shop cache contains a cached item for a specific category content.
     *
     * @param cc The category content to check.
     * @return true if the cache contains the item, false otherwise.
     */
    boolean hasCachedItem(ICategoryContent cc);

    /**
     * Set the weight for a specific shop category.
     *
     * @param sc     The shop category.
     * @param weight The weight to assign.
     */
    void setCategoryWeight(IShopCategory sc, byte weight);

    /**
     * Upgrade a cached item associated with a category content and slot.
     * If the item is not found, it will be added to the cache.
     *
     * @param cc   The category content of the item.
     * @param slot The slot of the item.
     */
    void upgradeCachedItem(ICategoryContent cc, int slot);

    /**
     * Get a cached item based on its identifier.
     *
     * @param identifier The identifier of the item.
     * @return The cached item, or null if not found.
     */
    ICachedItem getCachedItem(String identifier);

    /**
     * Get a cached item associated with a specific category content.
     *
     * @param cc The category content of the item.
     * @return The cached item, or null if not found.
     */
    ICachedItem getCachedItem(ICategoryContent cc);

    /**
     * Set the selected category slot.
     *
     * @param slot The slot of the selected category.
     */
    void setSelectedCategory(int slot);

    /**
     * Get the slot of the selected category.
     *
     * @return The slot of the selected category.
     */
    int getSelectedCategory();

    /**
     * Get the shop cache for a specific player.
     *
     * @param player The UUID of the player.
     * @return The shop cache for the player, or null if not found.
     */
    IShopCache getShopCache(UUID player);
}
