package com.tomkeuper.bedwars.handlers.main;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.items.handlers.HandlerType;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static com.tomkeuper.bedwars.BedWars.config;

public class CommandItemHandler extends LobbyItemHandler{
    public CommandItemHandler(String id, Plugin plugin) {
        super(id, plugin);
    }

    @Override
    public void handleUse(Player player, IArena arena, IPermanentItem lobbyItem) {
        String command = config.getYml().getString(ConfigPath.GENERAL_CONFIGURATION_LOBBY_ITEMS_COMMAND.replace("%path%", lobbyItem.getIdentifier()));
        player.performCommand(command);
    }

    @Override
    public HandlerType getType() {
        return HandlerType.COMMAND;
    }
}