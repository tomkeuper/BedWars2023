package com.tomkeuper.bedwars.api.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IQuickBuyButton {
    ItemStack getItemStack(Player player);
    int getSlot();
}
