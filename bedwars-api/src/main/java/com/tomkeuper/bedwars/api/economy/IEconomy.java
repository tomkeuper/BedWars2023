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

package com.tomkeuper.bedwars.api.economy;

import org.bukkit.entity.Player;

/**
 * Get Economy Methods
 */
public interface IEconomy {

    /**
     * Check if economy is enabled
     */
    boolean isEconomy();

    /**
     * Get player money balance
     * @param p player from which to get the economy balance
     */
    double getMoney(Player p);

    /**
     * give to player money
     * @param p player from which to get the economy balance
     * @param money money amount to give
     */
    void giveMoney(Player p, double money);

    /**
     * Get player money from balance to buy an item shop
     * @param p player from which to get the money
     * @param cost money amount to take
     */
    void buyAction(Player p, double cost);

}
