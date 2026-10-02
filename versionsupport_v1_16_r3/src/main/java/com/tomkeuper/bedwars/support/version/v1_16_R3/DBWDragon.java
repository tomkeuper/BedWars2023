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

package com.tomkeuper.bedwars.support.version.v1_16_R3;

import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.server.VersionSupport;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DBWDragon extends EntityEnderDragon {

    private final ITeam team;
    private int behaviorTicks = 0;
    private boolean isAttacking = true;
    private static final Random random = new Random();

    public DBWDragon(EntityTypes<? extends EntityEnderDragon> entitytypes, World world, ITeam team) {
        super(entitytypes, world);
        this.team = team;
    }

    public DBWDragon(EntityTypes<? extends EntityEnderDragon> entitytypes, World world) {
        super(entitytypes, world);
        this.team = null;
    }

    @Override
    protected void mobTick() {
        if (team == null) {
            super.mobTick();
            return;
        }

        behaviorTicks--;
        if (behaviorTicks <= 0) {
            isAttacking = random.nextBoolean();
            behaviorTicks = 100 + random.nextInt(200); // 5-15 seconds
        }

        if (isAttacking) {
            EntityLiving target = getNearestTarget();
            if (target != null) {
                VersionSupport.debug("Dragon [" + team.getName() + "]: Attacking enemy " + target.getName());
                double dx = target.locX() - this.locX();
                double dy = target.locY() + target.getHeadHeight() - this.locY();
                double dz = target.locZ() - this.locZ();
                double distSq = dx * dx + dy * dy + dz * dz;
                double dist = Math.sqrt(distSq);

                if (dist > 0.1) {
                    double speed = 0.5;
                    this.setMot(dx / dist * speed, dy / dist * speed, dz / dist * speed);
                }

                // Orientation
                this.yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
                this.pitch = (float) (-(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * 180.0 / Math.PI));
                this.lastYaw = this.yaw;
                this.lastPitch = this.pitch;
                this.setHeadRotation(this.yaw);

                // Damage player if close enough
                if (dist < 3.0) {
                    target.damageEntity(DamageSource.mobAttack(this), 10.0F);
                }
                return;
            }
        }

        // Default behavior (circling center or staying still)
        Location dragonLoc = team.getArena().getDragonSpawnLocation();
        double centerX = dragonLoc != null ? dragonLoc.getX() : 0.0;
        double centerZ = dragonLoc != null ? dragonLoc.getZ() : 0.0;
        double centerY = dragonLoc != null ? dragonLoc.getY() : 80; // Default height

        double dx = centerX - this.locX();
        double dy = centerY - this.locY();
        double dz = centerZ - this.locZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        double dist = Math.sqrt(distSq);

        if (dist > 10.0) {
            double speed = 0.3;
            this.setMot(dx / dist * speed, dy / dist * speed, dz / dist * speed);
        } else {
            // Circling logic
            double angle = (System.currentTimeMillis() / 1000.0) % (2 * Math.PI);
            double targetX = centerX + Math.cos(angle) * 10.0;
            double targetZ = centerZ + Math.sin(angle) * 10.0;
            dx = targetX - this.locX();
            dz = targetZ - this.locZ();
            dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0.1) {
                this.setMot(dx / dist * 0.3, 0, dz / dist * 0.3);
            }
        }
        Vec3D mot = this.getMot();
        this.setMot(mot.x, mot.y + 0.01 * (centerY - this.locY()), mot.z);

        this.yaw = (float) (Math.atan2(this.getMot().z, this.getMot().x) * 180.0 / Math.PI) - 90.0F;
        this.setHeadRotation(this.yaw);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource.getEntity() instanceof EntityHuman) {
            EntityHuman human = (EntityHuman) damagesource.getEntity();
            if (team != null && (team.wasMember(human.getUniqueID()) || team.getArena().isSpectator(human.getUniqueID()))) {
                return false;
            }
        }
        if (damagesource.getEntity() instanceof DBWDragon) {
            DBWDragon other = (DBWDragon) damagesource.getEntity();
            if (team != null && other.team != null && team.equals(other.team)) {
                return false;
            }
        }
        return super.damageEntity(damagesource, f);
    }

    @Nullable
    private EntityLiving getNearestTarget() {
        if (team == null) return null;
        List<? extends EntityHuman> players = this.world.getPlayers();
        EntityHuman nearest = null;
        double minDist = Double.MAX_VALUE;

        for (EntityHuman human : players) {
            if (human.isAlive() && !team.wasMember(human.getUniqueID()) && !team.getArena().isReSpawning(human.getUniqueID())
                    && !team.getArena().isSpectator(human.getUniqueID())) {
                double dist = human.h(this);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = human;
                }
            }
        }
        return nearest;
    }

    public static org.bukkit.entity.EnderDragon spawn(Location loc, ITeam team) {
        WorldServer mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
        DBWDragon dragon = new DBWDragon(EntityTypes.ENDER_DRAGON, mcWorld, team);
        dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        mcWorld.addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (org.bukkit.entity.EnderDragon) dragon.getBukkitEntity();
    }
}
