package com.tomkeuper.bedwars.api.items.handlers;

import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class PermanentItemHandler implements IPermanentItemHandler {
    private final String id;
    private final Plugin plugin;
    private final BedWars api;

    public PermanentItemHandler(@NotNull String id, @NotNull Plugin plugin, BedWars api) {
        this.id = id;
        this.plugin = plugin;
        this.api = api;
    }

    public boolean isVisible(Player player, IArena arena){
        return true;
    }

    public final String getId() {
        return this.id;
    }

    public final Plugin getPlugin() {
        return this.plugin;
    }

    public HandlerType getType() {
        return HandlerType.PLUGIN;
    }

    public final boolean isRegistered() {
        if (!api.getItemUtil().getItemHandlers().containsKey(this.getId())) return false;
        IPermanentItemHandler handler = api.getItemUtil().getItemHandlers().get(this.getId());
        return handler == this;
    }
}
