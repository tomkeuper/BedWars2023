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

