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

package com.tomkeuper.bedwars.support.version.v1_21_R7.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.support.version.v1_21_R7.v1_21_R7;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R7.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R7.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.*;

public class HoloLine implements IHoloLine {
    private String text;
    private IHologram hologram;
    public final EntityArmorStand entity;
    private boolean destroyed = false;

    public HoloLine(String text, IHologram hologram) {
        this.text = text;
        this.hologram = hologram;
        Location loc = hologram.getLocation();
        entity = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(), 0, 0, 0);
        entity.b(CraftChatMessage.fromStringOrNull(text)); // setCustomName
        entity.p(true); // setCustomNameVisible
        entity.l(true); // setInvisible
        entity.ag = true; // noPhysics (no gravity)
        entity.a_(loc.getX(), loc.getY() + hologram.size() * hologram.getGap(), loc.getZ());

        PacketPlayOutSpawnEntity packet = v1_21_R7.newPacketPlayOutSpawnEntity(entity);

        int entityId = entity.aA();
        var metadata = entity.aD().c();

        if (null == metadata) metadata = new ArrayList<>();

        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityId, metadata);

        final Vec3D delta = new Vec3D(0, 0, 0);
        final var positionMoveRotation = new PositionMoveRotation(entity.dJ(), delta, 0, entity.ee());
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityId, positionMoveRotation, new HashSet<>(), false);

        for (var player : hologram.getPlayers()) {
            v1_21_R7.sendPackets(player, packet, metadataPacket, teleportPacket);
        }
    }

    @Override
    public void setText(String text) {
        this.text = text;
        update();
    }

    @Override
    public void setText(String text, boolean update) {
        this.text = text;

        if (update) {
            update();
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setHologram(IHologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public IHologram getHologram() {
        return hologram;
    }

    @Override
    public void update() {
        entity.b(CraftChatMessage.fromStringOrNull(text));
        int position = hologram.getLines().indexOf(this);
        Location loc = hologram.getLocation();
        entity.a_(loc.getX(), loc.getY() + position * hologram.getGap(), loc.getZ());
        if (isDestroyed()) return;

        int entityId = entity.aA();
        var metadata = entity.aD().c();

        if (null == metadata) metadata = new ArrayList<>();

        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityId, metadata);

        final Vec3D delta = new Vec3D(0, 0, 0);
        final var positionMoveRotation = new PositionMoveRotation(entity.dJ(), delta, 0, entity.ee());

        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityId, positionMoveRotation, new HashSet<>(), false);

        for (var player : hologram.getPlayers()) {
            v1_21_R7.sendPackets(player, metadataPacket, teleportPacket);
        }
    }

    @Override
    public void update(Player player) {
        if (!hologram.getPlayers().contains(player)) return;
        entity.b(CraftChatMessage.fromStringOrNull(text));
        int position = hologram.getLines().indexOf(this);
        Location loc = hologram.getLocation();
        entity.a_(loc.getX(), loc.getY() + position * hologram.getGap(), loc.getZ());
        if (isDestroyed()) return;

        int entityId = entity.aA();
        var metadataUpdate = entity.aD().c();

        if (null == metadataUpdate) metadataUpdate = new ArrayList<>();

        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityId, metadataUpdate);

        final Vec3D delta = new Vec3D(0, 0, 0);
        final var positionMoveRotation = new PositionMoveRotation(entity.dJ(), delta, 0, entity.ee());

        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityId, positionMoveRotation, new HashSet<>(), false);

        v1_21_R7.sendPackets(player, metadataPacket, teleportPacket);
    }

    @Override
    public void reveal() {
        destroyed = false;

        PacketPlayOutSpawnEntity packet = v1_21_R7.newPacketPlayOutSpawnEntity(entity);
        for (var player : hologram.getPlayers()) {
            v1_21_R7.sendPacket(player, packet);
        }

        if (!hologram.getLines().contains(this)) hologram.addLine(this);
        hologram.update();
    }

    @Override
    public void reveal(Player player) {
        destroyed = false;

        PacketPlayOutSpawnEntity packet = v1_21_R7.newPacketPlayOutSpawnEntity(entity);
        v1_21_R7.sendPacket(player, packet);
    }

    @Override
    public void remove() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.aA());
        for (var player : hologram.getPlayers()) {
            v1_21_R7.sendPacket(player, packet);
        }
    }

    @Override
    public void remove(Player player) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.aA());
        v1_21_R7.sendPacket(player, packet);
    }

    @Override
    public void destroy() {
        destroyed = true;
        remove();
        hologram.removeLine(this);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
