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

import java.util.List;

/**
 * The manager responsible for handling addons.
 */
public interface IAddonManager {

    /**
     * Get the list of registered addons.
     *
     * @return The list of registered addons.
     */
    List<Addon> getAddons();

    /**
     * Get the list of loaded addons.
     *
     * @return The list of loaded addons.
     */
    List<Addon> getLoadedAddons();

    /**
     * Get the list of unloaded addons.
     *
     * @return The list of unloaded addons.
     */
    List<Addon> getUnloadedAddons();

    /**
     * Get the list of addons created by a specific author.
     *
     * @param author The author of the addons.
     * @return The list of addons created by the specified author.
     */
    List<Addon> getAddonsByAuthor(String author);

    /**
     * Load an addon.
     *
     * @param addon The addon to load.
     */
    void loadAddon(Addon addon);

    /**
     * Unload an addon.
     *
     * @param addon The addon to unload.
     */
    void unloadAddon(Addon addon);

    /**
     * Unload every addon
     */
    void unloadAddons();

    /**
     * Load every addon
     */
    void loadAddons();

    /**
     * Register an addon.
     *
     * @param addon The addon to register.
     */
    void registerAddon(Addon addon);
}
