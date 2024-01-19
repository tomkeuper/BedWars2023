package com.tomkeuper.bedwars.support.version.v1_19_R3.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;

public class HoloLine implements IHoloLine {
    private String text;
    private IHologram hologram;
    public final EntityArmorStand entity;
    private boolean showing = true;

    public HoloLine(String text, IHologram hologram) {
        this.text = text;
        this.hologram = hologram;
        entity = new EntityArmorStand(((CraftWorld) hologram.getLocation().getWorld()).getHandle(), 0, 0, 0);
        entity.b(CraftChatMessage.fromStringOrNull(text));
        entity.j(true);
        entity.n(true);
        entity.ae = true;
        Location loc = hologram.getLocation();
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity, 78);
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(packet);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.af(), entity.aj().c());
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(metadataPacket);
        entity.p(loc.getX(), loc.getY() + hologram.size() * hologram.getGap(), loc.getZ());
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
        entity.b(CraftChatMessage.fromStringOrNull(text));
        int position = hologram.getLines().indexOf(this);
        entity.p(hologram.getLocation().getX(), hologram.getLocation().getY() + position * hologram.getGap(), hologram.getLocation().getZ());
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.af(), entity.aj().c());
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(metadataPacket);
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entity);
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(teleportPacket);
    }

    @Override
    public void show() {
        this.showing = true;
        entity.j(true);
        update();
    }

    @Override
    public void hide() {
        this.showing = false;
        entity.j(false);
        update();
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void remove() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.af());
        ((CraftPlayer) hologram.getPlayer()).getHandle().b.a(packet);
        hologram.removeLine(this);
    }
}
