package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import com.tomkeuper.bedwars.configuration.InvisConfig;
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
    private final InvisConfig invisConfig;

    public ArenaListener(Plugin plugin, InvisConfig invisConfig) {
        this.plugin = plugin;
        this.invisConfig = invisConfig;
    }
    @EventHandler
    public void onArenaStart(GameStateChangeEvent event) {
        IArena arena = event.getArena();
        if (arena != null) {
            List<Player> list = arena.getPlayers();
            if (list != null) {
                // Remove wooden swords from players' inventories if the config option is enabled, to prevent them from being used as a weapon. This is done because wooden swords are often used as a cheap weapon in BedWars, and they can be easily obtained by players. By removing them, it encourages players to use other weapons and adds more variety to the gameplay.
                if (event.getNewState() == GameState.playing) {
                    if (this.invisConfig.isWoodSwordDisappearanceEnabled()) {
                        Bukkit.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
                            Stream<Player> stream = list.stream();
                            Objects.requireNonNull(arena);
                            Objects.requireNonNull(arena);
                            stream.filter(arena::isPlayer).forEach((p) -> {
                                if (p.getInventory().contains(Material.WOOD_SWORD) && (p.getInventory().contains(Material.STONE_SWORD) || p.getInventory().contains(Material.GOLD_SWORD) || p.getInventory().contains(Material.IRON_SWORD) || p.getInventory().contains(Material.DIAMOND_SWORD))) {
                                    p.getInventory().remove(Material.WOOD_SWORD);
                                }

                            });
                        },20L, 10L);
                    }
                }
                // Apply invisibility effect to players who are currently in the respawn session or spectator mode, if the config option is enabled. This is done to prevent other players from seeing them and to allow them to move around freely without being targeted by enemies. The invisibility effect is applied every 10 ticks (0.5 seconds) to ensure that it remains active as long as the player is in the respawn session or spectator mode.
                if (this.invisConfig.isRespawnSessionInvisibilityEnabled()) {
                    Bukkit.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
                        Stream<Player> stream = list.stream();
                        Objects.requireNonNull(arena);
                        Objects.requireNonNull(arena);
                        stream.filter(arena::isPlayer).forEach((p) -> {
                            if (arena.isReSpawning(p)) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                            }
                        });
                    },20L, 10L);
                    // Apply invisibility effect to players who are currently in the spectator mode, if the config option is enabled. This is done to prevent other players from seeing them and to allow them to move around freely without being targeted by enemies. The invisibility effect is applied every 10 ticks (0.5 seconds) to ensure that it remains active as long as the player is in the spectator mode.
                } else if (event.getNewState() == GameState.restarting) {
                    Bukkit.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                        Stream<Player> stream = list.stream();
                        Objects.requireNonNull(arena);
                        Objects.requireNonNull(arena);
                        stream.filter(arena::isPlayer).forEach((p) -> {
                            if (arena.isSpectator(p)) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                            }

                        });
                    },10L);
                }
            }
        }
    }
    // this for removing invisibility effect from players when they respawn, to ensure that they are visible to other players and can be targeted by enemies. The invisibility effect is removed 3 ticks (0.15 seconds) after the player respawns to allow them to fully respawn and be ready for combat before becoming visible again.
    @EventHandler
    public void onRespawning(PlayerReSpawnEvent event) {
        Bukkit.getServer().getScheduler().runTaskLater(this.plugin, () -> event.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY), 3L);// Remove invisibility effect from players when they respawn, to ensure that they are visible to other players and can be targeted by enemies. The invisibility effect is removed 3 ticks (0.15 seconds) after the player respawns to allow them to fully respawn and be ready for combat before becoming visible again.
    }
    // This event handler is responsible for handling player kills in the arena. If the config option to disable death animation is enabled, it removes the victim's entity from the game immediately after they are killed, preventing any death animation from playing. Additionally, if the config option to enable kill sound is enabled and the killer is not null, it plays a specified sound at the killer's location with the configured volume and pitch. This enhances the gameplay experience by providing audio feedback for kills and allowing players to customize their experience based on their preferences.
    @EventHandler
    public void onKill(PlayerKillEvent event) {
        if (this.invisConfig.isDeathAnimationDisabled()) {
            event.getVictim().remove();
        }
        if (this.invisConfig.isKillSoundEnabled() && event.getKiller() != null) {
            event.getKiller().playSound(event.getKiller().getLocation(), this.invisConfig.getKillSound(), this.invisConfig.getKillSoundVolume(), this.invisConfig.getKillSoundPitch());
        }
    }
}
