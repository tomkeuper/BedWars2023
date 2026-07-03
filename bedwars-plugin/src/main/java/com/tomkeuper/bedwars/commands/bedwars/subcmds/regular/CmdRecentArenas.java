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

package com.tomkeuper.bedwars.commands.bedwars.subcmds.regular;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.command.SubCommand;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.RecentlyPlayedTracker;
import com.tomkeuper.bedwars.arena.SetupSession;
import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import com.tomkeuper.bedwars.listeners.arenaselector.ArenaSelectorListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CmdRecentArenas extends SubCommand implements Listener {

    public CmdRecentArenas(ParentCommand parent, String name) {
        super(parent, name);
        setPriority(18);
        showInList(true);
        setDisplayInfo(MainCommand.createTC(
                "§6 ▪ §7/" + MainCommand.getInstance().getName() + " " + getSubCommandName(),
                "/" + getParent().getName() + " " + getSubCommandName(),
                "§fOpens your recently played arenas GUI."
        ));
        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);
    }

    // ── Inventory Holder ──────────────────────────────────────────────────────

    public static class RecentArenasHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() { return null; }
    }

    // ── Command ───────────────────────────────────────────────────────────────

    @Override
    public boolean execute(String[] args, CommandSender s) {
        if (s instanceof ConsoleCommandSender) return false;
        Player p = (Player) s;
        if (Arena.getArenaByPlayer(p) != null) return false;
        openRecentGui(p);
        return true;
    }

    // ── GUI Builder ───────────────────────────────────────────────────────────

    public static void openRecentGui(Player p) {
        Set<String> recentNames = RecentlyPlayedTracker.getRecentArenas(p.getUniqueId());

        int size = recentNames.isEmpty() ? 9 : Math.min(54, (int) Math.ceil(recentNames.size() / 9.0) * 9);

        Inventory inv = Bukkit.createInventory(
                new RecentArenasHolder(),
                size,
                ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Recently Played Arenas"
        );

        // Empty state — fill with glass panes (1.8: STAINED_GLASS_PANE data 7 = gray)
        if (recentNames.isEmpty()) {
            ItemStack pane = makeGrayPane();
            ItemMeta pm = pane.getItemMeta();
            pm.setDisplayName(ChatColor.GRAY + "No recently played arenas.");
            List<String> l = new ArrayList<>();
            l.add(ChatColor.DARK_GRAY + "Play a game and come back!");
            pm.setLore(l);
            pane.setItemMeta(pm);
            for (int i = 0; i < size; i++) inv.setItem(i, pane);
            p.openInventory(inv);
            return;
        }

        int slot = 0;
        for (String arenaName : recentNames) {
            if (slot >= size) break;

            IArena arena = Arena.getArenaByName(arenaName);

            // Arena no longer loaded
            if (arena == null) {
                ItemStack barrier = new ItemStack(Material.BARRIER);
                ItemMeta bm = barrier.getItemMeta();
                bm.setDisplayName(ChatColor.RED + arenaName);
                List<String> l = new ArrayList<>();
                l.add(ChatColor.DARK_GRAY + "This arena is currently unavailable.");
                bm.setLore(l);
                barrier.setItemMeta(bm);
                inv.setItem(slot++, barrier);
                continue;
            }

            // Wool color by status (compatible with 1.8 and 1.13+)
            ItemStack item = statusWool(arena);
            ItemMeta meta = item.getItemMeta();

            // Glowing effect for joinable arenas
            if (arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting) {
                meta.addEnchant(Enchantment.LURE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            meta.setDisplayName(statusColor(arena) + arena.getDisplayName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Status : " + statusColor(arena) + arena.getStatus().toString());
            lore.add(ChatColor.GRAY + "Players: " + ChatColor.WHITE + arena.getPlayers().size() + "/" + arena.getMaxPlayers());
            lore.add(ChatColor.GRAY + "Group  : " + ChatColor.WHITE + arena.getGroup());
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "⏱ Recently played");
            lore.add("");

            if (arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting) {
                lore.add(ChatColor.GREEN + "▶ Click to join!");
            } else if (arena.getStatus() == GameState.playing && arena.isAllowSpectate()) {
                lore.add(ChatColor.YELLOW + "👁 Click to spectate!");
            } else {
                lore.add(ChatColor.RED + "✗ Cannot join right now.");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            // Tag item so the click listener can identify which arena it belongs to
            item = BedWars.nms.addCustomData(item, ArenaSelectorListener.ARENA_SELECTOR_GUI_IDENTIFIER + arenaName);

            inv.setItem(slot++, item);
        }

        p.openInventory(inv);
    }

    // ── Click Listener ────────────────────────────────────────────────────────

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().getHolder() instanceof RecentArenasHolder)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR
                || clicked.getType() == Material.BARRIER
                || isGrayPane(clicked)) return;

        Player p = (Player) event.getWhoClicked();

        String tag = BedWars.nms.getCustomData(clicked);
        if (tag == null || !tag.startsWith(ArenaSelectorListener.ARENA_SELECTOR_GUI_IDENTIFIER)) return;

        String arenaName = tag.replace(ArenaSelectorListener.ARENA_SELECTOR_GUI_IDENTIFIER, "");
        IArena arena = Arena.getArenaByName(arenaName);

        if (arena == null) {
            p.sendMessage(ChatColor.RED + "That arena is no longer available.");
            return;
        }

        p.closeInventory();

        if (arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting) {
            arena.addPlayer(p, false);
        } else if (arena.getStatus() == GameState.playing && arena.isAllowSpectate()) {
            arena.addSpectator(p, false, null);
        } else {
            p.sendMessage(ChatColor.RED + "You cannot join " + arena.getDisplayName() + " right now.");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    @SuppressWarnings("deprecation")
    private static boolean isGrayPane(ItemStack item) {
        String name = item.getType().name();
        // 1.13+: GRAY_STAINED_GLASS_PANE | 1.8: STAINED_GLASS_PANE with data 7
        return name.equals("GRAY_STAINED_GLASS_PANE")
                || (name.equals("STAINED_GLASS_PANE") && item.getDurability() == 7);
    }

    @SuppressWarnings("deprecation")
    private static ItemStack makeGrayPane() {
        try {
            return new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"));
        } catch (IllegalArgumentException e) {
            // 1.8: STAINED_GLASS_PANE + data 7 = gray
            return new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 7);
        }
    }

    /**
     * Returns a colored wool ItemStack compatible with 1.8 and 1.13+.
     * In 1.8, all wool is Material.WOOL with a damage/data value for color.
     * In 1.13+, each color is its own Material (LIME_WOOL, etc.).
     */
    @SuppressWarnings("deprecation")
    private static ItemStack statusWool(IArena arena) {
        // Wool data values (1.8):
        //  0 = White,  1 = Orange,  2 = Magenta, 3 = Light Blue,
        //  4 = Yellow, 5 = Lime,   13 = Green,  14 = Red, 7 = Gray
        try {
            // 1.13+ — each color is its own Material
            switch (arena.getStatus()) {
                case waiting:  return new ItemStack(Material.valueOf("LIME_WOOL"));
                case starting: return new ItemStack(Material.valueOf("YELLOW_WOOL"));
                case playing:  return new ItemStack(Material.valueOf("RED_WOOL"));
                default:       return new ItemStack(Material.valueOf("GRAY_WOOL"));
            }
        } catch (IllegalArgumentException e) {
            // 1.8 fallback — Material.WOOL + data value for color
            Material wool = Material.valueOf("WOOL");
            switch (arena.getStatus()) {
                case waiting:  return new ItemStack(wool, 1, (short) 5);  // Lime
                case starting: return new ItemStack(wool, 1, (short) 4);  // Yellow
                case playing:  return new ItemStack(wool, 1, (short) 14); // Red
                default:       return new ItemStack(wool, 1, (short) 7);  // Gray
            }
        }
    }

    private static ChatColor statusColor(IArena arena) {
        switch (arena.getStatus()) {
            case waiting:  return ChatColor.GREEN;
            case starting: return ChatColor.YELLOW;
            case playing:  return ChatColor.RED;
            default:       return ChatColor.DARK_GRAY;
        }
    }

    // ── SubCommand boilerplate ────────────────────────────────────────────────

    @Override
    public List<String> getTabComplete() { return null; }

    @Override
    public boolean canSee(CommandSender s, com.tomkeuper.bedwars.api.BedWars api) {
        if (s instanceof ConsoleCommandSender) return false;
        Player p = (Player) s;
        if (Arena.isInArena(p)) return false;
        if (SetupSession.isInSetupSession(p.getUniqueId())) return false;
        return hasPermission(s);
    }
}