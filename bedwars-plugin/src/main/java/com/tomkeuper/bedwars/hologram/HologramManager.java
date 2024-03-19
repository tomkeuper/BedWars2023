package com.tomkeuper.bedwars.hologram;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.hologram.IHologramManager;
import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramManager implements IHologramManager {

    @Override
    public IHologram createHologram(Player p, Location location, String... lines) {
        return BedWars.nms.createHologram(p, location, lines);
    }

    @Override
    public IHologram createHologram(Player p, Location location, IHoloLine... lines) {
        return BedWars.nms.createHologram(p, location, lines);
    }

    @Override
    public IHoloLine lineFromText(String text, IHologram hologram) {
        return BedWars.nms.lineFromText(text, hologram);
    }
}
