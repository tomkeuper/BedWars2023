package com.tomkeuper.bedwars.commands.bedwars.subcmds.General;

import com.tomkeuper.bedwars.configuration.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class StartCommand extends BukkitCommand {

    public StartCommand(String name) {
        super(name);
    }


    @Override
    public boolean execute(CommandSender s, String st, String[] args) {
        if (s instanceof ConsoleCommandSender) return true;
        Player p = (Player) s;
        if (p.hasPermission(Permissions.PERMISSION_START)) {
            Bukkit.dispatchCommand(p, "bw forceStart");
        }
        return true;
    }
}