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

package com.tomkeuper.bedwars.api.upgrades;

import com.google.common.collect.ImmutableMap;
import com.tomkeuper.bedwars.api.BedWars;
import org.bukkit.entity.Player;

/**
 * The UpgradesIndex interface represents an upgrade menu in the BedWars game.
 */
public interface UpgradesIndex {

    /**
     * Get the name of the upgrade menu.
     *
     * @return The name of the menu.
     */
    String getName();

    /**
     * Open the upgrade menu for a player.
     * Make sure to use {@link BedWars.TeamUpgradesUtil#setWatchingGUI(Player)}.
     *
     * @param player The player to open the menu for.
     */
    void open(Player player);

    /**
     * Add content to the upgrade menu.
     *
     * @param content The content instance to add.
     * @param slot    The slot where to put the content in the menu.
     * @return `true` if the content was successfully added, `false` if the slot is already in use.
     */
    boolean addContent(MenuContent content, int slot);

    /**
     * Get the total number of tiers in the upgrades.
     *
     * @return The total number of tiers.
     */
    int countTiers();

    /**
     * Get an immutable map of the menu content by slot.
     *
     * @return An immutable map of the menu content by slot.
     */
    ImmutableMap<Integer, MenuContent> getMenuContentBySlot();
}

