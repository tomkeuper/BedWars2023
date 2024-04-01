/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
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
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.tomkeuper.bedwars.commands.bedwars.subcmds.regular;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.command.SubCommand;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.server.ServerType;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.Misc;
import com.tomkeuper.bedwars.arena.SetupSession;
import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.tomkeuper.bedwars.BedWars.config;

public class CmdLeave extends SubCommand {

    private static final HashMap<UUID, Long> delay = new HashMap<>();
    private static final HashMap<UUID, BukkitTask> leaving = new HashMap<>();

    public CmdLeave(ParentCommand parent, String name) {
        super(parent, name);
        setPriority(20);
        showInList(false);
        setDisplayInfo(MainCommand.createTC("§6 ▪ §7/"+ MainCommand.getInstance().getName()+" leave", "/"+getParent().getName()+" "+getSubCommandName(), "§fLeave an arena."));
    }

    @Override
    public boolean execute(String[] args, CommandSender s) {
        if (s instanceof ConsoleCommandSender) return false;
        Player p = (Player) s;

        IArena a = Arena.getArenaByPlayer(p);
        if (p.getWorld().getName().equalsIgnoreCase(BedWars.getLobbyWorld())) {
            Misc.moveToLobbyOrKick(p, a, a != null && a.isSpectator(p.getUniqueId()));
            return true;
        } else {
            if (a == null) {
                p.sendMessage(Language.getMsg(p, Messages.COMMAND_FORCESTART_NOT_IN_GAME));
                return true;
            }

            if (BedWars.getPartyManager().isOwner(p)){
                openLeaveGUI(p);
            } else {
                Misc.moveToLobbyOrKick(p, a, a.isSpectator(p.getUniqueId()));
            }
        }
        return true;
    }

    public static void openLeaveGUI(Player player) {
        LeaveGuiHolder holder = new LeaveGuiHolder();
        Inventory inv = Bukkit.createInventory(holder, 9, Language.getMsg(player, Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_TITLE));
        ItemStack leaveItem = BedWars.nms.greenGlassPane(1);
        ItemMeta leave = leaveItem.getItemMeta();
        leave.setDisplayName(Language.getMsg(player, Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_BRING_PARTY));
        leave.setLore(Language.getList(player, Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_BRING_PARTY_LORE));
        leaveItem.setItemMeta(leave);
        inv.setItem(1, BedWars.nms.addCustomData(leaveItem, "LEAVE"));
        inv.setItem(2, BedWars.nms.addCustomData(leaveItem, "LEAVE"));
        inv.setItem(3, BedWars.nms.addCustomData(leaveItem, "LEAVE"));

        ItemStack stayItem = BedWars.nms.redGlassPane(1);
        ItemMeta bringParty = stayItem.getItemMeta();
        bringParty.setDisplayName(Language.getMsg(player, Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_STAY));
        bringParty.setLore(Language.getList(player, Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_STAY_LORE));
        stayItem.setItemMeta(bringParty);
        inv.setItem(5, BedWars.nms.addCustomData(stayItem, "STAY"));
        inv.setItem(6, BedWars.nms.addCustomData(stayItem, "STAY"));
        inv.setItem(7, BedWars.nms.addCustomData(stayItem, "STAY"));

        player.openInventory(inv);
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }

    @Override
    public boolean canSee(CommandSender s, com.tomkeuper.bedwars.api.BedWars api) {
        if (s instanceof ConsoleCommandSender) return false;

        Player p = (Player) s;

        if (BedWars.getServerType() == ServerType.SHARED && !Arena.isInArena(p)) return false;

        if (SetupSession.isInSetupSession(p.getUniqueId())) return false;
        return hasPermission(s);
    }

    private static boolean cancel(UUID player){
        return delay.getOrDefault(player, 0L) > System.currentTimeMillis();
    }

    private static void update(UUID player){
        if (delay.containsKey(player)){
            delay.replace(player, System.currentTimeMillis() + 2500L);
            return;
        }
        delay.put(player, System.currentTimeMillis() + 2500L);
    }

    public static class LeaveGuiHolder implements InventoryHolder {

        public LeaveGuiHolder(){
        }

        @Override
        public Inventory getInventory() {
            return null;
        }

    }

}
