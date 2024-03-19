package com.tomkeuper.bedwars.api.arena.team;

import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.language.Messages;
import org.bukkit.Bukkit;

import static com.tomkeuper.bedwars.api.language.Language.getMsg;

public interface IBedHolo {
    /**
     * Create the hologram for the bed.
     */
    void create();

    /**
     * Hide the hologram for the bed.
     */
    void hide();

    /**
     * Destroy the hologram for the bed.
     */
    void destroy();

    /**
     * Show the hologram for the bed.
     */
    void show();

    /**
     * Get the main hologram associated with the bed.
     */
    IHologram getHologram();
}
