package com.tomkeuper.bedwars.commands.bedwars.subcmds.regular;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.configuration.Permissions;
import com.tomkeuper.bedwars.configuration.Sounds;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class CmdMAP extends BukkitCommand {
    public CmdMAP(String name) {
        super(name);
    }
    static {
        try {
            Sounds.addDefSound("Map", com.tomkeuper.bedwars.BedWars.getForCurrentVersion("LEVEL_UP", "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP"));
        } catch (Exception e) {
            BedWars.plugin.getLogger().warning("Could not register map sounds: " + e.getMessage());
        }
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.getMsg((Player)null, Messages.COMMAND_MAP_CONSOLE_DENIED));
            return true;
        }else {
            Player player = (Player) sender;
            player.hasPermission(Permissions.PERMISSION_COMMAND_MAP);
            IArena arena = Arena.getArenaByPlayer(player);
            if (arena == null) {
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_MAP_NOT_IN_GAME));
            } else {
                String map = arena.getDisplayName();
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_MAP_DISPLAY).replace("%bw_map%", map));
                Sounds.addDefSound("Map", com.tomkeuper.bedwars.BedWars.getForCurrentVersion("LEVEL_UP", "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP"));
            }
            return true;

        }
    }
    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return onCommand(commandSender, null, s,strings);
    }
}

