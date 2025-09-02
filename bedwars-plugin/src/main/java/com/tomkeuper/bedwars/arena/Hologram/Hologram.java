package com.tomkeuper.bedwars.arena.Hologram;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.Igram;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

public class Hologram implements Igram {

    private final List<ArmorStand> lines = new ArrayList<>();
    private final Location baseLocation;
    private boolean removed = false;

    public Hologram(Location loc) {
        this.baseLocation = loc.clone();

        // Register this hologram with the API
        BedWars.getAPI().getmama().registerHologram(this);

        // Fire creation event
        HologramCreateEvent event = new HologramCreateEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void addLine(Location loc, String text) {
        if (removed) return;

        double spacing = BedWars.config.getDouble(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_SPACING);
        Location lineLoc = loc.clone().add(0, -spacing * lines.size(), 0);

        ArmorStand stand = (ArmorStand) loc.getWorld().spawn(lineLoc, ArmorStand.class);
        stand.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
        stand.setCustomNameVisible(true);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(true);

        lines.add(stand);
    }

    @Override
    public void createResourceChestHologram(Location loc) {
        if (removed) return;

        String titleText = BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_TITLE);
        String subtitleText = BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_SUBTITLE);

        double yOffset = BedWars.config.getDouble(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_HOLOGRAM_Y_OFFSET);

        addLine(loc.clone().add(0.5, yOffset, 0.5), titleText);
        addLine(loc.clone().add(0.5, yOffset, 0.5), subtitleText);
    }

    @Override
    public void remove() {
        if (removed) return;

        // Fire remove event
        HologramRemoveEvent event = new HologramRemoveEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        lines.forEach(ArmorStand::remove);
        lines.clear();
        removed = true;

        // Unregister from API
        BedWars.getAPI().getmama().unregisterHologram(this);
    }

    @Override
    public List<ArmorStand> getLines() {
        return new ArrayList<>(lines);
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public Location getLocation() {
        return baseLocation.clone();
    }

    @Override
    public void updateLine(int index, String newText) {
        if (removed || index < 0 || index >= lines.size()) return;

        ArmorStand stand = lines.get(index);
        stand.setCustomName(ChatColor.translateAlternateColorCodes('&', newText));
    }
}