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

package com.tomkeuper.bedwars.support.version.v1_18_R2.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class HoloLine implements IHoloLine {
    private String text;
    private IHologram hologram;
    public final EntityArmorStand entity;
    private boolean destroyed = false;

    public HoloLine(String text, IHologram hologram) {
        this.text = text;
        this.hologram = hologram;
        entity = new EntityArmorStand(((CraftWorld) hologram.getLocation().getWorld()).getHandle(), 0, 0, 0);
        entity.a(CraftChatMessage.fromStringOrNull(text)); // setCustomName
        entity.n(true); // setCustomNameVisible
        entity.j(true); // setInvisible
        entity.Q = true; // noPhysics
        Location loc = hologram.getLocation();
        entity.o(loc.getX(), loc.getY() + hologram.size() * hologram.getGap(), loc.getZ());

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.ae(), entity.ai(), true);
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);

        for (Player player : hologram.getPlayers()) {
            PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
            pc.a(packet);
            pc.a(metadataPacket);
            pc.a(teleportPacket);
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
        entity.a(CraftChatMessage.fromStringOrNull(text));
        int position = hologram.getLines().indexOf(this);
        entity.o(hologram.getLocation().getX(), hologram.getLocation().getY() + position * hologram.getGap(), hologram.getLocation().getZ());
        if (destroyed) return;

        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.ae(), entity.ai(), true);
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);

        for (Player player : hologram.getPlayers()) {
            PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
            pc.a(metadataPacket);
            pc.a(teleportPacket);
        }
    }

    @Override
    public void update(Player player) {
        if (!hologram.getPlayers().contains(player)) return;
        entity.a(CraftChatMessage.fromStringOrNull(text));
        int position = hologram.getLines().indexOf(this);
        entity.o(hologram.getLocation().getX(), hologram.getLocation().getY() + position * hologram.getGap(), hologram.getLocation().getZ());
        if (destroyed) return;

        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.ae(), entity.ai(), true);
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);

        PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
        pc.a(metadataPacket);
        pc.a(teleportPacket);
    }

    @Override
    public void reveal() {
        destroyed = false;

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
        for (Player player : hologram.getPlayers()) {
            PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
            pc.a(packet);
        }

        if (!hologram.getLines().contains(this)) hologram.addLine(this);
        hologram.update();
    }

    @Override
    public void reveal(Player player) {
        destroyed = false;

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
        PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
        pc.a(packet);

        if (!hologram.getLines().contains(this)) hologram.addLine(this);
        hologram.update(player);
    }

    @Override
    public void remove() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.ae());
        for (Player player : hologram.getPlayers()) {
            PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
            pc.a(packet);
        }
    }

    @Override
    public void remove(Player player) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.ae());
        PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
        pc.a(packet);
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
