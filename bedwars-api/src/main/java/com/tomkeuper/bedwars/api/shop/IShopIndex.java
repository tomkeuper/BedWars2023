package com.tomkeuper.bedwars.api.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IShopIndex {
    String getNamePath();
    int getInvSize();
    List<IShopCategory> getCategoryList();
    IQuickBuyButton getQuickBuyButton();
    void addSeparator(Player player, Inventory inv);
    ItemStack getSelectedItem(Player player);
    void addShopCategory(IShopCategory sc);
}
