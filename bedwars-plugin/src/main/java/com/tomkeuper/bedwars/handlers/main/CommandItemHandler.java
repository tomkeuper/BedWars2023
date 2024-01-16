package com.tomkeuper.bedwars.handlers.main;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.items.handlers.ILobbyItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandItemHandler extends LobbyItemHandler{
    public CommandItemHandler(String id, Plugin plugin) {
        super(id, plugin);
    }

    @Override
    public void handleUse(Player player, IArena arena, ILobbyItem lobbyItem) {
        player.sendMessage("CommandItemHandler handleUse");
    }
}