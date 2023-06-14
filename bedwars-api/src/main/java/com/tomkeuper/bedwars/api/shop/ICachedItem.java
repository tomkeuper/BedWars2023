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
