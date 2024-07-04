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

package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.TeamEnchant;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.server.ServerType;
import com.tomkeuper.bedwars.api.server.SetupType;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.Misc;
import com.tomkeuper.bedwars.arena.SetupSession;
import com.tomkeuper.bedwars.commands.bedwars.subcmds.regular.CmdLeave;
import com.tomkeuper.bedwars.shop.main.ShopCategory;
import com.tomkeuper.bedwars.shop.main.ShopIndex;
import com.tomkeuper.bedwars.support.version.common.VersionCommon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

import static com.tomkeuper.bedwars.BedWars.nms;
import static org.bukkit.event.inventory.InventoryAction.HOTBAR_SWAP;
import static org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY;

public class Inventory implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (nms.getInventoryName(e).equalsIgnoreCase(SetupSession.getInvName())) {
            SetupSession ss = SetupSession.getSession(p.getUniqueId());
            if (ss != null) {
                if (ss.getSetupType() == null)
                    ss.cancel();
            }
        }
    }

    /**
     * Manage command-items when clicked in inventory
     */
    @EventHandler
    public void onCommandItemClick(InventoryClickEvent e) {
        //block moving from hotBar
        if (e.getAction() == HOTBAR_SWAP && e.getClick() == ClickType.NUMBER_KEY) {
            if (e.getHotbarButton() > -1) {
                ItemStack i = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
                if (i != null) {
                    if (isCommandItem(i)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }

        //block moving cursor item outside
        if (e.getCursor() != null) {
            if (e.getCursor().getType() != Material.AIR) {
                if (e.getClickedInventory() == null) {
                    if (isCommandItem(e.getCursor())) {
                        e.getWhoClicked().closeInventory();
                        e.setCancelled(true);
                    }
                } else if (e.getClickedInventory().getType() != e.getWhoClicked().getInventory().getType()) {
                    if (isCommandItem(e.getCursor())) {
                        e.getWhoClicked().closeInventory();
                        e.setCancelled(true);
                    }
                } else {
                    if (isCommandItem(e.getCursor())) e.setCancelled(true);
                }
            }
        }

        //block moving current item outside
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getType() != Material.AIR) {
                if (e.getClickedInventory() == null) {
                    if (isCommandItem(e.getCurrentItem())) {
                        e.getWhoClicked().closeInventory();
                        e.setCancelled(true);
                    }
                } else if (e.getClickedInventory().getType() != e.getWhoClicked().getInventory().getType()) {
                    if (isCommandItem(e.getCurrentItem())) {
                        e.getWhoClicked().closeInventory();
                        e.setCancelled(true);
                    }
                } else {
                    if (isCommandItem(e.getCurrentItem())) e.setCancelled(true);
                }
            }
        }

        //block moving with shift
        if (e.getAction() == MOVE_TO_OTHER_INVENTORY) {
            if (isCommandItem(e.getCurrentItem())) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        //issue #225
        if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
            if (Arena.getArenaByPlayer((Player) e.getWhoClicked()) != null) {
                if (e.getWhoClicked().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    e.getWhoClicked().closeInventory();
                    for (Player pl : e.getWhoClicked().getWorld().getPlayers()) {
                        BedWars.nms.hideArmor((Player) e.getWhoClicked(), pl);
                    }
                }
            }
        }

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();

        IArena a = Arena.getArenaByPlayer(p);
        if (a != null) {
            /* Make it so they can't toggle their armor */
            if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
                e.setCancelled(true);
                return;
            }
        }

        //Prevent players from moving items in stats GUI
        if (nms.getInventoryName(e).equals(Language.getMsg(p, Messages.PLAYER_STATS_GUI_INV_NAME).replace("%bw_playername%", p.getName()).replace("%bw_player%", p.getDisplayName()))) {
            e.setCancelled(true);
            return;
        }

        if (!i.hasItemMeta()) return;
        if (!i.getItemMeta().hasDisplayName()) return;
        if (BedWars.getServerType() == ServerType.MULTIARENA) {
            if (e.getWhoClicked().getLocation().getWorld().getName().equalsIgnoreCase(BedWars.getLobbyWorld())) {
                e.setCancelled(true);
            }
        }

        /* Check setup gui items */
        if (SetupSession.isInSetupSession(p.getUniqueId()) && nms.getInventoryName(e).equalsIgnoreCase(SetupSession.getInvName())) {
            SetupSession ss = SetupSession.getSession(p.getUniqueId());
            if (e.getSlot() == SetupSession.getAdvancedSlot()) {
                Objects.requireNonNull(ss).setSetupType(SetupType.ADVANCED);
            } else if (e.getSlot() == SetupSession.getAssistedSlot()) {
                Objects.requireNonNull(ss).setSetupType(SetupType.ASSISTED);
            }
            if (!Objects.requireNonNull(ss).startSetup()) {
                ss.getPlayer().sendMessage(ChatColor.RED + "Could not start setup session. Pleas check the console.");
            }
            p.closeInventory();
            return;
        }

        if (a != null) {
            if (a.isSpectator(p)) {
                e.setCancelled(true);
                //noinspection UnnecessaryReturnStatement
                return;
            }
        }
    }

    @EventHandler
    public void swordHandler(InventoryClickEvent event) {
        Player player = (Player) event.getView().getPlayer();
        IArena arena = Arena.getArenaByPlayer(player);

        // Check if player is in an arena or if they are watching a GUI
        if (arena == null || BedWars.getAPI().getTeamUpgradesUtil().isWatchingGUI(player)) {
            return;
        }

        // Check if player is watching a shop GUI
        if (ShopCategory.categoryViewers.contains(player.getUniqueId())) {
            return;
        }

        // Check if player is watching quick buy menu
        if (ShopIndex.indexViewers.contains(player.getUniqueId())) {
            return;
        }

        ItemStack cursorItem = event.getView().getCursor();

        // Material will be AIR if shift-clicking
        if (cursorItem.getType() == Material.AIR && !event.isShiftClick()) {
            return;
        }

        // Handle shift-clicking
        if (event.isShiftClick()) {
            cursorItem = event.getCurrentItem();
        }

        // Ensure the clicked inventory is the player's inventory and not an external one
        if (event.getClickedInventory() != null && !event.getClickedInventory().equals(player.getInventory()) && !event.isShiftClick()) {
            return;
        }

        // Check if the item being clicked is a sword
        if (VersionCommon.api.getVersionSupport().isSword(cursorItem)) {
            ItemStack[] playerInventory = player.getInventory().getContents();

            // Loop through the player's inventory
            int slotNum = 0;
            for (ItemStack item : playerInventory) {
                if (item == null || item.getType() == Material.AIR || !VersionCommon.api.getVersionSupport().isCustomBedWarsItem(item)) {
                    continue;
                }

                // Check if the item is a custom bedwars item with the specific data
                if (VersionCommon.api.getVersionSupport().getCustomData(item).equalsIgnoreCase("DEFAULT_ITEM")) {
                    player.getInventory().remove(item);

                    // Handle shift-clicking by swapping items
                    if (event.isShiftClick()) {
                        event.setCurrentItem(null);
                        player.getInventory().setItem(slotNum, cursorItem);
                    }
                    // Update inventory to prevent ghost item
                    Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("BedWars2023"), (player)::updateInventory, 1L);
                }
                slotNum++;
            }
        }
    }

    /**
     * Manage click events for leave GUI
     */
    @EventHandler
    public void onLeaveWithPartyClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof CmdLeave.LeaveGuiHolder)) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        if (!BedWars.nms.isCustomBedWarsItem(item)) return;

        String data = BedWars.nms.getCustomData(item);

        if (data.startsWith("LEAVE")) {
            IArena arena = Arena.getArenaByPlayer(player);
            Misc.moveToLobbyOrKick(player, arena, arena.isSpectator(player.getUniqueId()));
        } else if (data.startsWith("STAY")) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onSwordEnchantInChest(InventoryOpenEvent event) {
        Player player = (Player) event.getView().getPlayer();
        IArena arena = Arena.getArenaByPlayer(player);
        if (arena == null || BedWars.getAPI().getTeamUpgradesUtil().isWatchingGUI(player)) {
            return;
        }
        // Check if player is watching a shop GUI
        if (ShopCategory.categoryViewers.contains(player.getUniqueId())) {
            return;
        }
        // Check if player is watching quick buy menu
        if (ShopIndex.indexViewers.contains(player.getUniqueId())) {
            return;
        }
        // Check if player is in a team (not a spectator, waiting in lobby etc.)
        if (arena.getTeam(player) == null || arena.getStatus() != GameState.playing) {
            return;
        }
        org.bukkit.inventory.Inventory inventory = event.getInventory();
        InventoryType inventoryType = inventory.getType();
        if (inventoryType == InventoryType.ENDER_CHEST || inventoryType == InventoryType.CHEST) {
            for (ItemStack item : inventory.getContents()) {
                if (item != null && VersionCommon.api.getVersionSupport().isSword(item)) {
                    ItemMeta im = item.getItemMeta();
                    for (TeamEnchant e : arena.getTeam(player).getSwordsEnchantments()) {
                        im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                    }
                    nms.setUnbreakable(im);
                    item.setItemMeta(im);
                }
            }
        }
    }

    /**
     * Check if an item is command-item
     */
    private static boolean isCommandItem(ItemStack i) {
        if (i == null) return false;
        if (i.getType() == Material.AIR) return false;
        if (nms.isCustomBedWarsItem(i)) {
            String[] customData = nms.getCustomData(i).split("_");
            if (customData.length >= 2) {
                return customData[0].equals("RUNCOMMAND");
            }
        }
        return false;
    }

    @EventHandler
    public void onGameEnd(GameStateChangeEvent e) {
        if(e.getNewState() != GameState.restarting) return;
        e.getArena().getPlayers().forEach(Player::closeInventory); // close any open guis when the game ends (e.g. shop)
    }
}
