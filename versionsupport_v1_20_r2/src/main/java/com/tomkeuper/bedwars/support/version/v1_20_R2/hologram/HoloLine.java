package com.tomkeuper.bedwars.support.version.v1_20_R2.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftChatMessage;

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
        entity.b(CraftChatMessage.fromStringOrNull(text));
        entity.n(true);
        entity.j(true);
        entity.af = true;
        Location loc = hologram.getLocation();
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity, 78);
        ((CraftPlayer) hologram.getPlayer()).getHandle().c.a(packet);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.ah(), entity.al().c());
        ((CraftPlayer) hologram.getPlayer()).getHandle().c.a(metadataPacket);
        entity.p(loc.getX(), loc.getY() + hologram.size() * hologram.getGap(), loc.getZ());
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);
        ((CraftPlayer) hologram.getPlayer()).getHandle().c.a(teleportPacket);
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
        entity.p(hologram.getLocation().getX(), hologram.getLocation().getY() + position * hologram.getGap(), hologram.getLocation().getZ());
        if (isDestroyed()) return;
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.ah(), entity.al().c());
        ((CraftPlayer) hologram.getPlayer()).getHandle().c.a(metadataPacket);
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);
        ((CraftPlayer) hologram.getPlayer()).getHandle().c.a(teleportPacket);
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
    public void reveal() {
        destroyed = false;
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity);
        ((CraftPlayer) hologram.getPlayer()).getHandle().c.a(packet);
        if (!hologram.getLines().contains(this)) hologram.addLine(this);
        hologram.update();
    }

    @Override
    public void remove() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.ah());
        ((CraftPlayer) hologram.getPlayer()).getHandle().c.a(packet);
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