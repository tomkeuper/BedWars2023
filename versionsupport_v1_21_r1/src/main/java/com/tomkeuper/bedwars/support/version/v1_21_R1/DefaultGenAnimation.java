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

package com.tomkeuper.bedwars.support.version.v1_21_R1;

import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Lagggpixel
 * @since May 10, 2024
 */
public class DefaultGenAnimation implements IGeneratorAnimation {

    private final Entity armorStand;
    private final Location loc;
    private int tickCount = 0; // A counter to keep track of the ticks since the animation started.

    // Constants for the sinusoidal motion
    final double frequency = 0.035; // Controls the oscillation speed.
    final double amplitude = 260; // Controls the range of YAW motion.
    final double verticalAmplitude = 10; // Controls the range of vertical motion.

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
        float sinusoidalMotY = (float) (Math.sin(frequency * tickCount) * verticalAmplitude);

        // Update the armor stand's YAW and MotY based on the sinusoidal functions
        final double lastMotY = getArmorStandMotY();
        setArmorStandYAW(sinusoidalYaw);
        addArmorStandMotY(sinusoidalMotY);

        armorStand.o(loc.getX(), loc.getY(), loc.getZ()); // SETTING NEW LOCATION
        armorStand.aG = false; // SETTING ON GROUND TO FALSE

        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(armorStand);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveLookPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(armorStand.al(), (short) 0, (short) ((getArmorStandMotY() - lastMotY)*128), (short) 0, (byte) getArmorStandYAW(), (byte) 0, false);

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            v1_21_R1.sendPackets(p, teleportPacket, moveLookPacket);
        }
        tickCount++;
    }

    private void setArmorStandYAW(float yaw) {
        armorStand.r(yaw);
    }

    private void addArmorStandYAW(float yaw) {
        armorStand.r(getArmorStandYAW() + yaw);
    }

    private float getArmorStandYAW() {
        return armorStand.dG();
    }

    private void setArmorStandMotY(double y) {
        armorStand.h(new Vec3D(0, y, 0));
    }

    private void addArmorStandMotY(double y) {
        armorStand.h(new Vec3D(0, getArmorStandMotY() + y, 0));
    }

    private double getArmorStandMotY() {
        return armorStand.ds().d;
    }
}
