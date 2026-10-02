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

package com.tomkeuper.bedwars.support.version.v1_8_R3;

import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.server.VersionSupport;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DBWDragon extends EntityEnderDragon {

    private final ITeam team;
    private int retargetTicks = 0;
    private EntityLiving currentEnemy = null;
    private static final Random random = new Random();

    public DBWDragon(World world, ITeam team) {
        super(world);
        this.team = team;
    }

    public DBWDragon(World world) {
        super(world);
        this.team = null;
    }

    @Override
    public void m() {
        if (team == null || team.getArena() == null) {
            super.m();
            return;
        }

        // Validate current target
        if (currentEnemy != null) {
            if (currentEnemy.dead || !currentEnemy.isAlive() || currentEnemy.world != this.world) {
                currentEnemy = null;
                this.target = null;
            } else if (currentEnemy instanceof EntityHuman) {
                EntityHuman human = (EntityHuman) currentEnemy;
                if (team.wasMember(human.getUniqueID()) || team.getArena().isSpectator(human.getUniqueID()) || team.getArena().isReSpawning(human.getUniqueID())) {
                    currentEnemy = null;
                    this.target = null;
                }
            }
        }

        // Periodically retarget or acquire target if none
        retargetTicks--;
        if (retargetTicks <= 0 || currentEnemy == null) {
            retargetTicks = 20 + random.nextInt(20);
            EntityLiving newTarget = getNearestTarget();
            if (newTarget != currentEnemy) {
                currentEnemy = newTarget;
                if (currentEnemy != null) {
                    VersionSupport.debug("Dragon [" + team.getName() + "]: Acquired target " + currentEnemy.getName() + " at [" + (int) currentEnemy.locX + ", " + (int) currentEnemy.locY + ", " + (int) currentEnemy.locZ + "]");
                } else {
                    VersionSupport.debug("Dragon [" + team.getName() + "]: No target found, circling dragon spawn location");
                }
            }
        }

        if (currentEnemy != null) {
            this.target = currentEnemy;
            this.a = currentEnemy.locX;
            this.b = currentEnemy.locY + (double) currentEnemy.getHeadHeight();
            this.c = currentEnemy.locZ;
        } else {
            this.target = null;
            Location dragonLoc = team.getArena().getDragonSpawnLocation();
            double centerX = dragonLoc != null ? dragonLoc.getX() : 0.0;
            double centerZ = dragonLoc != null ? dragonLoc.getZ() : 0.0;
            double centerY = dragonLoc != null ? dragonLoc.getY() : 80.0;

            double dx = this.a - centerX;
            double dz = this.c - centerZ;
            double distSq = dx * dx + dz * dz;

            if (distSq > 3600.0 || (this.a == 0.0 && this.c == 0.0) || this.positionChanged) {
                this.a = centerX + (random.nextDouble() * 60.0 - 30.0);
                this.c = centerZ + (random.nextDouble() * 60.0 - 30.0);
                this.b = centerY + (random.nextDouble() * 20.0 - 10.0);
            }
        }

        super.m();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource.getEntity() instanceof EntityHuman) {
            EntityHuman human = (EntityHuman) damagesource.getEntity();
            if (team != null && (team.wasMember(human.getUniqueID()) || (team.getArena() != null && team.getArena().isSpectator(human.getUniqueID())))) {
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
        if (team == null || team.getArena() == null) return null;
        List<? extends EntityHuman> players = this.world.players;
        EntityHuman nearest = null;
        double minDist = Double.MAX_VALUE;

        for (EntityHuman human : players) {
            if (human.isAlive()
                    && !team.wasMember(human.getUniqueID())
                    && !team.getArena().isReSpawning(human.getUniqueID())
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
        DBWDragon dragon = new DBWDragon(mcWorld, team);
        dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        mcWorld.addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (org.bukkit.entity.EnderDragon) dragon.getBukkitEntity();
    }
}
