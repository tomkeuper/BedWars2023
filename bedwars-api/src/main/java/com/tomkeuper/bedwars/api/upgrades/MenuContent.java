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

package com.tomkeuper.bedwars.api.upgrades;

import com.tomkeuper.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * The MenuContent interface represents a piece of content in a menu.
 * It defines methods for retrieving the display item, handling click events,
 * and getting the name of the menu content.
 */
public interface MenuContent {

    /**
     * Retrieves the display item for this menu content.
     *
     * @param player the player viewing the menu
     * @param team   the team associated with the menu
     * @return the display item
     */
    ItemStack getDisplayItem(Player player, ITeam team);

    /**
     * Handles the click event for a specific upgrade item.
     *
     * @param player           The player who initiated the upgrade.
     * @param clickType        The type of click.
     * @param team             The team associated with the menu.
     * @param forFree          Indicates if the upgrade is obtained for free.
     * @param announcePurchase Indicates whether the purchase should be announced to the team.
     * @param openInv          Indicates whether to open the inventory menu after purchase.
     * @return True if the upgrade was successfully applied, false otherwise.
     */
    boolean onClick(Player player, ClickType clickType, ITeam team, boolean forFree, boolean announcePurchase, boolean announceAlreadyUnlocked, boolean openInv);

    /**
     * Retrieves the name of this menu content.
     *
     * @return the name of the menu content
     */
    String getName();
}

