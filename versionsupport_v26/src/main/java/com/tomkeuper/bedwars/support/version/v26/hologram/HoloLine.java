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

package com.tomkeuper.bedwars.support.version.v26.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.support.version.v26.v26;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.*;

public class HoloLine implements IHoloLine {
    private String text;
    private IHologram hologram;
    public final ArmorStand entity;
    private boolean destroyed = false;

    public HoloLine(String text, IHologram hologram) {
        this.text = text;
        this.hologram = hologram;
        Location loc = hologram.getLocation();
        entity = new ArmorStand(((CraftWorld) loc.getWorld()).getHandle(), 0, 0, 0);
        entity.setCustomName(CraftChatMessage.fromStringOrNull(text)); // setCustomName
        entity.setCustomNameVisible(true);
        entity.setInvisible(true);
        entity.noPhysics = true; // noPhysics (no gravity)
        entity.setPos(loc.getX(), loc.getY() + hologram.size() * hologram.getGap(), loc.getZ());

        ClientboundAddEntityPacket packet = v26.newAddEntityPacket(entity);

        int entityId = entity.getId();
        var metadata = entity.getEntityData().getNonDefaultValues();

        if (null == metadata) metadata = new ArrayList<>();

        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(entityId, metadata);

        final Vec3 delta = new Vec3(0, 0, 0);
        final var positionMoveRotation = new PositionMoveRotation(entity.trackingPosition(), delta, 0, entity.getXRot());
        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(entityId, positionMoveRotation, new HashSet<>(), false);

        for (var player : hologram.getPlayers()) {
            v26.sendPackets(player, packet, metadataPacket, teleportPacket);
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
        entity.setCustomName(CraftChatMessage.fromStringOrNull(text));
        int position = hologram.getLines().indexOf(this);
        Location loc = hologram.getLocation();
        entity.setPos(loc.getX(), loc.getY() + position * hologram.getGap(), loc.getZ());
        if (isDestroyed()) return;

        int entityId = entity.getId();
        var metadata = entity.getEntityData().getNonDefaultValues();

        if (null == metadata) metadata = new ArrayList<>();

        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(entityId, metadata);

        final Vec3 delta = new Vec3(0, 0, 0);
        final var positionMoveRotation = new PositionMoveRotation(entity.trackingPosition(), delta, 0, entity.getXRot());

        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(entityId, positionMoveRotation, new HashSet<>(), false);

        for (var player : hologram.getPlayers()) {
            v26.sendPackets(player, metadataPacket, teleportPacket);
        }
    }

    @Override
    public void update(Player player) {
        if (!hologram.getPlayers().contains(player)) return;
        entity.setCustomName(CraftChatMessage.fromStringOrNull(text));
        int position = hologram.getLines().indexOf(this);
        Location loc = hologram.getLocation();
        entity.setPos(loc.getX(), loc.getY() + position * hologram.getGap(), loc.getZ());
        if (isDestroyed()) return;

        int entityId = entity.getId();
        var metadataUpdate = entity.getEntityData().getNonDefaultValues();

        if (null == metadataUpdate) metadataUpdate = new ArrayList<>();

        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(entityId, metadataUpdate);

        final Vec3 delta = new Vec3(0, 0, 0);
        final var positionMoveRotation = new PositionMoveRotation(entity.trackingPosition(), delta, 0, entity.getXRot());

        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(entityId, positionMoveRotation, new HashSet<>(), false);

        v26.sendPackets(player, metadataPacket, teleportPacket);
    }

    @Override
    public void reveal() {
        destroyed = false;

        ClientboundAddEntityPacket packet = v26.newAddEntityPacket(entity);
        for (var player : hologram.getPlayers()) {
            v26.sendPacket(player, packet);
        }

        if (!hologram.getLines().contains(this)) hologram.addLine(this);
        hologram.update();
    }

    @Override
    public void reveal(Player player) {
        destroyed = false;

        ClientboundAddEntityPacket packet = v26.newAddEntityPacket(entity);
        v26.sendPacket(player, packet);
    }

    @Override
    public void remove() {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entity.getId());
        for (var player : hologram.getPlayers()) {
            v26.sendPacket(player, packet);
        }
    }

    @Override
    public void remove(Player player) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entity.getId());
        v26.sendPacket(player, packet);
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
