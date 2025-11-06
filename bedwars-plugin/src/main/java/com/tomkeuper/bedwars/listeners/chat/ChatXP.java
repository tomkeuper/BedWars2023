package com.tomkeuper.bedwars.listeners.chat;

import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerXpGainEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ChatXP implements Listener {
    private final BedWars api;
    private final HashMap<String, HashSet<UUID>> arenaPlayers = new HashMap<>();
    public ChatXP(BedWars api) {
        this.api = api;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatXP(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().trim();
        if (!message.equalsIgnoreCase("!Gl")) {
            return;
        }
        IArena arena = api.getArenaUtil().getArenaByPlayer(player);
        if (arena == null) {
            return;
        }
        if (!arena.isPlayer(player)) {
            return;
        }
        if (arena.getStatus() != GameState.playing) {
            player.sendMessage("§cYou can only use !GL after the game starts!");
            return;
        }
        String arenaname = arena.getArenaName();
        UUID playerid = player.getUniqueId();
        if (!arenaPlayers.containsKey(arenaname)) {
            arenaPlayers.put(arenaname, new HashSet<>());
        }
        if (arenaPlayers.get(arenaname).contains(playerid)) {
            player.sendMessage(ChatColor.RED + "You can only use this sentence per game");
            return;
        }

        int xpToAdd = 50;
        api.getLevelsUtil().addXp(player, xpToAdd, PlayerXpGainEvent.XpSource.OTHER);
        player.sendMessage("§a§l+ " + xpToAdd + " XP §7for saying Good Luck!");
        arenaPlayers.get(arenaname).add(playerid);
    }
    @EventHandler
    public void onChatXpEndGame(GameEndEvent event) {
        String arenaname = event.getArena().getArenaName();
        arenaPlayers.remove(arenaname);
    }
}
