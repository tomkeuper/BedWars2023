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
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.command.SubCommand;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.player.PlayerJoinLobbyEvent;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.SetupSession;
import com.tomkeuper.bedwars.configuration.Sounds;
import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import com.tomkeuper.bedwars.listeners.chat.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.tomkeuper.bedwars.api.language.Language.getMsg;

public class CmdJoin extends SubCommand {

    public CmdJoin(ParentCommand parent, String name) {
        super(parent, name);
        setPriority(19);
        showInList(false);
        setDisplayInfo(MainCommand.createTC("§6 ▪ §7/"+ MainCommand.getInstance().getName()+" join §e<random/ arena/ groupName>",
                "/"+getParent().getName()+" "+getSubCommandName(), "§fJoin an arena by name or by group.\n§f/bw join random - join random arena."));
    }

    @Override
    public boolean execute(String[] args, CommandSender s) {
        if (s instanceof ConsoleCommandSender) return false;
        Player p = (Player) s;
        if (args.length < 1) {
            BedWars.plugin.adventure().sender(s)
                    .sendMessage(ChatFormatting.parseLegacyMini(getMsg(p, Messages.COMMAND_JOIN_USAGE)));
            return true;
        }
        if (args[0].equalsIgnoreCase("random")){
            if (!Arena.joinRandomArena(p)){
                BedWars.plugin.adventure().sender(s)
                        .sendMessage(ChatFormatting.parseLegacyMini(getMsg(p, Messages.COMMAND_JOIN_NO_EMPTY_FOUND)));                Sounds.playSound("join-denied", p);
            } else {
                Sounds.playSound("join-allowed", p);
            }
            return true;
        }
        if (MainCommand.isArenaGroup(args[0]) || args[0].contains("+")) {
            if (!Arena.joinRandomFromGroup(p, args[0])) {
                BedWars.plugin.adventure().sender(s)
                        .sendMessage(ChatFormatting.parseLegacyMini(getMsg(p, Messages.COMMAND_JOIN_NO_EMPTY_FOUND)));                Sounds.playSound("join-denied", p);
            } else {
                Sounds.playSound("join-allowed", p);
            }
            return true;
        } else if (Arena.getArenaByName(args[0]) != null) {
            if (Arena.getArenaByName(args[0]).addPlayer(p, false)){
                // Fire the event after player joins
                PlayerJoinLobbyEvent event = new PlayerJoinLobbyEvent(p, args[0]);
                Bukkit.getPluginManager().callEvent(event);

                Sounds.playSound("join-allowed", p);
            } else {
                Sounds.playSound("join-denied", p);
            }
            return true;
        } else if (Arena.getArenaByIdentifier(args[0]) != null) {
            IArena arena = Arena.getArenaByIdentifier(args[0]);
            if (arena.addPlayer(p, false)){
                // Fire the event after player joins
                PlayerJoinLobbyEvent event = new PlayerJoinLobbyEvent(p, arena.getArenaName());
                Bukkit.getPluginManager().callEvent(event);

                Sounds.playSound("join-allowed", p);
            } else {
                Sounds.playSound("join-denied", p);
            }
            return true;
        }
        BedWars.plugin.adventure().sender(s)
                .sendMessage(ChatFormatting.parseLegacyMini(getMsg(p, Messages.COMMAND_JOIN_GROUP_OR_ARENA_NOT_FOUND).replace("%bw_name%", args[0])));
        return true;
    }
    @Override
    public List<String> getTabComplete() {
        List<String> tab = new ArrayList<>(BedWars.config.getYml().getStringList(ConfigPath.GENERAL_CONFIGURATION_ARENA_GROUPS));
        for (IArena arena : Arena.getArenas()){
            tab.add(arena.getArenaName());
        }
        return tab;
    }

    @Override
    public boolean canSee(CommandSender s, com.tomkeuper.bedwars.api.BedWars api) {
        if (s instanceof ConsoleCommandSender) return false;

        Player p = (Player) s;
        if (Arena.isInArena(p)) return false;

        if (SetupSession.isInSetupSession(p.getUniqueId())) return false;
        return hasPermission(s);
    }
}
