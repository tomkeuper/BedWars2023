package com.tomkeuper.bedwars.arena.feature;


import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.configuration.Permissions;
import com.tomkeuper.bedwars.listeners.chat.ChatFormatting;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GGFeature implements Listener {

    private final List<UUID> winners = new ArrayList<>();

    private String gg;

    public GGFeature() {
        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);
        gg = ChatColor.translateAlternateColorCodes('&', BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_GOLDEN_GG_COLOR));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameEnd(GameEndEvent event) {
        event.getWinners().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            winners.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLaterAsynchronously(BedWars.getPlugin(BedWars.class), () -> {winners.remove(player);}, BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_RESTART)  * 20L);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!BedWars.config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_AUTO_GG_ENABLED)) return;
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (!player.hasPermission(Permissions.PERMISSION_GOLDEN_GG)) return;
        if (!winners.contains(player.getUniqueId())) return;
        if (message.toLowerCase().contains("gg")) {
            message = message.replaceAll("(?i)gg",gg + "gg§r");
            event.setMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void autoGG(GameEndEvent event) {
        IArena arena = event.getArena();

        /*
         * Send message to players who have not died
         */
        arena.getPlayers().forEach(player -> event.getWinners().forEach(w -> {
            Player winner = Bukkit.getPlayer(w);
            if (winner != null && winner.hasPermission(Permissions.PERMISSION_AUTO_GG)) {
                ITeam team = arena.getTeam(winner);
                Component message = ChatFormatting.parseLegacyMini(BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_AUTO_GG_TEXT)
                        .replaceAll("§", "&")
                        .replace("%bw_player%", winner.getName())
                        .replace("%bw_level%", BedWars.getAPI().getLevelsUtil().getLevel(winner))
                        .replace("%bw_team_color%", team.getColor().chat() + "[" + team.getDisplayName(Language.getPlayerLanguage(winner)).toUpperCase() + "]"));
                BedWars.plugin.adventure().player(player).sendMessage(message);
                BedWars.plugin.adventure().player(winner).sendMessage(message);
                winners.remove(player.getUniqueId());
                winners.remove(winner.getUniqueId());
            }
        }));
        /*
         * Send the message to players who are spectators
         */
        arena.getSpectators().forEach(player -> event.getWinners().forEach(w -> {
            Player winner = Bukkit.getPlayer(w);
            if (winner != null && winner.hasPermission(Permissions.PERMISSION_AUTO_GG)) {
                ITeam team = arena.getTeam(winner);
                Component message = ChatFormatting.parseLegacyMini(BedWars.config.getString(ConfigPath.GENERAL_CONFIGURATION_AUTO_GG_TEXT)
                        .replaceAll("§", "&")
                        .replace("%bw_player%", winner.getName())
                        .replace("%bw_level%", BedWars.getAPI().getLevelsUtil().getLevel(winner))
                        .replace("%bw_team_color%", team.getColor().chat() + "[" + team.getDisplayName(Language.getPlayerLanguage(winner)).toUpperCase() + "]"));
                BedWars.plugin.adventure().player(player).sendMessage(message);
                BedWars.plugin.adventure().player(winner).sendMessage(message);
                winners.remove(player.getUniqueId());
                winners.remove(winner.getUniqueId());
            }
        }));
    }
}