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
