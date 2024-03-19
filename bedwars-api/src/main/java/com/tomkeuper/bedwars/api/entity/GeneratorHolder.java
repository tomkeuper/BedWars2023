package com.tomkeuper.bedwars.api.entity;

import com.tomkeuper.bedwars.api.BedWars;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GeneratorHolder {
    private final BedWars api;

    @Getter
    private ArmorStand armorStand;
    @Getter
    private ItemStack helmet;

    public GeneratorHolder(Location loc, ItemStack helmet) {
        api = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(BedWars.class)).getProvider();
        this.armorStand = api.getVersionSupport().createPacketArmorStand(loc);
        this.helmet = helmet;
        if (helmet != null) setHelmet(helmet, true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(false);
        update();
    }

    public void setHelmet(ItemStack helmet, boolean update) {
        this.helmet = helmet;
        if (update) {
            api.getVersionSupport().setGeneratorHolderHelmet(this, helmet);
        }
    }

    public void update() {
        api.getVersionSupport().updatePacketArmorStand(this);
    }

    public void destroy() {
        api.getVersionSupport().destroyPacketArmorStand(this);
    }
}
