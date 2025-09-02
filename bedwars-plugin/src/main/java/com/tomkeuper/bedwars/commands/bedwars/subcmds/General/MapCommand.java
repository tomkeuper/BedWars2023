//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.tomkeuper.bedwars.commands.bedwars.subcmds.General;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.configuration.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class MapCommand extends BukkitCommand {
    public MapCommand(String name) {
        super(name);
    }
    
    static {
        try {
            Sounds.addDefSound("Map", com.tomkeuper.bedwars.BedWars.getForCurrentVersion("LEVEL_UP", "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP"));
        } catch (Exception e) {
            com.tomkeuper.bedwars.BedWars.plugin.getLogger().warning("Could not register fly sounds: " + e.getMessage());
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.getMsg((Player)null, Messages.COMMAND_MAP_CONSOLE_DENIED));
            return true;
        } else {
            Player player = (Player)sender;
            IArena arena = Arena.getArenaByPlayer(player);
            if (arena == null) {
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_MAP_NOT_IN_GAME));
            } else {
                String mapName = arena.getDisplayName();
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_MAP_DISPLAY).replace("{map}", mapName));
                Sounds.addDefSound("Map", com.tomkeuper.bedwars.BedWars.getForCurrentVersion("LEVEL_UP", "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP"));
            }
            return true;
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return onCommand(commandSender, null, s, strings);
    }
}
