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

package com.tomkeuper.bedwars.arena;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.configuration.Sounds;
import com.tomkeuper.bedwars.listeners.arenaselector.ArenaSelectorListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArenaGUI {

    private static final YamlConfiguration yml = BedWars.config.getYml();

    private static final HashMap<UUID, Long> antiCalledTwice = new HashMap<>();

    public static void refreshInv(Player player, IArena arena, int players) {
        if (player == null || player.getOpenInventory() == null || !(player.getOpenInventory().getTopInventory().getHolder() instanceof ArenaSelectorHolder)) {
            return;
        }
        ArenaSelectorHolder arenaSelectorHolder = ((ArenaSelectorHolder) player.getOpenInventory().getTopInventory().getHolder());

        List<IArena> arenas;
        if (arenaSelectorHolder.getGroup().equalsIgnoreCase("default")) {
            arenas = new ArrayList<>(Arena.getArenas());
        } else {
            arenas = new ArrayList<>();
            for (IArena a : Arena.getArenas()) {
                if (a.getGroup().equalsIgnoreCase(arenaSelectorHolder.getGroup())) arenas.add(a);
            }
        }

        arenas = Arena.getSorted(arenas);

        List<Integer> usedSlots = getUsedSlots();
        for (int i = 0; i < usedSlots.size() && i < arenas.size(); i++) {
            IArena currentArena = arenas.get(i);
            String status = currentArena.getStatus().toString().toLowerCase();

            ItemStack item;

            item = BedWars.nms.createItemStack(yml.getString(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", status)),
                    1, (short) yml.getInt(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", status)));
            if (yml.getBoolean(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", status))) {
                ItemMeta im = item.getItemMeta();
                im.addEnchant(Enchantment.LURE, 1, true);
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(im);
            }


            ItemMeta im = item.getItemMeta();
            im.setDisplayName(Language.getMsg(player, Messages.ARENA_GUI_ARENA_CONTENT_NAME).replace("%bw_name%", arenas.get(i).getDisplayName()).replace("%bw_map_name%", arenas.get(i).getArenaName()));
            List<String> lore = new ArrayList<>();
            for (String loreLine : Language.getList(player, Messages.ARENA_GUI_ARENA_CONTENT_LORE)) {
                if (!(loreLine.contains("%bw_group%") && currentArena.getGroup().equalsIgnoreCase("default"))) {
                    String arenaStatus = currentArena.getDisplayStatus(Language.getPlayerLanguage(player));
                    String arenaGroup = currentArena.getDisplayGroup(player);
                    int currentPlayers = (arena != null && arena == currentArena) ? players : currentArena.getPlayers().size();
                    lore.add(loreLine
                            .replace("%bw_on%", String.valueOf(currentPlayers))
                            .replace("%bw_max%", String.valueOf(currentArena.getMaxPlayers()))
                            .replace("%bw_arena_status%", arenaStatus)
                            .replace("%bw_group%", arenaGroup)
                    );
                }
            }
            im.setLore(lore);
            item.setItemMeta(im);
            item = BedWars.nms.addCustomData(item, ArenaSelectorListener.ARENA_SELECTOR_GUI_IDENTIFIER + currentArena.getArenaName());

            int slot = usedSlots.get(i);
            player.getOpenInventory().getTopInventory().setItem(slot, item);
        }
        player.updateInventory();
    }

    public static void openGui(Player player, String group) {
        if (preventCalledTwice(player)) return;
        updateCalledTwice(player);

        int size = BedWars.config.getYml().getInt(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SIZE);
        if (size % 9 != 0) size = 27; // Ensure size is a multiple of 9 otherwise set to 27
        if (size > 54) size = 54; // Limit size to maximum 54
        ArenaSelectorHolder arenaSelectorHolder = new ArenaSelectorHolder(group);
        Inventory inventory = Bukkit.createInventory(arenaSelectorHolder, size, Language.getMsg(player, Messages.ARENA_GUI_INV_NAME));

        String skippedSlotMaterial = BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "skipped-slot"));
        if (!skippedSlotMaterial.equalsIgnoreCase("none") && !skippedSlotMaterial.equalsIgnoreCase("air")) {
            ItemStack skippedSlotItem = BedWars.nms.createItemStack(
                    skippedSlotMaterial,
                    1,
                    (byte) BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "skipped-slot"))
            );
            skippedSlotItem = BedWars.nms.addCustomData(skippedSlotItem, "RUNCOMMAND_bw join random");
            ItemMeta itemMeta = skippedSlotItem.getItemMeta();
            if (itemMeta != null) {
                String translatedDisplayName = ChatColor.translateAlternateColorCodes(
                        '&',
                        Language.getMsg(player, Messages.ARENA_GUI_SKIPPED_ITEM_NAME)
                                .replaceAll("%bw_server_ip%", BedWars.config.getString(ConfigPath.GENERAL_CONFIG_PLACEHOLDERS_REPLACEMENTS_SERVER_IP))
                );
                itemMeta.setDisplayName(translatedDisplayName);

                List<String> lore = new ArrayList<>();
                for (String loreLine : Language.getList(player, Messages.ARENA_GUI_SKIPPED_ITEM_LORE)) {
                    lore.add(loreLine.replaceAll("%bw_server_ip%", BedWars.config.getString(ConfigPath.GENERAL_CONFIG_PLACEHOLDERS_REPLACEMENTS_SERVER_IP)));
                }
                if (!lore.isEmpty()) {
                    itemMeta.setLore(lore);
                }
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                skippedSlotItem.setItemMeta(itemMeta);

                List<Integer> usedSlots = getUsedSlots();
                for (int x = 0; x < inventory.getSize(); x++) {
                    if (!usedSlots.contains(x)) {
                        inventory.setItem(x, skippedSlotItem);
                    }
                }
            }
        }

        player.openInventory(inventory);
        refreshInv(player, null, 0);
        Sounds.playSound("arena-selector-open", player);
    }

    public static class ArenaSelectorHolder implements InventoryHolder {

        private final String group;

        public ArenaSelectorHolder(String group){
            this.group = group;
        }

        public String getGroup() {
            return group;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }

    }

    @NotNull
    private static List<Integer> getUsedSlots() {
        List<Integer> ls = new ArrayList<>();
        for (String useSlot : BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_USE_SLOTS).split(",")) {
            try {
                int slot = Integer.parseInt(useSlot);
                ls.add(slot);
            } catch (Exception ignored) {
            }
        }
        return ls;
    }

    private static boolean preventCalledTwice(@NotNull Player player) {
        return antiCalledTwice.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
    }

    private static void updateCalledTwice(@NotNull Player player) {
        if (antiCalledTwice.containsKey(player.getUniqueId())) {
            antiCalledTwice.replace(player.getUniqueId(), System.currentTimeMillis() + 2000);
        } else {
            antiCalledTwice.put(player.getUniqueId(), System.currentTimeMillis() + 2000);
        }
    }
}
