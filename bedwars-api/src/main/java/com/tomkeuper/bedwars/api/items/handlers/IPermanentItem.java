package com.tomkeuper.bedwars.api.items.handlers;

import org.bukkit.inventory.ItemStack;

public interface IPermanentItem {
    IPermanentItemHandler getHandler();

    void setHandler(IPermanentItemHandler handler);

    void setItem(ItemStack item);

    ItemStack getItem();

    void setSlot(int slot);

    int getSlot();

    void setIdentifier(String identifier);

    String getIdentifier();
}
