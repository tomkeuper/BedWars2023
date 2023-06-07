package com.tomkeuper.bedwars.api.shop;

import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a cache for shop items and player's tiers.
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

    void setSelectedCategory(int slot);
    int getSelectedCategory();
    IShopCache getShopCache(UUID player);
}

