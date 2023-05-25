package com.tomkeuper.bedwars.sidebar;

import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.arena.Arena;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.bossbar.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class BoardListener implements Listener {
    @EventHandler
    public void onArenaLeave(PlayerLeaveArenaEvent event){
        if (TabAPI.getInstance().getBossBarManager() != null){
            for (BossBar bossBar : Arena.getArenaByPlayer(event.getPlayer()).getDragonBossbars()){
                bossBar.removePlayer(Objects.requireNonNull(TabAPI.getInstance().getPlayer(event.getPlayer().getUniqueId())));
            }
        }
    }
}
