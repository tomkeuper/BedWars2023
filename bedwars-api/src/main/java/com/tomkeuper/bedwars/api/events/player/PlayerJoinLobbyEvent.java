package com.tomkeuper.bedwars.api.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinLobbyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String arenaName;

    /**
     * Constructor for PlayerJoinLobbyEvent
     *
     * @param player The player joining the lobby
     * @param arenaName The name of the arena/lobby being joined
     */
    public PlayerJoinLobbyEvent(Player player, String arenaName) {
        this.player = player;
        this.arenaName = arenaName;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public String getArenaName() {
        return arenaName;
    }
}