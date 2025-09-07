package com.tomkeuper.bedwars.commands.bedwars.subcmds.General;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class LinkAccountCommand extends BukkitCommand {

    private static final String YOUTUBE_API_KEY = "AIzaSyD1r5YoxYiE6aIiO6q-srYaxG2Nx4N0pXA";

    // playerUUID -> PendingVerification
    private static final Map<String, PendingVerification> pendingVerifications = new ConcurrentHashMap<>();
    // code -> playerUUID (حتى ما أحد ثاني يستخدم نفس الكود)
    private static final Map<String, String> usedCodes = new ConcurrentHashMap<>();

    public LinkAccountCommand(String name) {
        super(name);
        this.description = "Link or unlink your YouTube channel with code verification";
        this.usageMessage = "/" + name + " youtube <channel_name_or_id> | verify <code> | unlink youtube";
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
            p.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length < 1) {
            showUsage(p);
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("verify")) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Usage: /linkaccount verify <code>");
                return true;
            }
            return handleVerification(p, args[1]);
        }

        if (action.equals("unlink")) {
            com.tomkeuper.bedwars.BedWars.StreamDataConfig.removeStreamer(p, "youtube");
            p.sendMessage(ChatColor.GREEN + "Unlinked your YouTube account!");
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
        p.sendMessage(ChatColor.YELLOW + "Checking your YouTube channel...");

        new BukkitRunnable() {
            @Override
            public void run() {
                YouTubeChannelInfo channelInfo = checkYouTubeChannel(channelInput);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (channelInfo == null) {
                            p.sendMessage(ChatColor.RED + "Channel not found!");
                            return;
                        }

                        // generate unique code
                        String code = generateCode();
                        pendingVerifications.put(p.getUniqueId().toString(),
                                new PendingVerification(channelInfo, code, System.currentTimeMillis()));
                        usedCodes.put(code, p.getUniqueId().toString());

                        p.sendMessage(ChatColor.GREEN + "Channel found: " + ChatColor.AQUA + channelInfo.title);
                        p.sendMessage(ChatColor.YELLOW + "To verify ownership:");
                        p.sendMessage(ChatColor.GRAY + "1) Add this code to your channel description OR");
                        p.sendMessage(ChatColor.GRAY + "2) Upload a video with the code in the title.");
                        p.sendMessage(ChatColor.GOLD + "Verification Code: " + ChatColor.RED + code);
                        p.sendMessage(ChatColor.GRAY + "Then type: " + ChatColor.AQUA + "/linkaccount verify " + code);
                        p.sendMessage(ChatColor.DARK_GRAY + "(Expires in 10 minutes)");
                    }
                }.runTask(com.tomkeuper.bedwars.BedWars.plugin);
            }
        }.runTaskAsynchronously(com.tomkeuper.bedwars.BedWars.plugin);

        return true;
    }

    private boolean handleVerification(Player p, String code) {
        String playerId = p.getUniqueId().toString();
        PendingVerification pending = pendingVerifications.get(playerId);

        if (pending == null) {
            p.sendMessage(ChatColor.RED + "No pending verification! Start with:");
            p.sendMessage(ChatColor.AQUA + "/linkaccount youtube <channel>");
            return true;
        }

        if (System.currentTimeMillis() - pending.timestamp > 600000) {
            pendingVerifications.remove(playerId);
            usedCodes.remove(pending.verificationCode);
            p.sendMessage(ChatColor.RED + "Verification code expired!");
            return true;
        }

        if (!code.equals(pending.verificationCode)) {
            p.sendMessage(ChatColor.RED + "Invalid code!");
            return true;
        }

        // check if code exists on channel
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean found = checkCodeOnChannel(pending.channelInfo.channelId, code);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!found) {
                            p.sendMessage(ChatColor.RED + "Code not found on channel!");
                            return;
                        }

                        // success
                        com.tomkeuper.bedwars.BedWars.StreamDataConfig.saveStreamer(p, "youtube", pending.channelInfo.channelId);
                        pendingVerifications.remove(playerId);
                        usedCodes.remove(code);

                        p.sendMessage(ChatColor.GREEN + "✓ YouTube linked successfully!");
                        p.sendMessage(ChatColor.GRAY + "Channel: " + ChatColor.AQUA + pending.channelInfo.title);
                        p.sendMessage(ChatColor.GRAY + "ID: " + ChatColor.WHITE + pending.channelInfo.channelId);
                    }
                }.runTask(com.tomkeuper.bedwars.BedWars.plugin);
            }
        }.runTaskAsynchronously(com.tomkeuper.bedwars.BedWars.plugin);

        return true;
    }

    private String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        // منع التكرار
        if (usedCodes.containsKey(code.toString())) return generateCode();
        return code.toString();
    }

    private YouTubeChannelInfo checkYouTubeChannel(String input) {
        try {
            if (input.startsWith("@")) {
                return searchBy("channels?part=snippet&forHandle=" + input);
            }
            if (input.startsWith("UC")) {
                return searchBy("channels?part=snippet&id=" + input);
            }
            return searchBy("search?part=snippet&type=channel&q=" + input + "&maxResults=1");
        } catch (Exception e) {
            return null;
        }
    }

    private YouTubeChannelInfo searchBy(String path) {
        try {
            String urlStr = "https://www.googleapis.com/youtube/v3/" + path + "&key=" + YOUTUBE_API_KEY;
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            if (conn.getResponseCode() != 200) return null;

            String json = new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
            String channelId = extract(json, "\"channelId\": \"", "\"");
            if (channelId == null) channelId = extract(json, "\"id\": \"", "\"");
            String title = extract(json, "\"title\": \"", "\"");
            return (channelId != null && title != null) ? new YouTubeChannelInfo(channelId, title) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean checkCodeOnChannel(String channelId, String code) {
        try {
            // description
            String urlStr = "https://www.googleapis.com/youtube/v3/channels?part=snippet&id=" + channelId + "&key=" + YOUTUBE_API_KEY;
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                String json = new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
                String desc = extract(json, "\"description\": \"", "\"");
                if (desc != null && desc.contains(code)) return true;
            }

            // latest video titles
            String vidsUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&order=date&type=video&maxResults=5&key=" + YOUTUBE_API_KEY;
            HttpURLConnection conn2 = (HttpURLConnection) new URL(vidsUrl).openConnection();
            conn2.setRequestMethod("GET");
            if (conn2.getResponseCode() == 200) {
                String json = new Scanner(conn2.getInputStream()).useDelimiter("\\A").next();
                Pattern pattern = Pattern.compile("\"title\": \"(.*?)\"");
                Matcher m = pattern.matcher(json);
                while (m.find()) {
                    if (m.group(1).contains(code)) return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private String extract(String json, String start, String end) {
        int i = json.indexOf(start);
        if (i == -1) return null;
        i += start.length();
        int j = json.indexOf(end, i);
        if (j == -1) return null;
        return json.substring(i, j);
    }

    private void showUsage(Player p) {
        p.sendMessage(ChatColor.YELLOW + "Usage:");
        p.sendMessage(ChatColor.GRAY + "/linkaccount youtube <channel>");
        p.sendMessage(ChatColor.GRAY + "/linkaccount verify <code>");
        p.sendMessage(ChatColor.GRAY + "/linkaccount unlink youtube");
    }

    private static class YouTubeChannelInfo {
        final String channelId;
        final String title;
        YouTubeChannelInfo(String channelId, String title) {
            this.channelId = channelId;
            this.title = title;
        }
    }

    private static class PendingVerification {
        final YouTubeChannelInfo channelInfo;
        final String verificationCode;
        final long timestamp;
        PendingVerification(YouTubeChannelInfo c, String v, long t) {
            this.channelInfo = c;
            this.verificationCode = v;
            this.timestamp = t;
        }
    }
}
