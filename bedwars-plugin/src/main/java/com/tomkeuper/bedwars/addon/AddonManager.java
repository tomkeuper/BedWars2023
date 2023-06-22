package com.tomkeuper.bedwars.addon;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.addon.Addon;
import com.tomkeuper.bedwars.api.addon.IAddonManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddonManager implements IAddonManager {
    private static List<Addon> addons;

    public AddonManager() {
        if (addons == null) addons = new ArrayList<>();
    }
    @Override
    public List<Addon> getAddons() {
        return addons;
    }

    @Override
    public List<Addon> getAddonsByAuthor(String author) {
        return addons.stream().filter(a -> a.getAuthor().equals(author)).collect(Collectors.toList());
    }

    @Override
    public void loadAddon(Addon addon) {
        addons.add(addon);
    }

    public void registerAddons() {
        String count = "";
        if (addons.size() < 1) {
            log("No addons were found!");
            return;
        }
        else if (addons.size() == 1) count = "addon";
        else if (addons.size() > 1) count = "addons";
        log(addons.size() + " " + count + " has been found!");
        log("Loading " + addons.size() + " " + count);
        for (Addon addon : addons) {
            log("Loading " + addon.getIdentifier() + " by " + addon.getAuthor()+". " + "Version " + addon.getVersion());
            addon.load();
            log(addon.getIdentifier() + " addon loaded and registered successfully!");
        }
    }

    private void log(String log) {
        BedWars.getPlugin(BedWars.class).getLogger().info(log);
    }


    @Override
    public void unloadAddon(Addon addon) {
        addons.remove(addon);
        addon.unload();
    }

    @Override
    public void unloadAddons() {
        for (Addon addon : addons) {
            addon.unload();
        }
    }
}