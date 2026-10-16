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

package com.tomkeuper.bedwars.utils;

import java.lang.reflect.Field;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Finds a declared field trying each name in order and makes it accessible.
     * <p>
     * Spigot keeps NMS members obfuscated at runtime, while Paper 1.20.5+ runs Mojang-mapped and does not
     * remap names passed to reflection, so the same field can have a different name on each platform.
     *
     * @param owner class that declares the field
     * @param names candidate names, for example the Spigot obfuscated name and the Mojang name
     * @return the first field found
     * @throws NoSuchFieldException if none of the names exist
     */
    public static Field findField(Class<?> owner, String... names) throws NoSuchFieldException {
        for (String name : names) {
            try {
                Field field = owner.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new NoSuchFieldException(String.join(" / ", names) + " in " + owner.getName());
    }
}
