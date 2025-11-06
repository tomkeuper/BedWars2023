package com.tomkeuper.bedwars.commands.play;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class PlayComamnd extends BukkitCommand {

    public PlayComamnd(String name) {
        super(name);
    }
    @Override
    public boolean execute(CommandSender s, String st, String[] args) {
        if (s instanceof ConsoleCommandSender) return false;
        Player player = (Player) s;
        Bukkit.dispatchCommand(player, "arena");
        return true;
    }
}
