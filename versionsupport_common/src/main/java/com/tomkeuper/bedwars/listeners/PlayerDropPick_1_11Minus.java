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

package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import com.tomkeuper.bedwars.api.server.ServerType;
import com.tomkeuper.bedwars.support.version.common.VersionCommon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static com.tomkeuper.bedwars.utils.MainUtils.getSimilarItemsAround;
import static com.tomkeuper.bedwars.utils.MainUtils.manageGeneratorPickUp;

public class PlayerDropPick_1_11Minus implements Listener {

    private static BedWars api;

    public PlayerDropPick_1_11Minus(BedWars bedWars){
        api = bedWars;
    }

    /* This Class is used for versions from 1.8.8 to 1.11 included */

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (api.getServerType() == ServerType.MULTIARENA ) {
            //noinspection ConstantConditions
            if (e.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(api.getLobbyWorld())) {
                e.setCancelled(true);
                return;
            }
        }

        IArena a = api.getArenaUtil().getArenaByPlayer(e.getPlayer());
        if (a == null) return;
        ItemStack stack = e.getItem().getItemStack();

        if (!a.isPlayer(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }

        if (a.getStatus() != GameState.playing) {
            e.setCancelled(true);
            return;
        }

        if (a.getRespawnSessions().containsKey(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }

        if (stack.getType() == Material.ARROW){
            e.getItem().setItemStack(api.getVersionSupport().createItemStack(stack.getType().toString(), stack.getAmount(), (short) 0));
            return;
        }

        if (VersionCommon.api.getVersionSupport().isBed(stack.getType())) {
            e.setCancelled(true);
            e.getItem().remove();
        } else if (stack.hasItemMeta()) {
            //noinspection ConstantConditions
            if (stack.getItemMeta().hasDisplayName()) {
                if (stack.getItemMeta().getDisplayName().contains("custom")) {
                    Material material = stack.getType();
                    ItemMeta itemMeta = new ItemStack(material).getItemMeta();

                    //Call ore pick up event

                    if (!api.getAFKUtil().isPlayerAFK(e.getPlayer())){
                        PlayerGeneratorCollectEvent event = new PlayerGeneratorCollectEvent(e.getPlayer(), e.getItem(), a, e.getRemaining());
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()){
                            e.setCancelled(true);
                        } else {
                            stack.setItemMeta(itemMeta);
                            e.getItem().setItemStack(stack);
                            Player p = e.getPlayer();

                            List<Item> items = getSimilarItemsAround(e.getItem());
                            items.add(e.getItem());

                            if (event.isCancelled()) return;

                            manageGeneratorPickUp(p, e.getItem(), items);
                        }
                    } else {  // Cancel Event if play is afk
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (api.getServerType() == ServerType.MULTIARENA) {
            //noinspection ConstantConditions
            if (e.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(api.getLobbyWorld())) {
                e.setCancelled(true);
                return;
            }
        }
        IArena a = api.getArenaUtil().getArenaByPlayer(e.getPlayer());
        if (a == null) return;

        if (!a.isPlayer(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }

        if (a.getStatus() != GameState.playing) {
            e.setCancelled(true);
        } else {
            ItemStack i = e.getItemDrop().getItemStack();
            if (i.getType() == Material.COMPASS) {
                e.setCancelled(true);
                return;
            }
        }

        if (a.getRespawnSessions().containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
