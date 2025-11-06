package com.tomkeuper.bedwars.shop.custom.frezze;

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

public class TimeFreezeEggContent extends CategoryContent {
    private final int slot;

    public TimeFreezeEggContent(IShopCategory father) {
        super(null, null,null, null, father);

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
        setLoaded(slot != 01);
        if (!isLoaded()) return;
        getContentTiers().add(new FreezeEggTier());
    }
    @Override
    public String getIdentifier() {
        return "time-freeze-egg";
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
        ItemStack freezeEgg = new ItemStack(Material.EGG, 1);
        boolean canAffrod = calculateMoney(player, tier.getCurrency()) >= tier.getPrice();
        String transCurrency = getMsg(player,getCurrencyMsgPath(tier));
        String buyStatus;
        if (!canAffrod) {
            buyStatus = getMsg(player, Messages.SHOP_LORE_STATUS_CANT_AFFORD).replace("%bw_currency%", transCurrency);
        } else {
            buyStatus = getMsg(player,Messages.SHOP_LORE_STATUS_CAN_BUY);
        }
        ChatColor cColor = getCurrencyColor(tier.getCurrency());
        String msgPath = Messages.SHOP_PATH + getIdentifier() + ".category-content.time-freeze-egg";
        ItemMeta itemMeta = freezeEgg.getItemMeta();
        String displayName = getMsg(player, msgPath + ".name");
        if (displayName == null || displayName.equals(msgPath + ".name")) {
            displayName = ChatColor.AQUA + "Time Freeze Egg";
        }
        itemMeta.setDisplayName(displayName);
        List<String> lore = new ArrayList<>();
        List<String> msglore = Language.getList(player, msgPath + ".lore");
        if (msglore != null && !msglore.isEmpty()) {
            for (String line : msglore) {
                line = line.replace("%bw_cost%", String.valueOf(tier.getPrice()))
                        .replace("%bw_currency%", transCurrency)
                        .replace("%bw_color%", cColor.toString())
                        .replace("%bw_buy_status%", buyStatus)
                        .replace("%bw_quick_buy%", "");
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        } else {
            lore.add(ChatColor.GRAY + "Freezes all enemies in a 3x3 area");
            lore.add(ChatColor.GRAY + "for 3 seconds. Except the one");
            lore.add(ChatColor.GRAY + "threw it.");
            lore.add(ChatColor.YELLOW + "Disables base Protection");
            lore.add("");
            lore.add(cColor + String.valueOf(tier.getPrice())+ " " + cColor + transCurrency);
            lore.add("");
            lore.add(buyStatus);
        }
        itemMeta.setLore(lore);
        freezeEgg.setItemMeta(itemMeta);
        return freezeEgg;
    }
    @Override
    public ItemStack getItemStack(Player player, IShopCache shopCache){
        return getItemStack(player);
    }
    private static class FreezeEggTier implements IContentTier {
        @Override
        public int getPrice() {
            return  8;
        }
        @Override
        public Material getCurrency(){
            return Material.GOLD_INGOT;
        }
        @Override
        public void setCurrency(Material currency) {}
        @Override
        public void setPrice(int price) {}
        @Override
        public void setItemStack(ItemStack itemStack) {}
        @Override
        public void setBuyItemsList(List<IBuyItem> buyItemsList) {}
        @Override
        public ItemStack getItemStack(){
            return new ItemStack(Material.EGG, 1);
        }
        @Override
        public int getValue() {
            return 8;
        }
        @Override
        public List<IBuyItem> getBuyItemsList(){
            return Collections.singletonList(new FreezeEggItem());
        }
    }
    private static class FreezeEggItem implements IBuyItem {
        @Override
        public boolean isLoaded(){
           return true;
        }

        @Override
        public void give(Player player, IArena arena) {
            ItemStack egg = new ItemStack(Material.EGG, 1);
            ItemMeta itemMeta = egg.getItemMeta();
            itemMeta.setDisplayName(ChatColor.AQUA + "TimeFreezeEgg");
            itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Throw to freeze enemies in 3x3 area!"));
            egg.setItemMeta(itemMeta);
            player.getInventory().addItem(egg);
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
        public void setItemStack(ItemStack itemStack) {}
        @Override
        public boolean isAutoEquip() {return false;}
        @Override
        public void setAutoEquip(boolean autoEquip) {}
        @Override
        public boolean isPermanent() {return false;}
        @Override
        public void setPermanent(boolean permanent){}
        @Override
        public boolean isUnbreakable() {return false;}
        @Override
        public void  setUnbreakable(boolean unbreakable) {}
    }
}
