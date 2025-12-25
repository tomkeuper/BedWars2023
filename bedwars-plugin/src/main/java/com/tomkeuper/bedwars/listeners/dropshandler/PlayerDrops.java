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

package com.tomkeuper.bedwars.listeners.dropshandler;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.language.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tomkeuper.bedwars.api.language.Language.getMsg;

public class PlayerDrops {

    private PlayerDrops() {
    }

    /**
     * if bedWars should handle drops behavior.
     *
     * @return true if event drops must be cleared.
     */
    public static boolean handlePlayerDrops(IArena arena, Player victim, Player killer, ITeam victimsTeam, ITeam killersTeam, PlayerKillEvent.PlayerKillCause cause, List<ItemStack> inventory) {
        BedWars.debug("PlayerDrops - Handling player drops for victim: " + victim.getName() + ", killer: " + (killer != null ? killer.getName() : "null") + ", cause: " + cause);
        if (arena.getConfig().getBoolean(ConfigPath.ARENA_NORMAL_DEATH_DROPS)) {
            BedWars.debug("PlayerDrops - Normal death drops enabled, skipping custom drop handling.");
            return false;
        }

        if ((cause == PlayerKillEvent.PlayerKillCause.PLAYER_PUSH || cause == PlayerKillEvent.PlayerKillCause.PLAYER_PUSH_FINAL) && killer == null) {
            // if died by fall damage drop items at location
            BedWars.debug("PlayerDrops - Death by player push but killer is null, dropping items at location.");
            return true;
        }

        if (killer == null) {
            // Death without an attacker drops items on the floor
            BedWars.debug("PlayerDrops - Death without an attacker, dropping items at location.");
            return true;
        }

        if (cause.isDespawnable()) {
            // If killed by an ironGolem or silverFish drop on floor
            BedWars.debug("PlayerDrops - Death by despawnable entity, dropping items at location.");
            return true;
        }
        if (cause.isPvpLogOut()) {
            // if is pvp log out drop at disconnect location
            BedWars.debug("PlayerDrops - Death by PvP logout, dropping items at location.");
            return true;
        }

        if (cause.isFinalKill()) {
            BedWars.debug("PlayerDrops - Final kill, dropping ender chest items at team generator.");
            // if is final kill drop items at generator
            if (victimsTeam != null) {
                Vector killDropsLocation = victimsTeam.getKillDropsLocation();
                Location dropsLocation = new Location(victim.getWorld(), killDropsLocation.getBlockX(), killDropsLocation.getY(), killDropsLocation.getZ());
                victim.getEnderChest().forEach(item -> {
                    if (item != null) victim.getWorld().dropItemNaturally(dropsLocation, item);
                });
                victim.getEnderChest().clear();
            }
        }

        // victim's inventory
        if (victimsTeam != null && !(victimsTeam.equals(killersTeam) && victim.equals(killer))) {
            BedWars.debug("PlayerDrops - Handling victim's inventory drops.");
            // if final kill give items at kill drops location (team generator)
            if (victimsTeam.isBedDestroyed()) {
                BedWars.debug("PlayerDrops - Victim's bed is destroyed, dropping items at team generator.");
                for (ItemStack i : inventory) {
                    if (i == null) continue;
                    if (i.getType() == Material.AIR) continue;
                    if (BedWars.nms.isArmor(i) || BedWars.nms.isBow(i) || BedWars.nms.isSword(i) || BedWars.nms.isTool(i)) continue;
                    if (!BedWars.nms.getShopUpgradeIdentifier(i).trim().isEmpty()) continue;
                    if (arena.getTeam(killer) != null) {
                        Vector v = victimsTeam.getKillDropsLocation();
                        killer.getWorld().dropItemNaturally(new Location(arena.getWorld(), v.getX(), v.getY(), v.getZ()), i);
                    }
                }
            } else {
                // add-to-inventory feature if receiver is not respawning
                if (!arena.isPlayer(killer)) {
                    BedWars.debug("PlayerDrops - Killer is not in arena.");
                    return true;
                }
                if (arena.isReSpawning(killer)) {
                    BedWars.debug("PlayerDrops - Killer is respawning.");
                    return true;
                }
                BedWars.debug("PlayerDrops - Adding eligible items to killer's inventory.");
                Map<Material, Integer> materialDrops = new HashMap<>();
                for (ItemStack i : inventory) {
                    if (i == null) continue;
                    if (i.getType() == Material.AIR) continue;
                    if (i.getType() == Material.DIAMOND || i.getType() == Material.EMERALD || i.getType() == Material.IRON_INGOT || i.getType() == Material.GOLD_INGOT) {

                        // add to killer inventory
                        killer.getInventory().addItem(i);

                        // count items
                        if (materialDrops.containsKey(i.getType())) materialDrops.replace(i.getType(), materialDrops.get(i.getType()) + i.getAmount());
                        else materialDrops.put(i.getType(), i.getAmount());
                    }
                }

                // send messages to killer
                for (Map.Entry<Material, Integer> entry : materialDrops.entrySet()) {
                    String msg = "";
                    int amount = entry.getValue();
                    switch (entry.getKey()) {
                        case DIAMOND:
                            msg = getMsg(killer, Messages.PLAYER_DIE_REWARD_DIAMOND).replace("%bw_meaning%", amount == 1 ?
                                    getMsg(killer, Messages.MEANING_DIAMOND_SINGULAR) : getMsg(killer, Messages.MEANING_DIAMOND_PLURAL));
                            break;
                        case EMERALD:
                            msg = getMsg(killer, Messages.PLAYER_DIE_REWARD_EMERALD).replace("%bw_meaning%", amount == 1 ?
                                    getMsg(killer, Messages.MEANING_EMERALD_SINGULAR) : getMsg(killer, Messages.MEANING_EMERALD_PLURAL));
                            break;
                        case IRON_INGOT:
                            msg = getMsg(killer, Messages.PLAYER_DIE_REWARD_IRON).replace("%bw_meaning%", amount == 1 ?
                                    getMsg(killer, Messages.MEANING_IRON_SINGULAR) : getMsg(killer, Messages.MEANING_IRON_PLURAL));
                            break;
                        case GOLD_INGOT:
                            msg = getMsg(killer, Messages.PLAYER_DIE_REWARD_GOLD).replace("%bw_meaning%", amount == 1 ?
                                    getMsg(killer, Messages.MEANING_GOLD_SINGULAR) : getMsg(killer, Messages.MEANING_GOLD_PLURAL));
                            break;
                    }
                    killer.sendMessage(msg.replace("%bw_amount%", String.valueOf(amount)));
                }
                materialDrops.clear();
            }
        }
        return true;
    }

    private static void dropItems(Player player, List<ItemStack> inventory) {
        for (ItemStack i : inventory) {
            if (i == null) continue;
            if (i.getType() == Material.AIR) continue;
            if (i.getType() == Material.DIAMOND || i.getType() == Material.EMERALD || i.getType() == Material.IRON_INGOT || i.getType() == Material.GOLD_INGOT) {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), i);
            }
        }
    }
}
