package com.tomkeuper.bedwars.commands.bedwars.subcmds.General;

import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import static com.tomkeuper.bedwars.BedWars.plugin;
import static com.tomkeuper.bedwars.api.language.Language.getMsg;
import com.tomkeuper.bedwars.api.language.Messages;
import org.bukkit.entity.Player;

public class SupportCommand extends BukkitCommand {
    public SupportCommand(String name) {
        super(name);
        this.setPermissionMessage("§cYou do not have permission to use this command.");
        this.description = "Shows support information for BedWars";
    }

    Player p;

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.hasPermission("bedwars.support")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("§cThis command can only be used by players.");
                return true;
            }
            p = (Player) commandSender;

            // Display support messages
            p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + MainCommand.getDot() + ChatColor.GOLD + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + ChatColor.GRAY + '-' + " " + ChatColor.AQUA + "Support Information for BedWars");
            p.sendMessage("§6Server Support:");
            p.sendMessage("§7Discord: https://discord.gg/m5fz8Yunkz");
            p.sendMessage("§7for-bug: https://discord.gg/ZQWrWvQdfC");
            p.sendMessage("§7For Suggestion: https://discord.gg/SExCmab9");
            p.sendMessage("§7For Report: /Report <player> <reason>");
            // Add more support links as needed
            return true;
        }
        if (commandSender instanceof Player) {
            p = (Player) commandSender;
            p.sendMessage(getMsg(p, Messages.Help_Command_No_Perms));
        } else {
            commandSender.sendMessage(getMsg(null, Messages.Help_Command_No_Perms));
        }
        return false;
    }
}

