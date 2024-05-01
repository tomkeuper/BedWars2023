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

package com.tomkeuper.bedwars.api.region;

import org.bukkit.Location;

/**
 * The Region interface represents a region in a game world.
 * Implementations of this interface provide methods for checking if a location is within the region
 * and determining if the region is protected.
 */
public interface Region {

    /**
     * Checks if a given location is within the region.
     *
     * @param location The location to check.
     * @return {@code true} if the location is within the region, {@code false} otherwise.
     */
    boolean isInRegion(Location location);

    /**
     * Checks if the region is protected.
     *
     * @return {@code true} if the region is protected, {@code false} otherwise.
     */
    boolean isProtected();
}
