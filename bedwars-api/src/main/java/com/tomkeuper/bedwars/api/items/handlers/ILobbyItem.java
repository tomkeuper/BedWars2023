package com.tomkeuper.bedwars.api.items.handlers;

import org.bukkit.inventory.ItemStack;

public interface ILobbyItem {
    ILobbyItemHandler getHandler();

    void setHandler(ILobbyItemHandler handler);

    void setItem(ItemStack item);

    ItemStack getItem();

    void setSlot(int slot);

    int getSlot();

    void setIdentifier(String identifier);

    String getIdentifier();
}
