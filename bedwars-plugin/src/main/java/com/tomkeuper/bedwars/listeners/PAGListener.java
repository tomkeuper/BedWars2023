package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PAGListener implements Listener {

    @EventHandler
    public void onGameStart(GameStateChangeEvent e) {

        if (e.getNewState().equals(GameState.playing)) {

            int ironAmount = BedWars.pagConfig.getInt("Arenas." + e.getArena().getArenaName() + ".iron.amount");
            int ironDelay = BedWars.pagConfig.getInt("Arenas." + e.getArena().getArenaName() + ".iron.delay");
            int ironSpawnAmount = BedWars.pagConfig.getInt("Arenas." + e.getArena().getArenaName() + ".iron.spawn-limit");

            int goldAmount = BedWars.pagConfig.getInt("Arenas." + e.getArena().getArenaName() + ".gold.amount");
            int goldDelay = BedWars.pagConfig.getInt("Arenas." + e.getArena().getArenaName() + ".gold.delay");
            int goldSpawnAmount = BedWars.pagConfig.getInt("Arenas." + e.getArena().getArenaName() + ".gold.spawn-limit");

            for (ITeam team : e.getArena().getTeams()) {

                for (IGenerator gen : team.getGenerators()) {

                    if (gen.getType() == GeneratorType.IRON) {

                        if (ironAmount != 0) {
                            gen.setAmount(ironAmount);
                        }

                        if (ironDelay != 0) {
                            gen.setDelay(ironDelay);
                        }

                        if (ironSpawnAmount != 0) {
                            gen.setSpawnLimit(ironSpawnAmount);
                        }

                    } else if (gen.getType() == GeneratorType.GOLD) {

                        if (goldAmount != 0) {
                            gen.setAmount(goldAmount);
                        }

                        if (goldDelay != 0) {
                            gen.setDelay(goldDelay);
                        }

                        if (goldSpawnAmount != 0) {
                            gen.setSpawnLimit(goldSpawnAmount);
                        }

                    }

                }

            }

        }

    }

}
