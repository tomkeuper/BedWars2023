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

package com.tomkeuper.bedwars.shop.custom.frezze;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeFreezeEggListener implements Listener {

    private static final Map<UUID, Player> freezeEggs = new HashMap<>();

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player thrower = (Player) event.getEntity().getShooter();
        Snowball egg = (Snowball) event.getEntity();
        IArena arena = Arena.getArenaByPlayer(thrower);
        if (arena == null || !arena.isPlayer(thrower)) return;
        ItemStack inHand = thrower.getItemInHand();
        if (inHand != null && inHand.getType() == Material.SNOW_BALL
                && inHand.hasItemMeta()
                && inHand.getItemMeta().hasDisplayName()) {
            String displayName = inHand.getItemMeta().getDisplayName();
            if (displayName.contains("Time Freeze") || displayName.contains("Freeze Egg")) {
                freezeEggs.put(egg.getUniqueId(), thrower);
                thrower.sendMessage(ChatColor.AQUA + "Time Freeze Egg launched!");
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;

        Snowball egg = (Snowball) event.getEntity();
        UUID eggUUID = egg.getUniqueId();

        if (!freezeEggs.containsKey(eggUUID)) return;

        Player thrower = freezeEggs.remove(eggUUID);
        if (thrower == null) return;

        IArena arena = Arena.getArenaByPlayer(thrower);
        if (arena == null) return;

        ITeam throwerTeam = arena.getTeam(thrower);
        if (throwerTeam == null) return;

        Location hitLocation = egg.getLocation();
        int frozenCount = 0;
        for (Player player : arena.getPlayers()) {
            if (player.equals(thrower)) continue;
            if (arena.isSpectator(player)) continue;

            ITeam playerTeam = arena.getTeam(player);
            if (playerTeam == null || playerTeam.equals(throwerTeam)) continue;
            Location playerLoc = player.getLocation();
            double distance = playerLoc.distance(hitLocation);

            if (distance <= 3.0) {
                freezePlayer(player, thrower);
                frozenCount++;
            }
        }
        hitLocation.getWorld().createExplosion(hitLocation, 0.0F, false);

        if (frozenCount > 0) {
            thrower.sendMessage(ChatColor.AQUA + "❄ Frozen " + frozenCount + " enemy player(s) for 3 seconds!");
        } else {
            thrower.sendMessage(ChatColor.RED + "No enemies in range!");
        }
    }

    private void freezePlayer(Player victim, Player thrower) {
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10, false, false)); // 3 seconds
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 60, 10, false, false)); // Mining fatigue
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 250, false, false)); // Can't jump (negative jump boost)
        victim.sendMessage(ChatColor.AQUA + "❄ You have been frozen by " + thrower.getName() + " for 3 seconds!");
        final Location frozenLocation = victim.getLocation().clone();

        new BukkitRunnable() {
            private int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 60 || !victim.isOnline() || Arena.getArenaByPlayer(victim) == null) {
                    if (victim.isOnline()) {
                        victim.sendMessage(ChatColor.GREEN + "✓ You are no longer frozen!");
                    }
                    cancel();
                    return;
                }
                Location current = victim.getLocation();
                if (current.distance(frozenLocation) > 0.5) {
                    victim.teleport(frozenLocation);
                }

                ticks++;
            }
        }.runTaskTimer(BedWars.plugin, 0L, 1L);
    }

    public static void clearFreezeEggs() {
        freezeEggs.clear();
    }
}