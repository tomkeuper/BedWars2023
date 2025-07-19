package com.tomkeuper.bedwars.api.stats;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IStatsManager {
    /**
     * Gets the stats of a player by their UUID.
     *
     * @param uuid the UUID of the player
     * @return the player's stats, never null
     */
    @NotNull IPlayerStats get(UUID uuid);

    /**
     * Gets the stats of a player by their UUID, may return null if the player does not exist.
     *
     * @param uuid the UUID of the player
     * @return the player's stats, may be null
     */
    @Nullable IPlayerStats getUnsafe(UUID uuid);

    /**
     * Gets the stats of a player by their Player object.
     *
     * @param player the Player object
     */
    void addPlayerKill(Player player);

    /**
     * Adds a final kill to the player's stats.
     *
     * @param player the Player object
     */
    void addFinalKill(Player player);


    /**
     * Adds a player death to the player's stats.
     *
     * @param player the Player object
     */
    void addPlayerDeath(Player player);


    /**
     * Adds a final player death to the player's stats.
     *
     * @param player the Player object
     */
    void addFinalDeath(Player player);

    /**
     * Adds a bed break to the player's stats.
     *
     * @param player the Player object
     */
    void addBedBreak(Player player);

    /**
     * Adds a win to the player's stats.
     *
     * @param player the Player object
     */
    void addWin(Player player);

    /**
     * Adds a loss to the player's stats.
     *
     * @param player the Player object
     */
    void addLoss(Player player);

    /**
     * Adds a game played to the player's stats.
     *
     * @param player the Player object
     */
    void addGamesPlayed(Player player);
}
