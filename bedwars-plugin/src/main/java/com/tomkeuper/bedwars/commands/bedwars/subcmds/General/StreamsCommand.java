package com.tomkeuper.bedwars.commands.bedwars.subcmds.General;

import com.tomkeuper.bedwars.configuration.StreamDataConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;

public class StreamsCommand extends BukkitCommand {

    public StreamsCommand(String name) {
        super(name);
        this.description = "View active server streamers";
        this.usageMessage = "/" + name;
        this.setPermission("bedwars.streams");
        setAliases(new ArrayList<>());
    }

    @Override
    public boolean execute(CommandSender s, String commandLabel, String[] args) {
        if (!s.hasPermission("bedwars.streams")) {
            s.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (!(s instanceof Player)) {
            s.sendMessage(ChatColor.RED + "This command is for players only!");
            return true;
        }

        Player p = (Player) s;

        // Display header
        p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "• " + ChatColor.GOLD + "Server Streams " + ChatColor.GRAY + "- " + ChatColor.AQUA + "Live Content Creators");
        p.sendMessage("");

        boolean foundStreamers = false;

        for (Player online : Bukkit.getOnlinePlayers()) {
            StreamDataConfig.StreamInfo info = StreamDataConfig.getStreamerInfo(online.getUniqueId());
            if (info != null) {

                // Determine suffix based on live status
                boolean isLive = info.isLive(); // Replace with your actual check
                String suffix = isLive ? ChatColor.RED + " •" : "";

                // Twitch
                if (info.twitchChannel != null) {
                    TextComponent streamer = new TextComponent(ChatColor.GRAY + "• " + ChatColor.AQUA + online.getName() + suffix + " ");
                    TextComponent twitchLink = new TextComponent(ChatColor.BLUE + "[Twitch]");
                    twitchLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitch.tv/" + info.twitchChannel));
                    twitchLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to open stream!").create()));
                    streamer.addExtra(twitchLink);
                    p.spigot().sendMessage(streamer);
                    foundStreamers = true;
                }

                // YouTube
                if (info.youtubeChannel != null) {
                    TextComponent streamer = new TextComponent(ChatColor.GRAY + "• " + ChatColor.AQUA + online.getName() + suffix + " ");
                    TextComponent youtubeLink = new TextComponent(ChatColor.RED + "[YouTube]");
                    youtubeLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://youtube.com/" + info.youtubeChannel));
                    youtubeLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to open channel!").create()));
                    streamer.addExtra(youtubeLink);
                    p.spigot().sendMessage(streamer);
                    foundStreamers = true;
                }
            }
        }

        if (!foundStreamers) {
            p.sendMessage(ChatColor.GRAY + "No active streamers online!");
        }

        p.sendMessage("");
        TextComponent footer = new TextComponent(ChatColor.GOLD + "Want to be listed? ");
        TextComponent linkCmd = new TextComponent(ChatColor.YELLOW + "[Link Your Channel]");
        linkCmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/linkaccount "));
        linkCmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to link your channel!").create()));
        footer.addExtra(linkCmd);
        p.spigot().sendMessage(footer);

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }
}
