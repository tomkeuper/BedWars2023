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

package com.tomkeuper.bedwars.arena.tasks;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.NextEvent;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.tasks.StartingTask;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.team.BedWarsTeam;
import com.tomkeuper.bedwars.arena.team.LegacyTeamAssigner;
import com.tomkeuper.bedwars.configuration.Sounds;
import com.tomkeuper.bedwars.support.papi.SupportPAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import static com.tomkeuper.bedwars.BedWars.config;
import static com.tomkeuper.bedwars.api.language.Language.getList;
import static com.tomkeuper.bedwars.api.language.Language.getMsg;

public class GameStartingTask implements Runnable, StartingTask {

    /**
     * -- GETTER --
     *  Get countdown value
     */
    @Setter
    @Getter
    private int countdown;
    /**
     * -- GETTER --
     *  Get arena
     */
    @Getter
    private final IArena arena;
    private final BukkitTask task;

    public GameStartingTask(Arena arena) {
        this.arena = arena;
        countdown = config.getInt(ConfigPath.GENERAL_CONFIGURATION_START_COUNTDOWN_REGULAR);
        task = Bukkit.getScheduler().runTaskTimer(BedWars.plugin, this, 0, 20L);
    }


    /**
     * Get task ID
     */
    public int getTask() {
        return task.getTaskId();
    }

    @Override
    public BukkitTask getBukkitTask() {
        return task;
    }

    @Override
    public void run() {
        if (countdown == 0) {
            if (config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_EXPERIMENTAL_TEAM_ASSIGNER)) {
                getArena().getTeamAssigner().assignTeams(getArena());
            } else {
                LegacyTeamAssigner.assignTeams(getArena());
            }


            //Color bed block if possible
            //Destroy bed if team is empty
            //Spawn shops and upgrades
            //Disable generators for empty teams if required
            for (ITeam team : getArena().getTeams()) {
                BedWars.nms.colorBed(team);
                if (team.getMembers().isEmpty()) {
                    team.setBedDestroyed(true);
                    if (getArena().getConfig().getBoolean(ConfigPath.ARENA_DISABLE_GENERATOR_FOR_EMPTY_TEAMS)) {
                        for (IGenerator gen : team.getGenerators()) {
                            gen.disable();
                        }
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
                //Enable diamond/ emerald generators
                for (IGenerator og : getArena().getOreGenerators()) {
                    if (og.getType() == GeneratorType.EMERALD || og.getType() == GeneratorType.DIAMOND)
                        og.enableRotation();
                }
            }, 60L);

            //Spawn players
            spawnPlayers();

            //Lobby removal
            BedWars.getAPI().getRestoreAdapter().onLobbyRemoval(arena);


            task.cancel();
            getArena().changeStatus(GameState.playing);

            // Check if emerald should be first based on time
            if (getArena().getUpgradeDiamondsCount() < getArena().getUpgradeEmeraldsCount()) {
                getArena().setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_II);
            } else {
                getArena().setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_II);
            }

            //Spawn shopkeepers
            for (ITeam bwt : getArena().getTeams()) {
                bwt.spawnNPCs();
            }
            return;
        }

        //Send countdown
        if (getCountdown() % 10 == 0 || getCountdown() <= 5) {
            if (getCountdown() < 5) {
                Sounds.playSound(ConfigPath.SOUNDS_COUNTDOWN_TICK_X + getCountdown(), getArena().getPlayers());
            } else {
                Sounds.playSound(ConfigPath.SOUNDS_COUNTDOWN_TICK, getArena().getPlayers());
            }
            for (Player player : getArena().getPlayers()) {
                Language playerLang = Language.getPlayerLanguage(player);
                String[] titleSubtitle = Language.getCountDownTitle(playerLang, getCountdown());
                BedWars.nms.sendTitle(player, titleSubtitle[0], titleSubtitle[1], 0, 20, 10);
                player.sendMessage(getMsg(player, Messages.ARENA_STATUS_START_COUNTDOWN_CHAT).replace("%bw_time%", String.valueOf(getCountdown())));
            }
        }
        countdown--;
    }

    //Spawn players
    private void spawnPlayers() {
        System.out.println("Spawning players...");
        List<Player> repeatedPlayers = new ArrayList<>();
        List<Player> playersToReAdd = new ArrayList<>();
        for (ITeam bwt : getArena().getTeams()) {
            for (Player p : bwt.getMembers()) {
                if (repeatedPlayers.contains(p)) {
                    playersToReAdd.add(p);
                }
                else repeatedPlayers.add(p);
            }

            for (Player p : playersToReAdd) {
                bwt.getMembers().remove(p);
                bwt.getMembers().add(p);
            }
        }

        playersToReAdd.clear();
        repeatedPlayers.clear();
        for (ITeam bwt : getArena().getTeams()) {
            System.out.println(bwt.getMembers());
            for (Player p : new ArrayList<>(bwt.getMembers())) {
                System.out.println("Spawning player: " + p.getName());
                BedWarsTeam.reSpawnInvulnerability.put(p.getUniqueId(), System.currentTimeMillis() + 2000L);
                bwt.firstSpawn(p);
                Sounds.playSound(ConfigPath.SOUND_GAME_START, p);
                BedWars.nms.sendTitle(p, getMsg(p, Messages.ARENA_STATUS_START_PLAYER_TITLE), null, 0, 30, 10);
                for (String tut : getList(p, Messages.ARENA_STATUS_START_PLAYER_TUTORIAL)) {
                    p.sendMessage(SupportPAPI.getSupportPAPI().replace(p, tut));
                }
            }
        }
    }

    public void cancel() {
        task.cancel();
    }
}
