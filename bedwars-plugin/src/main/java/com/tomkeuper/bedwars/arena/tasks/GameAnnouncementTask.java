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

package com.tomkeuper.bedwars.arena.tasks;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.tasks.AnnouncementTask;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static com.tomkeuper.bedwars.BedWars.config;

public class GameAnnouncementTask implements Runnable, AnnouncementTask {

    BukkitTask task;

    private Arena arena;

    LinkedHashMap<Player, List<String>> messages;

    int index = 0;

    public GameAnnouncementTask(Arena arena) {
        this.arena = arena;
        messages = new LinkedHashMap<>();
        for (Player player: arena.getPlayers()) {
            loadMessagesForPlayer(player, Messages.ARENA_IN_GAME_ANNOUNCEMENT);
        }
        for (Player player: arena.getSpectators()) {
            loadMessagesForPlayer(player, Messages.ARENA_IN_GAME_ANNOUNCEMENT);
        }
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(BedWars.plugin, this, config.getInt(ConfigPath.GENERAL_CONFIGURATION_IN_GAME_ANNOUNCEMENT_COOLDOW) * 20L, config.getInt(ConfigPath.GENERAL_CONFIGURATION_IN_GAME_ANNOUNCEMENT_COOLDOW) *20L);
    }

    @Override
    public void loadMessagesForPlayer(Player p, String path) {
        if (this.messages.containsKey(p)) this.messages.get(p).addAll(Language.getList(p, path));
        this.messages.put(p, Language.getList(p, path));
    }

    @Override
    public void addMessageForPlayer(Player p, String message){
        if (this.messages.containsKey(p))  this.messages.get(p).add(message);
        this.messages.put(p, Collections.singletonList(message));
    }

    @Override
    public void addMessagesForPlayer(Player p, List<String> messages){
        if (this.messages.containsKey(p))  this.messages.get(p).addAll(messages);
        this.messages.put(p, messages);
    }
    public void run() {
        if (arena == null) {
            cancel();
        }
        for (Player player : arena.getPlayers()) {
            if (arena.getStatus() == GameState.playing) {
                try {
                    player.sendMessage(messages.get(player).get(index % messages.get(player).size()));
                } catch (NullPointerException e){
                    // Player might lose data when rejoining after getting disconnected
                    loadMessagesForPlayer(player, Messages.ARENA_IN_GAME_ANNOUNCEMENT);
                }
            }
        }
        ++this.index;
    }

    @Override
    public IArena getArena() {
        return arena;
    }

    @Override
    public BukkitTask getBukkitTask() {
        return task;
    }

    @Override
    public int getTask() {
        return task.getTaskId();
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
