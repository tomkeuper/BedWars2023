package com.tomkeuper.bedwars.shop;

import com.tomkeuper.bedwars.BedWars;
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

            ShopCategory sc = new OverrideShopCategory(s, yml, name + "-" + s); // Identify shop with group name + shop name

            if (sc.isLoaded()) shopManager.getShop().addShopCategory(sc);

            for (IShopCategory sc2 : shopManager.getShop().getCategoryList()){
                BedWars.debug("Registered shop categories: " + sc2.getName());
            }

        }
    }
}
