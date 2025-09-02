package com.tomkeuper.bedwars.api;

import org.bukkit.Location;

import java.util.List;

public interface ihologrammanager {
    void registerHologram(Igram hologram);
    void unregisterHologram(Igram hologram);
    List<Igram> getAllHolograms();
    List<Igram> getHologramsInRadius(Location center, double radius);
    void removeAllHolograms();
    Igram createHologram(Location loc);
}
