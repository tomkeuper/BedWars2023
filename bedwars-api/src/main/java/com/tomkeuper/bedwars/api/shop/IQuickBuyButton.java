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

