package com.tomkeuper.bedwars.handlers.items;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.items.handlers.ILobbyItem;
import com.tomkeuper.bedwars.api.items.handlers.ILobbyItemHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LobbyItem implements ILobbyItem {

    private ILobbyItemHandler handler;
    private ItemStack item;
    private int slot;
    private List<String> commands;
    private String identifier;

    public LobbyItem(ILobbyItemHandler handler, ItemStack item, int slot, List<String> commands, String identifier) {
            this.handler = handler;
            this.item = BedWars.nms.setTag(item, "ACTION", identifier);
            this.slot = slot;
            this.commands = commands;
            this.identifier = identifier;
    }

    @Override
    public ILobbyItemHandler getHandler() {
        return handler;
    }

    @Override
    public void setHandler(ILobbyItemHandler handler) {
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
