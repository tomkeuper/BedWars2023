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

package com.tomkeuper.bedwars.shop.listeners;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import com.tomkeuper.bedwars.api.shop.ICachedItem;
import com.tomkeuper.bedwars.api.shop.IPlayerQuickBuyCache;
import com.tomkeuper.bedwars.api.shop.IQuickBuyElement;
import com.tomkeuper.bedwars.api.shop.IShopCategory;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.shop.ShopCache;
import com.tomkeuper.bedwars.shop.ShopManager;
import com.tomkeuper.bedwars.shop.main.ShopCategory;
import com.tomkeuper.bedwars.shop.main.ShopIndex;
import com.tomkeuper.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import com.tomkeuper.bedwars.shop.quickbuy.QuickBuyAdd;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.event.inventory.InventoryAction.HOTBAR_SWAP;
import static org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();

        IArena a = Arena.getArenaByPlayer(p);
        if (a == null) return;
        if (a.isSpectator(p)) return;

        ShopCache shopCache = ShopCache.getInstance().getShopCache(p.getUniqueId());
        IPlayerQuickBuyCache cache = PlayerQuickBuyCache.getInstance().getQuickBuyCache(p.getUniqueId());

        if (cache == null) return;
        if (shopCache == null) return;

        if(ShopIndex.getIndexViewers().contains(p.getUniqueId()) || ShopCategory.getInstance().getCategoryViewers().contains(p.getUniqueId())) {
            if (e.getClickedInventory() != null && e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                e.setCancelled(true);
                return;
            }
        }

        if (ShopIndex.getIndexViewers().contains(p.getUniqueId())) {
            e.setCancelled(true);

            // Use the arena-linked shop and its pre-resolved categories to handle clicks
            com.tomkeuper.bedwars.api.shop.IShopIndex idx = a.getLinkedShop() != null ? a.getLinkedShop() : ShopManager.shop;
            if (idx instanceof ShopIndex) {
                java.util.Map<Integer, IShopCategory> chosen = ((ShopIndex) idx).getResolvedBySlot(a);
                IShopCategory clickedCategory = chosen.get(e.getSlot());
                if (clickedCategory != null) {
                    if (clickedCategory instanceof ShopCategory) {
                        ((ShopCategory) clickedCategory).open(p, shopCache);
                    } else {
                        clickedCategory.open(p, idx, shopCache);
                    }
                    return;
                }
            } else {
                // Fallback: check default categories only
                for (IShopCategory sc : idx.getCategoryList()) {
                    String n = sc.getName() == null ? "" : sc.getName().toLowerCase();
                    if (n.startsWith("default-") && e.getSlot() == sc.getSlot()) {
                        sc.open(p, idx, shopCache);
                        return;
                    }
                }
            }

            // Quick Buy area clicks
            for (IQuickBuyElement element : cache.getElements()) {
                if (element.getSlot() == e.getSlot()) {
                    if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        cache.setElement(element.getSlot(), (ICategoryContent) null);
                        p.closeInventory();
                        return;
                    }
                    if (element.getCategoryContent().execute(p, shopCache, element.getSlot())) {
                        // Reload the shop page. Needed to recalculate item purchasable
                        (a.getLinkedShop() != null ? a.getLinkedShop() : ShopManager.shop).open(p, cache, false);
                    }
                    return;
                }
            }
        } else if (ShopCategory.getInstance().getCategoryViewers().contains(p.getUniqueId())) {
            e.setCancelled(true);

            // Check if item is null or air (don't process clicks on air)
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() == Material.AIR) return;

            // Use arena-linked shop for buttons
            com.tomkeuper.bedwars.api.shop.IShopIndex idx = a.getLinkedShop() != null ? a.getLinkedShop() : ShopManager.shop;

            // Quick Buy button at top bar → back to index
            if (e.getSlot() == idx.getQuickBuyButton().getSlot()) {
                idx.open(p, cache, false);
                return;
            }

            // Category tabs click in the top bar
            if (idx instanceof ShopIndex) {
                java.util.Map<Integer, IShopCategory> chosen = ((ShopIndex) idx).getResolvedBySlot(a);
                IShopCategory clickedCategory = chosen.get(e.getSlot());
                if (clickedCategory != null) {
                    if (clickedCategory instanceof ShopCategory) {
                        ((ShopCategory) clickedCategory).open(p, shopCache);
                    } else {
                        clickedCategory.open(p, idx, shopCache);
                    }
                    return;
                }
            }

            // Content clicks: only check the selected category’s contents
            int selectedSlot = shopCache.getSelectedCategory();
            IShopCategory selectedCategory = null;
            if (idx instanceof ShopIndex) {
                selectedCategory = ((ShopIndex) idx).getResolvedBySlot(a).get(selectedSlot);
            }
            if (selectedCategory == null) {
                // Fallback: try default categories
                for (IShopCategory sc : idx.getCategoryList()) {
                    String n = sc.getName() == null ? "" : sc.getName().toLowerCase();
                    if (n.startsWith("default-") && sc.getSlot() == selectedSlot) {
                        selectedCategory = sc;
                        break;
                    }
                }
            }
            if (selectedCategory != null) {
                for (ICategoryContent cc : selectedCategory.getCategoryContentList()) {
                    if (cc.getSlot() == e.getSlot()) {
                        if (e.isShiftClick()) {
                            if (!cache.hasCategoryContent(cc)) new com.tomkeuper.bedwars.shop.quickbuy.QuickBuyAdd(p, cc);
                            return;
                        }
                        if (cc.execute(p, shopCache, cc.getSlot())) {
                            if (selectedCategory instanceof ShopCategory) {
                                ((ShopCategory) selectedCategory).open(p, shopCache); // reload page after purchase
                            } else {
                                selectedCategory.open(p, idx, shopCache);
                            }
                        }
                        return;
                    }
                }
            }

        } else if (QuickBuyAdd.getQuickBuyAdds().containsKey(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            boolean add = false;
            for (int i : PlayerQuickBuyCache.quickSlots) {
                if (i == e.getSlot()) {
                    add = true;
                }
            }
            if (!add) return;
            ICategoryContent cc = QuickBuyAdd.getQuickBuyAdds().get(e.getWhoClicked().getUniqueId());
            if (cc != null) {
                cache.setElement(e.getSlot(), cc);
            }
            e.getWhoClicked().closeInventory();
        }
    }

    private boolean checkShops(InventoryClickEvent e, Player p, IArena a, ShopCache shopCache, IPlayerQuickBuyCache cache, boolean overrides) {
        for (IShopCategory sc : ShopManager.shop.getCategoryList()) {
            // Check if the shop name starts with the group name
            if (overrides && !sc.getName().toLowerCase().startsWith(a.getGroup().toLowerCase())) continue;

            if (ShopManager.shop.getQuickBuyButton().getSlot() == e.getSlot()) {
                ShopManager.shop.open(p, cache, false);
                return true;
            }
            if (e.getSlot() == sc.getSlot()) {
                sc.open(p, ShopManager.shop, shopCache);
                return true;
            }
            if (sc.getSlot() != shopCache.getSelectedCategory()) continue;
            for (ICategoryContent cc : sc.getCategoryContentList()) {
                // If we don't check this, the shop will be displayed in all arenas
                // Default category is already checked and thus does not need to be added here
                if (cc.getCategoryIdentifier().toLowerCase().startsWith(a.getGroup().toLowerCase())) {
                    if (checkSlot(e, p, shopCache, cache, cc, sc)) return true;
                }
            }
            for (ICategoryContent cc : sc.getCategoryContentList()) {
                if (checkSlot(e, p, shopCache, cache, cc, sc)) return true;
            }
        }
        return false;
    }

    private boolean checkSlot(InventoryClickEvent e, Player p, ShopCache shopCache, IPlayerQuickBuyCache cache, ICategoryContent cc, IShopCategory sc) {
        if (cc.getSlot() == e.getSlot()) {
            if (e.isShiftClick()) {
                if (cache.hasCategoryContent(cc)) return true;
                new QuickBuyAdd(p, cc);
                return true;
            }
            if (cc.execute(p, shopCache, cc.getSlot())){
                sc.open(p, ShopManager.shop, shopCache); // Reload the shop page. Needed to recalculate item purchasable
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onUpgradableMove(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ShopCache sc = ShopCache.getInstance().getShopCache(p.getUniqueId());
        if (sc == null) return;

        //block moving from hotbar
        if (e.getAction() == HOTBAR_SWAP && e.getClick() == ClickType.NUMBER_KEY) {
            if (e.getHotbarButton() > -1) {
                ItemStack i = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
                if (i != null) {
                    if (e.getClickedInventory() != e.getWhoClicked().getInventory()) {
                        if (shouldCancelMovement(i, sc)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }

        //block moving cursor item
        if (e.getCursor() != null) {
            if (e.getCursor().getType() != Material.AIR) {
                if (e.getClickedInventory() == null) {
                    if (shouldCancelMovement(e.getCursor(), sc)) {
                        e.getWhoClicked().closeInventory();
                        e.setCancelled(true);
                    }
                } else if (e.getClickedInventory().getType() != e.getWhoClicked().getInventory().getType()) {
                    if (shouldCancelMovement(e.getCursor(), sc)) {
                        e.getWhoClicked().closeInventory();
                        e.setCancelled(true);
                    }
                }
            }
        }

        //block moving current item
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getType() != Material.AIR) {
                if (e.getClickedInventory() == null) {
                    if (shouldCancelMovement(e.getCursor(), sc)) {
                        e.getWhoClicked().closeInventory();
                        e.setCancelled(true);
                    }
                } else if (e.getClickedInventory().getType() != e.getWhoClicked().getInventory().getType()) {
                    if (shouldCancelMovement(e.getCurrentItem(), sc)) {
                        e.getWhoClicked().closeInventory();
                        e.setCancelled(true);
                    }
                }
            }
        }

        //block moving with shift
        if (e.getAction() == MOVE_TO_OTHER_INVENTORY) {
            if (shouldCancelMovement(e.getCurrentItem(), sc)) {
                if (e.getView().getTopInventory().getHolder() != null && e.getInventory().getHolder() == e.getWhoClicked())
                    return;
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onShopClose(InventoryCloseEvent e) {
        ShopIndex.indexViewers.remove(e.getPlayer().getUniqueId());
        ShopCategory.categoryViewers.remove(e.getPlayer().getUniqueId());
        QuickBuyAdd.quickBuyAdds.remove(e.getPlayer().getUniqueId());
    }

    /**
     * Check can move item outside inventory.
     * Block despawnable, permanent and start items dropping and inventory change.
     */
    public static boolean shouldCancelMovement(ItemStack i, ShopCache sc) {
        if (i == null) return false;
        if (i.getType() == Material.AIR) return false;
        if (sc == null) return false;

        if (BedWars.nms.isCustomBedWarsItem(i)){
            if (BedWars.nms.getCustomData(i).equalsIgnoreCase("DEFAULT_ITEM")){
                return true;
            }
        }

        String identifier = BedWars.nms.getShopUpgradeIdentifier(i);
        if (identifier == null) return false;
        if (identifier.equals("null")) return false;
        ICachedItem cachedItem = sc.getCachedItem(identifier);
        return cachedItem != null;
        // the commented line bellow was blocking movement only if tiers amount > 1
        // return sc.getCachedItem(identifier).getCc().getContentTiers().size() > 1;
    }
}
