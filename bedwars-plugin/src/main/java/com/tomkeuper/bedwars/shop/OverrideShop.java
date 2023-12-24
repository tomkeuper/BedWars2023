package com.tomkeuper.bedwars.shop;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.shop.IBuyItem;
import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import com.tomkeuper.bedwars.api.arena.shop.IContentTier;
import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.shop.IShopCategory;
import com.tomkeuper.bedwars.shop.main.*;
import org.bukkit.configuration.file.YamlConfiguration;

public class OverrideShop extends ConfigManager {

    private final ShopManager shopManager;
    private final String name;
    public OverrideShop(ShopManager shopManager, String name) {
        super(BedWars.plugin, name, BedWars.plugin.getDataFolder().getPath() + "/Shops");
        this.name = name;
        this.shopManager = shopManager;
        loadShopOverride(getYml());
    }

    private void loadShopOverride(YamlConfiguration yml){
        for (String s : yml.getConfigurationSection("").getKeys(false)) {

            BedWars.debug("adding shop category: " + s);
            if (s.equalsIgnoreCase(ConfigPath.SHOP_SETTINGS_PATH)) continue;
            if (s.equals(ConfigPath.SHOP_QUICK_DEFAULTS_PATH)) continue;
            if (s.equalsIgnoreCase(ConfigPath.SHOP_SPECIALS_PATH)) continue;

            ShopCategory sc = new OverrideShopCategory(s, yml);
            sc.setName(name + "-" + s); // Overwrite normal name (as its just path value)

            if (sc.isLoaded()) shopManager.getShop().addShopCategory(sc);

            for (IShopCategory sc2 : shopManager.getShop().getCategoryList()){
                BedWars.debug("Registered shop categories: " + sc2.getName());
            }

        }
    }

    /**
     * Create a tier for a shop content
     */
//    public void addCategoryContentTier(String path, String contentName, int contentSlot, String tierName, String tierMaterial, int tierData, int amount, boolean enchant, int tierCost, String tierCurrency, boolean permanent,
//                                       boolean downgradable) {
//        path += ConfigPath.SHOP_CATEGORY_CONTENT_PATH + "." + contentName + ".";
//        getYml().addDefault(path + ConfigPath.SHOP_CATEGORY_CONTENT_CONTENT_SLOT, contentSlot);
//        getYml().addDefault(path + ConfigPath.SHOP_CATEGORY_CONTENT_IS_PERMANENT, permanent);
//        getYml().addDefault(path + ConfigPath.SHOP_CATEGORY_CONTENT_IS_DOWNGRADABLE, downgradable);
//        path += ConfigPath.SHOP_CATEGORY_CONTENT_CONTENT_TIERS + "." + tierName;
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_ITEM_MATERIAL, tierMaterial);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_ITEM_DATA, tierData);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_ITEM_AMOUNT, amount);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_ITEM_ENCHANTED, enchant);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_SETTINGS_COST, tierCost);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_SETTINGS_CURRENCY, tierCurrency);
//    }

    /**
     * Create a tier for a shop content (unbreakable)
     * Comment: Reason I made a new function; not all items can be unbreakable, thus shouldn't have the option.
     */
//    public void addCategoryContentTier(String path, String contentName, int contentSlot, String tierName, String tierMaterial, int tierData, int amount, boolean enchant, int tierCost, String tierCurrency, boolean permanent,
//                                       boolean downgradable, boolean unbreakable) {
//        path += ConfigPath.SHOP_CATEGORY_CONTENT_PATH + "." + contentName + ".";
//        getYml().addDefault(path + ConfigPath.SHOP_CATEGORY_CONTENT_CONTENT_SLOT, contentSlot);
//        getYml().addDefault(path + ConfigPath.SHOP_CATEGORY_CONTENT_IS_PERMANENT, permanent);
//        getYml().addDefault(path + ConfigPath.SHOP_CATEGORY_CONTENT_IS_DOWNGRADABLE, downgradable);
//        getYml().addDefault(path + ConfigPath.SHOP_CATEGORY_CONTENT_IS_UNBREAKABLE, unbreakable);
//        path += ConfigPath.SHOP_CATEGORY_CONTENT_CONTENT_TIERS + "." + tierName;
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_ITEM_MATERIAL, tierMaterial);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_ITEM_DATA, tierData);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_ITEM_AMOUNT, amount);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_ITEM_ENCHANTED, enchant);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_SETTINGS_COST, tierCost);
//        getYml().addDefault(path + ConfigPath.SHOP_CONTENT_TIER_SETTINGS_CURRENCY, tierCurrency);
//    }

    /**
     * Add buy items to a content tier
     */
//    public void addBuyItem(String path, String contentName, String tierName, String item, String material, int data, int amount, String enchant, String potion, String itemName, boolean autoEquip) {
//        path += ConfigPath.SHOP_CATEGORY_CONTENT_PATH + "." + contentName + "." + ConfigPath.SHOP_CATEGORY_CONTENT_CONTENT_TIERS + "." + tierName + "." + ConfigPath.SHOP_CONTENT_BUY_ITEMS_PATH + "." + item + ".";
//        getYml().addDefault(path + "material", material);
//        getYml().addDefault(path + "data", data);
//        getYml().addDefault(path + "amount", amount);
//        if (!enchant.isEmpty()) {
//            getYml().addDefault(path + "enchants", enchant);
//        }
//        if (!potion.isEmpty()) {
//            getYml().addDefault(path + "potion", potion);
//        }
//        if (autoEquip) {
//            getYml().addDefault(path + "auto-equip", true);
//        }
//        if (!itemName.isEmpty()) {
//            getYml().addDefault(path + "name", itemName);
//        }
//    }

    /**
     * Add buy potions to a content tier
     */
//    public void addBuyPotion(String path, String contentName, String tierName, String item, String material, int data, int amount, String enchant, String potion, String itemName) {
//        path += ConfigPath.SHOP_CATEGORY_CONTENT_PATH + "." + contentName + "." + ConfigPath.SHOP_CATEGORY_CONTENT_CONTENT_TIERS + "." + tierName + "." + ConfigPath.SHOP_CONTENT_BUY_ITEMS_PATH + "." + item + ".";
//        getYml().addDefault(path + "material", material);
//        getYml().addDefault(path + "data", data);
//        getYml().addDefault(path + "amount", amount);
//        if (!enchant.isEmpty()) {
//            getYml().addDefault(path + "enchants", enchant);
//        }
//        if (!potion.isEmpty()) {
//            getYml().addDefault(path + "potion", potion);
//        }
//        getYml().addDefault(path + "potion-color", "");
//        if (!itemName.isEmpty()) {
//            getYml().addDefault(path + "name", itemName);
//        }
//    }
}
