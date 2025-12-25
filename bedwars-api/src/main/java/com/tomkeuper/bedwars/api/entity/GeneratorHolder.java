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

package com.tomkeuper.bedwars.api.entity;

import com.tomkeuper.bedwars.api.BedWars;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class GeneratorHolder {
    private final BedWars api;

    @Getter
    private ArmorStand armorStand;
    @Getter
    private ItemStack helmet;
    @Getter
    private World world;

    public GeneratorHolder(Location loc, ItemStack helmet) {
        api = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(BedWars.class)).getProvider();
        this.world = loc.getWorld();
        if (world == null) throw new IllegalArgumentException("Location world cannot be null");
        this.armorStand = api.getVersionSupport().createPacketArmorStand(loc, world.getPlayers());
        this.helmet = helmet;
        if (helmet != null) setHelmet(helmet, true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(false);
        update();
    }

    public void setHelmet(ItemStack helmet, boolean update) {
        this.helmet = helmet;
        updateEquipment();
        if (update) update();
    }

    public void update() {
        api.getVersionSupport().updatePacketArmorStand(this, armorStand.getLocation().getWorld().getPlayers());
    }

    public void update(Player player) {
        api.getVersionSupport().updatePacketArmorStand(this, Collections.singletonList(player));
    }

    public void update(Player... players) {
        api.getVersionSupport().updatePacketArmorStand(this, Arrays.asList(players));
    }

    public void destroy() {
        api.getVersionSupport().destroyPacketArmorStand(this, world.getPlayers());
    }
    
    private void updateEquipment() {
        api.getVersionSupport().updatePacketArmorStandEquipment(this);
    }
}
