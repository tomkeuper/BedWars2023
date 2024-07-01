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

package com.tomkeuper.bedwars.support.version.v1_20_R4.despawnable;

import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.server.VersionSupport;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DespawnableFactory {

    private final VersionSupport versionSupport;
    private final List<DespawnableProvider<? extends LivingEntity>> providers = new ArrayList<>();

    public DespawnableFactory(VersionSupport versionSupport) {
        this.versionSupport = versionSupport;
        providers.add(new TeamIronGolem());
        providers.add(new TeamSilverfish());
    }

    public LivingEntity spawn(@NotNull DespawnableAttributes attr, @NotNull Location location, @NotNull ITeam team){
        return providers.stream().filter(provider -> provider.getType() == attr.type())
                .findFirst().orElseThrow().spawn(attr, location,team, versionSupport);
    }
}
