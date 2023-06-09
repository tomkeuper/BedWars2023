package com.tomkeuper.bedwars.api.shop;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The interface for managing the shop in the game.
 */
public interface IShopManager {

    /**
     * Get the shop index.
     *
     * @return The shop index.
     */
    IShopIndex getShop();

    /**
     * Hide item details in the item meta.
     *
     * @param im The item meta to hide details in.
     * @return The modified item meta with hidden details.
     */
    ItemMeta hideItemDetails(ItemMeta im);

    /**
     * Enchant an item stack.
     *
     * @param itemStack The item stack to enchant.
     * @return The enchanted item stack.
     */
    ItemStack enchantItem(ItemStack itemStack);

    /**
     * Add a tier to a shop category content.
     *
     * @param path The path of the shop category content.
     * @param contentName The name of the content.
     * @param contentSlot The slot of the content.
     * @param tierName The name of the tier.
     * @param tierMaterial The material of the tier item.
     * @param tierData The data value of the tier item.
     * @param amount The amount of the tier item.
     * @param enchant Whether the tier item should be enchanted.
     * @param tierCost The cost of the tier item.
     * @param tierCurrency The currency used to purchase the tier item.
     * @param permanent Whether the tier item is permanent.
     * @param downgradable Whether the tier item is downgradable.
     */
    void addCategoryContentTier(String path, String contentName, int contentSlot, String tierName, String tierMaterial, int tierData, int amount, boolean enchant, int tierCost, String tierCurrency, boolean permanent, boolean downgradable);

    /**
     * Add a tier to a shop category content with unbreakable option.
     *
     * @param path The path of the shop category content.
     * @param contentName The name of the content.
     * @param contentSlot The slot of the content.
     * @param tierName The name of the tier.
     * @param tierMaterial The material of the tier item.
     * @param tierData The data value of the tier item.
     * @param amount The amount of the tier item.
     * @param enchant Whether the tier item should be enchanted.
     * @param tierCost The cost of the tier item.
     * @param tierCurrency The currency used to purchase the tier item.
     * @param permanent Whether the tier item is permanent.
     * @param downgradable Whether the tier item is downgradable.
     * @param unbreakable Whether the tier item is unbreakable.
     */
    void addCategoryContentTier(String path, String contentName, int contentSlot, String tierName, String tierMaterial, int tierData, int amount, boolean enchant, int tierCost, String tierCurrency, boolean permanent, boolean downgradable, boolean unbreakable);

    /**
     * Add a buy item to a content tier.
     *
     * @param path The path of the shop category content.
     * @param contentName The name of the content.
     * @param tierName The name of the tier.
     * @param item The name of the buy item.
     * @param material The material of the buy item.
     * @param data The data value of the buy item.
     * @param amount The amount of the buy item.
     * @param enchant The enchantments applied to the buy item.
     * @param potion The potion effects applied to the buy item.
     * @param itemName The name of the buy item.
     * @param autoEquip Whether the buy item is automatically equipped.
     */
    void addBuyItem(String path, String contentName, String tierName, String item, String material, int data, int amount, String enchant, String potion, String itemName, boolean autoEquip);

    /**
     * Add a buy potion to a content tier.
     *
     * @param path The path of the shop category content.
     * @param contentName The name of the content.
     * @param tierName The name of the tier.
     * @param item The name of the buy item.
     * @param material The material of the buy item.
     * @param data The data value of the buy item.
     * @param amount The amount of the buy item.
     * @param enchant The enchantments applied to the buy item.
     * @param potion The potion effects applied to the buy item.
     * @param itemName The name of the buy item.
     */
    void addBuyPotion(String path, String contentName, String tierName, String item, String material, int data, int amount, String enchant, String potion, String itemName);
}

