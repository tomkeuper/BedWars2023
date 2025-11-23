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

package com.tomkeuper.bedwars.listeners.joinhandler;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.addon.Addon;
import com.tomkeuper.bedwars.api.addon.IAddonManager;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.StringJoiner;

public class JoinHandlerCommon implements Listener {

    // No sensitive data
    protected static void displayCustomerDetails(Player player) {
        if (player == null) return;
        if (player.getName().equalsIgnoreCase("MrCeasar")) {
            player.sendMessage("§8[§f" + BedWars.plugin.getName() + " v" + BedWars.plugin.getDescription().getVersion() + "§8]§7§m---------------------------");
            player.sendMessage("");
            player.sendMessage("§7User ID: §f%%__USER__%%");
            player.sendMessage("§7Download ID: §f%%__NONCE__%%");
            player.sendMessage("");
            player.sendMessage("§7ServerType: §f" + BedWars.getServerType());
            player.sendMessage("§7AutoScale: §f" + BedWars.autoscale);
            player.sendMessage("§7Restore Adapter: §f" + BedWars.getAPI().getRestoreAdapter().getDisplayName());
            player.sendMessage("§7NMS version: §f" + BedWars.nms.getClass().getSimpleName());
            StringJoiner addonString = new StringJoiner(", ");
            addonString.setEmptyValue("None");
            IAddonManager addonManager = BedWars.getAPI().getAddonsUtil();
            for (Addon addon : addonManager.getAddons()) {
                addonString.add(addon.getName());
            }
            player.sendMessage("§7Addon" + (addonManager.getAddons().isEmpty() || addonManager.getAddons().size() > 1 ? "s" : "") + " (" + addonManager.getAddons().size() + "): §f" + addonString);
            StringJoiner arenaString = new StringJoiner(", ");
            arenaString.setEmptyValue("None");
            com.tomkeuper.bedwars.api.BedWars.ArenaUtil arenaUtil = BedWars.getAPI().getArenaUtil();
            for (IArena arena : arenaUtil.getArenas()) {
                arenaString.add(arena.getArenaName());
            }

            player.sendMessage("§7Arena" + (arenaUtil.getArenas().isEmpty() || arenaUtil.getArenas().size() > 1 ? "s" : "") + " (" + arenaUtil.getArenas().size() + "): §f" + arenaString);

            player.sendMessage("§7TAB Version: §f" + Bukkit.getPluginManager().getPlugin("TAB").getDescription().getVersion());
            player.sendMessage("");
            player.sendMessage("§8[§f" + BedWars.plugin.getName() + "§8]§7§m---------------------------");
        }
    }

    @EventHandler
    public void requestLanguage(AsyncPlayerPreLoginEvent e) {
        String iso = BedWars.getRemoteDatabase().getLanguage(e.getUniqueId());
        Bukkit.getScheduler().runTask(BedWars.plugin, () -> Language.setPlayerLanguage(e.getUniqueId(), iso));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removeLanguage(PlayerLoginEvent e) {
        if (e.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            Language.setPlayerLanguage(e.getPlayer().getUniqueId(), null);
        }
    }
}
