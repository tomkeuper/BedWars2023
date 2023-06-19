package com.tomkeuper.bedwars.api.events.player;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStatChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;
    private IArena arena;
    private StatType statType;
    private boolean cancelled = false;

    /**
     * Called when player statistics get changes
     * @Param player - target player
     * @param arena  - target arena
     *
     */
    public PlayerStatChangeEvent(Player player, IArena arena, StatType statType) {
        this.player = player;
        this.arena = arena;
        this.statType = statType;
    }



    /**
     * Get the arena
     */
    public IArena getArena() {
        return arena;
    }

    /**
     * Get the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the stat type
     */
    public StatType getStatType() {
        return statType;
    }

    public enum StatType {
        FIRST_PLAY, LAST_PLAY, WINS, KILLS, FINAL_KILLS, LOSSES, DEATHS, FINAL_DEATHS, BEDS_DESTROYED, GAMES_PLAYED, CUSTOM
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Check if event was cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancel event
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
