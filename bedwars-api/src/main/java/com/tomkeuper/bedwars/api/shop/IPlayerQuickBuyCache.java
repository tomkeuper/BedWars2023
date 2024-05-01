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
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;


/**
 * Represents a cache for the quick buy settings of a player.
 */
public interface IPlayerQuickBuyCache {

    /**
     * Get the quick buy cache for a player with the specified UUID.
     *
     * @param uuid  The UUID of the player.
     * @return The cached player quick buy instance.
     */
    IPlayerQuickBuyCache getQuickBuyCache(UUID uuid);

    /**
     * Get the elements in the quick buy cache.
     *
     * @return A list of quick buy elements.
     */
    List<IQuickBuyElement> getElements();

    /**
     * Destroy the quick buy cache, clearing all elements and performing necessary clean-up.
     */
    void destroy();

    /**
     * Set the category content for a specific slot in the quick buy cache.
     *
     * @param slot The slot to set the category content for.
     * @param cc   The category content to set.
     */
    void setElement(int slot, ICategoryContent cc);

    /**
     * Set the category content for a specific slot in the quick buy cache.
     *
     * @param slot      The slot to set the category content for.
     * @param category  The category to set.
     */
    void setElement(int slot, String category);

    /**
     * Add a quick buy element to the cache.
     *
     * @param e     The quick buy element to add.
     */
    void addQuickElement(IQuickBuyElement e);

    /**
     * Check if the quick buy cache has a specific category content.
     *
     * @param cc    The category content to check.
     * @return true if the category content is present in the cache, false otherwise.
     */
    boolean hasCategoryContent(ICategoryContent cc);

    /**
     * Add the player's quick buy preferences to the specified inventory.
     *
     * @param inv        The inventory to add the preferences to.
     * @param shopCache  The shop cache to retrieve item stacks from.
     */
    void addInInventory(Inventory inv, IShopCache shopCache);

    /**
     * Save the QuickBuyCache to the database.
     */
    void pushChangesToDB();
}
