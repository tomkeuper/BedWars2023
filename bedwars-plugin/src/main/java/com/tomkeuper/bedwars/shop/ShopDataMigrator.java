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

package com.tomkeuper.bedwars.shop;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.database.IDatabase;

import java.util.*;

/**
 * Startup migration for legacy Quick Buy identifiers (unscoped) → scoped default identifiers.
 * Example: blocks-category.category-content.wool → default-blocks-category.category-content.wool
 */
public final class ShopDataMigrator {

    private static final String CFG_ENABLE = "shop.migration.legacyIdMigrationOnStartup";
    private static final String CFG_VERSION = "shop.migration.latestVersion";
    private static final int EXPECTED_VERSION = 1; // bump when adding new migrations

    private ShopDataMigrator() {}

    public static void runIfNeeded() {
        try {
            // Config gate
            boolean enabled = true;
            try {
                if (BedWars.config.getYml().isSet(CFG_ENABLE)) {
                    enabled = BedWars.config.getYml().getBoolean(CFG_ENABLE, true);
                } else {
                    BedWars.config.getYml().set(CFG_ENABLE, true);
                    BedWars.config.save();
                }
            } catch (Throwable ignored) {}
            if (!enabled) return;

            // Version stamp guard
            int latest = 0;
            try {
                latest = BedWars.config.getYml().getInt(CFG_VERSION, 0);
            } catch (Throwable ignored) {}
            if (latest >= EXPECTED_VERSION) return;

            BedWars.plugin.getLogger().info("Starting legacy Quick Buy identifier migration...");

            IDatabase db = BedWars.getRemoteDatabase();
            if (db == null) {
                BedWars.plugin.getLogger().warning("Database is not initialized. Skipping Quick Buy migration.");
                return;
            }

            // Collect all UUIDs that have Quick Buy rows
            List<UUID> uuids;
            try {
                uuids = db.listQuickBuyUUIDs();
            } catch (Throwable t) {
                BedWars.plugin.getLogger().warning("Database adapter does not support listing Quick Buy UUIDs. Skipping migration.");
                return;
            }

            if (uuids == null || uuids.isEmpty()) {
                BedWars.plugin.getLogger().info("No Quick Buy data found to migrate.");
                // still stamp version to avoid re-running
                try {
                    BedWars.config.getYml().set(CFG_VERSION, EXPECTED_VERSION);
                    BedWars.config.save();
                } catch (Throwable ignored) {}
                return;
            }

            int migrated = 0;
            int total = 0;

            for (UUID uuid : uuids) {
                Map<Integer, String> slots = db.getQuickBuySlots(uuid, com.tomkeuper.bedwars.shop.quickbuy.PlayerQuickBuyCache.quickSlots);
                if (slots == null || slots.isEmpty()) continue;
                total += slots.size();

                HashMap<Integer, String> updates = new HashMap<>();
                for (Map.Entry<Integer, String> e : slots.entrySet()) {
                    String oldId = e.getValue();
                    if (oldId == null || oldId.trim().isEmpty()) continue;
                    String newId = scopeDefaultIfLegacy(oldId);
                    if (!newId.equals(oldId)) {
                        updates.put(e.getKey(), newId);
                    }
                }
                if (!updates.isEmpty()) {
                    try {
                        // Pass empty element list; adapter will perform UPDATE on existing rows
                        db.pushQuickBuyChanges(updates, uuid, java.util.Collections.emptyList());
                        migrated += updates.size();
                    } catch (Throwable t) {
                        BedWars.plugin.getLogger().warning("Failed to persist Quick Buy migration for " + uuid + ": " + t.getMessage());
                    }
                }
            }

            BedWars.plugin.getLogger().info("Migrated " + migrated + " of " + total + " Quick Buy identifiers to scoped format.");
            BedWars.plugin.getLogger().info("Legacy Quick Buy identifier migration completed successfully.");
            try {
                BedWars.config.getYml().set(CFG_VERSION, EXPECTED_VERSION);
                BedWars.config.save();
            } catch (Throwable ignored) {}
        } catch (Throwable t) {
            BedWars.plugin.getLogger().warning("Unexpected error during Quick Buy migration: " + t.getMessage());
        }
    }

    private static String scopeDefaultIfLegacy(String id) {
        // Legacy if there is no '-' before ".category-content."
        if (id == null) return "";
        int marker = id.indexOf(".category-content.");
        if (marker < 0) return id; // not a known pattern
        String cat = id.substring(0, marker);
        if (cat.contains("-")) return id; // already scoped
        // Make it default-scoped
        return "default-" + cat + id.substring(marker);
    }
}
