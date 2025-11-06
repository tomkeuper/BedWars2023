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

package com.tomkeuper.bedwars.shop.custom.SlimeJump;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles Slime Jump block mechanics
 * - Base tier: Instant launch forward
 * - Gold tier: Slow/high launch upward
 */
public class SlimeJumpListener implements Listener {

    private static final Map<Location, String> slimeBlocks = new HashMap<>();
    private static final Map<UUID, Long> launchCooldown = new HashMap<>();
    private static final long COOLDOWN_TIME = 500; // 0.5 seconds

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        if (block.getType() == Material.SLIME_BLOCK) {
            IArena arena = Arena.getArenaByPlayer(player);
            if (arena == null || !arena.isPlayer(player)) return;

            // Check if it's a Slime Jump item
            if (event.getItemInHand().hasItemMeta()
                    && event.getItemInHand().getItemMeta().hasDisplayName()
                    && event.getItemInHand().getItemMeta().getDisplayName().contains("Slime Jump")) {

                String displayName = event.getItemInHand().getItemMeta().getDisplayName();
                String tier = "base";

                if (displayName.contains("Slow/High") || displayName.contains("Gold")) {
                    tier = "gold";
                }

                slimeBlocks.put(block.getLocation(), tier);
                arena.addPlacedBlock(block);

                player.sendMessage(ChatColor.GREEN + "Slime Jump placed! Step on it to launch.");
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        UUID playerUUID = player.getUniqueId();
        Long lastLaunch = launchCooldown.get(playerUUID);
        if (lastLaunch != null && System.currentTimeMillis() - lastLaunch < COOLDOWN_TIME) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        IArena arena = Arena.getArenaByPlayer(player);
        if (arena == null || !arena.isPlayer(player)) return;
        Block blockBelow = to.getBlock().getRelative(0, -1, 0);

        if (blockBelow.getType() == Material.SLIME_BLOCK) {
            Location blockLocation = blockBelow.getLocation();
            String tier = slimeBlocks.get(blockLocation);

            if (tier != null) {
                launchPlayer(player, tier);
                launchCooldown.put(playerUUID, System.currentTimeMillis());
                slimeBlocks.remove(blockLocation);
                blockBelow.setType(Material.AIR);
                arena.removePlacedBlock(blockBelow);
            }
        }
    }

    private void launchPlayer(Player player, String tier) {
        Vector velocity;

        if (tier.equals("base")) {
            Vector direction = player.getLocation().getDirection().normalize();
            velocity = new Vector(direction.getX() * 2.0, 0.5, direction.getZ() * 2.0);
            player.sendMessage(ChatColor.GREEN + "⚡ Instant Launch!");
        } else {
            Vector direction = player.getLocation().getDirection().normalize();
            velocity = new Vector(direction.getX() * 0.5, 1.5, direction.getZ() * 0.5);
            player.sendMessage(ChatColor.GOLD + "⬆ High Launch!");
        }

        player.setVelocity(velocity);
        player.setFallDistance(0);
    }

    public static void removeSlimeBlock(Location location) {
        slimeBlocks.remove(location);
    }

    public static void clearArenaSlimes(IArena arena) {
        slimeBlocks.entrySet().removeIf(entry ->
                entry.getKey().getWorld().getName().equals(arena.getWorldName())
        );
    }
}