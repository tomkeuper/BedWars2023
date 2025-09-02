package com.tomkeuper.bedwars.commands.bedwars.subcmds.sensitive;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.NextEvent;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class SetEventCommand extends BukkitCommand {
    public SetEventCommand(String name) {
        super(name);
        this.setPermissionMessage("§cYou do not have permission to use this command.");
        this.description = "Allows OP players to change the event for their current arena.";
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be OP to use this command.");
            return true;
        }
        IArena arena = Arena.getArenaByPlayer(player);
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "You must be in an arena to use this command.");
            return true;
        }
        if (args.length < 1) {
            StringBuilder eventsList = new StringBuilder(ChatColor.YELLOW + "Available events: ");
            for (NextEvent event : NextEvent.values()) {
                eventsList.append(event.name()).append(", ");
            }
            if (eventsList.length() > 2) eventsList.setLength(eventsList.length() - 2); // Remove last comma
            player.sendMessage(eventsList.toString());
            player.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <eventName>");
            return true;
        }
        String eventName = args[0].toUpperCase();
        NextEvent selectedEvent = null;
        for (NextEvent event : NextEvent.values()) {
            if (event.name().equalsIgnoreCase(eventName)) {
                selectedEvent = event;
                break;
            }
        }
        if (selectedEvent == null) {
            player.sendMessage(ChatColor.RED + "Invalid event name! Use one of: ");
            StringBuilder eventsList = new StringBuilder();
            for (NextEvent event : NextEvent.values()) {
                eventsList.append(event.name()).append(", ");
            }
            if (eventsList.length() > 2) eventsList.setLength(eventsList.length() - 2);
            player.sendMessage(ChatColor.YELLOW + eventsList.toString());
            return true;
        }
        // Set the event for the arena (if supported by your API)
        try {
            java.lang.reflect.Field eventField = arena.getClass().getDeclaredField("nextEvent");
            eventField.setAccessible(true);
            eventField.set(arena, selectedEvent);
            player.sendMessage(ChatColor.GREEN + "Event for arena '" + arena.getArenaName() + "' set to: " + selectedEvent.name());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Failed to set event: " + e.getMessage());
        }
        return true;
    }
}
