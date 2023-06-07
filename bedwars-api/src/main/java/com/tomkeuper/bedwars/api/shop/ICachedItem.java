package com.tomkeuper.bedwars.api.shop;

import org.bukkit.entity.Player;

public interface ICachedItem {
    void updateItem(int slot, Player p);
    int getTier();
    void upgrade(int slot);

}
