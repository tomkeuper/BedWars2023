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

package com.tomkeuper.bedwars.configuration;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.configuration.ConfigManager;

public class MoneyConfig extends ConfigManager {

    public static MoneyConfig money;

    private MoneyConfig() {
        super ( BedWars.plugin, "rewards", BedWars.plugin.getDataFolder ().toString () );
    }

    /**
     * Initialize money config.
     */
    public static void init() {
        money = new MoneyConfig ();
        money.getYml ().options ().copyDefaults ( true );
        money.getYml ().addDefault ( "money-rewards.per-minute", 5 );
        money.getYml ().addDefault ( "money-rewards.per-teammate", 30 );
        money.getYml ().addDefault ( "money-rewards.game-win", 90 );
        money.getYml ().addDefault ( "money-rewards.bed-destroyed", 60 );
        money.getYml ().addDefault ( "money-rewards.final-kill", 40 );
        money.getYml ().addDefault ( "money-rewards.regular-kill", 10 );
        money.save ();
    }
}