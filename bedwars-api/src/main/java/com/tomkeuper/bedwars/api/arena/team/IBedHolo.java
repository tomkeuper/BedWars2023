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

package com.tomkeuper.bedwars.api.arena.team;

import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import org.bukkit.entity.Player;

public interface IBedHolo {
    /**
     * Create the hologram for the bed.
     */
    void create();

    /**
     * Hide the hologram for the bed.
     */
    void hide();

    /**
     * Hide the hologram for a specific player.
     */
    void hide(Player player);

    /**
     * Destroy the hologram for the bed.
     */
    void destroy();

    /*+
        * Hide the hologram for a specific player.
     */
    void remove(Player player);

    /**
     * Show the hologram for the bed.
     */
    void show();

    /**
     * Show the hologram for a specific player.
     */
    void show(Player player);

    /**
     * Update the hologram for all players.
     */
    void update();

    /**
     * Update the hologram for a specific player.
     */
    void update(Player player);

    /**
     * Get the main hologram associated with the bed.
     */
    IHologram getHologram();
}
