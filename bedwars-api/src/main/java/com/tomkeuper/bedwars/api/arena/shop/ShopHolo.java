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

package com.tomkeuper.bedwars.api.arena.shop;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopHolo {
    /**
     * Shop holograms per language <iso, holo></iso,>
     */
    @Getter
    private static List<ShopHolo> shopHolo = new ArrayList<>();

    @Getter
    private final IHologram hologram;
    private final Location l;
    private final IArena a;
    public ShopHolo(@Nonnull IHologram hologram, Location l, IArena a) {
        this.l = l;
        this.hologram = hologram;
        this.a = a;
        shopHolo.add(this);
    }

    public void update() {
        if (l == null) Bukkit.broadcastMessage("LOCATION IS NULL");
        hologram.getLines().forEach(IHoloLine::reveal);
    }

    public static void clearForArena(IArena arena) {
        shopHolo.stream().filter(h -> h.getArena() == arena)
                .collect(Collectors.toList())
                .forEach(h -> h.getHologram().remove());
    }

    public IArena getArena() {
        return a;
    }
}
