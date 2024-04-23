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
     * Returns the total number of wins of the player.
     *
     * @return The number of wins.
     */
    int getWins();

    /**
     * Returns the number of wins of the player for a specified arena group.
     *
     * @param group The arena group
     * @return The number of wins.
     */
    int getWins(String group);

    /**
     * Sets the number of wins of the player for a specified arena group.
     *
     * @param group The arena group
     * @param wins  The number of wins.
     */
    void setWins(String group, int wins);

    /**
     * Returns the total number of kills of the player.
     *
     * @return The number of kills.
     */
    int getKills();

    /**
     * Returns the number of kills of the player for a specified arena group.
     *
     * @return The number of kills.
     */
    int getKills(String group);

    /**
     * Sets the number of kills of the player for a specified arena group.
     *
     * @param group The arena group
     * @param kills The number of kills.
     */
    void setKills(String group, int kills);

    /**
     * Returns the total number of final kills of the player.
     *
     * @return The number of final kills.
     */
    int getFinalKills();

    /**
     * Returns the number of final kills of the player for a specified arena group.
     *
     * @param group The arena group
     * @return The number of final kills.
     */
    int getFinalKills(String group);

    /**
     * Sets the number of final kills of the player for a specified arena group.
     *
     * @param group      The arena group
     * @param finalKills The number of final kills.
     */
    void setFinalKills(String group, int finalKills);

    /**
     * Returns the total number of losses of the player.
     *
     * @return The number of losses.
     */
    int getLosses();

    /**
     * Returns the number of losses of the player for a specified arena group.
     *
     * @param group The arena group
     * @return The number of losses.
     */
    int getLosses(String group);

    /**
     * Sets the number of losses of the player for a specified arena group.
     *
     * @param group  The arena group
     * @param losses The number of losses.
     */
    void setLosses(String group, int losses);

    /**
     * Returns the total number of deaths of the player.
     *
     * @return The number of deaths.
     */
    int getDeaths();

    /**
     * Returns the number of deaths of the player for a specified arena group.
     *
     * @param group The arena group
     * @return The number of deaths.
     */
    int getDeaths(String group);

    /**
     * Sets the number of deaths of the player for a specified arena group.
     *
     * @param group  The arena group
     * @param deaths The number of deaths.
     */
    void setDeaths(String group, int deaths);

    /**
     * Returns the total number of final deaths of the player.
     *
     * @return The number of final deaths.
     */
    int getFinalDeaths();

    /**
     * Returns the total number of final deaths of the player for a specified arena group.
     *
     * @param group The arena group
     * @return The number of final deaths.
     */
    int getFinalDeaths(String group);

    /**
     * Sets the number of final deaths of the player for a specified arena group.
     *
     * @param group       The arena group
     * @param finalDeaths The number of final deaths.
     */
    void setFinalDeaths(String group, int finalDeaths);

    /**
     * Returns the total number of beds destroyed by the player.
     *
     * @return The number of beds destroyed.
     */
    int getBedsDestroyed();

    /**
     * Returns the total number of beds destroyed by the player for a specified arena group.
     *
     * @param group The arena group
     * @return The number of beds destroyed.
     */
    int getBedsDestroyed(String group);

    /**
     * Sets the number of beds destroyed by the player for a specified arena group.
     *
     * @param group         The arena group
     * @param bedsDestroyed The number of beds destroyed.
     */
    void setBedsDestroyed(String group, int bedsDestroyed);

    /**
     * Returns the total number of games played by the player.
     *
     * @return The number of games played.
     */
    int getGamesPlayed();

    /**
     * Returns the total number of games played by the player for a specified arena group.
     *
     * @param group The arena group
     * @return The number of games played.
     */
    int getGamesPlayed(String group);

    /**
     * Sets the number of games played by the player for a specified arena group.
     *
     * @param group       The arena group
     * @param gamesPlayed The number of games played.
     */
    void setGamesPlayed(String group, int gamesPlayed);

    /**
     * Returns the total number of kills (including final kills) of the player across all arena groups.
     *
     * @return The total number of kills.
     */
    int getTotalKills();

    /**
     * Returns the total number of kills (including final kills) of the player across all arena groups for a specified arena group.
     *
     * @param group The arena group
     * @return The total number of kills.
     */
    int getTotalKills(String group);
}
