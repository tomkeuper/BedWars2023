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

package com.tomkeuper.bedwars.api.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ConfigManager {

    private YamlConfiguration yml;
    private File config;
    private String name;
    private boolean firstTime = false;
    private boolean loadError = false;

    /**
     * Create a new configuration file.
     *
     * @param plugin config owner.
     * @param name   config name. Do not include .yml in it.
     */
    public ConfigManager(Plugin plugin, String name, String dir) {
        File d = new File(dir);
        if (!d.exists()) {
            if (!d.mkdirs()) {
                plugin.getLogger().log(Level.SEVERE, "Could not create " + d.getPath());
                return;
            }
        }

        config = new File(dir, name + ".yml");
        if (!config.exists()) {
            firstTime = true;
            plugin.getLogger().log(Level.INFO, "Creating " + config.getPath());
            try {
                if (!config.createNewFile()) {
                    plugin.getLogger().log(Level.SEVERE, "Could not create " + config.getPath());
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Try to load YAML safely and detect syntax errors
        yml = new YamlConfiguration();
        try {
            yml.load(config);
            yml.options().copyDefaults(true);
        } catch (InvalidConfigurationException e) {
            loadError = true;
            plugin.getLogger().log(Level.SEVERE, "Failed to parse configuration file due to invalid YAML syntax: " + config.getPath());
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
            // Do NOT overwrite the file later on save; keep loadError=true to block saving.
        } catch (IOException e) {
            // IO error while reading: log and prevent saving to avoid overwriting existing content
            loadError = true;
            plugin.getLogger().log(Level.SEVERE, "I/O error while reading configuration file: " + config.getPath(), e);
        }
        this.name = name;
    }

    /**
     * Reload configuration.
     */
    public void reload() {
        loadError = false;
        YamlConfiguration newYml = new YamlConfiguration();
        try {
            newYml.load(config);
            newYml.options().copyDefaults(true);
            this.yml = newYml;
        } catch (InvalidConfigurationException e) {
            loadError = true;
            Bukkit.getLogger().log(Level.SEVERE, "Failed to parse configuration file on reload due to invalid YAML syntax: " + config.getPath());
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            loadError = true;
            Bukkit.getLogger().log(Level.SEVERE, "I/O error while reloading configuration file: " + config.getPath(), e);
        }
    }

    /**
     * Convert a location to an arena location syntax
     */
    public String stringLocationArenaFormat(Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + (double) loc.getYaw() + "," + (double) loc.getPitch();
    }

    /**
     * Convert a location to a string for general use
     * Use {@link #stringLocationArenaFormat(Location)} for arena locations
     */
    public String stringLocationConfigFormat(Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + (double) loc.getYaw() + "," + (double) loc.getPitch() + "," + loc.getWorld().getName();
    }

    /**
     * Save a general location to the config.
     * Use {@link #saveArenaLoc(String, Location)} for arena locations
     */
    public void saveConfigLoc(String path, Location loc) {
        String data = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + (double) loc.getYaw() + "," + (double) loc.getPitch() + "," + loc.getWorld().getName();
        yml.set(path, data);
        save();
    }

    /**
     * Save a location for arena use
     */
    public void saveArenaLoc(String path, Location loc) {
        String data = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + (double) loc.getYaw() + "," + (double) loc.getPitch();
        yml.set(path, data);
        save();
    }

    /**
     * Get a general location
     * Use {@link #getArenaLoc(String)} for locations stored using {@link #saveArenaLoc(String, Location)}
     */
    public Location getConfigLoc(String path) {
        String d = yml.getString(path);
        if (d == null) return null;
        String[] data = d.replace("[", "").replace("]", "").split(",");
        return new Location(Bukkit.getWorld(data[5]), Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4]));
    }

    /**
     * Get a location for arena use
     * Use {@link #getConfigLoc(String)} (String)} for locations stored using {@link #saveConfigLoc(String, Location)} (String, Location)}
     */
    public Location getArenaLoc(String path) {
        String d = yml.getString(path);
        if (d == null) return null;
        String[] data = d.replace("[", "").replace("]", "").split(",");
        return new Location(Bukkit.getWorld(name), Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4]));
    }

    /**
     * Convert string to arena location syntax
     */
    public Location convertStringToArenaLocation(String string) {
        String[] data = string.split(",");
        return new Location(Bukkit.getWorld(name), Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4]));

    }

    /**
     * Get list of arena locations at given path
     */
    public List<Location> getArenaLocations(String path) {
        List<Location> l = new ArrayList<>();
        for (String s : yml.getStringList(path)) {
            Location loc = convertStringToArenaLocation(s);
            if (loc != null) {
                l.add(loc);
            }
        }
        return l;
    }

    /**
     * Set and save data to config
     */
    public void set(String path, Object value) {
        yml.set(path, value);
        save();
    }

    /**
     * Get yml instance
     */
    public YamlConfiguration getYml() {
        return yml;
    }

    /**
     * Save config changes to file
     */
    public void save() {
        if (loadError) {
            // Prevent accidental overwrite of a malformed/invalid file
            Bukkit.getLogger().log(Level.SEVERE, "Skipping save for invalid configuration (would overwrite). Fix the YAML syntax in: " + config.getPath());
            return;
        }
        try {
            yml.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get list of strings at given path
     *
     * @return a list of string with colors translated
     */
    public List<String> getList(String path) {
        return yml.getStringList(path).stream().map(s -> s.replace("&", "§")).collect(Collectors.toList());
    }

    /**
     * Get boolean at given path
     */
    public boolean getBoolean(String path) {
        return yml.getBoolean(path);
    }

    /**
     * Get Integer at given path
     */
    public int getInt(String path) {
        return yml.getInt(path);
    }

    /**
     * Get Double at given path
     */
    public double getDouble(String path) {
        return yml.getDouble(path);
    }


    /**
     * Get string at given path
     */
    public String getString(String path) {
        return yml.getString(path);
    }

    /**
     * Returns true if this configuration had errors while loading (invalid YAML or I/O issues).
     * When true, the file will not be saved to avoid overwriting user content.
     */
    public boolean hasLoadError() {
        return loadError;
    }

    /**
     * Check if the config file was created for the first time
     * Can be used to add default values
     */
    public boolean isFirstTime() {
        return firstTime;
    }

    /**
     * Compare two arena locations
     * Return true if same location
     */
    public boolean compareArenaLoc(Location l1, Location l2) {
        return l1.getBlockX() == l2.getBlockX() && l1.getBlockZ() == l2.getBlockZ() && l1.getBlockY() == l2.getBlockY();
    }

    /**
     * Get config name
     */
    public String getName() {
        return name;
    }

    /**
     * Change internal name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
