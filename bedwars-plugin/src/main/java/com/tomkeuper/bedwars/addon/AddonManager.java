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

package com.tomkeuper.bedwars.addon;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.addon.Addon;
import com.tomkeuper.bedwars.api.addon.IAddonManager;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddonManager implements IAddonManager {
    private static List<Addon> registeredAddons;
    private static List<Addon> unloadedAddons;
    private static List<Addon> loadedAddons;

    public AddonManager() {
        if (registeredAddons == null) registeredAddons = new ArrayList<>();
        if (unloadedAddons == null) unloadedAddons = new ArrayList<>();
        if (loadedAddons == null) loadedAddons = new ArrayList<>();
    }


    @Override
    public List<Addon> getAddons() {
        return registeredAddons;
    }

    @Override
    public List<Addon> getLoadedAddons() {
        return loadedAddons;
    }

    @Override
    public List<Addon> getUnloadedAddons() {
        return unloadedAddons;
    }

    @Override
    public List<Addon> getAddonsByAuthor(String author) {
        return registeredAddons.stream().filter(a -> a.getAuthor().equalsIgnoreCase(author)).collect(Collectors.toList());
    }

    @Override
    public void loadAddon(Addon addon) {
        if (loadedAddons.contains(addon)) return;
        loadedAddons.add(addon);
        unloadedAddons.remove(addon);
        if (!Bukkit.getPluginManager().isPluginEnabled(addon.getPlugin()))
            Bukkit.getPluginManager().enablePlugin(addon.getPlugin());
    }

    @Override
    public void unloadAddon(Addon addon) {
        if (unloadedAddons.contains(addon)) return;
        unloadedAddons.add(addon);
        loadedAddons.remove(addon);
        addon.unload();
        if (Bukkit.getPluginManager().isPluginEnabled(addon.getPlugin()))
            Bukkit.getPluginManager().disablePlugin(addon.getPlugin());
    }

    @Override
    public void unloadAddons() {
        if (loadedAddons.isEmpty()) return;
        List<Addon> loadedAddonsToRemove = new ArrayList<>();
        log("Unloading addons...");
        for (Addon addon : loadedAddons) {
            if (unloadedAddons.contains(addon)) continue;
            String name, author;
            try{
                name = addon.getName();
            } catch (AbstractMethodError ignored){
                name = "null";
            }
            try{
                author = addon.getAuthor();
            } catch (AbstractMethodError ignored){
                author = "null";
            }

            log("Unloading " + name + " by " + author);
            unloadedAddons.add(addon);
            loadedAddonsToRemove.add(addon);
            addon.unload();
            if (Bukkit.getPluginManager().isPluginEnabled(addon.getPlugin())) {
                Bukkit.getPluginManager().disablePlugin(addon.getPlugin());
            }
            log("Addon unloaded successfully!");
        }

        for (Addon addon : loadedAddonsToRemove) {
            loadedAddons.remove(addon);
        }
    }

    @Override
    public void loadAddons() {
        if (registeredAddons.isEmpty()) {
            log("No addons were found!");
            return;
        }

        int addonCount = registeredAddons.size();
        String plural = (addonCount == 1) ? "addon" : "addons";

        log(registeredAddons.size() + " " + plural + " " + ((addonCount == 1) ? "was" : "were") + " found.");
        log("Loading " + registeredAddons.size() + " " + plural);

        for (Addon addon : registeredAddons) {
            if (loadedAddons.contains(addon)) continue;
            String name, author, version;
            try{
                name = addon.getName();
            } catch (AbstractMethodError ignored){
                name = "null";
            }
            try{
                author = addon.getAuthor();
            } catch (AbstractMethodError ignored){
                author = "null";
            }
            try{
                version = addon.getVersion();
            } catch (AbstractMethodError ignored){
                version = "null";
            }

            log("Loading " + name + " by " + author +". " + "Version " + version);
            loadedAddons.add(addon);
            unloadedAddons.remove(addon);
            addon.load();
            log(name + " addon loaded and registered successfully!");
        }
    }

    @Override
    public void registerAddon(Addon addon){
        if (addon == null) return;
        if (registeredAddons.contains(addon)) return;
        if (registeredAddons.stream().map(Addon::getPlugin).collect(Collectors.toList()).contains(addon.getPlugin())) return;
        registeredAddons.add(addon);
    }

    private void log(String log) {
        BedWars.getPlugin(BedWars.class).getLogger().info(log);
    }
}
