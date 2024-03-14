package com.tomkeuper.bedwars.support.version.v1_20_R3;

import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
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
        System.out.println("Created new animation");
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

        armorStand.p(loc.getX(), loc.getY(), loc.getZ()); // SETTING NEW LOCATION
        armorStand.aJ = false; // SETTING ON GROUND TO FALSE
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(armorStand);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveLookPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(armorStand.aj(), (byte) 0, (byte) 0, (byte) 0, (byte) getArmorStandYAW(), (byte) 0, false);

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            sendPackets(p, teleportPacket, moveLookPacket);
        }
    }

    private void sendPacket(Player p, Packet<PacketListenerPlayOut> packet) {
        ((CraftPlayer) p).getHandle().c.a(packet);
    }

    @SafeVarargs
    private void sendPackets(Player p, Packet<PacketListenerPlayOut>... packets) {
        PlayerConnection connection = ((CraftPlayer) p).getHandle().c;
        for (Packet<PacketListenerPlayOut> packet : packets) {
            connection.a(packet);
        }
    }

    private void setArmorStandYAW(float yaw) {
        armorStand.r(yaw);
    }

    private void addArmorStandYAW(float yaw) {
        armorStand.r(getArmorStandYAW() + yaw);
    }

    private float getArmorStandYAW() {
        return armorStand.dC();
    }

    private void setArmorStandMotY(double y) {
        armorStand.g(new Vec3D(0, y, 0));
    }

    private void addArmorStandMotY(double y) {
        armorStand.g(new Vec3D(0, getArmorStandMotY() + y, 0));
    }

    private double getArmorStandMotY() {
        return armorStand.dp().d;
    }
}
