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

package com.tomkeuper.bedwars.shop.quickbuy;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.shop.IPlayerQuickBuyCache;
import com.tomkeuper.bedwars.api.shop.IQuickBuyElement;
import com.tomkeuper.bedwars.api.shop.IShopCache;
import com.tomkeuper.bedwars.api.shop.IShopCategory;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerQuickBuyCache implements IPlayerQuickBuyCache {

    private static PlayerQuickBuyCache instance;
    private final List<IQuickBuyElement> elements = new ArrayList<>();
    private String emptyItemNamePath, emptyItemLorePath;
    private ItemStack emptyItem;
    private UUID player;
    private QuickBuyTask task;

    public static int[] quickSlots = new int[]{19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    private static final ConcurrentHashMap<UUID, PlayerQuickBuyCache> quickBuyCaches = new ConcurrentHashMap<>();
    private final HashMap<Integer, String> updateSlots = new HashMap<>();

    public PlayerQuickBuyCache(){
        instance = this;
    }

    public PlayerQuickBuyCache(Player player) {
        if (player == null) return;
        this.player = player.getUniqueId();
        this.emptyItem = BedWars.nms.createItemStack(BedWars.shop.getYml().getString(ConfigPath.SHOP_SETTINGS_QUICK_BUY_EMPTY_MATERIAL),
                BedWars.shop.getYml().getInt(ConfigPath.SHOP_SETTINGS_QUICK_BUY_EMPTY_AMOUNT),
                (short) BedWars.shop.getYml().getInt(ConfigPath.SHOP_SETTINGS_QUICK_BUY_EMPTY_DATA));
        if (BedWars.shop.getYml().getBoolean(ConfigPath.SHOP_SETTINGS_QUICK_BUY_EMPTY_ENCHANTED)) {
            this.emptyItem = BedWars.shop.enchantItem(emptyItem);
        }
        this.emptyItemNamePath = Messages.SHOP_QUICK_EMPTY_NAME;
        this.emptyItemLorePath = Messages.SHOP_QUICK_EMPTY_LORE;
        task = new QuickBuyTask(player.getUniqueId());
        quickBuyCaches.put(this.player, this);
    }

    /**
     * Add the player's preferences to the given inventory.
     * This will also add the red empty item.
     */
    @Override
    public void addInInventory(Inventory inv, IShopCache shopCache) {
        Player p = Bukkit.getPlayer(player);
        IArena arena = Arena.getArenaByPlayer(p);

        List<IShopCategory> registeredShops = BedWars.getAPI().getShopUtil().getShopManager().getShop().getCategoryList();

        // First, identify and remove categories with overrides
        List<IQuickBuyElement> elementsToRemove = new ArrayList<>();

        List<IShopCategory> matchingShops = registeredShops.stream()
                .filter(shopCategory -> shopCategory.getName().toLowerCase().startsWith(arena.getGroup().toLowerCase()))
                .collect(Collectors.toList());

        for (IQuickBuyElement qbe : elements) {
            String categoryIdentifier = qbe.getCategoryContent().getCategoryIdentifier().toLowerCase();
            if (categoryIdentifier.startsWith("default")){
                for (IShopCategory matchingShop : matchingShops){
                    String matchingShopName = matchingShop.getName().toLowerCase().replace(arena.getGroup() + "-shop-", "");
                    String qbeCategoryName = qbe.getCategoryContent().getCategoryIdentifier().toLowerCase().replace("default-","").split("\\.")[0];
                    if (matchingShopName.equalsIgnoreCase(qbeCategoryName)){
                        elementsToRemove.add(qbe);
                    }
                }
            }
        }

        // Remove categories with overrides
        elements.removeAll(elementsToRemove);

        // Second, add the remaining categories to the inventory
        for (IQuickBuyElement qbe : elements) {
            ICategoryContent categoryContent = qbe.getCategoryContent();
            inv.setItem(qbe.getSlot(), categoryContent.getItemStack(p, shopCache));
        }

        if (elements.size() == 21) return;

        ItemStack i = getEmptyItem(p);
        for (int x : quickSlots) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, i);
            }
        }
    }

    @Override
    public void destroy() {
        elements.clear();
        if (task != null) {
            task.cancel();
        }
        quickBuyCaches.remove(player);
        this.pushChangesToDB();
    }

    @Override
    public void setElement(int slot, ICategoryContent cc) {
        elements.removeIf(q -> q.getSlot() == slot);
        String element;
        if (cc == null){
            element = " ";
        } else {
            addQuickElement(new QuickBuyElement(cc.getIdentifier(), slot));
            element = cc.getIdentifier();
        }
        if (updateSlots.containsKey(slot)){
            updateSlots.replace(slot, element);
        } else {
            updateSlots.put(slot, element);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void setElement(int slot, String category) {
        elements.removeIf(q -> q.getSlot() == slot);
        String element;
        if (category == null){
            element = " ";
        } else {
            addQuickElement(new QuickBuyElement(category, slot));
            element = category;
        }
        if (updateSlots.containsKey(slot)){
            updateSlots.replace(slot, element);
        } else {
            updateSlots.put(slot, element);
        }
    }

    @NotNull
    private ItemStack getEmptyItem(Player player) {
        ItemStack i = emptyItem.clone();
        ItemMeta im = i.getItemMeta();
        if (im != null) {
            im.setDisplayName(Language.getMsg(player, emptyItemNamePath));
            im.setLore(Language.getList(player, emptyItemLorePath));
            i.setItemMeta(im);
        }
        return i;
    }

    /**
     * Check if as category content at quick buy
     */
    @Override
    public boolean hasCategoryContent(ICategoryContent cc) {
        for (IQuickBuyElement q : getElements()) {
            if (q.getCategoryContent() == cc) return true;
        }
        return false;
    }

    /**
     * Get a Player Quick buy cache
     */
    @Nullable
    @Override
    public IPlayerQuickBuyCache getQuickBuyCache(UUID uuid) {
        return quickBuyCaches.getOrDefault(uuid, null);
    }

    @Override
    public List<IQuickBuyElement> getElements() {
        return elements;
    }

    /**
     * Add a quick buy element
     */
    @Override
    public void addQuickElement(IQuickBuyElement e) {
        this.elements.add(e);
    }

    @Override
    public void pushChangesToDB() {
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.plugin,
                () -> BedWars.getRemoteDatabase().pushQuickBuyChanges(updateSlots, this.player, elements));
    }

    /**
     * Get instance
     */
    public static PlayerQuickBuyCache getInstance() {
        return instance;
    }

}
