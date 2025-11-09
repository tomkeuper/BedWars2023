package com.tomkeuper.bedwars.arena.tasks;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.generator.IGenHolo;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.arena.shop.ShopHolo;
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
        for (IArena a : Arena.getArenas()) {
            World world = a.getWorld();
            if (world == null) continue;
            if (a.getStatus() != GameState.playing) continue;

            if (world.getPlayers().isEmpty()) continue;

            for (Player p : world.getPlayers()) {
                String iso = Language.getPlayerLanguage(p).getIso();
                Location pLoc = p.getLocation();
                List<ShopHolo> shopHolos = a.getShopHolograms(iso);
                for (ShopHolo shopHolo : shopHolos) {
                    IHologram hologram = shopHolo.getHologram();
                    Location holoLoc = hologram.getLocation();
                    double distance = pLoc.distance(holoLoc);
                    if (distance <= BedWars.hologramUpdateDistance) continue;
                    hologram.getLines().forEach(line -> line.reveal(p));
                }
            }

            List<IGenerator> generators = a.getOreGenerators();
            for (IGenerator generator : generators) {
                GeneratorType type = generator.getType();
                if (type != GeneratorType.EMERALD && type !=  GeneratorType.DIAMOND) continue;
                Location genLoc = generator.getLocation();
                for (Player p : world.getPlayers()) {
                    String iso = Language.getPlayerLanguage(p).getIso();
                    IGenHolo holo = generator.getLanguageHolograms().get(iso);
                    Location pLoc = p.getLocation();
                    double distance = pLoc.distance(genLoc);
                    if (distance <= BedWars.hologramUpdateDistance) continue;
                    holo.update(p);
                    generator.getHologramHolder().update(p);
                }
            }
        }
    }
}
