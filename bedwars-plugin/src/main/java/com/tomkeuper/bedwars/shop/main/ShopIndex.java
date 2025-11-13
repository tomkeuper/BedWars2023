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
import com.tomkeuper.bedwars.api.events.shop.ShopOpenEvent;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.shop.IPlayerQuickBuyCache;
import com.tomkeuper.bedwars.api.shop.IShopCategory;
import com.tomkeuper.bedwars.api.shop.IShopIndex;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.shop.ShopCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class ShopIndex extends AbstractInventoryLayout implements IShopIndex {

    private final List<IShopCategory> categoryList = new ArrayList<>();
    private final QuickBuyButton quickBuyButton;

    // Caches pre-resolved categories per arena to avoid runtime priority scans
    private final java.util.concurrent.ConcurrentHashMap<IArena, Map<Integer, IShopCategory>> resolvedBySlot = new java.util.concurrent.ConcurrentHashMap<>();

    public static List<UUID> indexViewers = new ArrayList<>();


    /**
     * Create a shop index
     *
     * @param namePath          Message path for the shop inventory name
     * @param quickBuyButton    Player quick buy preferences cache
     * @param separatorNamePath Message path for the shop separator item name
     * @param separatorLorePath Message path for the shop separator lore name
     * @param separatorSelected ItemStack for selected category indicator
     * @param separatorStandard ItemStack for standard separator
     */
    public ShopIndex(String namePath, QuickBuyButton quickBuyButton, String separatorNamePath, String separatorLorePath, ItemStack separatorSelected, ItemStack separatorStandard) {
        super(54, namePath, separatorNamePath, separatorLorePath, separatorSelected, separatorStandard);
        this.quickBuyButton = quickBuyButton;
    }

    /**
     * Open this shop to a player
     *
     * @param callEvent     true if you want to call the shop open event
     * @param quickBuyCache the player cache regarding his preferences
     * @param player        target player
     */
    @Override
    public void open(Player player, IPlayerQuickBuyCache quickBuyCache, boolean callEvent) {

        if (quickBuyCache == null) return;

        IArena arena = Arena.getArenaByPlayer(player);

        if (callEvent) {
            ShopOpenEvent event = new ShopOpenEvent(player, arena);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;
        }

        // Ensure we have a pre-resolved mapping for this arena
        if (arena != null && !resolvedBySlot.containsKey(arena)) {
            preResolveForArena(arena);
        }

        Inventory inv = Bukkit.createInventory(null, getInvSize(), Language.getMsg(player, getNamePath()));

        inv.setItem(getQuickBuyButton().getSlot(), getQuickBuyButton().getItemStack(player));

        Map<Integer, IShopCategory> chosenBySlot = (arena == null) ? null : resolvedBySlot.get(arena);
        if (chosenBySlot == null) {
            // Fallback: if arena is null or not pre-resolved, render defaults only
            chosenBySlot = new HashMap<>();
            for (IShopCategory sc : getCategoryList()) {
                String n = sc.getName().toLowerCase();
                if (n.startsWith("default-")) {
                    chosenBySlot.put(sc.getSlot(), sc);
                }
            }
        }

        for (Map.Entry<Integer, IShopCategory> eCat : chosenBySlot.entrySet()) {
            inv.setItem(eCat.getKey(), eCat.getValue().getItemStack(player));
        }

        addSeparator(player, inv);

        inv.setItem(getQuickBuyButton().getSlot() + 9, getSelectedItem(player));

        ShopCache playerShopCache = ShopCache.getInstance().getShopCache(player.getUniqueId());
        playerShopCache.setSelectedCategory(getQuickBuyButton().getSlot());
        quickBuyCache.addInInventory(inv, playerShopCache);

        player.openInventory(inv);
        if (!indexViewers.contains(player.getUniqueId())) {
            indexViewers.add(player.getUniqueId());
        }
    }


    /**
     * Add shop separator between categories and items
     */
    @Override
    public void addSeparator(Player player, Inventory inv) {
        super.addSeparator(player, inv);
    }

    /**
     * This is the item that indicates the selected category
     */
    @Override
    public ItemStack getSelectedItem(Player player) {
        return super.getSelectedItem(player);
    }

    /**
     * Add a shop category
     */
    @Override
    public void addShopCategory(IShopCategory sc) {
        categoryList.add(sc);
        BedWars.debug("Adding shop category: " + sc.getName() + " at slot " + sc.getSlot());
    }

    /**
     * Get the shop's categories
     */
    public List<IShopCategory> getCategoryList() {
        return categoryList;
    }

    /**
     * Get the quick buy button
     */
    public QuickBuyButton getQuickBuyButton() {
        return quickBuyButton;
    }

    // ===== Pre-resolution API =====
    public void preResolveForArena(IArena arena) {
        if (arena == null) return;
        String arenaPrefixDisplay = arena.getDisplayName() == null ? "" : arena.getDisplayName().toLowerCase() + "-";
        String arenaPrefixWorld = arena.getArenaName() == null ? "" : arena.getArenaName().toLowerCase() + "-";
        String groupPrefix = arena.getGroup() == null ? "" : arena.getGroup().toLowerCase() + "-";
        String defaultPrefix = "default-";

        Map<Integer, IShopCategory> chosenBySlot = new HashMap<>();
        Map<Integer, Integer> priorityBySlot = new HashMap<>();

        for (IShopCategory sc : getCategoryList()) {
            String n = sc.getName() == null ? "" : sc.getName().toLowerCase();
            int pr = 0;
            if ((!arenaPrefixDisplay.isEmpty() && n.startsWith(arenaPrefixDisplay)) || (!arenaPrefixWorld.isEmpty() && n.startsWith(arenaPrefixWorld))) {
                pr = 3;
            } else if (!groupPrefix.isEmpty() && n.startsWith(groupPrefix)) {
                pr = 2;
            } else if (n.startsWith(defaultPrefix)) {
                pr = 1;
            }
            if (pr == 0) continue;

            // slot resolution
            int slot = sc.getSlot();
            int currentPr = priorityBySlot.getOrDefault(slot, 0);
            if (pr > currentPr) {
                priorityBySlot.put(slot, pr);
                chosenBySlot.put(slot, sc);
            }

        }

        resolvedBySlot.put(arena, chosenBySlot);
    }

    public Map<Integer, IShopCategory> getResolvedBySlot(IArena arena) {
        Map<Integer, IShopCategory> m = resolvedBySlot.get(arena);
        return m == null ? java.util.Collections.emptyMap() : new HashMap<>(m);
    }

    public void clearArenaCache(IArena arena) {
        if (arena == null) return;
        resolvedBySlot.remove(arena);
    }

    public void clearAllCaches() {
        resolvedBySlot.clear();
    }

    public static List<UUID> getIndexViewers() {
        return new ArrayList<>(indexViewers);
    }
}
