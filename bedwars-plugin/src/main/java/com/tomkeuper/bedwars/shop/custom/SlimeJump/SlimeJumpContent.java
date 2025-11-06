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

package com.tomkeuper.bedwars.shop.custom.SlimeJump;

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
import java.util.Collections;
import java.util.List;

import static com.tomkeuper.bedwars.api.language.Language.getMsg;

/**
 * Slime Jump - A slime block that launches players instantly or slowly
 * Can be transferred between Base and Gold 3
 */
public class SlimeJumpContent extends CategoryContent {

    private final int slot;

    public SlimeJumpContent(IShopCategory father) {
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


        getContentTiers().add(new BaseTier());
        getContentTiers().add(new GoldTier());
    }

    @Override
    public String getIdentifier() {
        return "slime-jump";
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
        ItemStack slimeBlock = new ItemStack(Material.SLIME_BLOCK, 1);

        boolean canAfford = calculateMoney(player, tier.getCurrency()) >= tier.getPrice();
        String translatedCurrency = getMsg(player, getCurrencyMsgPath(tier));

        String buyStatus;
        if (!canAfford) {
            buyStatus = getMsg(player, Messages.SHOP_LORE_STATUS_CANT_AFFORD).replace("%bw_currency%", translatedCurrency);
        } else {
            buyStatus = getMsg(player, Messages.SHOP_LORE_STATUS_CAN_BUY);
        }

        ChatColor cColor = getCurrencyColor(tier.getCurrency());
        String msgPath = Messages.SHOP_PATH + getIdentifier() + ".category-content.slime-jump";
        ItemMeta itemMeta = slimeBlock.getItemMeta();
        String displayName = getMsg(player, msgPath + ".name");
        if (displayName == null || displayName.equals(msgPath + ".name")) {
            displayName = ChatColor.GREEN + "Slime Jump";
        }
        itemMeta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();
        List<String> msgLore = Language.getList(player, msgPath + ".lore");

        if (msgLore != null && !msgLore.isEmpty()) {
            for (String line : msgLore) {
                line = line.replace("%bw_cost%", String.valueOf(tier.getPrice()))
                        .replace("%bw_currency%", translatedCurrency)
                        .replace("%bw_color%", cColor.toString())
                        .replace("%bw_buy_status%", buyStatus)
                        .replace("%bw_tier%", tier.getValue() == 1 ? "Base" : "Gold 3")
                        .replace("%bw_quick_buy%", "");
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        } else {
            if (tier.getValue() == 1) {
                lore.add(ChatColor.GRAY + "Launch players instantly!");
            } else {
                lore.add(ChatColor.GRAY + "Launch players slowly or high!");
            }
            lore.add(ChatColor.YELLOW + "⚠ One use only - disappears after use");
            lore.add("");
            lore.add(cColor + String.valueOf(tier.getPrice()) + " " + cColor + translatedCurrency);
            lore.add("");
            lore.add(buyStatus);
        }

        itemMeta.setLore(lore);
        slimeBlock.setItemMeta(itemMeta);
        return slimeBlock;
    }

    @Override
    public ItemStack getItemStack(Player player, IShopCache shopCache) {
        return getItemStack(player);
    }

    private static class BaseTier implements IContentTier {

        @Override
        public int getPrice() {
            return 0;
        }

        @Override
        public Material getCurrency() {
            return Material.AIR;
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
            return new ItemStack(Material.SLIME_BLOCK, 1);
        }

        @Override
        public int getValue() {
            return 1;
        }

        @Override
        public List<IBuyItem> getBuyItemsList() {
            return Collections.singletonList(new SlimeJumpItem("base"));
        }
    }
    private static class GoldTier implements IContentTier {

        @Override
        public int getPrice() {
            return 3;
        }

        @Override
        public Material getCurrency() {
            return Material.GOLD_INGOT;
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
            ItemStack slime = new ItemStack(Material.SLIME_BLOCK, 1);
            ItemMeta meta = slime.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Slime Jump " + ChatColor.GOLD + "(Gold 3)");
            slime.setItemMeta(meta);
            return slime;
        }

        @Override
        public int getValue() {
            return 2;
        }

        @Override
        public List<IBuyItem> getBuyItemsList() {
            return Collections.singletonList(new SlimeJumpItem("gold"));
        }
    }

    private static class SlimeJumpItem implements IBuyItem {

        private final String tier;

        public SlimeJumpItem(String tier) {
            this.tier = tier;
        }

        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public void give(Player player, IArena arena) {
            ItemStack slime = new ItemStack(Material.SLIME_BLOCK, 1);
            ItemMeta meta = slime.getItemMeta();

            if (tier.equals("base")) {
                meta.setDisplayName(ChatColor.GREEN + "Slime Jump " + ChatColor.GRAY + "(Instant)");
                meta.setLore(Collections.singletonList(ChatColor.GRAY + "Place to launch instantly! One use only."));
            } else {
                meta.setDisplayName(ChatColor.GREEN + "Slime Jump " + ChatColor.GOLD + "(Slow/High)");
                meta.setLore(Collections.singletonList(ChatColor.GRAY + "Place to launch slowly or high! One use only."));
            }

            slime.setItemMeta(meta);
            player.getInventory().addItem(slime);
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