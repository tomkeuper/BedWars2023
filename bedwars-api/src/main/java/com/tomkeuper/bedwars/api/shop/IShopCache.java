package com.tomkeuper.bedwars.api.shop;

import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import org.bukkit.entity.Player;

public interface IShopCache {
    byte getCategoryWeight(IShopCategory sc);
    int getContentTier(String identifier);
    boolean hasCachedItem(ICategoryContent cc);
    void setCategoryWeight(IShopCategory sc, byte weight);
    void upgradeCachedItem(ICategoryContent cc, int slot);
    ICachedItem getCachedItem(String identifier);
    ICachedItem getCachedItem(ICategoryContent cc);
}
