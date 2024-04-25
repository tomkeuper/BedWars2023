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

import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.language.Messages;
import org.bukkit.Bukkit;

import static com.tomkeuper.bedwars.api.language.Language.getMsg;

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
     * Destroy the hologram for the bed.
     */
    void destroy();

    /**
     * Show the hologram for the bed.
     */
    void show();

    /**
     * Get the main hologram associated with the bed.
     */
    IHologram getHologram();
}
