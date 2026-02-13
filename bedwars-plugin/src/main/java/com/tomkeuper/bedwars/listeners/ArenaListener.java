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
package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import com.tomkeuper.bedwars.configuration.InvisConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ArenaListener implements Listener {
    private final Plugin plugin;
    private final InvisConfig invisConfig;

    public ArenaListener(Plugin plugin, InvisConfig invisConfig) {
        this.plugin = plugin;
        this.invisConfig = invisConfig;
    }
    @EventHandler
    public void onArenaStart(GameStateChangeEvent event) {
        IArena arena = event.getArena();
        if (arena != null) {
            List<Player> list = arena.getPlayers();
            if (list != null && event.getNewState() == GameState.playing) {
                if (this.invisConfig.isWoodSwordDisappearanceEnabled()) {
                    Bukkit.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
                        list.stream()
                                .filter(arena::isPlayer)
                                .forEach(p -> {
                                    if (p.getInventory().contains(Material.WOOD_SWORD) &&
                                            (p.getInventory().contains(Material.STONE_SWORD) ||
                                                    p.getInventory().contains(Material.GOLD_SWORD) ||
                                                    p.getInventory().contains(Material.IRON_SWORD) ||
                                                    p.getInventory().contains(Material.DIAMOND_SWORD))) {
                                        p.getInventory().remove(Material.WOOD_SWORD);
                                    }
                                });
                    }, 20L, 10L);
                }
                if (this.invisConfig.isRespawnSessionInvisibilityEnabled()) {
                    Bukkit.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
                        list.stream()
                                .filter(arena::isPlayer)
                                .forEach(p -> {
                                    if (arena.isReSpawning(p)) {
                                        p.addPotionEffect(new PotionEffect(
                                                PotionEffectType.INVISIBILITY,
                                                Integer.MAX_VALUE,
                                                1,
                                                false,
                                                false
                                        ));
                                    } else {
                                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                                    }
                                });
                    }, 20L, 10L);
                }
                if (event.getNewState() == GameState.restarting) {
                    Bukkit.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                        list.stream()
                                .filter(arena::isPlayer)
                                .forEach(p -> {
                                    if (arena.isSpectator(p)) {
                                        p.addPotionEffect(new PotionEffect(
                                                PotionEffectType.INVISIBILITY,
                                                Integer.MAX_VALUE,
                                                1,
                                                false,
                                                false
                                        ));
                                    }
                                });
                    }, 10L);
                }
            }
        }
    }
    @EventHandler
    public void onRespawning(PlayerReSpawnEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        Bukkit.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }, 3L);
    }
    @EventHandler
    public void onKill(PlayerKillEvent event) {
        if (this.invisConfig.isKillSoundEnabled() && event.getKiller() != null) {
            event.getKiller().playSound(
                    event.getKiller().getLocation(),
                    this.invisConfig.getKillSound(),
                    this.invisConfig.getKillSoundVolume(),
                    this.invisConfig.getKillSoundPitch()
            );
        }
    }
}
