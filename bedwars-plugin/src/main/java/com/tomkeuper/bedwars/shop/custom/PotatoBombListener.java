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

package com.tomkeuper.bedwars.shop.custom;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Handles Potato Bomb tracking and damage mechanics
 * The potato bomb tracks the targeted player until they die
 */
public class PotatoBombListener implements Listener {

    // Tracks which players have purchased a potato bomb in their current match
    private static final Map<UUID, Set<String>> purchaseTracker = new HashMap<>();

    // Active tracking potatoes: Item UUID -> Target Player UUID
    private static final Map<UUID, UUID> activeTrackers = new HashMap<>();

    // Tracking tasks
    private static final Map<UUID, BukkitRunnable> trackingTasks = new HashMap<>();

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player thrower = event.getPlayer();
        Item droppedItem = event.getItemDrop();

        // Check if it's a potato bomb
        if (droppedItem.getItemStack().getType() == Material.POTATO_ITEM
                && droppedItem.getItemStack().hasItemMeta()
                && droppedItem.getItemStack().getItemMeta().hasDisplayName()
                && droppedItem.getItemStack().getItemMeta().getDisplayName().contains("Potato Bomb")) {

            IArena arena = Arena.getArenaByPlayer(thrower);
            if (arena == null || !arena.isPlayer(thrower)) return;

            // Find nearest enemy player
            Player target = findNearestEnemy(thrower, arena);

            if (target == null) {
                thrower.sendMessage(ChatColor.RED + "No enemy players nearby to track!");
                return;
            }

            // Start tracking
            startTracking(droppedItem, thrower, target, arena);
            thrower.sendMessage(ChatColor.GOLD + "Potato Bomb is now tracking " + ChatColor.RED + target.getName() + ChatColor.GOLD + "!");
            target.sendMessage(ChatColor.RED + "⚠ A Potato Bomb is tracking you!");
        }
    }

    private Player findNearestEnemy(Player thrower, IArena arena) {
        ITeam throwerTeam = arena.getTeam(thrower);
        if (throwerTeam == null) return null;

        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Player player : arena.getPlayers()) {
            if (player.equals(thrower)) continue;
            if (arena.isSpectator(player)) continue;

            ITeam playerTeam = arena.getTeam(player);
            if (playerTeam != null && playerTeam.equals(throwerTeam)) continue;

            double distance = player.getLocation().distance(thrower.getLocation());
            if (distance < nearestDistance && distance <= 30.0) { // Max 30 block range
                nearestDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    private void startTracking(Item potatoItem, Player thrower, Player target, IArena arena) {
        UUID itemUUID = potatoItem.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        activeTrackers.put(itemUUID, targetUUID);

        BukkitRunnable trackingTask = new BukkitRunnable() {
            private int ticks = 0;
            private final int maxTicks = 200; // 10 seconds max tracking

            @Override
            public void run() {
                // Check if potato item still exists
                if (potatoItem.isDead() || !potatoItem.isValid()) {
                    cleanup(itemUUID);
                    cancel();
                    return;
                }

                // Check if target is still valid
                if (!target.isOnline() || arena.isSpectator(target) || !arena.isPlayer(target)) {
                    cleanup(itemUUID);
                    potatoItem.remove();
                    cancel();
                    return;
                }

                // Check max time
                if (ticks >= maxTicks) {
                    target.sendMessage(ChatColor.GREEN + "The Potato Bomb lost track of you!");
                    cleanup(itemUUID);
                    potatoItem.remove();
                    cancel();
                    return;
                }

                // Move potato towards target
                Location potatoLoc = potatoItem.getLocation();
                Location targetLoc = target.getLocation().add(0, 1, 0);

                Vector direction = targetLoc.toVector().subtract(potatoLoc.toVector()).normalize();
                potatoItem.setVelocity(direction.multiply(0.8)); // Speed of tracking

                // Check if potato is close to target
                if (potatoLoc.distance(targetLoc) < 2.0) {
                    // Deal damage every tick when close
                    if (ticks % 5 == 0) { // Damage every 0.25 seconds
                        target.damage(2.0, thrower); // 1 heart damage

                        // Visual effect
                        target.getWorld().createExplosion(target.getLocation(), 0.0F, false);
                    }
                }

                ticks++;
            }
        };

        trackingTask.runTaskTimer(BedWars.plugin, 0L, 1L);
        trackingTasks.put(itemUUID, trackingTask);
    }

    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        Player victim = event.getVictim();
        UUID victimUUID = victim.getUniqueId();

        // Stop all potatoes tracking this victim
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, UUID> entry : activeTrackers.entrySet()) {
            if (entry.getValue().equals(victimUUID)) {
                toRemove.add(entry.getKey());

                // Find and remove the potato item
                for (Entity entity : victim.getWorld().getEntities()) {
                    if (entity instanceof Item && entity.getUniqueId().equals(entry.getKey())) {
                        entity.remove();
                        break;
                    }
                }
            }
        }

        for (UUID itemUUID : toRemove) {
            cleanup(itemUUID);
        }
    }

    private void cleanup(UUID itemUUID) {
        activeTrackers.remove(itemUUID);
        BukkitRunnable task = trackingTasks.remove(itemUUID);
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * Check if a player has already purchased a potato bomb this match
     */
    public static boolean hasPurchased(Player player, IArena arena) {
        UUID playerUUID = player.getUniqueId();
        String arenaIdentifier = arena.getArenaName();

        Set<String> arenas = purchaseTracker.get(playerUUID);
        return arenas != null && arenas.contains(arenaIdentifier);
    }

    /**
     * Mark that a player has purchased a potato bomb
     */
    public static void markPurchased(Player player, IArena arena) {
        UUID playerUUID = player.getUniqueId();
        String arenaIdentifier = arena.getArenaName();

        purchaseTracker.computeIfAbsent(playerUUID, k -> new HashSet<>()).add(arenaIdentifier);
    }

    /**
     * Clear purchase history for a player (called when they leave or arena restarts)
     */
    public static void clearPurchaseHistory(Player player, IArena arena) {
        UUID playerUUID = player.getUniqueId();
        String arenaIdentifier = arena.getArenaName();

        Set<String> arenas = purchaseTracker.get(playerUUID);
        if (arenas != null) {
            arenas.remove(arenaIdentifier);
            if (arenas.isEmpty()) {
                purchaseTracker.remove(playerUUID);
            }
        }
    }

    /**
     * Clear all purchase history for an arena (called on arena restart)
     */
    public static void clearArenaHistory(IArena arena) {
        String arenaIdentifier = arena.getArenaName();
        purchaseTracker.values().forEach(set -> set.remove(arenaIdentifier));
        purchaseTracker.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}