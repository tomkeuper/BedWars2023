package com.tomkeuper.bedwars.listeners;
/*
 * BedWars1058

 */

import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import com.tomkeuper.bedwars.configuration.InvsibltyConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ArenaListener implements Listener {

    private final Plugin plugin;
    private final InvsibltyConfig invsibltyConfig;

    public ArenaListener(Plugin plugin, InvsibltyConfig invsibltyConfig) {
        this.plugin = plugin;
        this.invsibltyConfig = invsibltyConfig;
    }

    @EventHandler
    public void onArenaStart(GameStateChangeEvent e) {
        IArena a = e.getArena();
        if (a != null) {
            List<Player> list = a.getPlayers();
            if (list != null) {
                if (e.getNewState() == GameState.playing) {
                    if (invsibltyConfig.isWoodSwordDisappearanceEnabled()) {
                        Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
                            Stream<Player> stream = list.stream();
                            Objects.requireNonNull(a);
                            stream.filter(a::isPlayer).forEach((p) -> {
                                if (p.getInventory().contains(Material.WOOD_SWORD) &&
                                        (p.getInventory().contains(Material.STONE_SWORD) ||
                                                p.getInventory().contains(Material.GOLD_SWORD) ||
                                                p.getInventory().contains(Material.IRON_SWORD) ||
                                                p.getInventory().contains(Material.DIAMOND_SWORD))) {
                                    p.getInventory().remove(Material.WOOD_SWORD);
                                }
                            });
                        }, 20L, 10L);
                    }

                    if (invsibltyConfig.isRespawnSessionInvisibilityEnabled()) {
                        Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
                            Stream<Player> stream = list.stream();
                            Objects.requireNonNull(a);
                            stream.filter(a::isPlayer).forEach((p) -> {
                                if (a.isReSpawning(p)) {
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                                }
                            });
                        }, 20L, 10L);
                    }

                } else if (e.getNewState() == GameState.restarting) {
                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                        Stream<Player> stream = list.stream();
                        Objects.requireNonNull(a);
                        stream.filter(a::isPlayer).forEach((p) -> {
                            if (a.isSpectator(p)) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                            }
                        });
                    }, 10L);
                }
            }
        }
    }

    @EventHandler
    public void onRespawning(PlayerReSpawnEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> e.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY), 3L);
    }

    @EventHandler
    public void onKill(PlayerKillEvent e) {
        if (invsibltyConfig.isDeathAnimationDisabled()) {
            e.getVictim().remove();
        }

        if (invsibltyConfig.isKillSoundEnabled() && e.getKiller() != null) {
            e.getKiller().playSound(
                    e.getKiller().getLocation(),
                    invsibltyConfig.getKillSound(),
                    invsibltyConfig.getKillSoundVolume(),
                    invsibltyConfig.getKillSoundPitch()
            );
        }
    }
}
