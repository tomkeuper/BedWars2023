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
    private int repositionTicks = 0;
    private int targetChaseTicks = 0;
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

    private void reposition() {
        currentEnemy = null;
        this.target = null;
        targetChaseTicks = 0;
        repositionTicks = 60 + random.nextInt(60); // Reposition for 3-6s
        retargetTicks = repositionTicks;

        Location dragonLoc = team != null && team.getArena() != null ? team.getArena().getDragonSpawnLocation() : null;
        double centerX = dragonLoc != null ? dragonLoc.getX() : this.locX;
        double centerZ = dragonLoc != null ? dragonLoc.getZ() : this.locZ;
        double centerY = dragonLoc != null ? dragonLoc.getY() : 80.0;

        double angle = random.nextDouble() * 2 * Math.PI;
        double dist = 40.0 + random.nextDouble() * 40.0;
        this.a = centerX + Math.cos(angle) * dist;
        this.c = centerZ + Math.sin(angle) * dist;
        this.b = centerY + 15.0 + (random.nextDouble() * 20.0);
        if (team != null) {
            VersionSupport.debug("Dragon [" + team.getName() + "]: Flying away to reposition");
        }
    }

    @Override
    public void m() {
        if (team == null || team.getArena() == null) {
            super.m();
            return;
        }

        // Handle active repositioning phase
        if (repositionTicks > 0) {
            repositionTicks--;
            this.target = null;
            super.m();
            return;
        }

        // Validate current target
        if (currentEnemy != null) {
            if (currentEnemy.dead || !currentEnemy.isAlive() || currentEnemy.world != this.world) {
                currentEnemy = null;
                this.target = null;
                targetChaseTicks = 0;
            } else if (currentEnemy instanceof EntityHuman) {
                EntityHuman human = (EntityHuman) currentEnemy;
                if (team.wasMember(human.getUniqueID()) || team.getArena().isSpectator(human.getUniqueID()) || team.getArena().isReSpawning(human.getUniqueID())) {
                    currentEnemy = null;
                    this.target = null;
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
            this.target = null;
            this.a = currentEnemy.locX;
            this.b = currentEnemy.locY - 4.0;
            this.c = currentEnemy.locZ;

            // Check if dragon reached / hit the player (or bounding box collision)
            double distSq = this.h(currentEnemy);
            // In Minecraft, dragon's body is large (~16m diameter); head or body reaches target within ~7-8 blocks or bounding box intersection
            boolean hitPlayer = this.getBoundingBox().grow(4.0, 4.0, 4.0).b(currentEnemy.getBoundingBox()) || distSq <= 49.0;

            if (hitPlayer) {
                currentEnemy.damageEntity(DamageSource.mobAttack(this), 10.0F);
                VersionSupport.debug("Dragon [" + team.getName() + "]: Hit target " + currentEnemy.getName() + ", repositioning");
                reposition();
            } else if (targetChaseTicks >= 400) { // 20 seconds at 20 ticks/sec
                VersionSupport.debug("Dragon [" + team.getName() + "]: Could not reach target " + currentEnemy.getName() + " within 20s, repositioning");
                reposition();
            }
        } else {
            this.target = null;
            targetChaseTicks = 0;
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
