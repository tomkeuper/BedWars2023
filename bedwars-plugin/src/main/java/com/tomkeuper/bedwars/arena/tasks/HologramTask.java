package com.tomkeuper.bedwars.arena.tasks;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.generator.IGenHolo;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.arena.shop.ShopHolo;
import com.tomkeuper.bedwars.api.arena.team.IBedHolo;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.entity.GeneratorHolder;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class HologramTask implements Runnable {

    @Override
    public void run() {
        try {
            for (IArena a : Arena.getArenas()) {
                World world = a.getWorld();
                if (world == null) continue;
                if (a.getStatus() != GameState.playing) continue;
                if (world.getPlayers().isEmpty()) continue;

                for (Player p : world.getPlayers()) {
                    if (p == null || !p.isOnline()) continue;

                    String iso = Language.getPlayerLanguage(p).getIso();
                    Location pLoc = p.getLocation();
                    List<ShopHolo> shopHolos = a.getShopHolograms(iso);
                    if (shopHolos == null) continue;

                    for (ShopHolo shopHolo : shopHolos) {
                        if (shopHolo == null) continue;

                        IHologram hologram = shopHolo.getHologram();
                        if (hologram == null) continue;

                        Location holoLoc = hologram.getLocation();
                        if (holoLoc == null || holoLoc.getWorld() != pLoc.getWorld()) continue;

                        double distance = pLoc.distance(holoLoc);
                        if (distance <= BedWars.hologramUpdateDistance) continue;

                        shopHolo.update(p);
                    }
                }

                for (ITeam team : a.getTeams()) {
                    for (Player p : team.getMembers()) {
                        if (p == null || !p.isOnline()) continue;
                        if (p.getWorld() != world) continue;

                        String iso = Language.getPlayerLanguage(p).getIso();
                        IBedHolo bedHolo = team.getBedHologram(iso);
                        if (bedHolo == null) continue;

                        Location bedLoc = bedHolo.getHologram().getLocation();
                        Location pLoc = p.getLocation();
                        double distance = pLoc.distance(bedLoc);

                        if (distance <= 4) bedHolo.hide(p);
                        else if (distance > 4 && distance <= 8) bedHolo.show(p);

                        if (distance >= BedWars.hologramUpdateDistance)
                            bedHolo.update(p);
                    }
                }

                List<IGenerator> generators = a.getOreGenerators();
                for (IGenerator generator : generators) {
                    GeneratorType type = generator.getType();
                    if (type != GeneratorType.EMERALD && type != GeneratorType.DIAMOND) continue;

                    Location genLoc = generator.getLocation();
                    for (Player p : world.getPlayers()) {
                        if (p == null || !p.isOnline()) continue;

                        String iso = Language.getPlayerLanguage(p).getIso();
                        IGenHolo holo = generator.getLanguageHolograms().get(iso);
                        if (holo == null) continue;

                        GeneratorHolder holder = generator.getHologramHolder();
                        Location pLoc = p.getLocation();
                        double distance = pLoc.distance(genLoc);
                        if (distance <= BedWars.hologramUpdateDistance) continue;

                        holo.update(p);
                        if (holder == null) continue;

                        holder.update(p);
                    }
                }
            }
        } catch (Exception e) {
            BedWars.debug("An error occurred while updating holograms: " + e.getMessage());
        }
    }
}
