package com.tomkeuper.bedwars.api.addon;

import java.util.ArrayList;
import java.util.List;

public class AddonStorer {
    private static List<Addon> registeredAddons;

    public AddonStorer() {
        if (registeredAddons == null) registeredAddons = new ArrayList<>();
    }

    public List<Addon> registeredAddons() {
        return registeredAddons;
    }

}