package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.api.events.player.PlayerJoinMainLobbyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainLobbyJoinListener implements Listener {
    private static final Set<UUID> shownPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerJoinMainLobby(PlayerJoinMainLobbyEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        if (shownPlayers.contains(playerId)) {
            return;
        }
        shownPlayers.add(playerId);
        sendLobbyWelcomeMessage(player);
    }

    public static void resetPlayer(UUID playerId) {
        shownPlayers.remove(playerId);
    }
    private void sendLobbyWelcomeMessage(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "                    " + ChatColor.BOLD + "BEDWARS");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "                Welcome to the lobby!");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "        Right-click the " + ChatColor.GOLD + "Bed " + ChatColor.GRAY + "to play");
        player.sendMessage("");

        // Count online players
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        String message = ChatColor.translateAlternateColorCodes('&',
                "        &7Online Players: &b" + onlinePlayers);
        player.sendMessage(message);

        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("");
    }
}