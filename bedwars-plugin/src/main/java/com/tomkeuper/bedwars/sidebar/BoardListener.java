package com.tomkeuper.bedwars.sidebar;

import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.arena.Arena;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BossBar;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
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

        // Force update the prefix and suffix
        PlayerPlaceholder prefixPlaceholder = (PlayerPlaceholder) TabAPI.getInstance().getPlaceholderManager().getPlaceholder("%bw_prefix%");
        PlayerPlaceholder suffixPlaceholder = (PlayerPlaceholder) TabAPI.getInstance().getPlaceholderManager().getPlaceholder("%bw_suffix%");
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(event.getPlayer().getUniqueId());

        assert tabPlayer != null;
        prefixPlaceholder.updateValue(tabPlayer, BoardManager.getInstance().getPrefix(tabPlayer));
        suffixPlaceholder.updateValue(tabPlayer, BoardManager.getInstance().getSuffix(tabPlayer));
    }
}
