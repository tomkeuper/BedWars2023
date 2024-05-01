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
