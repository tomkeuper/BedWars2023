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

package com.tomkeuper.bedwars.api.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IHologramManager {

    /**
     * Create a hologram with the given lines at the given location.
     *
     * @param p - the player to create the hologram for
     * @param location - the location to create the hologram at
     * @param lines - the lines to create the hologram from
     * @return the hologram
     */
    IHologram createHologram(Player p, Location location, String... lines);

    /**
     * Create a hologram with the given lines at the given location.
     *
     * @param p - the player to create the hologram for
     * @param location - the location to create the hologram at
     * @param lines - the lines to create the hologram from
     * @return the hologram
     */
    IHologram createHologram(Player p, Location location, IHoloLine... lines);

    /**
     * Create a hologram line from the given text and hologram.
     * @param text - the text to create the line from
     * @param hologram - the hologram bounded to
     * @return the hologram line
     */
    IHoloLine lineFromText(String text, IHologram hologram);
}
