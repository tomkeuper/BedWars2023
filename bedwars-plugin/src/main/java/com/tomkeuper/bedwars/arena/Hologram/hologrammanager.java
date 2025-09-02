package com.tomkeuper.bedwars.arena.Hologram;

import com.tomkeuper.bedwars.api.Igram;
import com.tomkeuper.bedwars.api.ihologrammanager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class hologrammanager implements ihologrammanager {

    private final List<Igram> holograms = new CopyOnWriteArrayList<>();

    @Override
    public void registerHologram(Igram hologram) {
        if (!holograms.contains(hologram)) {
            holograms.add(hologram);
        }
    }

    @Override
    public void unregisterHologram(Igram hologram) {
        holograms.remove(hologram);
    }

    @Override
    public List<Igram> getAllHolograms() {
        return new ArrayList<>(holograms);
    }

    @Override
    public List<Igram> getHologramsInRadius(Location center, double radius) {
        List<Igram> nearby = new ArrayList<>();
        for (Igram hologram : holograms) {
            if (hologram.getLocation().getWorld().equals(center.getWorld()) &&
                    hologram.getLocation().distance(center) <= radius) {
                nearby.add(hologram);
            }
        }
        return nearby;
    }

    @Override
    public void removeAllHolograms() {
        List<Igram> toRemove = new ArrayList<>(holograms);
        for (Igram hologram : toRemove) {
            hologram.remove();
        }
        holograms.clear();
    }

    @Override
    public Igram createHologram(Location location) {
        return new Hologram(location);
    }
}