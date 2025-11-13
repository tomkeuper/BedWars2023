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
import com.tomkeuper.bedwars.api.arena.shop.IBuyItem;
import com.tomkeuper.bedwars.api.arena.team.TeamEnchant;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.configuration.Sounds;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
@SuppressWarnings("WeakerAccess")
public class BuyItem implements IBuyItem {

    @Setter
    private ItemStack itemStack;
    @Setter
    private boolean autoEquip = false;
    @Setter
    private boolean permanent = false;
    @Setter
    private boolean unbreakable = false;
    /**
     * -- GETTER --
     *  Check if object created properly
     */
    private boolean loaded = false;
    /**
     * -- GETTER --
     *  Get upgrade identifier.
     *  Used to remove old tier items.
     */
    private final String upgradeIdentifier;

    /**
     * Create a shop item
     */
    public BuyItem(String path, YamlConfiguration yml, String upgradeIdentifier, ContentTier parent) {
        BedWars.debug("Loading BuyItems: " + path);
        this.upgradeIdentifier = upgradeIdentifier;

        if (yml.get(path + ".material") == null) {
            BedWars.plugin.getLogger().severe("BuyItem: Material not set at " + path);
            return;
        }

        itemStack = BedWars.nms.createItemStack(yml.getString(path + ".material"),
                yml.get(path + ".amount") == null ? 1 : yml.getInt(path + ".amount"),
                (short) (yml.get(path + ".data") == null ? 1 : yml.getInt(path + ".data")));

        if (yml.get(path + ".name") != null) {
            ItemMeta im = itemStack.getItemMeta();
            if (im != null) {
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&r"+yml.getString(path + ".name")));
                itemStack.setItemMeta(im);
            }
        }

        if (yml.get(path + ".enchants") != null && itemStack.getItemMeta() != null) {
            ItemMeta imm = itemStack.getItemMeta();
            String[] enchant = yml.getString(path + ".enchants").split(",");
            for (String enc : enchant) {
                String[] stuff = enc.split(" ");
                try {
                    Enchantment.getByName(stuff[0]);
                } catch (Exception ex) {
                    BedWars.plugin.getLogger().severe("BuyItem: Invalid enchants " + stuff[0] + " at: " + path + ".enchants");
                    continue;
                }
                int ieee = 1;
                if (stuff.length >= 2) {
                    try {
                        ieee = Integer.parseInt(stuff[1]);
                    } catch (Exception exx) {
                        BedWars.plugin.getLogger().severe("BuyItem: Invalid int " + stuff[1] + " at: " + path + ".enchants");
                        continue;
                    }
                }
                imm.addEnchant(Enchantment.getByName(stuff[0]), ieee, true);
            }
            itemStack.setItemMeta(imm);
        }

        if (yml.get(path + ".potion") != null && (itemStack.getType() == Material.POTION)) {
            // 1.16+ custom color
            if (yml.getString(path + ".potion-color") != null && !yml.getString(path + ".potion-color").isEmpty()) {
                itemStack = BedWars.nms.setTag(itemStack, "CustomPotionColor", yml.getString(path + ".potion-color"));
            }
            PotionMeta imm = (PotionMeta) itemStack.getItemMeta();
            if (imm != null) {
                String[] enchant = yml.getString(path + ".potion").split(",");
                for (String enc : enchant) {
                    String[] stuff = enc.split(" ");
                    try {
                        PotionEffectType.getByName(stuff[0].toUpperCase());
                    } catch (Exception ex) {
                        BedWars.plugin.getLogger().severe("BuyItem: Invalid potion effect " + stuff[0] + " at: " + path + ".potion");
                        continue;
                    }
                    int duration = 50, amplifier = 1;
                    if (stuff.length >= 3) {
                        try {
                            duration = Integer.parseInt(stuff[1]);
                        } catch (Exception exx) {
                            BedWars.plugin.getLogger().severe("BuyItem: Invalid int (duration) " + stuff[1] + " at: " + path + ".potion");
                            continue;
                        }
                        try {
                            amplifier = Integer.parseInt(stuff[2]);
                        } catch (Exception exx) {
                            BedWars.plugin.getLogger().severe("BuyItem: Invalid int (amplifier) " + stuff[2] + " at: " + path + ".potion");
                            continue;
                        }
                    }
                    imm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(stuff[0].toUpperCase()), duration * 20, amplifier), true);
                }
                itemStack.setItemMeta(imm);
            }

            itemStack = BedWars.nms.setTag(itemStack, "Potion", "minecraft:water");
            if (parent.getItemStack().getType() == Material.POTION && imm != null && !imm.getCustomEffects().isEmpty()) {
                ItemStack parentItemStack = parent.getItemStack();
                if (parentItemStack.getItemMeta() != null) {
                    PotionMeta potionMeta = (PotionMeta) parentItemStack.getItemMeta();
                    for (PotionEffect potionEffect : imm.getCustomEffects()) {
                        potionMeta.addCustomEffect(potionEffect, true);
                    }
                    parentItemStack.setItemMeta(potionMeta);
                }
                parentItemStack = BedWars.nms.setTag(parentItemStack, "Potion", "minecraft:water");
                parent.setItemStack(parentItemStack);
            }
        }

        if (yml.get(path + ".auto-equip") != null) {
            autoEquip = yml.getBoolean(path + ".auto-equip");
        }
        // Resolve content root from the current buy-item path (e.g., ...category-content.<name>.tiers.<tier>.buy-items.<item>)
        String contentRoot = path;
        int idx = contentRoot.indexOf(".tiers.");
        if (idx > 0) {
            contentRoot = contentRoot.substring(0, idx);
        }
        // Preferred: read flags relative to content root, with legacy fallback using the identifier path
        if (yml.get(contentRoot + "." + ConfigPath.SHOP_CATEGORY_CONTENT_IS_PERMANENT) != null) {
            permanent = yml.getBoolean(contentRoot + "." + ConfigPath.SHOP_CATEGORY_CONTENT_IS_PERMANENT);
        } else if (yml.get(upgradeIdentifier + "." + ConfigPath.SHOP_CATEGORY_CONTENT_IS_PERMANENT) != null) {
            // Backward compatibility: old loaders used the (un)scoped identifier as a YAML path
            permanent = yml.getBoolean(upgradeIdentifier + "." + ConfigPath.SHOP_CATEGORY_CONTENT_IS_PERMANENT);
        }
        if (yml.get(contentRoot + "." + ConfigPath.SHOP_CATEGORY_CONTENT_IS_UNBREAKABLE) != null) {
            unbreakable = yml.getBoolean(contentRoot + "." + ConfigPath.SHOP_CATEGORY_CONTENT_IS_UNBREAKABLE);
        } else if (yml.get(upgradeIdentifier + "." + ConfigPath.SHOP_CATEGORY_CONTENT_IS_UNBREAKABLE) != null) {
            // Backward compatibility
            unbreakable = yml.getBoolean(upgradeIdentifier + "." + ConfigPath.SHOP_CATEGORY_CONTENT_IS_UNBREAKABLE);
        }

        loaded = true;
    }

    /**
     * Give to a player
     */
    public void give(Player player, IArena arena) {

        ItemStack i = itemStack.clone();
        BedWars.debug("Giving BuyItem: " + getUpgradeIdentifier() + " to: " + player.getName());

        if (autoEquip && BedWars.nms.isArmor(itemStack)) {
            Material m = i.getType();

            ItemMeta im = i.getItemMeta();
            if (arena.getTeam(player) == null) {
                BedWars.debug("Could not give BuyItem to " + player.getName() + " - TEAM IS NULL");
                return;
            }
            if (im != null) {
                for (TeamEnchant e : arena.getTeam(player).getArmorsEnchantments()) {
                    im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                }
                if (permanent) BedWars.nms.setUnbreakable(im);
                i.setItemMeta(im);
            }

            if (m == Material.LEATHER_HELMET || m == Material.CHAINMAIL_HELMET || m == Material.IRON_HELMET || m == Material.DIAMOND_HELMET || m == BedWars.nms.materialGoldenHelmet() || m == BedWars.nms.materialNetheriteHelmet()) {
                if (permanent) i = BedWars.nms.setShopUpgradeIdentifier(i, upgradeIdentifier);
                player.getInventory().setHelmet(i);
            } else if (m == Material.LEATHER_CHESTPLATE || m == Material.CHAINMAIL_CHESTPLATE || m == Material.IRON_CHESTPLATE || m == Material.DIAMOND_CHESTPLATE || m == BedWars.nms.materialGoldenChestPlate() || m == BedWars.nms.materialNetheriteChestPlate() || m == BedWars.nms.materialElytra()) {
                if (permanent) i = BedWars.nms.setShopUpgradeIdentifier(i, upgradeIdentifier);
                player.getInventory().setChestplate(i);
            } else if (m == Material.LEATHER_LEGGINGS || m == Material.CHAINMAIL_LEGGINGS || m == Material.IRON_LEGGINGS  || m == Material.DIAMOND_LEGGINGS || m == BedWars.nms.materialGoldenLeggings()|| m == BedWars.nms.materialNetheriteLeggings()) {
                if (permanent) i = BedWars.nms.setShopUpgradeIdentifier(i, upgradeIdentifier);
                player.getInventory().setLeggings(i);
            } else {
                if (permanent) i = BedWars.nms.setShopUpgradeIdentifier(i, upgradeIdentifier);
                player.getInventory().setBoots(i);
            }
            player.updateInventory();
            Sounds.playSound("shop-auto-equip", player);

            Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
                // #274
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    for (Player p : arena.getPlayers()) {
                        BedWars.nms.hideArmor(player, p);
                    }
                }
                //
            }, 20L);
            return;
        } else {

            ItemMeta im = i.getItemMeta();
            // Attempt to color the item based on team; if colouring fails, keep the original item
            ItemStack original = i.clone();
            ItemStack coloured = null;
            try {
                if (arena.getTeam(player) != null) {
                    coloured = BedWars.nms.colourItem(i, arena.getTeam(player));
                } else {
                    BedWars.debug("Skipping colourItem for " + player.getName() + ": team is null");
                }
            } catch (Throwable t) {
                BedWars.debug("colourItem error for " + player.getName() + ": " + t.getMessage());
            }
            if (coloured != null && coloured.getType() != Material.AIR) {
                i = coloured;
            } else {
                BedWars.debug("colourItem returned null/AIR for " + player.getName() + ". Using original item: " + original.getType());
                i = original;
            }
            if (im != null) {
                if (permanent) BedWars.nms.setUnbreakable(im);
                if (unbreakable) BedWars.nms.setUnbreakable(im);
                if (i.getType() == Material.BOW) {
                    if (permanent) BedWars.nms.setUnbreakable(im);
                    if (arena.getTeam(player) != null) {
                        for (TeamEnchant e : arena.getTeam(player).getBowsEnchantments()) {
                            im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                        }
                    }
                } else if (BedWars.nms.isSword(i) || BedWars.nms.isAxe(i)) {
                    if (arena.getTeam(player) != null) {
                        for (TeamEnchant e : arena.getTeam(player).getSwordsEnchantments()) {
                            im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                        }
                    }
                }
                i.setItemMeta(im);
            }

            if (permanent) {
                i = BedWars.nms.setShopUpgradeIdentifier(i, upgradeIdentifier);
            }
        }

        // Extra debug info before adding to inventory
        try {
            BedWars.debug ("About to add item: type=" + i.getType() + ", amount=" + i.getAmount() + ", firstEmpty=" + player.getInventory().firstEmpty());
        } catch (Throwable ignored) {}

        //Remove swords with lower damage
        if (BedWars.nms.isSword(i)) {
            for (ItemStack itm : player.getInventory().getContents()) {
                if (itm == null) continue;
                if (itm.getType() == Material.AIR) continue;
                if (!BedWars.nms.isSword(itm)) continue;
                if (itm == i) continue;
                if (BedWars.nms.isCustomBedWarsItem(itm) && BedWars.nms.getCustomData(itm).equals("DEFAULT_ITEM")) {
                    if (BedWars.nms.getDamage(itm) <= BedWars.nms.getDamage(i)) {
                        player.getInventory().remove(itm);
                    }
                }
            }
        }
        //
        if (i != null && i.getType() != Material.AIR) {
            player.getInventory().addItem(i);
            player.updateInventory();
        } else {
            BedWars.debug("Attempted to give AIR/null item to " + player.getName() + " for upgrade: " + getUpgradeIdentifier());
        }
    }


}
