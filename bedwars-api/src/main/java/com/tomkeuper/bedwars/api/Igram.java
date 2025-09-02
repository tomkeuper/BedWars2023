package com.tomkeuper.bedwars.api;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.List;

public interface Igram {
    void addLine(Location loc, String text);
    void createResourceChestHologram(Location loc);
    void remove();
    List<ArmorStand> getLines();
    boolean isRemoved();
    Location getLocation();
    void updateLine(int index, String newText);
}
