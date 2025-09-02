package com.tomkeuper.bedwars.commands.bedwars.subcmds.General;

import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class SpectateCommand extends BukkitCommand {
    public SpectateCommand(String name) {
        super(name);
        this.setPermissionMessage("§cYou do not have permission to use this command.");
        this.description = "Allows a p to spectate the current arena.";
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by ps.");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("bedwars.spectate")) {
            p.sendMessage(Language.getMsg(p, Messages.COMMAND_SPECTATE_NO_PERMISSION));
            return true;
        }

        // If a player name is provided
        if (args.length > 0) {
            Player targetPlayer = p.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                p.sendMessage(Language.getMsg(p, Messages.COMMAND_NOT_FOUND_PLAYER_MATCH));
                return true;
            }

            IArena targetArena = Arena.getArenaByPlayer(targetPlayer);
            if (targetArena == null) {
                p.sendMessage(Language.getMsg(p, Messages.COMMAND_SPECTATE_PLAYER_NOT_IN_ARENA));
                return true;
            }

            // Make the player spectate the target's arena
            targetArena.addSpectator(p, false, null);
            return true;
        }

        // Original code for spectating current arena
        IArena arena = Arena.getArenaByPlayer(p);
        if (arena == null) {
            p.sendMessage(Language.getMsg(p, Messages.COMMAND_SPECTATE_NOT_IN_ARENA));
            return true;
        }
        if (arena.isSpectator(p)) {
            p.sendMessage(Language.getMsg(p, Messages.COMMAND_SPECTATE_ALREADY_SPECTATOR));
            return true;
        }
        // Spectate logic (set player as spectator)
        arena.addSpectator(p, false, null);
        p.sendMessage(Language.getMsg(p, Messages.COMMAND_SPECTATE_SUCCESS));
        return true;
    }
}
