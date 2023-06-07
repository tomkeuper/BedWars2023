package com.tomkeuper.bedwars.api.shop;

import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IShopCategory {
    int getSlot();
    String getName();
    boolean isLoaded();
    List<ICategoryContent> getCategoryContentList();
    ICategoryContent getCategoryContent(String identifier, IShopIndex shopIndex);
    ItemStack getItemStack(Player player);
    void open(Player player, IShopIndex index, IShopCache shopCache);
}
