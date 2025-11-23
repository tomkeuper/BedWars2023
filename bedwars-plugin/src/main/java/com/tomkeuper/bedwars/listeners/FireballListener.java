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

package com.tomkeuper.bedwars.listeners;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.LastHit;
import com.tomkeuper.bedwars.arena.team.BedWarsTeam;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

import static com.tomkeuper.bedwars.BedWars.config;

public class FireballListener implements Listener {


    private final List<String> explosionProofMaterials;
    private final double fireballExplosionSize, fireballHorizontalSelf, fireballHorizontalOthers, fireballVerticalSelf, fireballVerticalOthers;
    private final double damageSelf, damageEnemy, damageTeammates;
    private final double fireballSpeedMultiplier, fireballCooldown;
    private final boolean fireballMakeFire;

    public FireballListener() {
        YamlConfiguration config = BedWars.config.getYml();
        explosionProofMaterials = config.getList(ConfigPath.GENERAL_FIREBALL_EXPLOSION_PROOF_BLOCKS).stream().map(Object::toString).collect(Collectors.toList());
        fireballExplosionSize = config.getDouble(ConfigPath.GENERAL_FIREBALL_EXPLOSION_SIZE);
        fireballMakeFire = config.getBoolean(ConfigPath.GENERAL_FIREBALL_MAKE_FIRE);
        fireballHorizontalSelf = config.getDouble(ConfigPath.GENERAL_FIREBALL_KNOCKBACK_HORIZONTAL_SELF) * -1;
        fireballHorizontalOthers = config.getDouble(ConfigPath.GENERAL_FIREBALL_KNOCKBACK_HORIZONTAL_OTHERS) * -1;
        fireballVerticalSelf = config.getDouble(ConfigPath.GENERAL_FIREBALL_KNOCKBACK_VERTICAL_SELF);
        fireballVerticalOthers = config.getDouble(ConfigPath.GENERAL_FIREBALL_KNOCKBACK_VERTICAL_OTHERS);
        damageSelf = config.getDouble(ConfigPath.GENERAL_FIREBALL_DAMAGE_SELF);
        damageEnemy = config.getDouble(ConfigPath.GENERAL_FIREBALL_DAMAGE_ENEMY);
        damageTeammates = config.getDouble(ConfigPath.GENERAL_FIREBALL_DAMAGE_TEAMMATES);
        fireballSpeedMultiplier = config.getDouble(ConfigPath.GENERAL_FIREBALL_SPEED_MULTIPLIER);
        fireballCooldown = config.getDouble(ConfigPath.GENERAL_FIREBALL_COOLDOWN);
    }

    @EventHandler
    public void onFireballInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack handItem = e.getItem();
        Action action = e.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR || handItem == null) return;

        IArena arena = Arena.getArenaByPlayer(player);
        if (arena == null || arena.getStatus() != GameState.playing || handItem.getType() != BedWars.nms.materialFireball()) return;

        e.setCancelled(true);

        long cooldown = (long) (fireballCooldown * 1000);
        long timeDifference = System.currentTimeMillis() - arena.getFireballCooldowns().getOrDefault(player.getUniqueId(), 0L);
        if (timeDifference <= cooldown) {
            if (fireballCooldown >= 1.0) {
                player.sendMessage(Language.getMsg(player, Messages.ARENA_FIREBALL_COOLDOWN)
                        .replace("%bw_cooldown%", String.valueOf((cooldown - timeDifference)/1000)));
            }
            return;
        }

        arena.getFireballCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
        Fireball fireball = player.launchProjectile(Fireball.class);
        Vector direction = player.getEyeLocation().getDirection();
        fireball = BedWars.nms.setFireballDirection(fireball, direction);
        fireball.setVelocity(fireball.getDirection().multiply(fireballSpeedMultiplier));
        fireball.setYield((float) fireballExplosionSize);
        fireball.setMetadata("bw2023", new FixedMetadataValue(BedWars.plugin, "ceva"));
        BedWars.nms.minusAmount(player, handItem, 1);
    }

    @EventHandler
    public void fireballHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Fireball)) return;

        Location location = e.getEntity().getLocation();
        ProjectileSource projectileSource = e.getEntity().getShooter();
        if (!(projectileSource instanceof Player))  return;

        Player source = (Player) projectileSource;
        IArena arena = Arena.getArenaByPlayer(source);

        if (arena == null || arena.getStatus() != GameState.playing)  return;

        Vector vector = location.toVector();
        World world = location.getWorld();
        if (world == null)  return;

        Collection<Entity> nearbyEntities = world.getNearbyEntities(location, fireballExplosionSize, fireballExplosionSize, fireballExplosionSize);

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player) entity;

            if (!Arena.isInArena(player) || arena.isSpectator(player) || arena.isReSpawning(player)) continue;

            UUID playerUUID = player.getUniqueId();
            long respawnInvulnerability = BedWarsTeam.reSpawnInvulnerability.getOrDefault(playerUUID, 0L);

            if (respawnInvulnerability > System.currentTimeMillis()) continue;
            BedWarsTeam.reSpawnInvulnerability.remove(playerUUID);

            Vector playerVector = player.getLocation().toVector();
            Vector normalizedVector = playerVector.subtract(vector).normalize();
            Vector horizontalVector;
            double y;

            if (entity.getUniqueId() == source.getUniqueId()) {
                horizontalVector = normalizedVector.multiply(Math.abs(fireballHorizontalSelf));
                y = normalizedVector.getY();

                // FIXED: Check horizontal distance instead of just Y tolerance
                double horizontalDistance = Math.sqrt(normalizedVector.getX() * normalizedVector.getX() +
                        normalizedVector.getZ() * normalizedVector.getZ());

                if (horizontalDistance <= config.getDouble(ConfigPath.GENERAL_FIREBALL_JUMP_TOLERANCE)) {
                    // Mostly vertical explosion (including straight down)
                    y = fireballVerticalSelf * 1.5;
                } else {
                    // Has horizontal component
                    y = Math.abs(y) * fireballVerticalSelf * 1.5;
                }
            } else {
                horizontalVector = normalizedVector.multiply(Math.abs(fireballHorizontalOthers));
                y = normalizedVector.getY();

                // FIXED: Check horizontal distance instead of just Y tolerance
                double horizontalDistance = Math.sqrt(normalizedVector.getX() * normalizedVector.getX() +
                        normalizedVector.getZ() * normalizedVector.getZ());

                if (horizontalDistance <= config.getDouble(ConfigPath.GENERAL_FIREBALL_JUMP_TOLERANCE)) {
                    // Mostly vertical explosion (including straight down)
                    y = fireballVerticalOthers * 1.5;
                } else {
                    // Has horizontal component
                    y = Math.abs(y) * fireballVerticalOthers * 1.5;
                }
            }

            try {
                player.setVelocity(horizontalVector.setY(y));
            } catch (IllegalArgumentException ignored) {}

            LastHit lh = LastHit.getLastHit(player);
            if (lh != null) {
                lh.setDamager(source);
                lh.setTime(System.currentTimeMillis());
            } else  new LastHit(player, source, System.currentTimeMillis());

            if (player.equals(source)) {
                if (damageSelf > 0) player.damage(damageSelf);
            } else {
                ITeam playerTeam = arena.getTeam(player);
                ITeam sourceTeam = arena.getTeam(source);

                if (playerTeam != null && playerTeam.equals(sourceTeam)) damagePlayer(player, damageTeammates);
                else damagePlayer(player, damageEnemy);
            }
        }
    }

    @EventHandler
    public void onFireballExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Fireball)) return;

        ProjectileSource projectileSource = ((Fireball) event.getEntity()).getShooter();
        if (!(projectileSource instanceof Player)) return;

        Player source = (Player) projectileSource;
        IArena arena = Arena.getArenaByPlayer(source);

        if (arena == null || arena.getStatus() != GameState.playing)  return;

        Location explosionLocation = event.getLocation();
        World world = explosionLocation.getWorld();
        if (world == null) return;

        event.blockList().removeIf( block -> explosionProofMaterials.contains(block.getType().toString()));
    }

    private void damagePlayer(Player player, double damageTeammates) {
        if (damageTeammates > 0) {
            EntityDamageEvent damageEvent = new EntityDamageEvent(
                    player,
                    EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, damageTeammates)),
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(damageTeammates)))
            );
            player.setLastDamageCause(damageEvent);
            player.damage(damageTeammates); // damage teammates
        }
    }

    @EventHandler
    public void fireballDirectHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Fireball) || !(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        if (!Arena.isInArena(player))  return;
        e.setCancelled(true);
    }

    @EventHandler
    public void fireballPrime(ExplosionPrimeEvent e) {
        if (!(e.getEntity() instanceof Fireball)) return;

        Fireball fireball = (Fireball) e.getEntity();
        ProjectileSource shooter = fireball.getShooter();

        if (!(shooter instanceof Player) || !Arena.isInArena((Player) shooter))  return;

        e.setFire(fireballMakeFire);
    }
}