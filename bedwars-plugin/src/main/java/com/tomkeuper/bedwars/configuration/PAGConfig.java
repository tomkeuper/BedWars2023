package com.tomkeuper.bedwars.configuration;

import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class PAGConfig extends ConfigManager {

    public PAGConfig(Plugin plugin, String name, String dir) {

        super(plugin, name, dir);

        YamlConfiguration yml = getYml();

        yml.options().copyDefaults(true);

        yml.options().header("Per Arena Generator Config.\nPlease use Arena name and NOT Arena displayname.\nDelay is in seconds. \nAmount cannot be 0 \nPlease don't put anything other than natural numbers.");

        yml.addDefault("Arenas." + "arena1" + ".iron.amount", 2);
        yml.addDefault("Arenas." + "arena1" + ".iron.delay", 1);
        yml.addDefault("Arenas." + "arena1" + ".iron.spawn-limit", 48);

        yml.addDefault("Arenas." + "arena1" + ".gold.amount", 1);
        yml.addDefault("Arenas." + "arena1" + ".gold.delay", 4);
        yml.addDefault("Arenas." + "arena1" + ".gold.spawn-limit", 16);

        save();

    }

}
