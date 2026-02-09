package com.tomkeuper.bedwars.configuration;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class InvisConfig extends ConfigManager {
    /**
     * Create a new configuration file.
     *
     * @param plugin config owner.
     * @param name   config name. Do not include .yml in it.
     */
    public InvisConfig(Plugin plugin, String name) {
        super(plugin, name, BedWars.plugin.getDataFolder().getPath());
        YamlConfiguration yml = this.getYml();
        yml.options().header(plugin.getDescription().getName() + "Create by abbflaabb \n" +
                "Documentation here: https://wiki.tomkeuper.com/docs/BedWars2023\n");
        yml.addDefault("enable-wood-sowrd-disappearance", true);
        yml.addDefault("enable-respawn-session-invisibility", true);
        yml.addDefault("disable-death-anmation", true);
        yml.addDefault("kill-sound-settings.enabled", true);
        yml.addDefault("kill-sound-settings.sound", Sounds.getSounds().getString(Sounds.getSounds().getString("kill-sound")));
        yml.addDefault("kill-sound-settings.volume", (double)1.0F);
        yml.addDefault("kill-sound-settings.pitch", (double)1.0F);
        yml.options().copyDefaults(true);
        this.save();
    }
    // -- GETTER --
    //  Get whether the wood sword disappearance feature is enabled
    public boolean isWoodSwordDisappearanceEnabled() {
        return this.getYml().getBoolean("enable-wood-sowrd-disappearance", true);
    }
    // get whether the respawn session invisibility feature is enabled
    public boolean isRespawnSessionInvisibilityEnabled() {
        return this.getYml().getBoolean("enable-respawn-session-invisibility", true);
    }
    // get whether the death animation is disabled
    public boolean isDeathAnimationDisabled() {
        return this.getYml().getBoolean("disable-death-anmation", true);
    }
     // get whether the kill sound is enabled
    public boolean isKillSoundEnabled() {
        return this.getYml().getBoolean("kill-sound-settings.enabled", true);
    }
    // get the kill sound
    public Sound getKillSound() {
        String soundName = this.getYml().getString("kill-sound-settings.sound", Sounds.getSounds().getString("kill-sound"));
        return Sound.valueOf(soundName.toUpperCase());
    }
    // get the kill sound volume
    public float getKillSoundVolume() {
        return (float) this.getYml().getDouble("kill-sound-settings.volume", 1.0);
    }
    // get the kill sound pitch
    public float getKillSoundPitch() {
        return (float) this.getYml().getDouble("kill-sound-settings.pitch", 1.0);
    }
}
