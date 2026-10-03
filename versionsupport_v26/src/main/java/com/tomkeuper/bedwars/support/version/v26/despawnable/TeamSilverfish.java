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

package com.tomkeuper.bedwars.support.version.v26.despawnable;

import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.server.VersionSupport;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TeamSilverfish extends DespawnableProvider<Silverfish> {
    @Override
    public DespawnableType getType() {
        return DespawnableType.SILVERFISH;
    }

    @Override
    String getDisplayName(@NotNull DespawnableAttributes attr, @NotNull ITeam team) {
        Language lang = Language.getDefaultLanguage();
        return lang.m(Messages.SHOP_UTILITY_NPC_SILVERFISH_NAME).replace("%bw_despawn_time%", String.valueOf(attr.despawnSeconds())
                .replace("%bw_health%", StringUtils.repeat(lang.m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH) + " ", 10))
                .replace("%bw_team_color%", team.getColor().chat().toString())
        );
    }

    @Override
    public Silverfish spawn(@NotNull DespawnableAttributes attr, @NotNull Location location, @NotNull ITeam team, VersionSupport api, int pathFindingTicks) {
        var bukkitEntity = (Silverfish) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SILVERFISH);
        applyDefaultSettings(bukkitEntity, attr, team);

        var entity = (net.minecraft.world.entity.monster.Silverfish) ((CraftEntity) bukkitEntity).getHandle();
        clearSelectors(entity);

        var goalSelector = getGoalSelector(entity);
        var targetSelector = getTargetSelector(entity);
        goalSelector.addGoal(1, new FloatGoal(entity));
        goalSelector.addGoal(2, new MeleeAttackGoal(entity, 1.9D, false));
        goalSelector.addGoal(3, new RandomStrollGoal(entity, 2D));
        goalSelector.addGoal(4, new RandomLookAroundGoal(entity));
        targetSelector.addGoal(1, new HurtByTargetGoal(entity));
        targetSelector.addGoal(2, getTargetGoal(entity, team, api, pathFindingTicks));

        return bukkitEntity;
    }
}
