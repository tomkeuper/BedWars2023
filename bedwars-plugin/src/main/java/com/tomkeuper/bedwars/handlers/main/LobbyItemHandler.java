package com.tomkeuper.bedwars.handlers.main;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.items.handlers.HandlerType;
import com.tomkeuper.bedwars.api.items.handlers.ILobbyItemHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class LobbyItemHandler implements ILobbyItemHandler {
    private final String id;
    private final Plugin plugin;

    public LobbyItemHandler(@NotNull String id, @NotNull Plugin plugin) {
        this.id = id;
        this.plugin = plugin;
    }

    public void handleUse(Player player, IArena arena){
    }

    public boolean isVisible(Player player, IArena arena){
        return true;
    }

    public final String getId() {
        return this.id;
    }

    public final Plugin getPlugin() {
        return this.plugin;
    }

    public HandlerType getType() {
        return HandlerType.PLUGIN;
    }

    public final boolean isRegistered() {
        // TODO is always null
//        ILobbyItemHandler handler = BedWars.getAPI().getItemUtil().getItemHandler().getLobbyItemHandler(this.getId());
//        return handler != null && handler == this;
        return true;
    }
}
