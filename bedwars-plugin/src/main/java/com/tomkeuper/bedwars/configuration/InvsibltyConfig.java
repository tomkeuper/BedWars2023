package com.tomkeuper.bedwars.configuration;

import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class InvsibltyConfig extends ConfigManager {

    public InvsibltyConfig(Plugin plugin, String name, String dir) {
        super(plugin, name, dir);
        YamlConfiguration yml = getYml();
        yml.addDefault("enable-wood-sword-disappearance", true);
        yml.addDefault("enable-respawn-session-invisibility", true);
        yml.addDefault("disable-death-animation", false);
        yml.addDefault("kill-sound-settings.enabled", false);
        yml.addDefault("kill-sound-settings.sound", "ENTITY_PLAYER_LEVELUP");
        yml.addDefault("kill-sound-settings.volume", 1.0);
        yml.addDefault("kill-sound-settings.pitch", 1.0);
        yml.options().copyDefaults(true);
        save();
    }

    public boolean isWoodSwordDisappearanceEnabled() {
        return getYml().getBoolean("enable-wood-sword-disappearance", true);
    }

    public boolean isRespawnSessionInvisibilityEnabled() {
        return getYml().getBoolean("enable-respawn-session-invisibility", true);
    }

    public boolean isDeathAnimationDisabled() {
        return getYml().getBoolean("disable-death-animation", false);
    }

    public boolean isKillSoundEnabled() {
        return getYml().getBoolean("kill-sound-settings.enabled", false);
    }

    public Sound getKillSound() {
        String soundName = getYml().getString("kill-sound-settings.sound", "ENTITY_PLAYER_LEVELUP");
        return Sound.valueOf(soundName.toUpperCase());
    }

    public float getKillSoundVolume() {
        return (float) getYml().getDouble("kill-sound-settings.volume", 1.0);
    }

    public float getKillSoundPitch() {
        return (float) getYml().getDouble("kill-sound-settings.pitch", 1.0);
    }
}