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

package com.tomkeuper.bedwars.support.version.v1_12_R1;

import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.server.VersionSupport;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DBWDragon extends EntityEnderDragon {

    private final ITeam team;
    private int retargetTicks = 0;
    private int repositionTicks = 0;
    private int targetChaseTicks = 0;
    private EntityLiving currentEnemy = null;
    private double targetPosX = 0.0;
    private double targetPosY = 80.0;
    private double targetPosZ = 0.0;
    private static final Random random = new Random();

    public DBWDragon(World world, ITeam team) {
        super(world);
        this.team = team;
    }

    public DBWDragon(World world) {
        super(world);
        this.team = null;
    }

    private void reposition() {
        currentEnemy = null;
        targetChaseTicks = 0;
        repositionTicks = 60 + random.nextInt(60); // Reposition for 3-6s
        retargetTicks = repositionTicks;

        Location dragonLoc = team != null && team.getArena() != null ? team.getArena().getDragonSpawnLocation() : null;
        double centerX = dragonLoc != null ? dragonLoc.getX() : this.locX;
        double centerZ = dragonLoc != null ? dragonLoc.getZ() : this.locZ;
        double centerY = dragonLoc != null ? dragonLoc.getY() : 80.0;

        double angle = random.nextDouble() * 2 * Math.PI;
        double dist = 40.0 + random.nextDouble() * 40.0;
        this.targetPosX = centerX + Math.cos(angle) * dist;
        this.targetPosZ = centerZ + Math.sin(angle) * dist;
        this.targetPosY = centerY + 15.0 + (random.nextDouble() * 20.0);
        if (team != null) {
            VersionSupport.debug("Dragon [" + team.getName() + "]: Flying away to reposition");
        }
    }

    @Override
    public void n() {
        if (team == null || team.getArena() == null) {
            super.n();
            return;
        }

        // Handle active repositioning phase
        if (repositionTicks > 0) {
            repositionTicks--;
            moveTowards(this.targetPosX, this.targetPosY, this.targetPosZ, 0.45);
            return;
        }

        // Validate current target
        if (currentEnemy != null) {
            if (currentEnemy.dead || !currentEnemy.isAlive() || currentEnemy.world != this.world) {
                currentEnemy = null;
                targetChaseTicks = 0;
            } else if (currentEnemy instanceof EntityHuman) {
                EntityHuman human = (EntityHuman) currentEnemy;
                if (team.wasMember(human.getUniqueID()) || team.getArena().isSpectator(human.getUniqueID()) || team.getArena().isReSpawning(human.getUniqueID())) {
                    currentEnemy = null;
                    targetChaseTicks = 0;
                }
            }
        }

        // Acquire target if none
        if (currentEnemy == null) {
            retargetTicks--;
            if (retargetTicks <= 0) {
                retargetTicks = 20 + random.nextInt(20);
                EntityLiving newTarget = getNearestTarget();
                if (newTarget != null) {
                    currentEnemy = newTarget;
                    targetChaseTicks = 0;
                    VersionSupport.debug("Dragon [" + team.getName() + "]: Acquired target " + currentEnemy.getName() + " at [" + (int) currentEnemy.locX + ", " + (int) currentEnemy.locY + ", " + (int) currentEnemy.locZ + "]");
                }
            }
        }

        if (currentEnemy != null) {
            targetChaseTicks++;

            // Target lock and dive towards the player (aiming below player height so the dragon sweeps low through the island)
            double targetX = currentEnemy.locX;
            double targetY = currentEnemy.locY - 4.0;
            double targetZ = currentEnemy.locZ;

            moveTowards(targetX, targetY, targetZ, 0.6);

            // Check if dragon reached / hit the player (or bounding box collision)
            double distSq = this.h(currentEnemy);
            boolean hitPlayer = this.getBoundingBox().grow(4.0, 4.0, 4.0).c(currentEnemy.getBoundingBox()) || distSq <= 49.0;

            if (hitPlayer) {
                currentEnemy.damageEntity(DamageSource.mobAttack(this), 10.0F);
                VersionSupport.debug("Dragon [" + team.getName() + "]: Hit target " + currentEnemy.getName() + ", repositioning");
                reposition();
            } else if (targetChaseTicks >= 400) { // 20 seconds at 20 ticks/sec
                VersionSupport.debug("Dragon [" + team.getName() + "]: Could not reach target " + currentEnemy.getName() + " within 20s, repositioning");
                reposition();
            }
        } else {
            targetChaseTicks = 0;
            Location dragonLoc = team.getArena().getDragonSpawnLocation();
            double centerX = dragonLoc != null ? dragonLoc.getX() : 0.0;
            double centerZ = dragonLoc != null ? dragonLoc.getZ() : 0.0;
            double centerY = dragonLoc != null ? dragonLoc.getY() : 80.0;

            double dx = this.targetPosX - centerX;
            double dz = this.targetPosZ - centerZ;
            double distSq = dx * dx + dz * dz;

            if (distSq > 3600.0 || (this.targetPosX == 0.0 && this.targetPosZ == 0.0) || this.positionChanged) {
                this.targetPosX = centerX + (random.nextDouble() * 60.0 - 30.0);
                this.targetPosZ = centerZ + (random.nextDouble() * 60.0 - 30.0);
                this.targetPosY = centerY + (random.nextDouble() * 20.0 - 10.0);
            }

            moveTowards(this.targetPosX, this.targetPosY, this.targetPosZ, 0.35);
        }
    }

    private void moveTowards(double targetX, double targetY, double targetZ, double speed) {
        double dx = targetX - this.locX;
        double dy = targetY - this.locY;
        double dz = targetZ - this.locZ;
        double distSq = dx * dx + dy * dy + dz * dz;
        double dist = Math.sqrt(distSq);

        if (dist > 0.1) {
            this.motX = dx / dist * speed;
            this.motY = dy / dist * speed;
            this.motZ = dz / dist * speed;
        }

        this.yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
        this.pitch = (float) (-(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * 180.0 / Math.PI));
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.setHeadRotation(this.yaw);

        this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
    }

    @Override
    public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, float f) {
        if (checkDamage(damagesource)) {
            return false;
        }
        return super.a(entitycomplexpart, damagesource, f);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (checkDamage(damagesource)) {
            return false;
        }
        return super.damageEntity(damagesource, f);
    }

    private boolean checkDamage(DamageSource damagesource) {
        Entity damager = damagesource.getEntity();
        if (damager instanceof EntityHuman) {
            EntityHuman human = (EntityHuman) damager;
            if (team != null && (team.wasMember(human.getUniqueID()) || (team.getArena() != null && team.getArena().isSpectator(human.getUniqueID())))) {
                return true;
            }
        }
        if (damager instanceof DBWDragon) {
            DBWDragon other = (DBWDragon) damager;
            if (team != null && other.team != null && team.equals(other.team)) {
                return true;
            }
        }

        // If hit by the team it is currently targeting (or the target itself), chance of losing target
        if (currentEnemy != null && damager != null && team != null && team.getArena() != null) {
            boolean hitByTargetTeam = false;
            if (damager.equals(currentEnemy)) {
                hitByTargetTeam = true;
            } else if (damager instanceof EntityHuman) {
                ITeam enemyTeam = team.getArena().getTeam((org.bukkit.entity.Player) damager.getBukkitEntity());
                if (enemyTeam != null && currentEnemy instanceof EntityHuman) {
                    ITeam targetTeam = team.getArena().getTeam((org.bukkit.entity.Player) currentEnemy.getBukkitEntity());
                    if (enemyTeam.equals(targetTeam)) {
                        hitByTargetTeam = true;
                    }
                }
            }

            if (hitByTargetTeam && random.nextInt(2) == 0) { // 50% chance
                VersionSupport.debug("Dragon [" + team.getName() + "]: Hit by target/target team, scared away from " + currentEnemy.getName());
                reposition();
            }
        }

        return false;
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
