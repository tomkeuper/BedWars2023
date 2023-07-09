package com.tomkeuper.bedwars.api.stats;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents the statistics of a player.
 */
public interface IPlayerStats {

    /**
     * Returns the UUID of the player.
     *
     * @return The UUID of the player.
     */
    UUID getUuid();

    /**
     * Returns the name of the player.
     *
     * @return The name of the player.
     */
    String getName();

    /**
     * Sets the name of the player.
     *
     * @param name The name of the player.
     */
    void setName(String name);

    /**
     * Returns the timestamp of the first play of the player.
     *
     * @return The timestamp of the first play.
     */
    Instant getFirstPlay();

    /**
     * Sets the timestamp of the first play of the player.
     *
     * @param firstPlay The timestamp of the first play.
     */
    void setFirstPlay(Instant firstPlay);

    /**
     * Returns the timestamp of the last play of the player.
     *
     * @return The timestamp of the last play.
     */
    Instant getLastPlay();

    /**
     * Sets the timestamp of the last play of the player.
     *
     * @param lastPlay The timestamp of the last play.
     */
    void setLastPlay(Instant lastPlay);

    /**
     * Returns the number of wins of the player.
     *
     * @return The number of wins.
     */
    int getWins();

    /**
     * Sets the number of wins of the player.
     *
     * @param wins The number of wins.
     */
    void setWins(int wins);

    /**
     * Returns the number of kills of the player.
     *
     * @return The number of kills.
     */
    int getKills();

    /**
     * Sets the number of kills of the player.
     *
     * @param kills The number of kills.
     */
    void setKills(int kills);

    /**
     * Returns the number of final kills of the player.
     *
     * @return The number of final kills.
     */
    int getFinalKills();

    /**
     * Sets the number of final kills of the player.
     *
     * @param finalKills The number of final kills.
     */
    void setFinalKills(int finalKills);

    /**
     * Returns the number of losses of the player.
     *
     * @return The number of losses.
     */
    int getLosses();

    /**
     * Sets the number of losses of the player.
     *
     * @param losses The number of losses.
     */
    void setLosses(int losses);

    /**
     * Returns the number of deaths of the player.
     *
     * @return The number of deaths.
     */
    int getDeaths();

    /**
     * Sets the number of deaths of the player.
     *
     * @param deaths The number of deaths.
     */
    void setDeaths(int deaths);

    /**
     * Returns the number of final deaths of the player.
     *
     * @return The number of final deaths.
     */
    int getFinalDeaths();

    /**
     * Sets the number of final deaths of the player.
     *
     * @param finalDeaths The number of final deaths.
     */
    void setFinalDeaths(int finalDeaths);

    /**
     * Returns the number of beds destroyed by the player.
     *
     * @return The number of beds destroyed.
     */
    int getBedsDestroyed();

    /**
     * Sets the number of beds destroyed by the player.
     *
     * @param bedsDestroyed The number of beds destroyed.
     */
    void setBedsDestroyed(int bedsDestroyed);

    /**
     * Returns the number of games played by the player.
     *
     * @return The number of games played.
     */
    int getGamesPlayed();

    /**
     * Sets the number of games played by the player.
     *
     * @param gamesPlayed The number of games played.
     */
    void setGamesPlayed(int gamesPlayed);

    /**
     * Returns the total number of kills (including final kills) of the player.
     *
     * @return The total number of kills.
     */
    int getTotalKills();
}
