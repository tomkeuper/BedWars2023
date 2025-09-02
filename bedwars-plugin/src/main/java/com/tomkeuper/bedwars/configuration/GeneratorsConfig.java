package com.tomkeuper.bedwars.configuration;

import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class GeneratorsConfig extends ConfigManager {

    public GeneratorsConfig(Plugin plugin, String name, String dir) {
        super(plugin, name, dir);

        YamlConfiguration yml = getYml();
        yml.options().header(plugin.getDescription().getName() + " by andrei1058." +
                "\ngenerators.yml Documentation: https://gitlab.com/andrei1058/BedWars1058/wikis/generators-configuration\n");
        yml.addDefault("Default." + ConfigPath.GENERATOR_IRON_SPAWN_LIMIT, 48);
        yml.addDefault("Default." + ConfigPath.GENERATOR_GOLD_SPAWN_LIMIT, 16);
        yml.addDefault(ConfigPath.GENERATOR_STACK_ITEMS, false);

        yml.addDefault("Default." + ConfigPath.GENERATOR_IRON_SLOW + ".amount", 2);
        yml.addDefault("Default." + ConfigPath.GENERATOR_IRON_SLOW + ".delay", 2.0);

        yml.addDefault("Default." + ConfigPath.GENERATOR_IRON_MEDIUM + ".amount", 2);
        yml.addDefault("Default." + ConfigPath.GENERATOR_IRON_MEDIUM + ".delay", 1.0);

        yml.addDefault("Default." + ConfigPath.GENERATOR_IRON_FAST + ".amount", 3);
        yml.addDefault("Default." + ConfigPath.GENERATOR_IRON_FAST + ".delay", 1.0);

        yml.addDefault("Default." + ConfigPath.GENERATOR_GOLD_SLOW + ".amount", 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_GOLD_SLOW + ".delay", 6.0);

        yml.addDefault("Default." + ConfigPath.GENERATOR_GOLD_MEDIUM + ".amount", 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_GOLD_MEDIUM + ".delay", 6.0);

        yml.addDefault("Default." + ConfigPath.GENERATOR_GOLD_FAST + ".amount", 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_GOLD_FAST + ".delay", 6.0);

        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_I_DELAY, 30);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_I_AMOUNT, 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_I_SPAWN_LIMIT, 4);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_II_DELAY, 20);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_II_AMOUNT, 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_II_SPAWN_LIMIT, 6);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_II_START, 360);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_III_DELAY, 15);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_III_AMOUNT, 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_III_SPAWN_LIMIT, 8);
        yml.addDefault("Default." + ConfigPath.GENERATOR_DIAMOND_TIER_III_START, 1080);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_I_DELAY, 70);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_I_AMOUNT, 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_I_SPAWN_LIMIT, 4);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_II_DELAY, 50);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_II_AMOUNT, 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_II_SPAWN_LIMIT, 6);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_II_START, 720);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_III_DELAY, 30);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_III_AMOUNT, 1);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_III_SPAWN_LIMIT, 8);
        yml.addDefault("Default." + ConfigPath.GENERATOR_EMERALD_TIER_III_START, 1440);
        yml.options().copyDefaults(true);
        save();
    }

}