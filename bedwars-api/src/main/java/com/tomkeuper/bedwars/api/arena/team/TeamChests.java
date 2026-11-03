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

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chests found once on a team island.
 * A block kept here can be broken later in the match, so check its type before using it.
 */
public final class TeamChests {

    public static final TeamChests NONE = new TeamChests(Collections.emptyList(), Collections.emptyList());

    private final List<Block> chests;
    private final List<Block> enderChests;
    private final List<Block> all;

    public TeamChests(List<Block> chests, List<Block> enderChests) {
        this.chests = Collections.unmodifiableList(new ArrayList<>(chests));
        this.enderChests = Collections.unmodifiableList(new ArrayList<>(enderChests));
        List<Block> merged = new ArrayList<>(this.chests.size() + this.enderChests.size());
        merged.addAll(this.chests);
        merged.addAll(this.enderChests);
        this.all = Collections.unmodifiableList(merged);
    }

    /**
     * Regular chests on the island, including both halves of a double chest.
     */
    @NotNull
    public List<Block> getChests() {
        return chests;
    }

    /**
     * Ender chests on the island.
     */
    @NotNull
    public List<Block> getEnderChests() {
        return enderChests;
    }

    /**
     * Regular chests followed by ender chests.
     */
    @NotNull
    public List<Block> getAll() {
        return all;
    }
}
