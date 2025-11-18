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

package com.tomkeuper.bedwars.shop.main;

import com.tomkeuper.bedwars.api.language.Language;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Base inventory layout with common UI behavior for shops/menus.
 * Encapsulates name paths, separators and helper methods to keep UI consistent.
 */
public abstract class AbstractInventoryLayout {

    @Getter
    private final int invSize;
    @Getter
    private final String namePath;
    private final String separatorNamePath;
    private final String separatorLorePath;
    protected ItemStack separatorSelected;
    protected ItemStack separatorStandard;

    protected AbstractInventoryLayout(int invSize,
                                      String namePath,
                                      String separatorNamePath,
                                      String separatorLorePath,
                                      ItemStack separatorSelected,
                                      ItemStack separatorStandard) {
        this.invSize = invSize;
        this.namePath = namePath;
        this.separatorNamePath = separatorNamePath;
        this.separatorLorePath = separatorLorePath;
        this.separatorSelected = separatorSelected;
        this.separatorStandard = separatorStandard;
    }

    /**
     * Add shop separator between categories and items.
     */
    public void addSeparator(Player player, Inventory inv) {
        ItemStack i = separatorStandard.clone();
        ItemMeta im = i.getItemMeta();
        if (im != null) {
            im.setDisplayName(Language.getMsg(player, separatorNamePath));
            im.setLore(Language.getList(player, separatorLorePath));
            i.setItemMeta(im);
        }

        for (int x = 9; x < 18; x++) {
            inv.setItem(x, i);
        }
    }

    /**
     * This is the item that indicates the selected category.
     */
    public ItemStack getSelectedItem(Player player) {
        ItemStack i = separatorSelected.clone();
        ItemMeta im = i.getItemMeta();
        if (im != null) {
            im.setDisplayName(Language.getMsg(player, separatorNamePath));
            im.setLore(Language.getList(player, separatorLorePath));
            i.setItemMeta(im);
        }
        return i;
    }
}
