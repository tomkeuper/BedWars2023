/*
 * BedWars2023 - A bed wars mini-game.
 * Copyright (C) 2024 Tomas Keuper
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: contact@fyreblox.com
 */

package com.tomkeuper.bedwars.sidebar;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.arena.Arena;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BossBar;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class BoardListener implements Listener {

    @EventHandler
    public void onArenaLeave(PlayerLeaveArenaEvent event){
        IArena arena = Arena.getArenaByPlayer(event.getPlayer());
        if (TabAPI.getInstance().getPlayer(event.getPlayer().getUniqueId()) == null) return;
        if (TabAPI.getInstance().getBossBarManager() != null && arena != null){
            for (BossBar bossBar : arena.getDragonBossbars()){
                bossBar.removePlayer(Objects.requireNonNull(TabAPI.getInstance().getPlayer(event.getPlayer().getUniqueId())));
            }
        }

        // Force update the prefix and suffix
        PlayerPlaceholder prefixPlaceholderTab = (PlayerPlaceholder) TabAPI.getInstance().getPlaceholderManager().getPlaceholder("%bw_prefix_tab%");
        PlayerPlaceholder suffixPlaceholderTab = (PlayerPlaceholder) TabAPI.getInstance().getPlaceholderManager().getPlaceholder("%bw_suffix_tab%");
        PlayerPlaceholder prefixPlaceholderHead = (PlayerPlaceholder) TabAPI.getInstance().getPlaceholderManager().getPlaceholder("%bw_prefix_head%");
        PlayerPlaceholder suffixPlaceholderHead = (PlayerPlaceholder) TabAPI.getInstance().getPlaceholderManager().getPlaceholder("%bw_suffix_head%");
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(event.getPlayer().getUniqueId());

        assert tabPlayer != null;
        prefixPlaceholderTab.updateValue(tabPlayer, BoardManager.getInstance().getPrefixTab(tabPlayer));
        suffixPlaceholderTab.updateValue(tabPlayer, BoardManager.getInstance().getSuffixTab(tabPlayer));
        prefixPlaceholderHead.updateValue(tabPlayer, BoardManager.getInstance().getPrefixHead(tabPlayer));
        suffixPlaceholderHead.updateValue(tabPlayer, BoardManager.getInstance().getSuffixHead(tabPlayer));
    }

    @EventHandler
    public void onDisconnect(PlayerLeaveArenaEvent event) {
        BoardManager.getInstance().cleanupPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        BoardManager.getInstance().tabPlayerCache.remove(event.getPlayer().getUniqueId());
    }
}
