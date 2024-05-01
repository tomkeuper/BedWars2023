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

package com.tomkeuper.bedwars.arena.upgrades;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.api.events.server.ArenaDisableEvent;
import com.tomkeuper.bedwars.api.events.team.TeamEliminatedEvent;
import com.tomkeuper.bedwars.api.events.upgrades.UpgradeBuyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HealPoolListener implements Listener {
    @EventHandler
    public void onTeamUpgrade(UpgradeBuyEvent e){
        if (e.getTeamUpgrade().getName().contains("heal-pool")){
            IArena a = e.getArena();
            if (a == null) return;
            ITeam bwt = a.getTeam(e.getPlayer());
            if (bwt == null) return;
            if (!HealPoolTask.exists(a, bwt)){
                new HealPoolTask(bwt);
            }
        }
    }

    @EventHandler
    public void onDisable(ArenaDisableEvent e){
        HealPoolTask.removeForArena(e.getWorldName());
    }

    @EventHandler
    public void onEnd(GameEndEvent e) {
        HealPoolTask.removeForArena(e.getArena());
    }

    @EventHandler
    public void teamDead(TeamEliminatedEvent e)
    {
        HealPoolTask.removeForTeam(e.getTeam());
    }

    @EventHandler
    public void LastLeave(PlayerLeaveArenaEvent event){
        if (event.getArena().getPlayers().isEmpty())
            HealPoolTask.removeForArena(event.getArena());
    }
}
