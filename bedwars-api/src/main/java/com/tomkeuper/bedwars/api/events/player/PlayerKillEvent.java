/*
 * BedWars2023 - A bed wars mini-game.
 * Copyright (C) 2024 Tomas Keuper
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: contact@fyreblox.com
 */

package com.tomkeuper.bedwars.api.events.player;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.function.Function;

public class PlayerKillEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final IArena arena;
    private final Player victim;
    private final Player killer;
    private final PlayerKillCause cause;
    private Function<Player, String> message;
    private boolean playSound = true;

    /**
     * Called when a Player got killed during the game.
     *
     * @param killer can be NULL.
     */
    public PlayerKillEvent(IArena arena, Player victim, Player killer, Function<Player, String> message, PlayerKillCause cause) {
        this.arena = arena;
        this.victim = victim;
        this.killer = killer;
        this.message = message;
        this.cause = cause;
    }

    /**
     * Enumeration representing various causes of death events.
     */
    public enum PlayerKillCause {
        UNKNOWN(false, false, false),
        UNKNOWN_FINAL_KILL(true, false, false),
        EXPLOSION(false, false, false),
        EXPLOSION_FINAL_KILL(true, false, false),
        VOID(false, false, false),
        VOID_FINAL_KILL(true, false, false),
        PVP(false, false, false),
        PVP_FINAL_KILL(true, false, false),
        PLAYER_SHOOT(false, false, false),
        PLAYER_SHOOT_FINAL_KILL(true, false, false),
        SILVERFISH(false, true, false),
        SILVERFISH_FINAL_KILL(true, true, false),
        IRON_GOLEM(false, true, false),
        IRON_GOLEM_FINAL_KILL(true, true, false),
        PLAYER_PUSH(false, false, false),
        PLAYER_PUSH_FINAL(true, false, false),
        PLAYER_DISCONNECT(false, false, true),
        PLAYER_DISCONNECT_FINAL(true, false, true);

        private boolean finalKill;
        private final boolean despawnable;
        private final boolean pvpLogOut;

        PlayerKillCause(boolean finalKill, boolean despawnable, boolean pvpLogOut) {
            this.finalKill = finalKill;
            this.despawnable = despawnable;
            this.pvpLogOut = pvpLogOut;
        }

        /**
         * Checks if the kill is a final kill.
         *
         * @return true if the kill is a final kill, false otherwise.
         */
        public boolean isFinalKill() {
            return finalKill;
        }

        /**
         * Sets whether the kill is a final kill.
         *
         * @param finalKill true if the kill is a final kill, false otherwise.
         */
        public void setFinalKill(boolean finalKill) {
            this.finalKill = finalKill;
        }

        /**
         * Checks if the victim is killed by a player's ironGolem, silverfish etc.
         *
         * @return true if the killer is killed by a player's ironGolem, silverfish etc, false otherwise.
         */
        public boolean isDespawnable() {
            return despawnable;
        }

        /**
         * Checks if the kill is due to a PvP log out.
         *
         * @return true if the kill is due to a PvP log out, false otherwise.
         */
        public boolean isPvpLogOut() {
            return pvpLogOut;
        }
    }

    /**
     * Get the killer player. Can be NULL (e.g., in cases of falling into void).
     *
     * @return The player who performed the kill.
     */
    public Player getKiller() {
        return killer;
    }

    /**
     * Get the function that generates the kill chat message.
     *
     * @return The function generating the kill chat message.
     */
    public Function<Player, String> getMessage() {
        return message;
    }

    /**
     * Set the function that generates the kill chat message.
     *
     * @param message The function to set.
     */
    public void setMessage(Function<Player, String> message) {
        this.message = message;
    }

    /**
     * Get the cause of the player's death.
     *
     * @return The cause of the player's death.
     */
    public PlayerKillCause getCause() {
        return cause;
    }

    /**
     * Get the arena where the kill occurred.
     *
     * @return The arena where the kill occurred.
     */
    public IArena getArena() {
        return arena;
    }

    /**
     * Get the player who died.
     *
     * @return The player who died.
     */
    public Player getVictim() {
        return victim;
    }

    /**
     * Checks if the kill sound should be played for the killer.
     *
     * @return true if the kill sound should be played for the killer, false otherwise.
     */
    public boolean playSound() {
        return playSound;
    }

    /**
     * Set whether the kill sound should be played for the killer.
     *
     * @param playSound true to play the kill sound for the killer, false otherwise.
     */
    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
