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

package com.tomkeuper.bedwars.support.version.v26;

import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class DefaultGenAnimation implements IGeneratorAnimation {

    private final Entity armorStand;
    private final Location loc;
    private int tickCount = 0; // A counter to keep track of the ticks since the animation started.

    // Constants for the sinusoidal motion
    final double frequency = 0.035; // Controls the oscillation speed.
    final double amplitude = 260; // Controls the range of YAW motion.
    final double verticalAmplitude = 0.15; // Controls the range of vertical motion.

    public DefaultGenAnimation(ArmorStand armorStand) {
        this.armorStand = ((CraftArmorStand) armorStand).getHandle();
        this.loc = armorStand.getLocation();
        setArmorStandYAW(0);
        setArmorStandMotY(0);
    }

    @Override
    public String getIdentifier() {
        return "bw2023:default";
    }

    @Override
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("BedWars2023");
    }

    @Override
    public void run() {
        // Calculate sinusoidal values for YAW and MotY
        float sinusoidalYaw = (float) (Math.sin(frequency * tickCount) * amplitude);
        float sinusoidalMotY = (float) (Math.sin((frequency * tickCount) + Math.PI/2) * verticalAmplitude);

        // Update the armor stand's YAW and MotY based on the sinusoidal functions
        final double lastMotY = getArmorStandMotY();
        setArmorStandYAW(sinusoidalYaw);
        addArmorStandMotY(sinusoidalMotY);

        armorStand.dismountTo(loc.getX(), loc.getY(), loc.getZ()); // SETTING NEW LOCATION
        armorStand.onGround = false; // SETTING ON GROUND TO FALSE

        final var delta = new Vec3(0,0,0);
        final var positionMoveRotation = new PositionMoveRotation(armorStand.trackingPosition(), delta, 0, 0);
        final Set<Relative> set = new HashSet<>();

        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(armorStand.getId(), positionMoveRotation, set, false);

        ClientboundMoveEntityPacket.PosRot moveLookPacket = new ClientboundMoveEntityPacket.PosRot(armorStand.getId(), (short) 0, (short) ((getArmorStandMotY() - lastMotY)*128), (short) 0, (byte) getArmorStandYAW(), (byte) 0, false);

        for (Player p : loc.getWorld().getPlayers()) {
            v26.sendPackets(p, teleportPacket, moveLookPacket);
        }
        tickCount++;
    }

    private void setArmorStandYAW(float yaw) {
        armorStand.setYRot(yaw);
    }

    private void addArmorStandYAW(float yaw) {
        armorStand.setYRot(getArmorStandYAW() + yaw);
    }

    private float getArmorStandYAW() {
        return armorStand.getYRot();
    }

    private void setArmorStandMotY(double y) {
        armorStand.setDeltaMovement(new Vec3(0, y, 0));
    }

    private void addArmorStandMotY(double y) {
        armorStand.setDeltaMovement(new Vec3(0, getArmorStandMotY() + y, 0));
    }

    private double getArmorStandMotY() {
        return armorStand.getDeltaMovement().y;
    }
}
