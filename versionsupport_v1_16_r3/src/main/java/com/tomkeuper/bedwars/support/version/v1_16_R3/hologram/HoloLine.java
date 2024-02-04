package com.tomkeuper.bedwars.support.version.v1_16_R3.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;

public class HoloLine implements IHoloLine {
    private String text;
    private IHologram hologram;
    public final EntityArmorStand entity;

    private boolean showing = true;
    private boolean destroyed = false;

    public HoloLine(String text, IHologram hologram) {
        this.text = text;
        this.hologram = hologram;
        entity = new EntityArmorStand(((CraftWorld) hologram.getLocation().getWorld()).getHandle(), 0, 0, 0);
        entity.setCustomName(CraftChatMessage.fromStringOrNull(text));
        entity.setCustomNameVisible(true);
        entity.setInvisible(true);
        entity.setNoGravity(true);
        Location loc = hologram.getLocation();
        entity.setLocation(loc.getX(), loc.getY() + hologram.size() * hologram.getGap(), loc.getZ(), loc.getYaw(), loc.getPitch());

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);

        ((CraftPlayer) hologram.getPlayer()).getHandle().playerConnection.sendPacket(packet);
        ((CraftPlayer) hologram.getPlayer()).getHandle().playerConnection.sendPacket(metadataPacket);
        ((CraftPlayer) hologram.getPlayer()).getHandle().playerConnection.sendPacket(teleportPacket);
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
        Location loc = hologram.getLocation();
        int position = hologram.getLines().indexOf(this);
        entity.setLocation(loc.getX(), loc.getY() + position * hologram.getGap(), loc.getZ(), loc.getYaw(), loc.getPitch());
        if (destroyed) return;

        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(entity);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);

        ((CraftPlayer) hologram.getPlayer()).getHandle().playerConnection.sendPacket(packet);
        ((CraftPlayer) hologram.getPlayer()).getHandle().playerConnection.sendPacket(metadataPacket);
    }

    @Override
    public void show() {
        this.showing = true;
        entity.setCustomNameVisible(true);
        update();
    }

    @Override
    public void hide() {
        this.showing = false;
        entity.setCustomNameVisible(false);
        update();
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void reveal() {
        destroyed = false;

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
        ((CraftPlayer) hologram.getPlayer()).getHandle().playerConnection.sendPacket(packet);

        if (!hologram.getLines().contains(this)) hologram.addLine(this);
        hologram.update();
    }

    @Override
    public void remove() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getId());
        ((CraftPlayer) hologram.getPlayer()).getHandle().playerConnection.sendPacket(packet);
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
