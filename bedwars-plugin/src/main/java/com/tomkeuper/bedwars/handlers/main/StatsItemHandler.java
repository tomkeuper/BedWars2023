package com.tomkeuper.bedwars.handlers.main;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItem;
import com.tomkeuper.bedwars.arena.Misc;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class StatsItemHandler extends LobbyItemHandler {
    public StatsItemHandler(String id, Plugin plugin) {
        super(id, plugin);
    }

    @Override
    public void handleUse(Player player, IArena arena, IPermanentItem item) {
        Misc.openStatsGUI(player);
    }

}
