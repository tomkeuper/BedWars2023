package com.tomkeuper.bedwars.support.version.v1_18_R2.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage;

public class HoloLine implements IHoloLine {
    private String text;
    private IHologram hologram;
    public final EntityArmorStand entity;
    private boolean showing = true;

    public HoloLine(String text, IHologram hologram) {
        this.text = text;
        this.hologram = hologram;
        entity = new EntityArmorStand(((CraftWorld) hologram.getLocation().getWorld()).getHandle(), 0, 0, 0);
        entity.a(CraftChatMessage.fromStringOrNull(text));
        entity.n(true);
        entity.j(true);
        entity.Q = true;

        Location loc = hologram.getLocation();
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(packet);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.ae(), entity.ai(), true);
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(metadataPacket);
        entity.o(loc.getX(), loc.getY() + hologram.size() * hologram.getGap(), loc.getZ());
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(teleportPacket);
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
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.ae(), entity.ai(), true);
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(metadataPacket);
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(teleportPacket);
    }

    @Override
    public void show() {
        this.showing = true;
        entity.n(true);
        update();
    }

    @Override
    public void hide() {
        this.showing = false;
        entity.n(false);
        update();
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void remove() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.ae());
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(packet);
        hologram.removeLine(this);
    }
}
