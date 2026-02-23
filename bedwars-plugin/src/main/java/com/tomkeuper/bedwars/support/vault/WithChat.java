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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WithChat implements IChat {

    private static net.milkbowl.vault.chat.Chat chat;

    @Override
    public String getPrefix(Player p) {
        return ChatColor.translateAlternateColorCodes('&', chat.getPlayerPrefix(p));
    }

    @Override
    public String getSuffix(Player p) {
        return ChatColor.translateAlternateColorCodes('&', chat.getPlayerSuffix(p));
    }

    @Override
    public void sendMessage(Player player, String msg) {
        BedWars.plugin.adventure().player(player).sendMessage(ChatFormatting.parseLegacyMini(msg));
    }

    public static void setChat(net.milkbowl.vault.chat.Chat chat) {
        WithChat.chat = chat;
    }
}
