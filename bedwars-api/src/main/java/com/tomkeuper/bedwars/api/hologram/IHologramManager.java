package com.tomkeuper.bedwars.api.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IHologramManager {

    /**
     * Create a hologram with the given lines at the given location.
     * <p>
     * NOTE: Please, note that the lines WILL BE REVERSED, so the first line will be the last line in the hologram.
     *
     * @param p - the player to create the hologram for
     * @param location - the location to create the hologram at
     * @param lines - the lines to create the hologram from
     * @return the hologram
     */
    IHologram createHologram(Player p, Location location, String... lines);

    /**
     * Create a hologram with the given lines at the given location.
     * <p>
     * NOTE: Please, note that the lines WILL BE REVERSED, so the first line will be the last line in the hologram.
     *
     * @param p - the player to create the hologram for
     * @param location - the location to create the hologram at
     * @param lines - the lines to create the hologram from
     * @return the hologram
     */
    IHologram createHologram(Player p, Location location, IHoloLine... lines);

    /**
     * Create a hologram line from the given text and hologram.
     * @param text - the text to create the line from
     * @param hologram - the hologram bounded to
     * @return the hologram line
     */
    IHoloLine lineFromText(String text, IHologram hologram);
}
