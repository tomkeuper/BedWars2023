package com.tomkeuper.bedwars.api.addon;

import java.util.ArrayList;
import java.util.List;

public class AddonStorer {
    private static List<Addon> registeredAddons;
    private static List<Addon> unloadedAddons;
    private static List<Addon> loadedAddons;

    public AddonStorer() {
        if (registeredAddons == null) registeredAddons = new ArrayList<>();
        if (unloadedAddons == null) unloadedAddons = new ArrayList<>();
        if (loadedAddons == null) loadedAddons = new ArrayList<>();
    }

    public List<Addon> loadedAddons() {
        return loadedAddons;
    }

    public List<Addon> registeredAddons() {
        return registeredAddons;
    }

    public List<Addon> unloadedAddons() {
        return unloadedAddons;
    }
}