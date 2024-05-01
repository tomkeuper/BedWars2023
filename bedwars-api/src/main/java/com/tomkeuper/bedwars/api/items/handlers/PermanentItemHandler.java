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
