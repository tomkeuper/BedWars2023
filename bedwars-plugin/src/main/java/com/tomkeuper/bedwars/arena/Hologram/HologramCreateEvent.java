package com.tomkeuper.bedwars.arena.Hologram;

import com.tomkeuper.bedwars.api.Igram;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HologramCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Igram hologram;

    public HologramCreateEvent(Igram hologram) {
        this.hologram = hologram;
    }

    public Igram getHologram() {
        return hologram;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}