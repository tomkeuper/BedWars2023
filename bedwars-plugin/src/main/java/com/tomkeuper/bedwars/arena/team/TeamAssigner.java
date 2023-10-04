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

package com.tomkeuper.bedwars.arena.team;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.arena.team.ITeamAssigner;
import com.tomkeuper.bedwars.api.events.gameplay.TeamAssignEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TeamAssigner implements ITeamAssigner {

    private final LinkedList<Player> skip = new LinkedList<>();

    /**
     * Assigns teams to players in the given arena.
     *
     * @param arena The arena to assign teams in.
     */
    public void assignTeams(IArena arena) {

        // setup team up parties first
        if (arena.getPlayers().size() > arena.getMaxInTeam() && arena.getMaxInTeam() > 1) {
            LinkedList<List<Player>> teams = new LinkedList<>();

            // get all parties
            List<Player> members;
            for (Player player : arena.getPlayers()) {
                if (!BedWars.getParty().isOwner(player)) continue; //prevent processing 1 party multiple times

                members = BedWars.getParty().getMembers(player);
                if (members == null) continue;
                members = new ArrayList<>(members);

                if (members.isEmpty()) continue;
                members.removeIf(member -> !arena.isPlayer(member));
                if (members.isEmpty()) continue;

                teams.add(members);
            }

            // prioritize bigger teams
            if (!teams.isEmpty()) {
                for (ITeam team : arena.getTeams()) {
                    teams.sort(Comparator.comparingInt(List::size)); // sort teams based on player amount.
                    if (teams.isEmpty()) break;
                    if (teams.getFirst().isEmpty()) break;

                    for (int i = 0; i < arena.getMaxInTeam() && team.getMembers().size() < arena.getMaxInTeam(); i++) {
                        if (teams.isEmpty()) break;
                        if (teams.get(0).size() > i) {
                            List<Player> players = teams.poll(); // get and remove the head of teams list
                            for (Player toAdd : players) {
                                if (team.getSize() >= arena.getMaxInTeam())
                                    continue; //prevent bigger teams from joining the same team.

                                TeamAssignEvent e = new TeamAssignEvent(toAdd, team, arena);
                                Bukkit.getPluginManager().callEvent(e);
                                if (!e.isCancelled()) {
                                    toAdd.closeInventory();
                                    team.addPlayers(toAdd);
                                    skip.add(toAdd);
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        // Party teams are already filled
        List<Player> remainingPlayers = new ArrayList<>();
        for (Player player : arena.getPlayers()) { // Make sure to only get players who are NOT in a team already
            if (skip.contains(player)) continue;
            remainingPlayers.add(player);
        }
        if (remainingPlayers.isEmpty()) return;

        BedWars.debug("Assigning teams for arena: " + arena.getArenaName() + " with; max in team: " + arena.getMaxInTeam() + " size: " + arena.getPlayers().size() + " teams: " + arena.getTeams().size());
        for (Player player: remainingPlayers) {
            player.closeInventory();
            findTargetTeam(arena.getTeams(),arena.getMaxInTeam(), arena.getPlayers().size()).addPlayers(player);
        }
    }

    /**
     * Finds the target team to add a player based on the following criteria:
     * - If there is a team with fewer players than the maximum allowed per team, and fewer than maxPlayersPerTeam - 1 players,
     *   it returns that team.
     * - If player amount > 2, it will check for teams with player count of 1 to return as target
     * - If no such team is found, it returns the first team with available space (i.e., fewer players than maxPlayersPerTeam).
     * - If no team with available space is found, it returns null (arena = full).
     *
     * @param teams             The list of teams to search for the target team.
     * @param maxPlayersPerTeam The maximum number of players allowed per team.
     * @param playerAmount      The amount of players in an arena.
     * @return The target team to add a player, or null if no suitable team is found.
     */
    private static ITeam findTargetTeam(List<ITeam> teams, int maxPlayersPerTeam, int playerAmount) {
        ITeam targetTeam = null;
        int minPlayers = Integer.MAX_VALUE;

        // Find a team with fewer players than maxPlayersPerTeam - 1
        for (ITeam team : teams) {
            int numPlayers = team.getSize();
            if (numPlayers < minPlayers && numPlayers < maxPlayersPerTeam - 1) {
                targetTeam = team;
                minPlayers = numPlayers;
            }

            if (numPlayers == 1 && (playerAmount > 2)) {
                BedWars.debug("found team with 1 player (" + team.getName() + ")");
                return team;
            }
        }

        // If no suitable team is found, find the first team with available space and no more than 1 player
        if (targetTeam == null) {
            for (ITeam team : teams) {
                if (team.getSize() == 1 && playerAmount <= maxPlayersPerTeam) {
                    // Skip teams with 1 player when playerAmount is set to 2
                    continue;
                }
                if (team.getSize() < maxPlayersPerTeam - 1) {
                    targetTeam = team;
                    break;
                }
            }
        }

        // If ALL other team assigners fail. Fall back to first open spot
        if (targetTeam == null) {
            for (ITeam team : teams) {
                if (team.getSize() < maxPlayersPerTeam) {
                    targetTeam = team;
                    break;
                }
            }
        }

        return targetTeam;
    }
}
