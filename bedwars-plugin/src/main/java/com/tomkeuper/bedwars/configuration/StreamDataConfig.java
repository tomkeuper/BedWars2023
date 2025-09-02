package com.tomkeuper.bedwars.configuration;

import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StreamDataConfig extends ConfigManager {

    private static StreamDataConfig instance;
    private static final Map<UUID, StreamInfo> streamCache = new HashMap<>();

    public StreamDataConfig(Plugin plugin, String name, String dir) {
        super(plugin, name, dir);
        instance = this;

        YamlConfiguration yml = getYml();
        if (yml != null) {
            yml.options().copyDefaults(true);
            save();
            loadStreamCache();
        }
    }

    public static StreamDataConfig getInstance() {
        return instance;
    }

    private void loadStreamCache() {
        streamCache.clear();
        YamlConfiguration yml = getYml();
        if (yml == null) return;

        for (String uuidStr : yml.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                String twitch = yml.getString(uuidStr + ".twitch");
                String youtube = yml.getString(uuidStr + ".youtube");
                boolean isLive = yml.getBoolean(uuidStr + ".isLive", false);

                if ((twitch != null && !twitch.isEmpty()) || (youtube != null && !youtube.isEmpty())) {
                    streamCache.put(uuid, new StreamInfo(twitch, youtube, isLive));
                }
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Invalid UUID in streamData.yml: " + uuidStr);
            }
        }
    }

    public void saveStreamer(Player player, String platform, String channelName) {
        if (player == null || platform == null || channelName == null || channelName.trim().isEmpty()) return;

        String uuidStr = player.getUniqueId().toString();
        StreamInfo info = streamCache.computeIfAbsent(player.getUniqueId(), k -> new StreamInfo(null, null, false));
        YamlConfiguration yml = getYml();
        if (yml == null) return;

        switch (platform.toLowerCase()) {
            case "twitch":
                info.twitchChannel = channelName;
                yml.set(uuidStr + ".twitch", channelName);
                break;
            case "youtube":
                info.youtubeChannel = channelName;
                yml.set(uuidStr + ".youtube", channelName);
                break;
            default:
                return;
        }

        yml.set(uuidStr + ".isLive", info.isLive());
        save();
    }

    public void removeStreamer(Player player, String platform) {
        if (player == null || platform == null) return;

        String uuidStr = player.getUniqueId().toString();
        StreamInfo info = streamCache.get(player.getUniqueId());
        YamlConfiguration yml = getYml();
        if (info == null || yml == null) return;

        switch (platform.toLowerCase()) {
            case "twitch":
                info.twitchChannel = null;
                yml.set(uuidStr + ".twitch", null);
                break;
            case "youtube":
                info.youtubeChannel = null;
                yml.set(uuidStr + ".youtube", null);
                break;
            default:
                return;
        }

        // Remove entire entry if both channels are null
        if (!info.hasTwitchChannel() && !info.hasYouTubeChannel()) {
            streamCache.remove(player.getUniqueId());
            yml.set(uuidStr, null);
        } else {
            yml.set(uuidStr + ".isLive", info.isLive());
        }

        save();
    }

    public static StreamInfo getStreamerInfo(UUID uuid) {
        return streamCache.get(uuid);
    }

    public StreamInfo getStreamerInfo(Player player) {
        return player != null ? getStreamerInfo(player.getUniqueId()) : null;
    }

    public boolean isStreamer(Player player) {
        StreamInfo info = getStreamerInfo(player);
        return info != null && (info.hasTwitchChannel() || info.hasYouTubeChannel());
    }

    public boolean isStreamer(UUID uuid) {
        StreamInfo info = getStreamerInfo(uuid);
        return info != null && (info.hasTwitchChannel() || info.hasYouTubeChannel());
    }

    public Map<UUID, StreamInfo> getAllStreamers() {
        return new HashMap<>(streamCache);
    }

    @Override
    public void reload() {
        super.reload();
        loadStreamCache();
    }

    public static class StreamInfo {
        public String twitchChannel;
        public String youtubeChannel;
        private boolean isLive;

        public StreamInfo(String twitch, String youtube, boolean isLive) {
            this.twitchChannel = twitch;
            this.youtubeChannel = youtube;
            this.isLive = isLive;
        }

        public boolean hasTwitchChannel() {
            return twitchChannel != null && !twitchChannel.trim().isEmpty();
        }

        public boolean hasYouTubeChannel() {
            return youtubeChannel != null && !youtubeChannel.trim().isEmpty();
        }

        public String getTwitchUrl() {
            return hasTwitchChannel() ? "https://twitch.tv/" + twitchChannel : null;
        }

        public String getYouTubeUrl() {
            return hasYouTubeChannel() ? "https://youtube.com/c/" + youtubeChannel : null;
        }

        public boolean isLive() {
            return isLive;
        }

        public void setLive(boolean live) {
            this.isLive = live;

            // Save live status immediately
            YamlConfiguration yml = getInstance().getYml();
            if (yml != null) {
                for (Map.Entry<UUID, StreamInfo> entry : streamCache.entrySet()) {
                    if (entry.getValue() == this) {
                        yml.set(entry.getKey().toString() + ".isLive", live);
                        getInstance().save();
                        break;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "StreamInfo{" +
                    "twitch='" + twitchChannel + '\'' +
                    ", youtube='" + youtubeChannel + '\'' +
                    ", isLive=" + isLive +
                    '}';
        }
    }
}
