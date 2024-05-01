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

package com.tomkeuper.bedwars.api.util;

import com.tomkeuper.bedwars.api.server.VersionSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FileUtil {

	public static void delete(File file) {
		if(file.isDirectory()) {
			//noinspection ConstantConditions
			for(File subfile : file.listFiles()) {
				delete(subfile);
			}
		} else {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
		}
	}

	public static void setMainLevel(String worldName, VersionSupport vs){
		Properties properties = new Properties();

		try (FileInputStream in = new FileInputStream("server.properties")) {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		properties.setProperty("level-name", worldName);
		properties.setProperty("generator-settings", vs.getVersion() > 5 ? "minecraft:air;minecraft:air;minecraft:air" : "1;0;1");
		properties.setProperty("allow-nether", "false");
		properties.setProperty("level-type", "flat");
		properties.setProperty("generate-structures", "false");
		properties.setProperty("spawn-monsters", "false");
		properties.setProperty("max-world-size", "1000");
		properties.setProperty("spawn-animals", "false");

		try (FileOutputStream out = new FileOutputStream("server.properties")) {
			properties.store(out, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
