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

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.shop.IShopCache;
import com.tomkeuper.bedwars.api.shop.IShopCategory;
import com.tomkeuper.bedwars.api.shop.IShopIndex;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.shop.ShopManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopCategory implements IShopCategory {

    public int slot;
    public ItemStack itemStack;
    public String itemNamePath, itemLorePath, invNamePath;
    public boolean loaded = false;
    public final List<ICategoryContent> categoryContentList = new ArrayList<>();
    public static List<UUID> categoryViewers = new ArrayList<>();
    public String name;

    /**
     * Load a shop category from the given path
     */
    public ShopCategory(String path, YamlConfiguration yml, String name) {
        BedWars.debug("Loading shop category: " + path);
        this.name = name;

        if (yml.get(path + ConfigPath.SHOP_CATEGORY_ITEM_MATERIAL) == null) {
            BedWars.plugin.getLogger().severe("Category material not set at: " + path);
            return;
        }

        if (yml.get(path + ConfigPath.SHOP_CATEGORY_SLOT) == null) {
            BedWars.plugin.getLogger().severe("Category slot not set at: " + path);
            return;
        }
        slot = yml.getInt(path + ConfigPath.SHOP_CATEGORY_SLOT);

        if (slot < 1 || slot > 8) {
            BedWars.plugin.getLogger().severe("Slot must be n > 1 and n < 9 at: " + path);
            return;
        }

        // Enforce unique slot only for default categories. Allow overrides to reuse slots so
        // per-arena/group priority can replace defaults at render time.
        if (this.name != null && this.name.toLowerCase().startsWith("default-")) {
            for (IShopCategory sc : ShopManager.shop.getCategoryList()){
                if (sc.getSlot() == slot){
                    BedWars.plugin.getLogger().severe("Slot is already in use at: " + path);
                    return;
                }
            }
        }

        itemStack = BedWars.nms.createItemStack(yml.getString(path + ConfigPath.SHOP_CATEGORY_ITEM_MATERIAL),
                yml.get(path + ConfigPath.SHOP_CATEGORY_ITEM_AMOUNT) == null ? 1 : yml.getInt(path + ConfigPath.SHOP_CATEGORY_ITEM_AMOUNT),
                (short) (yml.get(path + ConfigPath.SHOP_CATEGORY_ITEM_DATA) == null ? 0 : yml.getInt(path + ConfigPath.SHOP_CATEGORY_ITEM_DATA)));


        if (yml.get(path + ConfigPath.SHOP_CATEGORY_ITEM_ENCHANTED) != null) {
            if (yml.getBoolean(path + ConfigPath.SHOP_CATEGORY_ITEM_ENCHANTED)) {
                itemStack = BedWars.shop.enchantItem(itemStack);
            }
        }

        // potion display color based on NBT tag
        if (yml.getString(path + ".category-item.potion-display") != null && !yml.getString(path + ".category-item.potion-display").isEmpty()) {
            itemStack = BedWars.nms.setTag(itemStack, "Potion", yml.getString(path + ".category-item.potion-display"));
        }
        // 1.16+ custom color
        if (yml.getString(path + ".category-item.potion-color") != null && !yml.getString(path + ".category-item.potion-color").isEmpty()) {
            itemStack = BedWars.nms.setTag(itemStack, "CustomPotionColor", yml.getString(path + ".category-item.potion-color"));
        }

        if (itemStack.getItemMeta() != null) {
            itemStack.setItemMeta(BedWars.shop.hideItemDetails(itemStack.getItemMeta()));
        }

        itemNamePath = Messages.SHOP_CATEGORY_ITEM_NAME.replace("%category%", path);
        itemLorePath = Messages.SHOP_CATEGORY_ITEM_LORE.replace("%category%", path);
        invNamePath = Messages.SHOP_CATEGORY_INVENTORY_NAME.replace("%category%", path);
        loaded = true;

        CategoryContent cc;
        for (String s : yml.getConfigurationSection(path + "." + ConfigPath.SHOP_CATEGORY_CONTENT_PATH).getKeys(false)) {
            cc = new CategoryContent(path + ConfigPath.SHOP_CATEGORY_CONTENT_PATH + "." + s, s, path, yml, this);
            // Prefix the content identifier with the full category name (default-, group-, or arena-specific)
            String currId = cc.getCategoryIdentifier();
            if (currId != null && currId.startsWith(path)) {
                cc.setCategoryIdentifier(this.name + currId.substring(path.length()));
            }
            if (cc.isLoaded()) {
                categoryContentList.add(cc);
                BedWars.debug("Adding CategoryContent: " + s + " to Shop Category: " + path);
            }
        }
    }

    /**
     * Open this category for the player using the arena-linked shop index.
     * This avoids passing a ShopIndex at call sites and uses pre-resolved data.
     */
    public void open(Player player, IShopCache shopCache){
        IArena arena = Arena.getArenaByPlayer(player);
        IShopIndex idxToUse = (arena != null && arena.getLinkedShop() != null) ? arena.getLinkedShop() : ShopManager.shop;
        open(player, idxToUse, shopCache);
    }

    /**
     * Deprecated: prefer {@link #open(Player, IShopCache)} which resolves the shop from the arena.
     */
    public void open(Player player, IShopIndex index, IShopCache shopCache){
        BedWars.debug("opening ShopCategory: " + name + " for player: " + player.getName());
        if (player.getOpenInventory().getTopInventory() == null) return;
        ShopIndex.indexViewers.remove(player.getUniqueId());

        IArena arena = Arena.getArenaByPlayer(player);
        // Prefer the arena-linked shop to ensure pre-resolved categories are used
        IShopIndex idxToUse = (arena != null && arena.getLinkedShop() != null) ? arena.getLinkedShop() : index;

        Inventory inv = Bukkit.createInventory(null, idxToUse.getInvSize(), Language.getMsg(player, invNamePath));

        inv.setItem(idxToUse.getQuickBuyButton().getSlot(), idxToUse.getQuickBuyButton().getItemStack(player));

        // Render category buttons using pre-resolved mapping when available
        if (arena != null && idxToUse instanceof ShopIndex) {
            Map<Integer, IShopCategory> chosenBySlot = ((ShopIndex) idxToUse).getResolvedBySlot(arena);
            if (chosenBySlot == null || chosenBySlot.isEmpty()) {
                // Fallback: defaults only
                for (IShopCategory sc : idxToUse.getCategoryList()) {
                    String n = sc.getName() == null ? "" : sc.getName().toLowerCase();
                    if (n.startsWith("default-")) {
                        inv.setItem(sc.getSlot(), sc.getItemStack(player));
                    }
                }
            } else {
                for (Map.Entry<Integer, IShopCategory> e : chosenBySlot.entrySet()) {
                    inv.setItem(e.getKey(), e.getValue().getItemStack(player));
                }
            }
        } else {
            // No arena context: show defaults only to be safe
            for (IShopCategory sc : idxToUse.getCategoryList()) {
                String n = sc.getName() == null ? "" : sc.getName().toLowerCase();
                if (n.startsWith("default-")) {
                    inv.setItem(sc.getSlot(), sc.getItemStack(player));
                }
            }
        }

        idxToUse.addSeparator(player, inv);

        inv.setItem(getSlot() + 9, idxToUse.getSelectedItem(player));

        shopCache.setSelectedCategory(getSlot());

        for (ICategoryContent cc : getCategoryContentList()) {
            inv.setItem(cc.getSlot(), cc.getItemStack(player, shopCache));
        }

        player.openInventory(inv);
        if (!categoryViewers.contains(player.getUniqueId())){
            categoryViewers.add(player.getUniqueId());
        }
    }

    /**
     * Get the category preview item in player's language
     */
    @Override
    public ItemStack getItemStack(Player player) {
        ItemStack i = itemStack.clone();
        ItemMeta im = i.getItemMeta();
        if (im != null) {
            im.setDisplayName(Language.getMsg(player, itemNamePath));
            im.setLore(Language.getList(player, itemLorePath));
            i.setItemMeta(im);
        }
        return i;
    }

    /**
     * Check if category was loaded
     */
    @Override
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Get category slot in shop index
     */
    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public List<ICategoryContent> getCategoryContentList() {
        return categoryContentList;
    }

    /**Get a category content by identifier*/
    @Override
    public ICategoryContent getCategoryContent(String identifier, IShopIndex shopIndex){
        for (IShopCategory sc : shopIndex.getCategoryList()){
            for (ICategoryContent cc : sc.getCategoryContentList()){
                if (cc.getIdentifier().equals(identifier)) return cc;
            }
        }
        return null;
    }

    /**
     * Static helper to resolve a category content by its identifier within a given shop index.
     * Uses the same logic as the instance method but avoids relying on a singleton instance.
     */
    public static ICategoryContent resolveCategoryContent(String identifier, IShopIndex shopIndex){
        if (identifier == null || shopIndex == null) return null;
        for (IShopCategory sc : shopIndex.getCategoryList()){
            for (ICategoryContent cc : sc.getCategoryContentList()){
                if (identifier.equals(cc.getIdentifier())) return cc;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public static List<UUID> getCategoryViewers() {
        return new ArrayList<>(categoryViewers);
    }

}
