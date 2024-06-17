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

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerInvisibilityPotionEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tomkeuper.bedwars.BedWars.nms;
import static com.tomkeuper.bedwars.BedWars.plugin;

/**
 * This is used to hide and show player name tag above head when he drinks an invisibility
 * potion or when the potion is gone. It is required because it is related to scoreboards.
 */
public class InvisibilityPotionListener implements Listener {
    private final List<Player> invisiblePlayers = new ArrayList<>();
    private final boolean footstepsEnabled = BedWars.config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_ENABLE_FOOTSTEPS_ON_INVISIBILITY);
    private final HashMap<Player, Integer> steps = new HashMap<>();

    @EventHandler
    public void onPotion(PlayerInvisibilityPotionEvent e) {
        if (!footstepsEnabled) return;
        if (e.getType() == PlayerInvisibilityPotionEvent.Type.ADDED) {
            if (this.invisiblePlayers.contains(e.getPlayer())) return;
            this.invisiblePlayers.add(e.getPlayer());
            steps.put(e.getPlayer(), 12);
        } else if (e.getType() == PlayerInvisibilityPotionEvent.Type.REMOVED) {
            this.invisiblePlayers.remove(e.getPlayer());
            steps.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        for (Player p : e.getArena().getPlayers()) {
            if (this.invisiblePlayers.contains(p)) {
                this.invisiblePlayers.remove(p);
                steps.remove(p);
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveArenaEvent e) {
        Player p = e.getPlayer();
        if (this.invisiblePlayers.contains(p)) {
            this.invisiblePlayers.remove(p);
            steps.remove(p);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!footstepsEnabled) return;
        Player p = e.getPlayer();
        if (!this.invisiblePlayers.contains(p)) return;

        // TODO implement particles on higher version (issue: #112)
        if (nms.getVersion() > 5) return; // check if higher than 1.12

        if (p.isSneaking()) return;
        Material blockBelow = p.getLocation().clone().add(0, -1, 0).getBlock().getType();
        if (blockBelow == Material.AIR) return;
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getBlock() != to.getBlock()) {
            if (this.steps.get(p) == 6) {
                p.getWorld().playEffect(p.getLocation().add(0.0D, 0.01D, 0.4D), Effect.FOOTSTEP, 1);
                this.steps.put(p, steps.get(p) - 1);
            } else if (this.steps.get(p) <= 0) {
                p.getWorld().playEffect(p.getLocation().add(0.4D, 0.01D, 0.0D), Effect.FOOTSTEP, 1);
                this.steps.put(p, 12);
            } else {
                this.steps.put(p, steps.get(p) - 1);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerKillEvent event) {
        if (this.invisiblePlayers.contains(event.getVictim())) {
            event.getArena().getShowTime().remove(event.getVictim());
            Bukkit.getPluginManager().callEvent(new PlayerInvisibilityPotionEvent(PlayerInvisibilityPotionEvent.Type.REMOVED, event.getArena().getTeam(event.getVictim()), event.getVictim(), event.getArena()));
        }
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        IArena a = Arena.getArenaByPlayer(e.getPlayer());
        if (a == null) return;
        if (e.getItem().getType() != Material.POTION) return;
        // remove potion bottle
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                        nms.minusAmount(e.getPlayer(), new ItemStack(Material.GLASS_BOTTLE), 1),
                5L);
        //

        if (nms.isInvisibilityPotion(e.getItem())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (PotionEffect pe : e.getPlayer().getActivePotionEffects()) {
                    if (pe.getType().toString().contains("INVISIBILITY")) {
                        // if is already invisible
                        if (a.getShowTime().containsKey(e.getPlayer())) {
                            ITeam t = a.getTeam(e.getPlayer());
                            // increase invisibility timer
                            // keep trace of invisible players to send hide armor packet when required
                            // because potions do not hide armors
                            a.getShowTime().replace(e.getPlayer(), pe.getDuration() / 20);
                            // call custom event
                            Bukkit.getPluginManager().callEvent(new PlayerInvisibilityPotionEvent(PlayerInvisibilityPotionEvent.Type.ADDED, t, e.getPlayer(), t.getArena()));
                        } else {
                            // if not already invisible
                            ITeam t = a.getTeam(e.getPlayer());
                            // keep track of invisible players to send hide armor packet when required
                            // because potions do not hide armor
                            a.getShowTime().put(e.getPlayer(), pe.getDuration() / 20);
                            //
                            for (Player p1 : e.getPlayer().getWorld().getPlayers()) {
                                if (a.isSpectator(p1)) {
                                    // hide player armor to spectators
                                    nms.hideArmor(e.getPlayer(), p1);
                                } else if (t != a.getTeam(p1)) {
                                    // hide player armor to other teams
                                    nms.hideArmor(e.getPlayer(), p1);
                                }
                            }
                            // call custom event
                            Bukkit.getPluginManager().callEvent(new PlayerInvisibilityPotionEvent(PlayerInvisibilityPotionEvent.Type.ADDED, t, e.getPlayer(), t.getArena()));
                        }
                        break;
                    }
                }
            }, 5L);
        }
    }
}
