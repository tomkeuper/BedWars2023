package com.tomkeuper.bedwars.commands.bedwars.subcmds.regular;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.command.ParentCommand;
import com.tomkeuper.bedwars.api.command.SubCommand;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.configuration.Sounds;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Fly extends SubCommand {

    // Cooldown management
    private static final Map<UUID, Long> flyCooldowns = new HashMap<>();
    private static final Map<UUID, Long> flyDurations = new HashMap<>();
    private static final Map<UUID, BukkitRunnable> flyTasks = new HashMap<>();

    // Static initializer to add default sounds
    static {
        try {
            // Add fly-related sounds with version compatibility
            Sounds.addDefSound("FlyEnabled", BedWars.getForCurrentVersion("ENDERDRAGON_HIT", "ENTITY_ENDER_DRAGON_HURT", "ENTITY_ENDER_DRAGON_HURT"));
            Sounds.addDefSound("FlyDisabled", BedWars.getForCurrentVersion("LEVEL_UP", "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP"));
            Sounds.addDefSound("FlyAdminAction", BedWars.getForCurrentVersion("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"));
            Sounds.addDefSound("FlySpeedChange", BedWars.getForCurrentVersion("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"));
        } catch (Exception e) {
            BedWars.plugin.getLogger().warning("Could not register fly sounds: " + e.getMessage());
        }
    }

    public Fly(ParentCommand parent, String name) {
        super(parent, name);
        setArenaSetupCommand(false);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            // Fixed: Don't cast ConsoleCommandSender to Player
            // Use a default message or get message differently for console
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        // Check if player has permission
        if (!player.hasPermission("bw.fly.vip")) {
            player.sendMessage(Language.getMsg(player, Messages.FLY_ONLY_VIP));
            return true;
        }

        // Handle different command arguments
        if (args.length == 0) {
            // Toggle fly for self
            return toggleFly(player, player, -1);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendHelpMessage(player);
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                listFlyingPlayers(player);
                return true;
            } else if (isNumeric(args[0])) {
                // Toggle fly with duration
                int duration = Integer.parseInt(args[0]);
                return toggleFly(player, player, duration);
            } else {
                // Toggle fly for another player
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Language.getMsg(player, Messages.FLY_PLAYER_NOT_FOUND));
                    return true;
                }
                return toggleFly(player, target, -1);
            }
        } else if (args.length == 2) {
            // Toggle fly for another player with duration
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Language.getMsg(player, Messages.FLY_PLAYER_NOT_FOUND));
                return true;
            }

            if (!isNumeric(args[1])) {
                player.sendMessage(Language.getMsg(player, Messages.FLY_INVALID_DURATION));
                return true;
            }

            int duration = Integer.parseInt(args[1]);
            return toggleFly(player, target, duration);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("speed")) {
            // Set fly speed
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Language.getMsg(player, Messages.FLY_PLAYER_NOT_FOUND));
                return true;
            }

            if (!isNumeric(args[2])) {
                player.sendMessage(Language.getMsg(player, Messages.FLY_INVALID_SPEED));
                return true;
            }

            int speed = Integer.parseInt(args[2]);
            return setFlySpeed(player, target, speed);
        }

        sendHelpMessage(player);
        return true;
    }

    private boolean toggleFly(Player sender, Player target, int duration) {
        // Check cooldown (only for self, not for admin commands)
        if (sender.equals(target) && !sender.hasPermission("bw.fly.bypass")) {
            long cooldownTime = BedWars.config.getInt(ConfigPath.FLY_COMMAND_COOLDOWN) * 1000L;
            if (flyCooldowns.containsKey(sender.getUniqueId())) {
                long timeLeft = (flyCooldowns.get(sender.getUniqueId()) + cooldownTime) - System.currentTimeMillis();
                if (timeLeft > 0) {
                    sender.sendMessage(Language.getMsg(sender, Messages.FLY_COOLDOWN).replace("{time}", String.valueOf(timeLeft / 1000)));
                    return true;
                }
            }
        }

        // Check if player is in arena and if fly is allowed
        IArena arena = BedWars.getAPI().getArenaUtil().getArenaByPlayer(target);
        if (arena != null && !sender.hasPermission("bw.fly.arena")) {
            if (!BedWars.config.getBoolean(ConfigPath.FLY_COMMAND_ALLOW_IN_GAME)) {
                sender.sendMessage(Language.getMsg(sender, Messages.FLY_NOT_ALLOWED_IN_GAME));
                return true;
            }
        }

        // Toggle fly
        boolean newFlyState = !target.getAllowFlight();
        target.setAllowFlight(newFlyState);
        target.setFlying(newFlyState);

        // Play sound effects (only if enabled in config)
        if (BedWars.config.getBoolean(ConfigPath.FLY_COMMAND_ENABLE_SOUNDS)) {
            if (newFlyState) {
                Sounds.playSound("FlyEnabled", target);
                if (!sender.equals(target)) {
                    Sounds.playSound("FlyAdminAction", sender);
                }
            } else {
                Sounds.playSound("FlyDisabled", target);
                if (!sender.equals(target)) {
                    Sounds.playSound("FlyAdminAction", sender);
                }
            }
        }

        // Send messages
        if (newFlyState) {
            if (sender.equals(target)) {
                target.sendMessage(Language.getMsg(target, Messages.FLY_ENABLED));
            } else {
                target.sendMessage(Language.getMsg(target, Messages.FLY_ENABLED_OTHER).replace("{player}", sender.getName()));
                sender.sendMessage(Language.getMsg(sender, Messages.FLY_ENABLED_FOR).replace("{player}", target.getName()));
            }

            // Set cooldown
            if (sender.equals(target) && !sender.hasPermission("bw.fly.bypass")) {
                flyCooldowns.put(sender.getUniqueId(), System.currentTimeMillis());
            }

            // Handle duration
            if (duration > 0) {
                int maxDuration = BedWars.config.getInt(ConfigPath.FLY_COMMAND_MAX_DURATION);
                if (duration > maxDuration && !sender.hasPermission("bw.fly.bypass")) {
                    duration = maxDuration;
                    sender.sendMessage(Language.getMsg(sender, Messages.FLY_DURATION_LIMITED).replace("{max}", String.valueOf(maxDuration)));
                }

                flyDurations.put(target.getUniqueId(), System.currentTimeMillis() + (duration * 1000L));
                target.sendMessage(Language.getMsg(target, Messages.FLY_DURATION_WARNING).replace("{time}", String.valueOf(duration)));

                // Cancel existing task if any
                if (flyTasks.containsKey(target.getUniqueId())) {
                    flyTasks.get(target.getUniqueId()).cancel();
                }

                // Create new task
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (target.isOnline() && target.getAllowFlight()) {
                            target.setAllowFlight(false);
                            target.setFlying(false);
                            target.sendMessage(Language.getMsg(target, Messages.FLY_AUTO_DISABLED));
                            if (BedWars.config.getBoolean(ConfigPath.FLY_COMMAND_ENABLE_SOUNDS)) {
                                Sounds.playSound("FlyDisabled", target);
                            }
                        }
                        flyDurations.remove(target.getUniqueId());
                        flyTasks.remove(target.getUniqueId());
                    }
                };
                task.runTaskLater(BedWars.plugin, duration * 20L);
                flyTasks.put(target.getUniqueId(), task);
            }
        } else {
            if (sender.equals(target)) {
                target.sendMessage(Language.getMsg(target, Messages.FLY_DISABLED));
            } else {
                target.sendMessage(Language.getMsg(target, Messages.FLY_DISABLED_OTHER).replace("{player}", sender.getName()));
                sender.sendMessage(Language.getMsg(sender, Messages.FLY_DISABLED_FOR).replace("{player}", target.getName()));
            }

            // Cancel duration task if exists
            if (flyTasks.containsKey(target.getUniqueId())) {
                flyTasks.get(target.getUniqueId()).cancel();
                flyTasks.remove(target.getUniqueId());
            }
            flyDurations.remove(target.getUniqueId());
        }

        return true;
    }

    private boolean setFlySpeed(Player sender, Player target, int speed) {
        if (!sender.hasPermission("bw.fly.speed")) {
            sender.sendMessage(Language.getMsg(sender, Messages.FLY_NO_PERMISSION_SPEED));
            return true;
        }

        if (speed < 1 || speed > 10) {
            sender.sendMessage(Language.getMsg(sender, Messages.FLY_INVALID_SPEED));
            return true;
        }

        if (!target.getAllowFlight()) {
            sender.sendMessage(Language.getMsg(sender, Messages.FLY_TARGET_NO_FLY));
            return true;
        }

        float flySpeed = speed / 10.0f;
        target.setFlySpeed(flySpeed);

        if (sender.equals(target)) {
            target.sendMessage(Language.getMsg(target, Messages.FLY_SPEED_SET).replace("{speed}", String.valueOf(speed)));
        } else {
            target.sendMessage(Language.getMsg(target, Messages.FLY_SPEED_SET_OTHER).replace("{speed}", String.valueOf(speed)).replace("{player}", sender.getName()));
            sender.sendMessage(Language.getMsg(sender, Messages.FLY_SPEED_SET_FOR).replace("{speed}", String.valueOf(speed)).replace("{player}", target.getName()));
        }

        if (BedWars.config.getBoolean(ConfigPath.FLY_COMMAND_ENABLE_SOUNDS)) {
            Sounds.playSound("FlySpeedChange", target);
        }
        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_HEADER));
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_TOGGLE));
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_DURATION));
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_PLAYER));
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_PLAYER_DURATION));
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_SPEED));
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_LIST));
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_HELP));
        player.sendMessage(Language.getMsg(player, Messages.FLY_HELP_FOOTER));
    }

    private void listFlyingPlayers(Player sender) {
        if (!sender.hasPermission("bw.fly.list")) {
            sender.sendMessage(Language.getMsg(sender, Messages.FLY_NO_PERMISSION_LIST));
            return;
        }

        sender.sendMessage(Language.getMsg(sender, Messages.FLY_FLYING_PLAYERS_HEADER));

        boolean foundFlying = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getAllowFlight()) {
                foundFlying = true;
                String status = player.isFlying() ? Language.getMsg(sender, Messages.FLY_STATUS_FLYING) : Language.getMsg(sender, Messages.FLY_STATUS_CAN_FLY);

                String messageText = Language.getMsg(sender, Messages.FLY_PLAYER_ENTRY).replace("{player}", player.getName()).replace("{status}", status);

                // Check if message is null or empty before creating TextComponent
                if (messageText == null || messageText.trim().isEmpty()) {
                    messageText = "§7" + player.getName() + " - " + status;
                }

                TextComponent message = new TextComponent(messageText);

                if (flyDurations.containsKey(player.getUniqueId())) {
                    long timeLeft = (flyDurations.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                    if (timeLeft > 0) {
                        String durationText = Language.getMsg(sender, Messages.FLY_PLAYER_DURATION).replace("{time}", String.valueOf(timeLeft));
                        if (durationText != null && !durationText.trim().isEmpty()) {
                            message.addExtra(durationText);
                        }
                    }
                }

                // Add click to teleport (if permission)
                if (sender.hasPermission("bw.fly.teleport")) {
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));

                    String hoverText = Language.getMsg(sender, Messages.FLY_TELEPORT_HOVER).replace("{player}", player.getName());
                    if (hoverText != null && !hoverText.trim().isEmpty()) {
                        ComponentBuilder hoverBuilder = new ComponentBuilder(hoverText);
                        if (hoverBuilder != null) {
                            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.create()));
                        }
                    }
                }

                // Send message safely
                if (sender.spigot() != null) {
                    sender.spigot().sendMessage(message);
                } else {
                    sender.sendMessage(messageText);
                }
            }
        }

        if (!foundFlying) {
            sender.sendMessage(Language.getMsg(sender, Messages.FLY_NO_FLYING_PLAYERS));
        }

        sender.sendMessage(Language.getMsg(sender, Messages.FLY_HELP_FOOTER));
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<String> getTabComplete() {
        return List.of("help", "list", "speed");
    }

    // Clean up when plugin disables
    public static void cleanup() {
        for (BukkitRunnable task : flyTasks.values()) {
            task.cancel();
        }
        flyTasks.clear();
        flyDurations.clear();
        flyCooldowns.clear();
    }
}