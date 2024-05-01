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

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.command.SubCommand;
import com.tomkeuper.bedwars.configuration.LevelsConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

public class RedisUpdate extends SubCommand {

    public RedisUpdate(ParentCommand parent, String name) {
        super(parent, name);
        showInList(false);
    }

    @Override
    public boolean execute(String[] args, CommandSender s) {
        if (!(s instanceof ConsoleCommandSender)) return false;

        if (args.length < 1) {
            s.sendMessage("§cUsage: §o/bedwars redisUpdate <key>");
            return true;
        }

        String key = args[0];
        if (key.equals("default_rankup_cost")){
            BedWars.getRedisConnection().storeSettings(key, String.valueOf(LevelsConfig.getNextCost(1)));
            s.sendMessage("§aUpdated default rankup cost to " + LevelsConfig.getNextCost(1));
        } else {
            s.sendMessage("§cUnknown key: §o" + key);
        }


        return true;
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }
}
