package com.tomkeuper.bedwars.api.shop;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface IShopManager {
    IShopIndex getShop();

    ItemMeta hideItemDetails(ItemMeta im);
    ItemStack enchantItem(ItemStack itemStack);
    void addCategoryContentTier(String path, String contentName, int contentSlot, String tierName, String tierMaterial, int tierData, int amount, boolean enchant, int tierCost, String tierCurrency, boolean permanent,
                                boolean downgradable);
    void addCategoryContentTier(String path, String contentName, int contentSlot, String tierName, String tierMaterial, int tierData, int amount, boolean enchant, int tierCost, String tierCurrency, boolean permanent,
                                boolean downgradable, boolean unbreakable);
    void addBuyItem(String path, String contentName, String tierName, String item, String material, int data, int amount, String enchant, String potion, String itemName, boolean autoEquip);
    void addBuyPotion(String path, String contentName, String tierName, String item, String material, int data, int amount, String enchant, String potion, String itemName);
}
