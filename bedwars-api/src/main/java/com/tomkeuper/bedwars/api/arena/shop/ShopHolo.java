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

package com.tomkeuper.bedwars.api.arena.shop;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import lombok.Getter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ShopHolo {

    @Getter
    private final IHologram hologram;
    @Getter
    private final String iso;
    @Getter
    private final IArena arena;
    @Getter
    private final ITeam team;

    public ShopHolo(@Nonnull IHologram hologram, @Nonnull ITeam team, @Nonnull String iso) {
        this.hologram = hologram;
        this.team = team;
        this.arena = team.getArena();
        this.iso = iso;
        arena.addShopHologram(iso, this);
    }

    public void update() {
        hologram.update();
    }

    public void update(Player p) {
        hologram.update(p);
    }

    public void clear() {
        hologram.remove();
    }

    public void clearForPlayer(Player p) {
        hologram.removePlayer(p);
    }
}
