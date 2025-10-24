package com.tomkeuper.bedwars.api.events.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerJoinMainLobbyEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    @Setter
    private boolean cancelled = false;

    public PlayerJoinMainLobbyEvent(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
