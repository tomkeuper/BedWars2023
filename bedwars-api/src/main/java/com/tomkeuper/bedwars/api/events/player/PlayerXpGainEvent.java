package com.tomkeuper.bedwars.api.events.player;

import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerXpGainEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    /**
     * -- SETTER --
     * Set the amount of xp received.
     */
    @Setter
    private int amount;

    private final XpSource xpSource;

    /**
     * -- SETTER --
     * Cancel event
     */
    @Setter
    private boolean cancelled = false;

    /**
     * Called when a player receives new xp.
     * This only works when the internal Level System is used.
     * Developers can "inject" their own level system.
     *
     * @param player   - target player.
     * @param amount   - amount of xp.
     * @param xpSource - where did the player receive xp from.
     */
    public PlayerXpGainEvent(Player player, int amount, XpSource xpSource) {
        this.player = player;
        this.amount = amount;
        this.xpSource = xpSource;
    }

    /**
     * Get the player that has received new xp.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the amount of xp received.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Get xp source
     */
    public XpSource getXpSource() {
        return xpSource;
    }

    /**
     * Check if event was cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Lets you know why did the player receive new xp.
     */
    public enum XpSource {
        PER_MINUTE,             // XP per minute in game
        PER_TEAMMATE,           // XP for team support
        GAME_WIN,               // XP for winning the game
        BED_DESTROYED,          // XP for destroying a bed
        FINAL_KILL,             // XP for final kill
        REGULAR_KILL,           // XP for regular kill
        FIRST_BLOOD,            // XP for first kill in the game
        FIRST_BED_DESTROYED,    // XP for first bed destroyed
        NO_DEATH_WIN,           // XP for winning without dying
        FLAWLESS_VICTORY,       // XP for winning with all teammates alive
        TEAM_ELIMINATED,        // XP for eliminating entire team
        VOID_KILL,              // XP for killing a player by void
        KILL_STREAK,            // XP for reaching kill streak
        OTHER                   // Custom or unknown source
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
