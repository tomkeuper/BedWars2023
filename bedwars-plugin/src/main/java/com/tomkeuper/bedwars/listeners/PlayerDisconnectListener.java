package com.tomkeuper.bedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        MainLobbyJoinListener.resetPlayer(e.getPlayer().getUniqueId());
    }
}