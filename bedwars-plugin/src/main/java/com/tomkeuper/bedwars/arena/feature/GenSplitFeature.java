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

package com.tomkeuper.bedwars.arena.feature;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GenSplitFeature implements Listener {
    private static GenSplitFeature instance;

    public GenSplitFeature() {
        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);
    }

    public static void init() {
        if (BedWars.config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_PERFORMANCE_GENERATOR_SPLIT))
            if (instance == null) instance = new GenSplitFeature();
    }


    @EventHandler
    public void onIslandGenPickup(PlayerGeneratorCollectEvent e) {
        if (!e.isCancelled() && (e.getItemStack().getType() == Material.IRON_INGOT || e.getItemStack().getType() == Material.GOLD_INGOT)) {
            Location pl = e.getPlayer().getLocation();
            Player p = e.getPlayer();

            int splitRange = Arena.getArenaByPlayer(p).getConfig().getInt(ConfigPath.ARENA_GENERATOR_SPLIT_RANGE);
            List<Entity> nearbyEntities = (List) pl.getWorld().getNearbyEntities(pl, splitRange, splitRange, 2.0);

            for (Entity entity : pl.getWorld().getEntities()) {
                if (nearbyEntities.contains(entity) && entity instanceof Player) {
                    Player pickupPlayer = (Player) entity;
                    if (pickupPlayer.getUniqueId() != p.getUniqueId()) {
                        if (Arena.getArenaByPlayer(pickupPlayer) == null) BedWars.debug("pickupPlayer: " + pickupPlayer.getName() + " is not in an arena. Event triggered by: " + p.getName());
                        ITeam team = Arena.getArenaByPlayer(pickupPlayer).getTeam(p);
                        ITeam rt = Arena.getArenaByPlayer(pickupPlayer).getTeam(pickupPlayer);

                        if (team == rt) {
                            ItemStack item = new ItemStack(e.getItemStack().getType(), e.getAmount());
                            if (!BedWars.getAPI().getAFKUtil().isPlayerAFK(pickupPlayer)) pickupPlayer.getInventory().addItem(item);
                        }
                    }
                }
            }
        }
    }
}
