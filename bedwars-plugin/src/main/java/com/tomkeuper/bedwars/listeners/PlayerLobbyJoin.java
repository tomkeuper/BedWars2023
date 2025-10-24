package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.player.PlayerJoinLobbyEvent;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLobbyJoin implements Listener {

    @EventHandler
    public void onPlayerLobbyJoinMessage(PlayerJoinLobbyEvent e) {
        Player player = e.getPlayer();
        String arenaName = e.getArenaName();
        Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
            IArena arena = Arena.getArenaByName(arenaName);
            if (arena == null) return;
            sendLobbyMessage(player, arena, arenaName);
            for (Player p : arena.getPlayers()) {
                if (!p.equals(player)) {
                    sendLobbyMessage(p, arena, arenaName);
                }
            }
        }, 1L);
    }

    private void sendLobbyMessage(Player player, IArena arena, String arenaName) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "                    " + ChatColor.BOLD + "BEDWARS");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "                Arena: " + ChatColor.YELLOW + arenaName);
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "        Waiting for players to join...");
        String message = ChatColor.translateAlternateColorCodes('&',
                "        &7Players: &8(&b%bw_on%&8/&b%bw_max%&8)");
        message = message.replace("%bw_on%", String.valueOf(arena.getPlayers().size()));
        message = message.replace("%bw_max%", String.valueOf(arena.getMaxPlayers()));
        player.sendMessage(message);
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("");
    }
}
