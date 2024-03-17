package com.tomkeuper.bedwars.support.version.v1_16_R3;

import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class DefaultGenAnimation implements IGeneratorAnimation {
    private final Entity armorStand;
    private final Location loc;
    private boolean up = false;

    public DefaultGenAnimation(ArmorStand armorStand) {
        this.armorStand = ((CraftArmorStand) armorStand).getHandle();
        this.loc = armorStand.getLocation();
        setArmorStandYAW(0);
        setArmorStandMotY(0);
    }

    @Override
    public void run() {
        if (up) {
            if (getArmorStandYAW() >= 540) up = false;

            if (getArmorStandYAW() > 500) {
                addArmorStandYAW(1);
            } else if (getArmorStandYAW() > 470) {
                addArmorStandYAW(2);
            } else if (getArmorStandYAW() > 450) {
                addArmorStandYAW(3);
            } else {
                addArmorStandYAW(4);
            }
        } else {
            if (getArmorStandYAW() <= 0) up = true;

            if (getArmorStandYAW() > 120) {
                addArmorStandYAW(-4);
            } else if (getArmorStandYAW() > 90) {
                addArmorStandYAW(-3);
            } else if (getArmorStandYAW() > 70) {
                addArmorStandYAW(-2);
            } else {
                addArmorStandYAW(-1);
            }
        }

        armorStand.setLocation(loc.getX(), loc.getY(), loc.getZ(), getArmorStandYAW(), 0);
        armorStand.setOnGround(false);
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(armorStand);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveLookPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(armorStand.getId(), (byte) 0, (byte) getArmorStandMotY(), (byte) 0, (byte) getArmorStandYAW(), (byte) 0, false);

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            sendPackets(p, teleportPacket, moveLookPacket);
        }
    }

    private void sendPacket(Player p, Packet<PacketListenerPlayOut> packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    @SafeVarargs
    private void sendPackets(Player p, Packet<PacketListenerPlayOut>... packets) {
        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
        for (Packet<PacketListenerPlayOut> packet : packets) {
            connection.sendPacket(packet);
        }
    }

    private void setArmorStandYAW(float yaw) {
        armorStand.yaw = yaw;
    }

    private void addArmorStandYAW(float yaw) {
        armorStand.yaw += yaw;
    }

    private float getArmorStandYAW() {
        return  armorStand.yaw;
    }

    private void setArmorStandMotY(double y) {
        armorStand.setMot(new Vec3D(0, y, 0));
    }

    private void addArmorStandMotY(double y) {
        armorStand.setMot(armorStand.getMot().add(0, y, 0));
    }

    private double getArmorStandMotY() {
        return armorStand.getMot().y;
    }
}
