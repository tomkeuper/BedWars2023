
package com.tomkeuper.bedwars.shop;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.database.IDatabase;
import com.tomkeuper.bedwars.database.MySQL;
import com.tomkeuper.bedwars.shop.quickbuy.PlayerQuickBuyCache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Startup migration for legacy Quick Buy identifiers (unscoped) → scoped default identifiers.
 * Example: blocks-category.category-content.wool → default-blocks-category.category-content.wool
 */
public final class ShopDataMigrator {

    private static final String CFG_ENABLE = "shop.migration.legacyIdMigrationOnStartup";
    private static final String CFG_VERSION = "shop.migration.latestVersion";
    private static final String CFG_TABLE_MIGRATION = "shop.migration.quickBuyTableMigrated";
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
            if (!enabled) {
                BedWars.plugin.getLogger().info("Shop migration is disabled in config (" + CFG_ENABLE + " = false)");
                return;
            }

            // Run table migration first (quick_buy_2 → quick_buy)
            migrateQuickBuyTable();

            // Version stamp guard
            int latest = 0;
            try {
                latest = BedWars.config.getYml().getInt(CFG_VERSION, 0);
            } catch (Throwable ignored) {}
            if (latest >= EXPECTED_VERSION) {
                BedWars.plugin.getLogger().info("Shop migration not needed. Current version: " + latest + ", Expected version: " + EXPECTED_VERSION);
                return;
            }

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
                Map<Integer, String> slots = db.getQuickBuySlots(uuid, PlayerQuickBuyCache.quickSlots);
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
                        db.pushQuickBuyChanges(updates, uuid, Collections.emptyList());
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
    /**
     * Migrates quick_buy_2 table to quick_buy table.
     * This is a one-time operation.
     */
    private static void migrateQuickBuyTable() {
        try {
            // Check if already migrated
            boolean alreadyMigrated = false;
            try {
                alreadyMigrated = BedWars.config.getYml().getBoolean(CFG_TABLE_MIGRATION, false);
            } catch (Throwable ignored) {}

            if (alreadyMigrated) {
                BedWars.plugin.getLogger().info("Quick Buy table migration not needed. Already migrated (" + CFG_TABLE_MIGRATION + " = true)");
                return;
            }

            IDatabase db = BedWars.getRemoteDatabase();
            if (!(db instanceof com.tomkeuper.bedwars.database.MySQL)) {
                // Only MySQL needs this migration
                BedWars.plugin.getLogger().info("Quick Buy table migration not needed. Database type: " + db.getClass().getSimpleName() + " (only MySQL requires migration)");
                try {
                    BedWars.config.getYml().set(CFG_TABLE_MIGRATION, true);
                    BedWars.config.save();
                } catch (Throwable ignored) {}
                return;
            }

            BedWars.plugin.getLogger().info("Checking for Quick Buy table migration (quick_buy_2 → quick_buy)...");

            com.tomkeuper.bedwars.database.MySQL mysql = (com.tomkeuper.bedwars.database.MySQL) db;
            if (mysql.migrateQuickBuyTable()) {
                BedWars.plugin.getLogger().info("Quick Buy table migration completed successfully.");
                try {
                    BedWars.config.getYml().set(CFG_TABLE_MIGRATION, true);
                    BedWars.config.save();
                } catch (Throwable ignored) {}
            } else {
                BedWars.plugin.getLogger().warning("Quick Buy table migration failed. Check the logs for details.");
            }
        } catch (Throwable t) {
            BedWars.plugin.getLogger().warning("Error during Quick Buy table migration: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private static String scopeDefaultIfLegacy(String id) {
        // Legacy identifiers have only one hyphen in the category segment (e.g., "melee-category")
        // New scoped identifiers have at least two hyphens (e.g., "default-melee-category" or "Swashbuckle-blocks-category").
        if (id == null) return "";
        int marker = id.indexOf(".category-content.");
        if (marker < 0) return id; // not a known pattern
        String cat = id.substring(0, marker);

        // Already default-scoped
        if (cat.startsWith("default-")) return id;

        // Count hyphens in the category segment
        int dashCount = 0;
        for (int i = 0; i < cat.length(); i++) {
            if (cat.charAt(i) == '-') dashCount++;
        }
        if (dashCount >= 2) return id; // already has a scope prefix

        // Make it default-scoped
        return "default-" + cat + id.substring(marker);
    }
}