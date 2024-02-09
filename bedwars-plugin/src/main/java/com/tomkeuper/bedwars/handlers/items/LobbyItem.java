package com.tomkeuper.bedwars.handlers.items;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItem;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItemHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LobbyItem implements IPermanentItem {

    private IPermanentItemHandler handler;
    private ItemStack item;
    private int slot;
    private String identifier;

    public LobbyItem(IPermanentItemHandler handler, ItemStack item, int slot, String identifier) {
            this.handler = handler;
            this.item = BedWars.nms.setTag(item, "ACTION", identifier);
            this.slot = slot;
            this.identifier = identifier;
    }

    @Override
    public IPermanentItemHandler getHandler() {
        return handler;
    }

    @Override
    public void setHandler(IPermanentItemHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }
}
