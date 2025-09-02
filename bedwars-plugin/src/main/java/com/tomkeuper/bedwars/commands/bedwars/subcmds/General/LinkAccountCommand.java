package com.tomkeuper.bedwars.commands.bedwars.subcmds.General;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Scanner;

public class LinkAccountCommand extends BukkitCommand {

    private static final String YOUTUBE_API_KEY = "AIzaSyD1r5YoxYiE6aIiO6q-srYaxG2Nx4N0pXA";

    public LinkAccountCommand(String name) {
        super(name);
        this.description = "Link or unlink your YouTube channel";
        this.usageMessage = "/" + name + " youtube <channel_name_or_id> | unlink <platform>";
        this.setPermission("bedwars.linkaccount");
        setAliases(Arrays.asList("linkacct"));
    }

    @Override
    public boolean execute(CommandSender s, String commandLabel, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(ChatColor.RED + "This command is for players only!");
            return true;
        }

        Player p = (Player) s;

        if (!p.hasPermission("bedwars.linkaccount")) {
            p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            showUsage(p);
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("unlinkacct")) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Specify a platform to unlink (e.g., youtube)");
                return true;
            }

            String platform = args[1].toLowerCase();
            if (!platform.equals("youtube")) {
                p.sendMessage(ChatColor.RED + "Only YouTube unlinking is supported!");
                return true;
            }

            com.tomkeuper.bedwars.BedWars.StreamDataConfig.removeStreamer(p, platform);
            p.sendMessage(ChatColor.GREEN + "Successfully unlinked your " + platform + " account!");
            return true;
        }

        if (!action.equals("youtube")) {
            p.sendMessage(ChatColor.RED + "Only YouTube linking is supported!");
            return true;
        }

        if (args.length < 2) {
            showUsage(p);
            return true;
        }

        String channelInput = args[1];

        p.sendMessage(ChatColor.YELLOW + "Checking if your YouTube channel exists...");

        YouTubeChannelInfo channelInfo = checkYouTubeChannel(channelInput);
        if (channelInfo == null) {
            p.sendMessage(ChatColor.RED + "YouTube channel not found! Make sure to use:");
            p.sendMessage(ChatColor.GRAY + "• Channel handle (e.g., @MrBeast)");
            p.sendMessage(ChatColor.GRAY + "• Channel ID (e.g., UCX6OQ3DkcsbYNE6H8uQQuVA)");
            p.sendMessage(ChatColor.GRAY + "• Custom URL name (e.g., mrbeast)");
            return true;
        }

        com.tomkeuper.bedwars.BedWars.StreamDataConfig.saveStreamer(p, action, channelInfo.channelId);

        sendSuccessMessage(p, "YouTube", channelInfo.title, channelInfo.customUrl, channelInfo.channelId);
        return true;
    }

    private void showUsage(Player p) {
        p.sendMessage("");
        p.sendMessage(ChatColor.GOLD + "Account Linking Command:");
        p.sendMessage(ChatColor.GRAY + "• " + ChatColor.AQUA + "/linkaccount youtube <channel_handle>");
        p.sendMessage(ChatColor.GRAY + "• " + ChatColor.AQUA + "/linkaccount youtube <channel_id>");
        p.sendMessage(ChatColor.GRAY + "• " + ChatColor.AQUA + "/linkaccount unlink <platform>");
        p.sendMessage("");

        TextComponent example1 = new TextComponent(ChatColor.GRAY + "Example 1: ");
        TextComponent cmd1 = new TextComponent(ChatColor.YELLOW + "/linkaccount youtube @MrBeast");
        cmd1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/linkaccount youtube @"));
        cmd1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to use command!").create()));
        example1.addExtra(cmd1);
        p.spigot().sendMessage(example1);

        TextComponent example2 = new TextComponent(ChatColor.GRAY + "Example 2: ");
        TextComponent cmd2 = new TextComponent(ChatColor.YELLOW + "/linkaccount youtube UCX6OQ3DkcsbYNE6H8uQQuVA");
        cmd2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/linkaccount youtube "));
        cmd2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to use command with Channel ID!").create()));
        example2.addExtra(cmd2);
        p.spigot().sendMessage(example2);
    }

    private void sendSuccessMessage(Player p, String platform, String channelTitle, String customUrl, String channelId) {
        p.sendMessage("");
        p.sendMessage(ChatColor.GREEN + "Successfully linked your " + platform + " account!");

        TextComponent message = new TextComponent(ChatColor.GRAY + "Channel: ");
        TextComponent link = new TextComponent(ChatColor.AQUA + channelTitle);

        String url = customUrl != null ? customUrl : "https://www.youtube.com/channel/" + channelId;
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to open channel!").create()));
        message.addExtra(link);

        p.spigot().sendMessage(message);
        p.sendMessage(ChatColor.GRAY + "Channel ID: " + ChatColor.WHITE + channelId);
        p.sendMessage("");
    }

    private YouTubeChannelInfo checkYouTubeChannel(String input) {
        YouTubeChannelInfo result;

        if (input.startsWith("@")) {
            result = searchByHandle(input);
            if (result != null) return result;
        }

        if (input.startsWith("UC") && input.length() == 24) {
            result = searchByChannelId(input);
            if (result != null) return result;
        }

        result = searchByCustomName(input);
        if (result != null) return result;

        result = searchChannelByQuery(input);
        if (result != null) return result;

        return null;
    }

    private YouTubeChannelInfo searchByHandle(String handle) {
        try {
            String urlStr = "https://www.googleapis.com/youtube/v3/channels?part=snippet,contentDetails&forHandle="
                    + handle + "&key=" + YOUTUBE_API_KEY;
            return makeApiCall(urlStr);
        } catch (Exception e) {
            return null;
        }
    }

    private YouTubeChannelInfo searchByChannelId(String channelId) {
        try {
            String urlStr = "https://www.googleapis.com/youtube/v3/channels?part=snippet,contentDetails&id="
                    + channelId + "&key=" + YOUTUBE_API_KEY;
            return makeApiCall(urlStr);
        } catch (Exception e) {
            return null;
        }
    }

    private YouTubeChannelInfo searchByCustomName(String customName) {
        try {
            String urlStr = "https://www.googleapis.com/youtube/v3/channels?part=snippet,contentDetails&forUsername="
                    + customName + "&key=" + YOUTUBE_API_KEY;
            return makeApiCall(urlStr);
        } catch (Exception e) {
            return null;
        }
    }

    private YouTubeChannelInfo searchChannelByQuery(String query) {
        try {
            String urlStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=channel&q="
                    + query + "&key=" + YOUTUBE_API_KEY + "&maxResults=1";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() != 200) return null;

            String json = new Scanner(conn.getInputStream()).useDelimiter("\\A").next();

            if (json.contains("\"items\": []")) return null;

            String channelId = extractFromJson(json, "\"channelId\": \"", "\"");
            if (channelId != null) {
                return searchByChannelId(channelId);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private YouTubeChannelInfo makeApiCall(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() != 200) return null;

            String json = new Scanner(conn.getInputStream()).useDelimiter("\\A").next();

            if (!json.contains("\"items\": [") || json.contains("\"items\": []")) {
                return null;
            }

            String channelId = extractFromJson(json, "\"id\": \"", "\"");
            String title = extractFromJson(json, "\"title\": \"", "\"");
            String customUrl = extractFromJson(json, "\"customUrl\": \"", "\"");

            if (channelId != null && title != null) {
                return new YouTubeChannelInfo(channelId, title, customUrl);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractFromJson(String json, String startPattern, String endPattern) {
        int startIndex = json.indexOf(startPattern);
        if (startIndex == -1) return null;

        startIndex += startPattern.length();
        int endIndex = json.indexOf(endPattern, startIndex);
        if (endIndex == -1) return null;

        return json.substring(startIndex, endIndex);
    }

    private static class YouTubeChannelInfo {
        final String channelId;
        final String title;
        final String customUrl;

        YouTubeChannelInfo(String channelId, String title, String customUrl) {
            this.channelId = channelId;
            this.title = title;
            this.customUrl = customUrl != null ? "https://www.youtube.com/" + customUrl : null;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("youtube", "unlink");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("unlink")) {
            return Collections.singletonList("youtube");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("youtube")) {
            return Arrays.asList("@YourHandle", "UCChannelID", "CustomName");
        }
        return new ArrayList<>();
    }
}