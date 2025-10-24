package com.tomkeuper.bedwars.commands.bedwars.subcmds.sensitive;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.command.SubCommand;
import com.tomkeuper.bedwars.api.events.player.PlayerJoinMainLobbyEvent;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.Misc;
import com.tomkeuper.bedwars.arena.SetupSession;
import com.tomkeuper.bedwars.commands.bedwars.MainCommand;
import com.tomkeuper.bedwars.configuration.Permissions;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class SetLobby extends SubCommand implements Listener {

    public SetLobby(ParentCommand parent, String name) {
        super(parent, name);
        setPriority(1);
        showInList(true);
        setPermission(Permissions.PERMISSION_SETUP_ARENA);
        setDisplayInfo(Misc.msgHoverClick("§6 ▪ §7/"+ MainCommand.getInstance().getName()+" "+getSubCommandName()+ (BedWars.config.getLobbyWorldName().isEmpty() ? " §c(not set)" : " §a(set)"),
                "§aSet the main lobby. §fThis is required but\n§fif you are going to use the server in §eBUNGEE §fmode\n§fthe lobby location will §enot §fbe used.\n§eType again to replace the old spawn location.",
                "/"+getParent().getName()+" "+getSubCommandName(), ClickEvent.Action.RUN_COMMAND));

        // Register this class as a listener
        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender s) {
        if (s instanceof ConsoleCommandSender) return false;
        Player p = (Player) s;
        if (SetupSession.isInSetupSession(p.getUniqueId())){
            p.sendMessage("§6 ▪ §4This command can't be used in arenas. It is meant for the main lobby!");
            return true;
        }
        BedWars.config.saveConfigLoc("lobbyLoc", p.getLocation());
        p.sendMessage("§6 ▪ §7Lobby location set!");
        BedWars.config.reload();
        BedWars.setLobbyWorld(p.getLocation().getWorld().getName());
        return true;
    }

    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // Check if player is in lobby world
        if (!isInLobby(player)) return;

        // Call custom event
        callLobbyJoinEvent(player);
    }

    @EventHandler
    public void onPlayerTeleportToLobby(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        // Check if player is teleporting to lobby world
        if (e.getTo() == null || e.getTo().getWorld() == null) return;
        if (!e.getTo().getWorld().getName().equals(BedWars.getLobbyWorld())) return;

        // Call custom event after a short delay
        Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
            if (isInLobby(player)) {
                callLobbyJoinEvent(player);
            }
        }, 10L);
    }

    private boolean isInLobby(Player player) {
        if (BedWars.getLobbyWorld().isEmpty()) return false;
        if (player.getWorld() == null) return false;
        return player.getWorld().getName().equals(BedWars.getLobbyWorld());
    }

    private void callLobbyJoinEvent(Player player) {
        // Create and call the custom event
        PlayerJoinMainLobbyEvent event = new PlayerJoinMainLobbyEvent(player);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }

    @Override
    public boolean canSee(CommandSender s, com.tomkeuper.bedwars.api.BedWars api) {
        if (s instanceof ConsoleCommandSender) return false;

        Player p = (Player) s;
        if (Arena.isInArena(p)) return false;

        if (SetupSession.isInSetupSession(p.getUniqueId())) return false;

        if (!BedWars.getLobbyWorld().isEmpty()) return false;

        return hasPermission(s);
    }
}