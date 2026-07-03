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
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.gameplay.EggBridgeThrowEvent;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.server.ServerType;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.tasks.EggBridgeTask;
import com.tomkeuper.bedwars.listeners.chat.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.tomkeuper.bedwars.BedWars.config;
import static com.tomkeuper.bedwars.api.language.Language.getMsg;

@SuppressWarnings("WeakerAccess")
public class EggBridge implements Listener {

    //Active eggBridges
    private static final Map<Egg, EggBridgeTask> bridges = new HashMap<>();

    /**
     * Remove an egg from the active eggs list
     *
     * @since API 7
     */
    public static void removeEgg(Egg e) {
        if (bridges.containsKey(e)) {
            if (bridges.get(e) != null) {
                bridges.get(e).cancel();
            }
            bridges.remove(e);
        }
    }

    /**
     * Get active egg bridges.
     * Modified  in api 11
     *
     * @since API 11
     */
    public static Map<Egg, EggBridgeTask> getBridges() {
        return Collections.unmodifiableMap(bridges);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (BedWars.getServerType() == ServerType.MULTIARENA) {
            if (event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(BedWars.getLobbyWorld())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getEntity() instanceof Egg) {
            Egg projectile = (Egg) event.getEntity();
            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }

            Player shooter = (Player) projectile.getShooter();
            IArena arena = Arena.getArenaByPlayer(shooter);
            if (arena == null) {
                return;
            }

            if (!arena.isPlayer(shooter)) {
                return;
            }

            boolean isCloseToMaxBuildLimit = shooter.getLocation().getY() > arena.getConfig().getInt(ConfigPath.ARENA_CONFIGURATION_MAX_BUILD_Y) - config.getInt(ConfigPath.GENERAL_EGGBRIDGE_MAX_BUILD_LIMIT_WARNING_DISTANCE);
            boolean isCloseToMinBuildLimit = shooter.getLocation().getY() < arena.getConfig().getInt(ConfigPath.ARENA_CONFIGURATION_MIN_BUILD_Y) + config.getInt(ConfigPath.GENERAL_EGGBRIDGE_MIN_BUILD_LIMIT_WARNING_DISTANCE);
            boolean isUnderBuildLimit = shooter.getLocation().getY() < arena.getConfig().getInt(ConfigPath.ARENA_CONFIGURATION_MIN_BUILD_Y);
            boolean isAboveLimit = shooter.getLocation().getY() > arena.getConfig().getInt(ConfigPath.ARENA_CONFIGURATION_MAX_BUILD_Y);
            boolean isLookingUp = shooter.getLocation().getPitch() < -5.0;
            boolean isLookingDown = shooter.getLocation().getPitch() > 5.0;

            if ((isCloseToMaxBuildLimit && isLookingUp) || (isCloseToMinBuildLimit && isLookingDown) || isUnderBuildLimit || isAboveLimit) {
                if (config.getBoolean(ConfigPath.GENERAL_EGGBRIDGE_BUILD_LIMIT_WARN_PLAYER)) {
                    BedWars.plugin.adventure().player(shooter).sendMessage(ChatFormatting.parseLegacyMini(getMsg(shooter, Messages.EGGBRIDGE_BUILD_LIMIT_WARNING)));
                }
                if (config.getBoolean(ConfigPath.GENERAL_EGGBRIDGE_BUILD_LIMIT_CANCEL_USAGE)) {
                    if (shooter.getGameMode() != GameMode.CREATIVE) {
                        shooter.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    event.setCancelled(true);
                    return;
                }
            }

            EggBridgeThrowEvent throwEvent = new EggBridgeThrowEvent(shooter, arena);
            Bukkit.getPluginManager().callEvent(throwEvent);
            if (event.isCancelled()) {
                event.setCancelled(true);
                return;
            }

            bridges.put(projectile, new EggBridgeTask(shooter, projectile, arena.getTeam(shooter).getColor()));
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Egg) {
            removeEgg((Egg) e.getEntity());
        }
    }
}