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

package com.tomkeuper.bedwars.api.arena.generator;

import org.bukkit.entity.Player;

public interface IGenHolo {

    /**
     * Set timer hologram display text.
     */
    void setTimerName(String timer);

    /**
     * Set tier hologram display text.
     */
    void setTierName(String tier);

    /**
     * Get language iso associated with this hologram.
     */
    String getIso();

    /**
     * Get the player associated with this hologram.
     */
    Player getPlayer();

    /**
     * Get the generator associated with this hologram.
     */
    IGenerator getGenerator();

    /**
     * Update the hologram.
     */
    void update();

    /**
     * This must be called when disabling the generator {@link IGenerator#disable()}
     */
    void destroy();
}
