package com.tomkeuper.bedwars.api.items.handlers;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface ILobbyItemHandler {

    void handleUse(Player player, IArena arena, ILobbyItem item);

    boolean isVisible(Player player, IArena arena);

    String getId();

    Plugin getPlugin();
    HandlerType getType();

    boolean isRegistered();
}
