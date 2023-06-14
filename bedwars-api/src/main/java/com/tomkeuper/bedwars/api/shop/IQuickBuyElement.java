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

