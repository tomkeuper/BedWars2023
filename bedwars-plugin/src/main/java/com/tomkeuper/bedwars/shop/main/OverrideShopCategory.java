package com.tomkeuper.bedwars.shop.main;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Messages;
import org.bukkit.configuration.file.YamlConfiguration;
public class OverrideShopCategory extends ShopCategory {

    public OverrideShopCategory(String path, YamlConfiguration yml) {
        super();

        BedWars.debug("Loading override shop category: " + path);
        name = path;

        if (yml.get(path + ConfigPath.SHOP_CATEGORY_ITEM_MATERIAL) == null) {
            BedWars.plugin.getLogger().severe("Category material not set at: " + path);
            return;
        }

        if (yml.get(path + ConfigPath.SHOP_CATEGORY_SLOT) == null) {
            BedWars.plugin.getLogger().severe("Category slot not set at: " + path);
            return;
        }
        slot = yml.getInt(path + ConfigPath.SHOP_CATEGORY_SLOT);

        if (slot < 1 || slot > 8) {
            BedWars.plugin.getLogger().severe("Slot must be n > 1 and n < 9 at: " + path);
            return;
        }

//        for (IShopCategory sc : ShopManager.shopIndex.getCategoryList()){
//            if (sc.getSlot() == slot){
//                BedWars.plugin.getLogger().severe("Slot is already in use at: " + path);
//                return;
//            }
//        }

        itemStack = BedWars.nms.createItemStack(yml.getString(path + ConfigPath.SHOP_CATEGORY_ITEM_MATERIAL),
                yml.get(path + ConfigPath.SHOP_CATEGORY_ITEM_AMOUNT) == null ? 1 : yml.getInt(path + ConfigPath.SHOP_CATEGORY_ITEM_AMOUNT),
                (short) (yml.get(path + ConfigPath.SHOP_CATEGORY_ITEM_DATA) == null ? 0 : yml.getInt(path + ConfigPath.SHOP_CATEGORY_ITEM_DATA)));


        if (yml.get(path + ConfigPath.SHOP_CATEGORY_ITEM_ENCHANTED) != null) {
            if (yml.getBoolean(path + ConfigPath.SHOP_CATEGORY_ITEM_ENCHANTED)) {
                itemStack = BedWars.shop.enchantItem(itemStack);
            }
        }

        // potion display color based on NBT tag
        if (yml.getString(path + ".category-item.potion-display") != null && !yml.getString(path + ".category-item.potion-display").isEmpty()) {
            itemStack = BedWars.nms.setTag(itemStack, "Potion", yml.getString(path + ".category-item.potion-display"));
        }
        // 1.16+ custom color
        if (yml.getString(path + ".category-item.potion-color") != null && !yml.getString(path + ".category-item.potion-color").isEmpty()) {
            itemStack = BedWars.nms.setTag(itemStack, "CustomPotionColor", yml.getString(path + ".category-item.potion-color"));
        }

        if (itemStack.getItemMeta() != null) {
            itemStack.setItemMeta(BedWars.shop.hideItemDetails(itemStack.getItemMeta()));
        }

        itemNamePath = Messages.SHOP_CATEGORY_ITEM_NAME.replace("%category%", path);
        itemLorePath = Messages.SHOP_CATEGORY_ITEM_LORE.replace("%category%", path);
        invNamePath = Messages.SHOP_CATEGORY_INVENTORY_NAME.replace("%category%", path);
        loaded = true;

        CategoryContent cc;
        for (String s : yml.getConfigurationSection(path + "." + ConfigPath.SHOP_CATEGORY_CONTENT_PATH).getKeys(false)) {
            cc = new CategoryContent(path + ConfigPath.SHOP_CATEGORY_CONTENT_PATH + "." + s, s, path, yml, this);
            BedWars.debug("blabla: " + s + cc.getCategoryIdentifier());
            cc.setCategoryIdentifier(s + cc.getCategoryIdentifier());
            if (cc.isLoaded()) {
                categoryContentList.add(cc);
                BedWars.debug("Adding Override CategoryContent: " + s + " to Shop Category: " + cc.getCategoryIdentifier());
            }
        }
        instance = this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

}
