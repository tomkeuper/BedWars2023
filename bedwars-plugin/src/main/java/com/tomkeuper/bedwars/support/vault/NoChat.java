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

package com.tomkeuper.bedwars.support.vault;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.chat.IChat;
import com.tomkeuper.bedwars.listeners.chat.ChatFormatting;
import com.tomkeuper.bedwars.support.papi.SupportPAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class NoChat implements IChat {
    @Override
    public String getPrefix(Player p) {
        return "";
    }

    @Override
    public String getSuffix(Player p) {
        return "";
    }

    @Override
    public void sendMessage(Player player, String msg) {
        BedWars.plugin.sendMessage(player, ChatFormatting.parseLegacyMini(msg));
    }

    @Override
    public Component parseMiniMessage(String msg) {
        return ChatFormatting.parseLegacyMini(msg);
    }

    @Override
    public Component parsePlaceholders(Player player, String msg) {
        String format = SupportPAPI.getSupportPAPI().replace(player, msg);
        return ChatFormatting.parseLegacyMini(format);
    }
}
