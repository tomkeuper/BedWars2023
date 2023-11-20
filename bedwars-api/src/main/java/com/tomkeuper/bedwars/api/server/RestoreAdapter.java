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

package com.tomkeuper.bedwars.api.server;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public abstract class RestoreAdapter {

    private final Plugin plugin;

    /**
     * Constructor for RestoreAdapter.
     *
     * @param owner The owner plugin of the adapter.
     */
    public RestoreAdapter(Plugin owner) {
        this.plugin = owner;
    }

    /**
     * Get the owner plugin of the adapter.
     *
     * @return The owner plugin.
     */
    public Plugin getOwner() {
        return plugin;
    }

    /**
     * Load the world.
     * Arenas will be initialized automatically based on WorldLoadEvent.
     *
     * @param arena The arena to enable.
     */
    public abstract void onEnable(IArena arena);

    /**
     * Restore the world.
     * Call new Arena when it's done.
     *
     * @param arena The arena to restart.
     */
    public abstract void onRestart(IArena arena);

    /**
     * Unload the world.
     * This is usually used for /bw unloadArena name.
     *
     * @param arena The arena to disable.
     */
    public abstract void onDisable(IArena arena);

    /**
     * Load the world for setting it up.
     *
     * @param setupSession The setup session containing the world to load.
     */
    public abstract void onSetupSessionStart(ISetupSession setupSession);

    /**
     * Unload the world.
     *
     * @param setupSession The setup session containing the world to unload.
     */
    public abstract void onSetupSessionClose(ISetupSession setupSession);

    /**
     * Remove lobby blocks.
     *
     * @param arena The arena to remove lobby blocks from.
     */
    public void onLobbyRemoval(@NotNull IArena arena) {
        this.foreachBlockInRegion(
                arena.getConfig().getArenaLoc(ConfigPath.ARENA_WAITING_POS1),
                arena.getConfig().getArenaLoc(ConfigPath.ARENA_WAITING_POS2),
                (block) -> block.setType(Material.AIR)
        );

        Bukkit.getScheduler().runTaskLater(getOwner(), () -> clearItems(arena.getWorld()), 15L);
    }

    /**
     * Check if the given world exists.
     *
     * @param name The name of the world to check.
     * @return `true` if the world exists, `false` otherwise.
     */
    public abstract boolean isWorld(String name);

    /**
     * Delete a world.
     *
     * @param name The name of the world to delete.
     */
    public abstract void deleteWorld(String name);

    /**
     * Clone an arena world.
     *
     * @param sourceArena The name of the source world to clone.
     * @param destinationArena The name of the destination world to create.
     */
    public abstract void cloneArena(String sourceArena, String destinationArena);

    /**
     * Get the list of worlds.
     *
     * @return The list of world names.
     */
    public abstract List<String> getWorldsList();

    /**
     * Convert worlds if necessary before loading them.
     * Let them load on BedWars2023 main Thread, so they will be converted before getting loaded.
     */
    public abstract void convertWorlds();

    /**
     * Get the display name of the restore adapter.
     *
     * @return The display name.
     */
    public abstract String getDisplayName();

    public void foreachBlockInRegion(
            @Nullable Location corner1, @Nullable Location corner2,
            @NotNull Consumer<Block> consumer
    ) {
        if (null == corner1 || null == corner2) {
            return;
        }

        Vector min = new Vector(
                Math.min(corner1.getBlockX(), corner2.getBlockX()),
                Math.min(corner1.getBlockY(), corner2.getBlockY()),
                Math.min(corner1.getBlockZ(), corner2.getBlockZ())
        );

        Vector max = new Vector(
                Math.max(corner1.getBlockX(), corner2.getBlockX()),
                Math.max(corner1.getBlockY(), corner2.getBlockY()),
                Math.max(corner1.getBlockZ(), corner2.getBlockZ())
        );

        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                    consumer.accept(corner1.getWorld().getBlockAt(x, y, z));
                }
            }
        }
    }

    /**
     * Clear all entities for a given world
     *
     * @param world The world instance.
     */
    public void clearItems(@NotNull World world) {
        world.getEntities().forEach(e -> {
            if (e instanceof Item) e.remove();
        });
    }
}