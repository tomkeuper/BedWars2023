//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.tomkeuper.bedwars.commands.bedwars.subcmds.regular;

import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class Gmsp extends BukkitCommand {
    public Gmsp(String name) {
        super(name);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.getMsg((Player)null, Messages.COMMAND_GMSP_CONSOLE_DENIED));
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("bedwars.gmsp")) {
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_GMSP_NO_PERMISSION));
                return true;
            } else if (Arena.isInArena(player)) {
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_GMSP_DISABLED));
                return true;
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_GMSP_ENABLED));
                return true;
            }
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return onCommand(commandSender, null, s, strings);
    }
}