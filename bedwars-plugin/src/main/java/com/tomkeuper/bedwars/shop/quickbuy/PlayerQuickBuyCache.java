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

        // Resolve the arena-linked shop index (no global scans)
        com.tomkeuper.bedwars.api.shop.IShopIndex idx = (arena != null && arena.getLinkedShop() != null)
                ? arena.getLinkedShop()
                : BedWars.getAPI().getShopUtil().getShopManager().getShop();

        // Collect contents available in this arena only
        java.util.List<ICategoryContent> arenaContents = new java.util.ArrayList<>();
        if (arena != null && idx instanceof com.tomkeuper.bedwars.shop.main.ShopIndex) {
            java.util.Map<Integer, IShopCategory> chosen = ((com.tomkeuper.bedwars.shop.main.ShopIndex) idx).getResolvedBySlot(arena);
            for (IShopCategory sc : chosen.values()) {
                arenaContents.addAll(sc.getCategoryContentList());
            }
        } else {
            // Fallback: defaults only when no arena context or no pre-resolved data
            for (IShopCategory sc : idx.getCategoryList()) {
                String n = sc.getName() == null ? "" : sc.getName().toLowerCase();
                if (n.startsWith("default-")) {
                    arenaContents.addAll(sc.getCategoryContentList());
                }
            }
        }

        // Map of slot -> element to render (no priority; keep first encountered)
        java.util.Map<Integer, IQuickBuyElement> toRenderBySlot = new java.util.HashMap<>();

        for (IQuickBuyElement qbe : elements) {
            ICategoryContent content = qbe.getCategoryContent();
            if (content == null) continue;

            // Find exact match by identifier within arena contents; if not found, skip
            ICategoryContent match = null;
            String idExact = content.getIdentifier();
            for (ICategoryContent c : arenaContents) {
                if (idExact.equalsIgnoreCase(c.getIdentifier())) { match = c; break; }
            }
            if (match == null) {
                // Not available in this arena; do not render this element
                continue;
            }

            // Rebind to arena instance if needed
            if (match != content && qbe instanceof QuickBuyElement) {
                ((QuickBuyElement) qbe).setCategoryContent(match);
                content = match;
            }

            // Put in its saved slot if not already occupied
            int slot = qbe.getSlot();
            if (!toRenderBySlot.containsKey(slot)) {
                toRenderBySlot.put(slot, qbe);
            }
        }

        // Render quick-buy elements that are available in this arena
        for (java.util.Map.Entry<Integer, IQuickBuyElement> e : toRenderBySlot.entrySet()) {
            IQuickBuyElement qbe = e.getValue();
            ICategoryContent cc = qbe.getCategoryContent();
            if (cc != null) {
                inv.setItem(e.getKey(), cc.getItemStack(p, shopCache));
            }
        }

        // Fill empty quick slots with the empty placeholder
        ItemStack empty = getEmptyItem(p);
        for (int x : quickSlots) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, empty);
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

    // Normalize an identifier by stripping the arena/group/default prefix from the base category
    private static String normalizeIdentifier(String id) {
        if (id == null) return "";
        int idxMarker = id.indexOf(".category-content.");
        if (idxMarker < 0) return id;
        String cat = id.substring(0, idxMarker);
        int dash = cat.indexOf('-');
        if (dash >= 0) {
            cat = cat.substring(dash + 1);
        }
        return cat + id.substring(idxMarker);
    }
}
