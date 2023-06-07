package com.tomkeuper.bedwars.api.shop;

import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

public interface IPlayerQuickBuyCache {
    IPlayerQuickBuyCache getQuickBuyCache(UUID uuid);
    List<IQuickBuyElement> getElements();
    void destroy();
    void setElement(int slot, ICategoryContent cc);
    void addQuickElement(IQuickBuyElement e);
    boolean hasCategoryContent(ICategoryContent cc);
    void addInInventory(Inventory inv, IShopCache shopCache);
}
