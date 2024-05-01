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

package com.tomkeuper.bedwars.commands.bedwars.subcmds.sensitive;

import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.command.SubCommand;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.Misc;
import com.tomkeuper.bedwars.arena.SetupSession;
import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArenaList extends SubCommand {

    private static final int ARENAS_PER_PAGE = 10;

    public ArenaList(ParentCommand parent, String name) {
        super(parent, name);
        setPriority(3);
        showInList(true);
        setDisplayInfo(Misc.msgHoverClick("§6 ▪ §7/" + MainCommand.getInstance().getName() + " " + getSubCommandName() + ((getArenas().size() == 0) ? " §c(0 set)" : " §a(" + getArenas().size() + " set)"),
                "§fShow available arenas", "/" + MainCommand.getInstance().getName() + " " + getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }

    @Override
    public boolean execute(String[] args, CommandSender s) {
        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 0) {
                    page = 1;
                }
            } catch (Exception ignored) {
            }
        }
        int start = (page - 1) * ARENAS_PER_PAGE;
        List<IArena> arenas = new ArrayList<>(Arena.getArenas());
        if (arenas.size() <= start){
            page = 1;
            start = 0;
        }

        s.sendMessage(color(" &c|| &6" + com.tomkeuper.bedwars.BedWars.plugin.getName() + " &cConfigs found: &f" + getArenas().size() + "&7 Instantiated games:"));

        if (arenas.isEmpty()) {
            s.sendMessage(ChatColor.RED + "No arenas to display.");
            return true;
        }

        int limit = Math.min(arenas.size(), start + ARENAS_PER_PAGE);

        arenas.subList(start, limit).forEach(arena -> {
            String gameState = arena.getDisplayStatus(s instanceof ConsoleCommandSender ? Language.getDefaultLanguage() : Language.getPlayerLanguage((Player) s));
            String msg = color(
                    "ID: &e" + arena.getWorldName() +
                            (com.tomkeuper.bedwars.BedWars.autoscale ? " &fN: &e" + arena.getArenaName(): "") +
                            " &fG: &e" + arena.getDisplayGroup(s instanceof ConsoleCommandSender ? Language.getDefaultLanguage() : Language.getPlayerLanguage((Player) s)) +
                            " &fP: &e" + (arena.getPlayers().size() + arena.getSpectators().size()) +
                            " &fS: " + gameState +
                            " &fWl: &e" + (Bukkit.getWorld(arena.getWorldName()) != null)
            );
            s.sendMessage(msg + " \n");
        });

        if (arenas.size() > ARENAS_PER_PAGE * page) {
            s.sendMessage(ChatColor.GRAY + "Type /" + ChatColor.GREEN + MainCommand.getInstance().getName() + " arenaList " + ++page + ChatColor.GRAY + " for next page.");
        }
        return true;
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }

    @NotNull
    private java.util.List<String> getArenas() {
        ArrayList<String> arene = new ArrayList<>();
        File dir = new File(com.tomkeuper.bedwars.BedWars.plugin.getDataFolder(), "/Arenas");
        if (dir.exists()) {
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.isFile()) {
                    if (f.getName().contains(".yml")) {
                        arene.add(f.getName().replace(".yml", ""));
                    }
                }
            }
        }
        return arene;
    }

    @Override
    public boolean canSee(CommandSender s, BedWars api) {

        if (s instanceof Player) {
            Player p = (Player) s;
            if (Arena.isInArena(p)) return false;

            if (SetupSession.isInSetupSession(p.getUniqueId())) return false;
        }

        return hasPermission(s);
    }

    private static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
