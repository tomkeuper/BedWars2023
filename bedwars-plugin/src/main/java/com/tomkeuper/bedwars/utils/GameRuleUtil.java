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

import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Applies game rules by name on any server version.
 * <p>
 * Minecraft 1.21.11 replaced the camelCase game rule names ({@code doDaylightCycle}) with namespaced
 * snake_case ids ({@code advance_time}), renamed several of them and turned {@code doFireTick} into the
 * integer rule {@code fire_spread_radius_around_player}. Since then {@link World#setGameRuleValue(String, String)}
 * returns {@code false} for the old names, without any error. This class translates between both naming
 * schemes in either direction, so arena configs keep working whichever names they use.
 */
public final class GameRuleUtil {

    private static final String FIRE_SPREAD_RADIUS = "fire_spread_radius_around_player";
    /** Radius used by vanilla when fire spread is enabled ({@code doFireTick = true}). */
    private static final String FIRE_SPREAD_ENABLED = "128";
    private static final String FIRE_SPREAD_DISABLED = "0";

    private static final Map<String, String> LEGACY_TO_MODERN = new HashMap<>();
    private static final Map<String, String> MODERN_TO_LEGACY = new HashMap<>();
    /** Legacy rules whose boolean meaning was inverted, e.g. {@code disableRaids = true} is {@code raids = false}. */
    private static final Set<String> INVERTED = new HashSet<>();

    static {
        rename("announceAdvancements", "show_advancement_messages");
        rename("commandBlockOutput", "command_block_output");
        rename("doDaylightCycle", "advance_time");
        rename("doEntityDrops", "entity_drops");
        rename("doLimitedCrafting", "limited_crafting");
        rename("projectilesCanBreakBlocks", "projectiles_can_break_blocks");
        rename("doMobLoot", "mob_drops");
        rename("doMobSpawning", "spawn_mobs");
        rename("doTileDrops", "block_drops");
        rename("doWeatherCycle", "advance_weather");
        rename("keepInventory", "keep_inventory");
        rename("logAdminCommands", "log_admin_commands");
        rename("mobGriefing", "mob_griefing");
        rename("naturalRegeneration", "natural_health_regeneration");
        rename("reducedDebugInfo", "reduced_debug_info");
        rename("sendCommandFeedback", "send_command_feedback");
        rename("showDeathMessages", "show_death_messages");
        rename("spectatorsGenerateChunks", "spectators_generate_chunks");
        rename("doInsomnia", "spawn_phantoms");
        rename("doImmediateRespawn", "immediate_respawn");
        rename("drowningDamage", "drowning_damage");
        rename("fallDamage", "fall_damage");
        rename("fireDamage", "fire_damage");
        rename("freezeDamage", "freeze_damage");
        rename("doPatrolSpawning", "spawn_patrols");
        rename("doTraderSpawning", "spawn_wandering_traders");
        rename("doWardenSpawning", "spawn_wardens");
        rename("forgiveDeadPlayers", "forgive_dead_players");
        rename("universalAnger", "universal_anger");
        rename("blockExplosionDropDecay", "block_explosion_drop_decay");
        rename("mobExplosionDropDecay", "mob_explosion_drop_decay");
        rename("tntExplosionDropDecay", "tnt_explosion_drop_decay");
        rename("waterSourceConversion", "water_source_conversion");
        rename("lavaSourceConversion", "lava_source_conversion");
        rename("globalSoundEvents", "global_sound_events");
        rename("doVinesSpread", "spread_vines");
        rename("enderPearlsVanishOnDeath", "ender_pearls_vanish_on_death");
        rename("tntExplodes", "tnt_explodes");
        rename("locatorBar", "locator_bar");
        rename("spawnMonsters", "spawn_monsters");
        rename("allowEnteringNetherUsingPortals", "allow_entering_nether_using_portals");
        rename("commandBlocksEnabled", "command_blocks_work");
        rename("spawnerBlocksEnabled", "spawner_blocks_work");
        rename("randomTickSpeed", "random_tick_speed");
        rename("spawnRadius", "respawn_radius");
        rename("maxEntityCramming", "max_entity_cramming");
        rename("maxCommandChainLength", "max_command_sequence_length");
        rename("maxCommandForkCount", "max_command_forks");
        rename("commandModificationBlockLimit", "max_block_modifications");
        rename("playersSleepingPercentage", "players_sleeping_percentage");
        rename("snowAccumulationHeight", "max_snow_accumulation_height");
        rename("playersNetherPortalDefaultDelay", "players_nether_portal_default_delay");
        rename("playersNetherPortalCreativeDelay", "players_nether_portal_creative_delay");
        rename("minecartMaxSpeed", "max_minecart_speed");

        inverted("disablePlayerMovementCheck", "player_movement_check");
        inverted("disableElytraMovementCheck", "elytra_movement_check");
        inverted("disableRaids", "raids");

        // boolean -> integer radius; see toModernValue / toLegacyValue
        rename("doFireTick", FIRE_SPREAD_RADIUS);
        // Shares the modern rule with doFireTick, so only the legacy -> modern direction is kept
        LEGACY_TO_MODERN.put("allowFireTicksAwayFromPlayer", FIRE_SPREAD_RADIUS);
    }

    private GameRuleUtil() {
    }

    private static void rename(String legacy, String modern) {
        LEGACY_TO_MODERN.put(legacy, modern);
        MODERN_TO_LEGACY.put(modern, legacy);
    }

    private static void inverted(String legacy, String modern) {
        rename(legacy, modern);
        INVERTED.add(legacy);
    }

    /**
     * Sets a game rule, accepting either the legacy camelCase name or the 1.21.11+ snake_case id
     * (optionally prefixed with {@code minecraft:}), and converting the value when the rule changed meaning.
     *
     * @param world the world to change
     * @param rule  the game rule name, in either naming scheme
     * @param value the value, as it would be written for that name
     * @return {@code true} if the rule was applied
     */
    public static boolean apply(World world, String rule, String value) {
        if (world == null || rule == null || value == null) return false;
        rule = rule.trim();
        value = value.trim();

        if (trySet(world, rule, value)) return true;

        String key = rule.regionMatches(true, 0, "minecraft:", 0, 10) ? rule.substring(10) : rule;
        if (!key.equals(rule) && trySet(world, key, value)) return true;

        // Legacy name on a 1.21.11+ server
        String modern = LEGACY_TO_MODERN.get(key);
        if (modern != null) {
            String converted = toModernValue(key, value);
            return converted != null && trySet(world, modern, converted);
        }

        // Modern id on an older server
        String legacy = MODERN_TO_LEGACY.get(key);
        if (legacy != null) {
            String converted = toLegacyValue(legacy, value);
            return converted != null && trySet(world, legacy, converted);
        }
        return false;
    }

    private static boolean trySet(World world, String rule, String value) {
        try {
            return world.setGameRuleValue(rule, value);
        } catch (RuntimeException e) {
            // 1.21.11+ throws when the value does not match the rule type
            return false;
        }
    }

    private static String toModernValue(String legacy, String value) {
        if (INVERTED.contains(legacy)) return invert(value);
        if (legacy.equals("doFireTick")) {
            Boolean enabled = parseBoolean(value);
            return enabled == null ? null : (enabled ? FIRE_SPREAD_ENABLED : FIRE_SPREAD_DISABLED);
        }
        if (legacy.equals("allowFireTicksAwayFromPlayer")) {
            Boolean enabled = parseBoolean(value);
            return enabled == null ? null : (enabled ? "-1" : FIRE_SPREAD_ENABLED);
        }
        return value;
    }

    private static String toLegacyValue(String legacy, String value) {
        if (INVERTED.contains(legacy)) return invert(value);
        if (legacy.equals("doFireTick")) {
            try {
                return Integer.parseInt(value) != 0 ? "true" : "false";
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return value;
    }

    private static String invert(String value) {
        Boolean parsed = parseBoolean(value);
        return parsed == null ? null : String.valueOf(!parsed);
    }

    private static Boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) return Boolean.TRUE;
        if (value.equalsIgnoreCase("false")) return Boolean.FALSE;
        return null;
    }
}
