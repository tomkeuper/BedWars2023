package com.tomkeuper.bedwars.support.version.v1_8_R3;

import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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
            if (getArmorStandYAW() >= 500) {
                addArmorStandYAW(-1);
            } else if (getArmorStandYAW() >= 400) {
                addArmorStandYAW(-2);
            } else if (getArmorStandYAW() >= 300) {
                addArmorStandMotY(0.1);
                addArmorStandYAW(-4);
            } else if (getArmorStandYAW() >= 200) {
                addArmorStandMotY(0.2);
                addArmorStandYAW(-6);
            } else {
                addArmorStandYAW(-8);
            }

            if (getArmorStandYAW() >= 540) {
                up = false;
            }
        } else {
            if (getArmorStandYAW() <= 40) {
                addArmorStandYAW(1);
            } else if (getArmorStandYAW() <= 140) {
                addArmorStandYAW(2);
            } else if (getArmorStandYAW() <= 240) {
                addArmorStandMotY(-0.1);
                addArmorStandYAW(4);
            } else if (getArmorStandYAW() <= 340) {
                addArmorStandMotY(-0.2);
                addArmorStandYAW(6);
            } else {
                addArmorStandYAW(8);
            }

            if (getArmorStandYAW() <= 0) {
                up = true;
            }
        }

        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(armorStand.getId(), MathHelper.floor(loc.getX() * 32), MathHelper.floor(loc.getY() * 32), MathHelper.floor(loc.getZ() * 32), (byte) 0, (byte) 0, false);
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
        armorStand.motY = y;
    }

    private void addArmorStandMotY(double y) {
        armorStand.motY += y;
    }

    private double getArmorStandMotY() {
        return armorStand.motY;
    }
}
