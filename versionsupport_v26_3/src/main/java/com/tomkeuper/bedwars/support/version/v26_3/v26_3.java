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

package com.tomkeuper.bedwars.support.version.v26_3;

import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import com.tomkeuper.bedwars.support.version.v26_1.v26_1;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.bukkit.Tag;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class v26_3 extends v26_1 {

    public v26_3(Plugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    public boolean isAxe(org.bukkit.inventory.ItemStack itemStack) {
        if (itemStack == null) return false;
        return Tag.ITEMS_AXES.isTagged(itemStack.getType());
    }

    @Override
    public void registerTntWhitelist(float endStoneBlast, float glassBlast) {
        try {
            Field field = BlockBehaviour.class.getDeclaredField("explosionResistance");
            field.setAccessible(true);
            field.set(Blocks.END_STONE, endStoneBlast);
            field.set(Blocks.OBSIDIAN, glassBlast);
            field.set(Blocks.GLASS, glassBlast);

            var coloredGlass = new ArrayList<>(Blocks.STAINED_GLASS.asList());
            coloredGlass.add(Blocks.GLASS);
            coloredGlass.forEach(
                    glass -> {
                        try {
                            field.set(glass, glassBlast);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    @Override
    public IGeneratorAnimation createDefaultGeneratorAnimation(ArmorStand armorStand) {
        return new DefaultGenAnimation(armorStand);
    }
}
