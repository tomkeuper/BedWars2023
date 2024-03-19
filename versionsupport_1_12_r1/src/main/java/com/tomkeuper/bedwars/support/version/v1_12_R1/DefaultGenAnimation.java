package com.tomkeuper.bedwars.support.version.v1_12_R1;

import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DefaultGenAnimation implements IGeneratorAnimation {
    private final Entity armorStand;
    private final Location loc;
    private int tickCount = 0; // A counter to keep track of the ticks since the animation started.

    // Constants for the sinusoidal motion
    final double frequency = 0.035; // Controls the oscillation speed.
    final double amplitude = 260; // Controls the range of YAW motion.
    final double verticalAmplitude = 10; // Controls the range of vertical motion.

    public DefaultGenAnimation(ArmorStand armorStand) {
        this.armorStand = ((CraftArmorStand) armorStand).getHandle();
        this.loc = armorStand.getLocation();
        setArmorStandYAW(0);
        setArmorStandMotY(0);
    }

    @Override
    public String getIdentifier() {
        return "bw2023:default";
    }

    @Override
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("BedWars2023");
    }

    @Override
    public void run() {
        // Calculate sinusoidal values for YAW and MotY
        float sinusoidalYaw = (float) (Math.sin(frequency * tickCount) * amplitude);
        float sinusoidalMotY = (float) (Math.sin(frequency * tickCount) * verticalAmplitude);

        // Update the armor stand's YAW and MotY based on the sinusoidal functions
        final double lastMotY = getArmorStandMotY();
        setArmorStandYAW(sinusoidalYaw);
        addArmorStandMotY(sinusoidalMotY);

        armorStand.setLocation(loc.getX(), loc.getY(), loc.getZ(), getArmorStandYAW(), 0);
        armorStand.onGround = false;
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(armorStand);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveLookPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(armorStand.getId(), 0, (long) ((getArmorStandMotY() - lastMotY)*128), 0, (byte) getArmorStandYAW(), (byte) 0, false);

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            sendPackets(p, teleportPacket, moveLookPacket);
        }
        tickCount++;
    }

    private void sendPacket(Player p, Packet<PacketListenerPlayOut> packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    @SafeVarargs
    private void sendPackets(Player p, @NotNull Packet<PacketListenerPlayOut>... packets) {
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
