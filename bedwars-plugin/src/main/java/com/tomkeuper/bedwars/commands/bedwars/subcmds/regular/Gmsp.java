//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.tomkeuper.bedwars.commands.bedwars.subcmds.regular;

import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.command.SubCommand;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Gmsp extends SubCommand {
    public Gmsp(ParentCommand parent, String name) {
        super(parent, name);
        this.showInList(true);
        this.setDisplayInfo(MainCommand.createTC("§6 ▪ §7/" + MainCommand.getInstance().getName() + " gmsp", "/" + this.getParent().getName() + " " + this.getSubCommandName(), "§fSwitch to Spectator mode.\n§eClick to activate"));
    }

    public boolean execute(String[] args, CommandSender sender) {
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

    public List<String> getTabComplete() {
        return null;
    }

    public boolean canSee(CommandSender sender, BedWars api) {
        if (!(sender instanceof Player)) {
            return false;
        } else {
            Player player = (Player)sender;
            return !Arena.isInArena(player) && this.hasPermission(sender);
        }
    }
}
