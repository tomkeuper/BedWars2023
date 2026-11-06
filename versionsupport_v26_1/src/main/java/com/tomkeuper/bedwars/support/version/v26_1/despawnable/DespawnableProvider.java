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
import com.tomkeuper.bedwars.api.server.VersionSupport;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class DespawnableProvider<T> {

    abstract DespawnableType getType();

    abstract String getDisplayName(DespawnableAttributes attr, ITeam team);

    abstract T spawn(@NotNull DespawnableAttributes attr, @NotNull Location location, @NotNull ITeam team, VersionSupport api, int pathFindingTicks);

    protected boolean notSameTeam(@NotNull Entity entity, ITeam team, @NotNull VersionSupport api) {
        var despawnable = api.getDespawnablesList().getOrDefault(entity.getBukkitEntity().getUniqueId(), null);
        return null == despawnable || despawnable.getTeam() != team;
    }

    protected GoalSelector getTargetSelector(@NotNull PathfinderMob entityLiving) {
        return entityLiving.targetSelector;
    }

    protected GoalSelector getGoalSelector(@NotNull PathfinderMob entityLiving) {
        return entityLiving.goalSelector;
    }

    protected void clearSelectors(@NotNull PathfinderMob entityLiving) {
        entityLiving.targetSelector.getAvailableGoals().clear();
        entityLiving.goalSelector.getAvailableGoals().clear();
    }

    protected Goal getTargetGoal(Mob entity, ITeam team, VersionSupport api, int pathFindingTicks) {
        return new NearestAttackableTargetGoal<>(entity, LivingEntity.class, pathFindingTicks, true, false,
                (entityLiving, sourceEntity) -> { // Two parameters now
                    if (entityLiving instanceof Player) {
                        return !((Player) entityLiving).getBukkitEntity().isDead() &&
                                !team.wasMember(((Player) entityLiving).getBukkitEntity().getUniqueId()) &&
                                !team.getArena().isReSpawning(((Player) entityLiving).getBukkitEntity().getUniqueId())
                                && !team.getArena().isSpectator(((Player) entityLiving).getBukkitEntity().getUniqueId());
                    }
                    return notSameTeam(entityLiving, team, api);
                });
    }

    protected void applyDefaultSettings(org.bukkit.entity.@NotNull LivingEntity bukkitEntity, DespawnableAttributes attr,
                                        ITeam team) {
        bukkitEntity.setRemoveWhenFarAway(false);
        bukkitEntity.setPersistent(true);
        bukkitEntity.setCustomNameVisible(true);
        bukkitEntity.setCustomName(getDisplayName(attr, team));

        var entity = ((Mob)((CraftEntity)bukkitEntity).getHandle());

        Objects.requireNonNull(entity.getAttributes().getInstance(Attributes.MAX_HEALTH)).setBaseValue(attr.health());
        Objects.requireNonNull(entity.getAttributes().getInstance(Attributes.MOVEMENT_SPEED)).setBaseValue(attr.speed());
        Objects.requireNonNull(entity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE)).setBaseValue(attr.damage());
    }
}
