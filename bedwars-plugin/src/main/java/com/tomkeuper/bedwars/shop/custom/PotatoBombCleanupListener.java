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

package com.tomkeuper.bedwars.shop.custom;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.api.events.server.ArenaRestartEvent;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Cleans up Potato Bomb purchase history when players leave or arenas restart
 */
public class PotatoBombCleanupListener implements Listener {

    @EventHandler
    public void onPlayerLeaveArena(PlayerLeaveArenaEvent event) {
        IArena arena = Arena.getArenaByPlayer(event.getPlayer());
        if (arena != null) {
            PotatoBombListener.clearPurchaseHistory(event.getPlayer(), arena);
        }
    }

    @EventHandler
    public void onArenaRestart(ArenaRestartEvent event) {
        IArena arena = Arena.getArenaByName(event.getArenaName());
        if (arena != null) {
            PotatoBombListener.clearArenaHistory(arena);
        }
    }
}