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
