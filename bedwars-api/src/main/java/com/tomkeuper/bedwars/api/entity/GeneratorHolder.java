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
