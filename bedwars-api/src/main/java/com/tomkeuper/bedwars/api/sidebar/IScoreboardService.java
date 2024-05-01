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

package com.tomkeuper.bedwars.api.sidebar;

import com.tomkeuper.bedwars.api.arena.IArena;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BedWars scoreboard manager.
 */
public interface IScoreboardService {

    /**
     * Send player scoreboard based on conditions.
     */
    void giveTabFeatures(@NotNull Player player, @Nullable IArena arena, boolean delay);

    /**
     * Remove a player scoreboard.
     */
    void remove(@NotNull Player player);

    /**
     * @return true if tab formatting is disabled for current sidebar/ arena stage
     */
    boolean isTabFormattingDisabled(IArena arena);

    /**
     * @return Get the current active scoreboard of a player.
     */
    @Nullable
    Scoreboard getScoreboard(@NotNull Player player);
}
