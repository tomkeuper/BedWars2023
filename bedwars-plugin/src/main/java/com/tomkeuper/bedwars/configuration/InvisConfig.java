/*
 * BedWars2023 - A bed wars mini-game.
 * Copyright (C) 2024 Tomas Keuper
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: contact@fyreblox.com
 */
package com.tomkeuper.bedwars.configuration;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class InvisConfig extends ConfigManager {
    public InvisConfig(Plugin plugin, String name) {
        super(plugin,name, BedWars.plugin.getDataFolder().getPath());

        YamlConfiguration yml = getYml();
        yml.options().header(
                plugin.getDescription().getName() + " configuration\n" +
                        "Documentation: https://wiki.tomkeuper.com/docs/BedWars2023"
        );

        // wood sword disappearance
        yml.addDefault("enable-wood-sword-disappearance", true);
        yml.addDefault("enable-respawn-session-invisibility", true);
        yml.addDefault("kill-sound-settings.enabled", true);
        String defaultSound = Sounds.getSounds().getString("kill-sound");
        if (defaultSound == null || defaultSound.isEmpty()) {
            defaultSound = "LEVEL_UP";
        }
        yml.addDefault("kill-sound-settings.sound", defaultSound);
        yml.addDefault("kill-sound-settings.volume", 1.0);
        yml.addDefault("kill-sound-settings.pitch", 1.0);

        yml.options().copyDefaults(true);
        this.save();
    }
    public boolean isWoodSwordDisappearanceEnabled() {
        return getYml().getBoolean("enable-wood-sword-disappearance", true);
    }

    public boolean isRespawnSessionInvisibilityEnabled() {
        return getYml().getBoolean("enable-respawn-session-invisibility", true);
    }

    public boolean isKillSoundEnabled() {
        return getYml().getBoolean("kill-sound-settings.enabled", true);
    }

    public Sound getKillSound() {
        String soundName = getYml().getString("kill-sound-settings.sound", "LEVEL_UP");
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Sound.LEVEL_UP;
        }
    }

    public float getKillSoundVolume() {
        return (float) getYml().getDouble("kill-sound-settings.volume", 1.0);
    }

    public float getKillSoundPitch() {
        return (float) getYml().getDouble("kill-sound-settings.pitch", 1.0);
    }
}