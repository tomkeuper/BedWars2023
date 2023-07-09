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

