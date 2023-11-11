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
        String count, message;

        if (registeredAddons.isEmpty()) {
            log("No addons were found!");
            return;
        }
        else if (registeredAddons.size() == 1) {
            count = "addon";
            message = "has been found!";
        } else {
            count = "addons";
            message = "were found!";
        }

        log(registeredAddons.size() + " " + count + " " + message);
        log("Loading " + registeredAddons.size() + " " + count);

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
