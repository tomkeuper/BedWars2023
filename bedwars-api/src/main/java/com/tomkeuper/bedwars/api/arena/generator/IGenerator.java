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

package com.tomkeuper.bedwars.api.arena.generator;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.entity.GeneratorHolder;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;

public interface IGenerator {

    /**
     * Get holograms associated to players and generators
     */
    HashMap<Player, IGenHolo> getPlayerHolograms();

    /**
     * Disable a generator and remove the holograms.
     */
    void disable();

    /**
     * Enable a generator and respawn the holograms.
     */
    void enable();

    /**
     * Manage what to do when the generator upgrade is called from {@link IArena#updateNextEvent}
     */
    void upgrade();

    /**
     * This will attempt to spawn the items every second.
     */
    void spawn();

    /**
     * This will drop the item at a given location.
     *
     * @param location You can customize this location in order to drop items near a player if it's a base generator with multiple teammates.
     */
    void dropItem(Location location);

    /**
     * Change the item that this generator will spawn.
     */
    void setOre(ItemStack ore);

    /**
     * Get the arena assigned to this generator.
     */
    IArena getArena();

    /**
     * Get the animation of the generator.
     *
     * @return the animation of the generator
     */
    List<IGeneratorAnimation> getAnimations();

    /**
     * Set the animation of the generator.
     *
     * @param animations the animation of the generator
     */
    void addAnimation(IGeneratorAnimation animations);

    /**
     * This method is called every tick to manage the animation of the generator.
     */
    void rotate();

    /**
     * Change item spawn delay. In seconds.
     * Internally any corrections will be made to compensate for the increased speed
     */
    void setDelay(double delay);

    /**
     * Set how many items should the generator spawn at once.
     */
    void setAmount(int amount);

    /**
     * Get the generator location.
     */
    Location getLocation();
    /**
     * set the generator location.
     *
     * @param location new generator drop location.
     */
    void setLocation(Location location);

    /**
     * Get generator ore.
     */
    ItemStack getOre();

    /**
     * This will update the holograms for one player.
     * Holograms are only visible to players in the same world.
     * <p>
     * @param p player to update holograms for.
     * <p>
     * This method only works for generators that are not team generators,
     * executing it on a team generator will do nothing.
     */
    void updateHolograms(Player p);

    /**
     * Enable generator rotation.
     * Make sure it has a helmet set.
     * DIAMOND and EMERALD generator types will get
     * the rotation activated when the arena starts.
     * If you want to have a different rotating type you should call this manually at {@link GameStateChangeEvent}
     */
    void enableRotation();

    /**
     * This is the limit when the generator will stop spawning new items until they are collected.
     */
    void setSpawnLimit(int value);

    /**
     * Get the team assigned to this generator.
     *
     * @return null if this is not a team generator.
     * @deprecated replaced by getBedWarsTeam()
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
    ITeam getBwt();

    /**
     * Get the team assigned to this generator.
     *
     * @return null if this is not a team generator.
     */
    ITeam getBedWarsTeam();

    /**
     * Get generator hologram holder (armor stand) containing the rotating item.
     *
     * @return null if there is no rotating item.
     */
    GeneratorHolder getHologramHolder();

    /**
     * Get generator type.
     */
    GeneratorType getType();

    /**
     * Get the amount of items that are dropped once.
     */
    int getAmount();

    /**
     * Get spawn rate delay.
     */
    double getDelay();

    /**
     * Get seconds before next item spawn.
     */
    double getNextSpawn();

    /**
     * Get the spawn limit of the generators.
     * If there is this amount of items dropped near the generator
     * it will stop spawning new items.
     */
    int getSpawnLimit();

    /**
     * Set the remaining time till the next item spawn.
     *
     * @param nextSpawn time to next spawn in seconds
     */
    void setNextSpawn(double nextSpawn);

    /**
     * Should the dropped items be stacked?
     */
    void setStack(boolean stack);

    /**
     * Check if the dropped items can be stacked.
     */
    boolean isStack();

    /**
     * Check if the generator hologram is enabled.
     */
    boolean isHologramEnabled();

    /**
     * Set generator type.
     * This may break things.
     */
    void setType(GeneratorType type);

    /**
     * This only must be called by the arena instance when it restarts.
     * Do never call it unless you have a custom arena.
     * Manage your data destroy.
     */
    void destroyData();
}
