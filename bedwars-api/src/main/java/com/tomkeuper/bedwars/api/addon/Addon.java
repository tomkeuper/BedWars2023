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

package com.tomkeuper.bedwars.api.addon;

import org.bukkit.plugin.Plugin;

public abstract class Addon {
    /**
     * Get the author of an addon
     */
    public abstract String getAuthor();

    /**
     * Get the addon plugin
     */
    public abstract Plugin getPlugin();

    /**
     * Get the version of an addon
     */
    public abstract String getVersion();

    /**
     * Get the name of an addon
     */
    public abstract String getName();

    /**
     * Get the identifier of an addon
     */
    @SuppressWarnings("unused")
    public abstract String getDescription();

    /**
     * Load everything from the addon
     * Listeners, config files, databases,...
     */
    public abstract void load();

    /**
     * Unload everything from the addon
     */
    public abstract void unload();
}
