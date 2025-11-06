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

package com.tomkeuper.bedwars.shop.custom;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.shop.IBuyItem;
import com.tomkeuper.bedwars.api.arena.shop.IContentTier;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.shop.IShopCache;
import com.tomkeuper.bedwars.api.shop.IShopCategory;
import com.tomkeuper.bedwars.shop.main.CategoryContent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tomkeuper.bedwars.api.language.Language.getMsg;

/**
 * Potato Bomb - A throwable explosive item
 * Can be purchased from the shop and explodes on impact
 */
public class PotatoBombContent extends CategoryContent {

    private final int slot;

    public PotatoBombContent(IShopCategory father) {
        super(null, null, null, null, father);

        int foundSlot = -1;
        for (int i = 19; i < 26; i++) {
            int finalI = i;
            if (father.getCategoryContentList().stream().noneMatch(categoryContent -> categoryContent.getSlot() == finalI)) {
                foundSlot = i;
                break;
            }
        }
        if (foundSlot == -1) {
            for (int i = 28; i < 35; i++) {
                int finalI = i;
                if (father.getCategoryContentList().stream().noneMatch(categoryContent -> categoryContent.getSlot() == finalI)) {
                    foundSlot = i;
                    break;
                }
            }
        }
        if (foundSlot == -1) {
            for (int i = 37; i < 44; i++) {
                int finalI = i;
                if (father.getCategoryContentList().stream().noneMatch(categoryContent -> categoryContent.getSlot() == finalI)) {
                    foundSlot = i;
                    break;
                }
            }
        }

        this.slot = foundSlot;
        setLoaded(slot != -1);
        if (!isLoaded()) return;
        PotatoBombTier tier = new PotatoBombTier();
        getContentTiers().add(tier);
    }

    @Override
    public String getIdentifier() {
        return "potato-bomb";
    }

    @Override
    public String getCategoryIdentifier() {
        return "default-" + getIdentifier();
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public boolean isPermanent() {
        return false;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        IContentTier tier = getContentTiers().get(0);
        ItemStack potatoBomb = new ItemStack(Material.POTATO_ITEM, 1);

        IArena arena = com.tomkeuper.bedwars.arena.Arena.getArenaByPlayer(player);
        boolean alreadyPurchased = arena != null && PotatoBombListener.hasPurchased(player, arena);
        boolean canAfford = calculateMoney(player, tier.getCurrency()) >= tier.getPrice();
        String translatedCurrency = getMsg(player, getCurrencyMsgPath(tier));

        String buyStatus;

        if (alreadyPurchased) {
            buyStatus = ChatColor.RED + "Already purchased this match!";
        } else if (!canAfford) {
            buyStatus = getMsg(player, Messages.SHOP_LORE_STATUS_CANT_AFFORD).replace("%bw_currency%", translatedCurrency);
        } else {
            buyStatus = getMsg(player, Messages.SHOP_LORE_STATUS_CAN_BUY);
        }

        ChatColor cColor = getCurrencyColor(tier.getCurrency());

        // Get message path for potato-bomb
        String msgPath = Messages.SHOP_PATH + getIdentifier() + ".category-content.potato-bomb";

        ItemMeta itemMeta = potatoBomb.getItemMeta();

        // Try to get display name from messages, fallback to default
        String displayName = getMsg(player, msgPath + ".name");
        if (displayName == null || displayName.equals(msgPath + ".name")) {
            displayName = ChatColor.GOLD + "Potato Bomb";
        }
        itemMeta.setDisplayName(displayName);

        // Get lore from messages
        List<String> lore = new ArrayList<>();
        List<String> msgLore = Language.getList(player, msgPath + ".lore");

        if (msgLore != null && !msgLore.isEmpty()) {
            for (String line : msgLore) {
                line = line.replace("%bw_cost%", String.valueOf(tier.getPrice()))
                        .replace("%bw_currency%", translatedCurrency)
                        .replace("%bw_color%", cColor.toString())
                        .replace("%bw_buy_status%", buyStatus)
                        .replace("%bw_quick_buy%", "");
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        } else {
            // Fallback lore if messages not found
            lore.add(ChatColor.GRAY + "Tracks the nearest enemy player");
            lore.add(ChatColor.GRAY + "and deals continuous damage until");
            lore.add(ChatColor.GRAY + "they are eliminated!");
            lore.add(ChatColor.YELLOW + "⚠ One purchase per match");
            lore.add("");
            lore.add(cColor + String.valueOf(tier.getPrice()) + " " + cColor + translatedCurrency);
            lore.add("");
            lore.add(buyStatus);
        }

        itemMeta.setLore(lore);
        potatoBomb.setItemMeta(itemMeta);
        return potatoBomb;
    }

    @Override
    public ItemStack getItemStack(Player player, IShopCache shopCache) {
        return getItemStack(player);
    }

    public boolean canBuy(Player player, IShopCache shopCache) {
        IArena arena = com.tomkeuper.bedwars.arena.Arena.getArenaByPlayer(player);
        if (arena == null) return false;

        // Check if already purchased
        if (PotatoBombListener.hasPurchased(player, arena)) {
            return false;
        }

        // Check if can afford
        IContentTier tier = getContentTiers().get(0);
        return calculateMoney(player, tier.getCurrency()) >= tier.getPrice();
    }

    private static class PotatoBombTier implements IContentTier {

        @Override
        public int getPrice() {
            return 4;
        }

        @Override
        public Material getCurrency() {
            return Material.EMERALD;
        }

        @Override
        public void setCurrency(Material currency) {
        }

        @Override
        public void setPrice(int price) {
        }

        @Override
        public void setItemStack(ItemStack itemStack) {
        }

        @Override
        public void setBuyItemsList(List<IBuyItem> buyItemsList) {
        }

        @Override
        public ItemStack getItemStack() {
            ItemStack potato = new ItemStack(Material.POTATO_ITEM, 1);
            ItemMeta meta = potato.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Potato Bomb");
            potato.setItemMeta(meta);
            return potato;
        }

        @Override
        public int getValue() {
            return 4;
        }

        @Override
        public List<IBuyItem> getBuyItemsList() {
            return Collections.singletonList(new PotatoBombItem());
        }
    }

    private static class PotatoBombItem implements IBuyItem {

        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public void give(Player player, IArena arena) {
            // Check if already purchased
            if (PotatoBombListener.hasPurchased(player, arena)) {
                player.sendMessage(ChatColor.RED + "You can only purchase one Potato Bomb per match!");
                return;
            }

            ItemStack potato = new ItemStack(Material.POTATO_ITEM, 1);
            ItemMeta meta = potato.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Potato Bomb");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Drop to activate tracking!",
                    ChatColor.YELLOW + "Tracks nearest enemy"
            ));
            potato.setItemMeta(meta);
            player.getInventory().addItem(potato);

            // Mark as purchased
            PotatoBombListener.markPurchased(player, arena);
            player.sendMessage(ChatColor.GREEN + "Potato Bomb purchased! Drop it to activate tracking.");
        }

        @Override
        public String getUpgradeIdentifier() {
            return null;
        }

        @Override
        public ItemStack getItemStack() {
            return null;
        }

        @Override
        public void setItemStack(ItemStack itemStack) {
        }

        @Override
        public boolean isAutoEquip() {
            return false;
        }

        @Override
        public void setAutoEquip(boolean autoEquip) {
        }

        @Override
        public boolean isPermanent() {
            return false;
        }

        @Override
        public void setPermanent(boolean permanent) {
        }

        @Override
        public boolean isUnbreakable() {
            return false;
        }

        @Override
        public void setUnbreakable(boolean unbreakable) {
        }
    }
}