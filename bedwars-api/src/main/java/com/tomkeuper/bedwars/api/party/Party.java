/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
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
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.tomkeuper.bedwars.api.party;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The Party interface represents a party system for players.
 * Implementations of this interface provide methods for managing parties and their members.
 */
public interface Party {
    /**
     * Checks if a player has a party.
     *
     * @param player The player to check.
     * @return {@code true} if the player has a party, {@code false} otherwise.
     */
    boolean hasParty(Player player);

    /**
     * Retrieves the size of the party that the player belongs to.
     *
     * @param player The player.
     * @return The size of the party that the player belongs to.
     */
    int partySize(Player player);

    /**
     * Checks if the player is the owner/leader of the party.
     *
     * @param player The player to check.
     * @return {@code true} if the player is the owner/leader of the party, {@code false} otherwise.
     */
    boolean isOwner(Player player);

    /**
     * Retrieves a list of all members in the party.
     *
     * @param owner The owner/leader of the party.
     * @return A list of all members in the party.
     */
    List<Player> getMembers(Player owner);

    /**
     * Creates a new party with the specified owner and members.
     *
     * @param owner   The owner/leader of the party.
     * @param members Additional members to add to the party.
     */
    void createParty(Player owner, Player... members);

    /**
     * Adds a member to the party.
     *
     * @param owner  The owner/leader of the party.
     * @param member The member to add to the party.
     */
    void addMember(Player owner, Player member);

    /**
     * Removes a member from the party.
     *
     * @param member The member to remove from the party.
     */
    void removeFromParty(Player member);

    /**
     * Disbands the party, removing all members and deleting the party.
     *
     * @param owner The owner/leader of the party.
     */
    void disband(Player owner);

    /**
     * Checks if a player is a member of a specific party.
     *
     * @param owner The owner/leader of the party.
     * @param check The player to check.
     * @return {@code true} if the player is a member of the party, {@code false} otherwise.
     */
    boolean isMember(Player owner, Player check);

    /**
     * Removes a player from the party.
     *
     * @param owner  The owner/leader of the party.
     * @param target The player to remove.
     */
    void removePlayer(Player owner, Player target);

    /**
     * Retrieves the owner/leader of the party that the specified member belongs to.
     *
     * @param member The member of the party.
     * @return The owner/leader of the party.
     */
    default Player getOwner(Player member) {
        for (Player m : getMembers(member)) {
            if (isOwner(m)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Promotes a player to the owner/leader of the party (Not implemented).
     * Sends a message to the owner and target players indicating that this feature is not implemented.
     *
     * @param owner  The current owner/leader of the party.
     * @param target The player to promote.
     */
    default void promote(@NotNull Player owner, @NotNull Player target) {
        String msg = ChatColor.RED + "Not implemented! Contact an administrator";
        owner.sendMessage(msg);
        target.sendMessage(msg);
    }

    /**
     * Checks if the party implementation is internal or external.
     *
     * @return {@code true} if the party implementation is internal, {@code false} otherwise.
     */
    boolean isInternal();
}