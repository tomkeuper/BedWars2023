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

package com.tomkeuper.bedwars.arena.feature;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.stream.Collectors;

public class ResourceChestFeature implements Listener {

    private static ResourceChestFeature instance;

    private final Set<Material> blocked;

    private ResourceChestFeature() {
        this.blocked = BedWars.config.getYml()
                .getStringList(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_BLOCKED)
                .stream()
                .map(String::toUpperCase)
                .map(Material::valueOf)
                .collect(Collectors.toSet());

        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);
    }

    public static void init() {
        if (BedWars.config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_RESOURCE_CHEST_ENABLED)
                && instance == null) {
            instance = new ResourceChestFeature();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeftClickChest(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;

        // get the clicked block and verify it's a chest or ender chest
        Block block = e.getClickedBlock();
        if (block == null) return;

        boolean isChest = block.getType() == Material.CHEST;
        boolean isEnderChest = block.getType() == Material.ENDER_CHEST;
        if (!isChest && !isEnderChest) return;

        // get player and the item in hand
        Player player = e.getPlayer();
        ItemStack hand = e.getItem();
        if (hand == null || hand.getType() == Material.AIR) return;

        // skip if item is in the blocked set or is a tool (axe/pickaxe/shears)
        if (blocked.contains(hand.getType())
                || hand.getType().name().contains("AXE")
                || hand.getType().name().contains("PICKAXE")
                || hand.getType() == Material.SHEARS) {
            return;
        }

        // determine which inventory to deposit into
        Inventory inventory = isChest
                ? ((Chest) block.getState()).getBlockInventory()
                : player.getEnderChest();

        inventory.addItem(hand.clone());
        player.getInventory().remove(hand);
    }
}