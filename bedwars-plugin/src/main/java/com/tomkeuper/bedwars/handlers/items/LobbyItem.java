/*
 * BedWars2023 - A bed wars mini-game.
 * Copyright (C) 2024 Tomas Keuper
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: contact@fyreblox.com
 */

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
