/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
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
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.tomkeuper.bedwars.arena;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.generator.IGenHolo;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.entity.GeneratorHolder;
import com.tomkeuper.bedwars.api.events.gameplay.GeneratorUpgradeEvent;
import com.tomkeuper.bedwars.api.events.gameplay.GeneratorDropEvent;
import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.region.Cuboid;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@SuppressWarnings("WeakerAccess")
public class OreGenerator implements IGenerator {

    private Location location;
    private double upgradeStage = 1.0;
    private int spawnLimit = 0, amount = 1;
    final int speedMultiplier = 4;
    private double delay = 1, lastSpawn;
    /**
     * -- GETTER --
     *  Get the arena assigned to this generator.
     */
    @Getter
    private IArena arena;
    private ItemStack ore;
    private GeneratorType type;
    private IGeneratorAnimation animation;
    private int rotate = 0, dropID = 0;
    private ITeam bwt;
    boolean up = true, disabled = false;

    /**
     * Generator holograms per language <iso, holo></iso,>
     */
    public HashMap<Player, IGenHolo> holograms = new HashMap<>();

    private GeneratorHolder item;
    public boolean stack = BedWars.getGeneratorsCfg().getBoolean(ConfigPath.GENERATOR_STACK_ITEMS);

    @Getter
    private static final ConcurrentLinkedDeque<OreGenerator> rotation = new ConcurrentLinkedDeque<>();

    public OreGenerator(Location location, IArena arena, GeneratorType type, ITeam bwt) {
        if (type == GeneratorType.EMERALD || type == GeneratorType.DIAMOND) {
            this.location = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 1.3, location.getBlockZ() + 0.5);
        } else {
            this.location = location.add(0, 1.3, 0);
        }
        this.arena = arena;
        this.bwt = bwt;
        this.type = type;
        loadDefaults();
        BedWars.debug("Initializing new generator at: " + location + " - " + type + " - " + (bwt == null ? "NOTEAM" : bwt.getName()));

        Cuboid c = new Cuboid(location, arena.getConfig().getInt(ConfigPath.ARENA_GENERATOR_PROTECTION), true);
        c.setMaxY(c.getMaxY() + 5);
        c.setMinY(c.getMinY() - 2);
        arena.getRegionsList().add(c);
    }

    @Override
    public void upgrade() {
        switch (type) {
            case DIAMOND:
                upgradeStage++;
                if (upgradeStage == 2) {
                    delay = BedWars.getGeneratorsCfg().getDouble(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_II_DELAY) == null ?
                            "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_II_DELAY : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_II_DELAY) * speedMultiplier;
                    amount = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_II_AMOUNT) == null ?
                            "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_II_AMOUNT : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_II_AMOUNT);
                    spawnLimit = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_II_SPAWN_LIMIT) == null ?
                            "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_II_SPAWN_LIMIT : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_II_SPAWN_LIMIT);
                } else if (upgradeStage == 3) {
                    delay = BedWars.getGeneratorsCfg().getDouble(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_III_DELAY) == null ?
                            "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_III_DELAY : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_III_DELAY) * speedMultiplier;
                    amount = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_III_AMOUNT) == null ?
                            "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_III_AMOUNT : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_III_AMOUNT);
                    spawnLimit = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_II_SPAWN_LIMIT) == null ?
                            "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_III_SPAWN_LIMIT : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_III_SPAWN_LIMIT);
                }
                ore = new ItemStack(Material.DIAMOND);
                for (IGenHolo e : holograms.values()) {
                    e.setTierName(Language.getLang(e.getIso()).m(Messages.GENERATOR_HOLOGRAM_TIER).replace("%bw_tier%", Language.getLang(e.getIso())
                            .m(upgradeStage == 2 ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
                }
                break;
            case EMERALD:
                upgradeStage++;
                if (upgradeStage == 2) {
                    delay = BedWars.getGeneratorsCfg().getDouble(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_II_DELAY) == null ?
                            "Default." + ConfigPath.GENERATOR_EMERALD_TIER_II_DELAY : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_II_DELAY) * speedMultiplier;
                    amount = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_II_AMOUNT) == null ?
                            "Default." + ConfigPath.GENERATOR_EMERALD_TIER_II_AMOUNT : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_II_AMOUNT);
                    spawnLimit = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_II_SPAWN_LIMIT) == null ?
                            "Default." + ConfigPath.GENERATOR_EMERALD_TIER_II_SPAWN_LIMIT : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_II_SPAWN_LIMIT);
                } else if (upgradeStage == 3) {
                    delay = BedWars.getGeneratorsCfg().getDouble(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_III_DELAY) == null ?
                            "Default." + ConfigPath.GENERATOR_EMERALD_TIER_III_DELAY : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_III_DELAY) * speedMultiplier;
                    amount = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_III_AMOUNT) == null ?
                            "Default." + ConfigPath.GENERATOR_EMERALD_TIER_III_AMOUNT : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_III_AMOUNT);
                    spawnLimit = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_II_SPAWN_LIMIT) == null ?
                            "Default." + ConfigPath.GENERATOR_EMERALD_TIER_III_SPAWN_LIMIT : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_III_SPAWN_LIMIT);
                }
                ore = new ItemStack(Material.EMERALD);
                for (IGenHolo e : holograms.values()) {
                    e.setTierName(Language.getLang(e.getIso()).m(Messages.GENERATOR_HOLOGRAM_TIER).replace("%bw_tier%",
                            Language.getLang(e.getIso()).m(upgradeStage == 2 ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
                }
                break;
        }
        Bukkit.getPluginManager().callEvent(new GeneratorUpgradeEvent(this));
    }

    @Override
    public void spawn() {
        if (arena.getStatus() != GameState.playing){
            return;
        }

        if (disabled) return;

        if (lastSpawn <= 0) {
            lastSpawn = delay - 1;

            if (spawnLimit != 0) {
                int oreCount = 0;

                for (Entity e : location.getWorld().getNearbyEntities(location, 3, 3, 3)) {
                    if (e.getType() == EntityType.DROPPED_ITEM) {
                        Item i = (Item) e;
                        if (i.getItemStack().getType() == ore.getType()) {
                            oreCount++;
                        }
                        if (oreCount >= spawnLimit) return;
                    }
                }
            }

            GeneratorDropEvent event;
            Bukkit.getPluginManager().callEvent(event = new GeneratorDropEvent(this));

            if (event.isCancelled()){
                return;
            }
            dropItem(location);
            return;
        }
        lastSpawn--;

        if (getType() == GeneratorType.EMERALD || getType() == GeneratorType.DIAMOND) {
            for (Player p : arena.getWorld().getPlayers()) {
                IGenHolo e = holograms.get(p);
                if (e == null) holograms.put(p, new HoloGram(p));
                e = holograms.get(p);

                e.setTimerName(Language.getLang(e.getIso()).m(Messages.GENERATOR_HOLOGRAM_TIMER).replace("%bw_seconds%", String.valueOf((int) Math.ceil(lastSpawn/speedMultiplier))));
            }
        }
    }

    private void dropItem(Location location, double amount) {
        for (double temp = amount; temp > 0; temp--) {
            ItemStack itemStack = new ItemStack(ore);
            if (!stack) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("custom" + dropID++);
                itemStack.setItemMeta(itemMeta);
            }
            Item item = location.getWorld().dropItem(location, itemStack);
            item.setVelocity(new Vector(0, 0, 0));
        }
    }

    /**
     * Drop item stack with ID
     */
    @Override
    public void dropItem(Location location) {
        dropItem(location, amount);
    }

    @Override
    public void setOre(ItemStack ore) {
        BedWars.debug("Changing ore for generator at " + location.toString() + " from " + this.ore + " to " + ore);
        this.ore = ore;
    }

    @Override
    public IGeneratorAnimation getAnimation() {
        return animation;
    }

    @Override
    public void setAnimation(IGeneratorAnimation animation) {
        this.animation = animation;
    }

    @Override
    public HashMap<Player, IGenHolo> getPlayerHolograms() {
        return holograms;
    }

    @SuppressWarnings("WeakerAccess")
    public class HoloGram implements IGenHolo {
        String iso;
        IHologram hologram;
        IHoloLine tier, timer, name;
        Player p;

        public HoloGram(Player p) {
            this.p = p;
            this.iso = Language.getPlayerLanguage(p).getIso();

            if (getType() != GeneratorType.EMERALD && getType() != GeneratorType.DIAMOND) return;

            String tierText = Language.getLang(iso).m(Messages.GENERATOR_HOLOGRAM_TIER)
                    .replace("%bw_tier%", Language.getLang(iso).m(Messages.FORMATTING_GENERATOR_TIER1));
            String timerText = Language.getLang(iso).m(Messages.GENERATOR_HOLOGRAM_TIMER)
                    .replace("%bw_seconds%", String.valueOf(lastSpawn));
            String nameText = Language.getLang(iso).m(getOre().getType() == Material.DIAMOND ? Messages.GENERATOR_HOLOGRAM_TYPE_DIAMOND
                    : Messages.GENERATOR_HOLOGRAM_TYPE_EMERALD);
            hologram = BedWars.getAPI().getHologramsUtil().createHologram(p, location.clone().add(0, 0.5, 0), nameText, tierText, timerText);
            hologram.setGap(0.3);

            this.timer = hologram.getLine(0);
            this.tier = hologram.getLine(1);
            this.name = hologram.getLine(2);
        }

        @Override
        public void setTierName(String name) {
            tier.setText(name);
        }

        @Override
        public void setTimerName(String name) {
            timer.setText(name);
        }

        @Override
        public String getIso() {
            return iso;
        }

        @Override
        public Player getPlayer() {
            return p;
        }

        @Override
        public IGenerator getGenerator() {
            return OreGenerator.this;
        }

        @Override
        public void update() {
            hologram.getLines().forEach(IHoloLine::reveal);
        }

        @Override
        public void destroy() {
            tier.remove();
            timer.remove();
            name.remove();
        }
    }

    private static ArmorStand createArmorStand(Location l) {
        ArmorStand a = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        a.setGravity(false);
        if (null != null) {
            a.setCustomName(null);
            a.setCustomNameVisible(true);
        }
        a.setRemoveWhenFarAway(false);
        a.setVisible(false);
        a.setCanPickupItems(false);
        a.setArms(false);
        a.setBasePlate(false);
        a.setMarker(true);
        return a;
    }

    @Override
    public void rotate() {
        if (item == null) return;
        animation.run();
    }

    @Override
    public void setDelay(double delay) {
        this.delay = delay * speedMultiplier;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public ItemStack getOre() {
        return ore;
    }

    @Override
    public void disable() {
        if (getType() == GeneratorType.DIAMOND || getType() == GeneratorType.EMERALD) {
            rotation.remove(this);
            for (IGenHolo a : holograms.values()) {
                a.destroy();
            }
            if (item != null) {
                item.destroy();
                item = null;
            }
            holograms.clear();
        }
        disabled = true;
    }

    @Override
    public void enable() {
        if (getType() == GeneratorType.DIAMOND || getType() == GeneratorType.EMERALD) {
            enableRotation();
        }
        disabled = false;
    }

    @Override
    public void updateHolograms(Player p) {
        if (getType() != GeneratorType.EMERALD && getType() != GeneratorType.DIAMOND) return;
        if (!arena.getWorld().getPlayers().contains(p)) return;

        IGenHolo h = holograms.get(p);
        if (h == null) holograms.put(p, new HoloGram(p));
        h = holograms.get(p);

        h.update();
    }

    @Override
    public void enableRotation() {
        //loadDefaults(false);
        //if (getType() == GeneratorType.EMERALD || getType() == GeneratorType.DIAMOND) {
        rotation.add(this);
        for (Player p : arena.getWorld().getPlayers()) {
            IGenHolo h = holograms.get(p);
            if (h == null) {
                holograms.put(p, new HoloGram(p));
            }
        }
        for (IGenHolo hg : holograms.values()) {
            hg.update();
        }

        this.item = new GeneratorHolder(location.add(0, 0.5, 0), new ItemStack(type == GeneratorType.DIAMOND ? Material.DIAMOND_BLOCK : Material.EMERALD_BLOCK));
        this.animation = BedWars.nms.createDefaultGeneratorAnimation(item.getArmorStand());
        //}
    }

    @Override
    public void setSpawnLimit(int value) {
        this.spawnLimit = value;
    }

    private void loadDefaults() {
        switch (type) {
            case GOLD:
                delay = BedWars.getGeneratorsCfg().getDouble(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_GOLD_DELAY) == null ?
                        "Default." + ConfigPath.GENERATOR_GOLD_DELAY : arena.getGroup() + "." + ConfigPath.GENERATOR_GOLD_DELAY) * speedMultiplier;
                ore = new ItemStack(Material.GOLD_INGOT);
                amount = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_GOLD_AMOUNT) == null ?
                        "Default." + ConfigPath.GENERATOR_GOLD_AMOUNT : arena.getGroup() + "." + ConfigPath.GENERATOR_GOLD_AMOUNT);
                spawnLimit = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_GOLD_SPAWN_LIMIT) == null ?
                        "Default." + ConfigPath.GENERATOR_GOLD_SPAWN_LIMIT : arena.getGroup() + "." + ConfigPath.GENERATOR_GOLD_SPAWN_LIMIT);
                break;
            case IRON:
                delay = BedWars.getGeneratorsCfg().getDouble(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_IRON_DELAY) == null ?
                        "Default." + ConfigPath.GENERATOR_IRON_DELAY : arena.getGroup() + "." + ConfigPath.GENERATOR_IRON_DELAY) * speedMultiplier;
                amount = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_IRON_AMOUNT) == null ?
                        "Default." + ConfigPath.GENERATOR_IRON_AMOUNT : arena.getGroup() + "." + ConfigPath.GENERATOR_IRON_AMOUNT);
                ore = new ItemStack(Material.IRON_INGOT);
                spawnLimit = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_IRON_SPAWN_LIMIT) == null ?
                        "Default." + ConfigPath.GENERATOR_IRON_SPAWN_LIMIT : arena.getGroup() + "." + ConfigPath.GENERATOR_IRON_SPAWN_LIMIT);
                break;
            case DIAMOND:
                delay = BedWars.getGeneratorsCfg().getDouble(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_I_DELAY) == null ?
                        "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_I_DELAY : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_I_DELAY) * speedMultiplier;
                amount = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_I_AMOUNT) == null ?
                        "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_I_AMOUNT : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_I_AMOUNT);
                spawnLimit = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_I_SPAWN_LIMIT) == null ?
                        "Default." + ConfigPath.GENERATOR_DIAMOND_TIER_I_SPAWN_LIMIT : arena.getGroup() + "." + ConfigPath.GENERATOR_DIAMOND_TIER_I_SPAWN_LIMIT);
                ore = new ItemStack(Material.DIAMOND);
                break;
            case EMERALD:
                delay = BedWars.getGeneratorsCfg().getDouble(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_I_DELAY) == null ?
                        "Default." + ConfigPath.GENERATOR_EMERALD_TIER_I_DELAY : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_I_DELAY) * speedMultiplier;
                amount = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_I_AMOUNT) == null ?
                        "Default." + ConfigPath.GENERATOR_EMERALD_TIER_I_AMOUNT : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_I_AMOUNT);
                spawnLimit = BedWars.getGeneratorsCfg().getInt(BedWars.getGeneratorsCfg().getYml().get(arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_I_SPAWN_LIMIT) == null ?
                        "Default." + ConfigPath.GENERATOR_EMERALD_TIER_I_SPAWN_LIMIT : arena.getGroup() + "." + ConfigPath.GENERATOR_EMERALD_TIER_I_SPAWN_LIMIT);
                ore = new ItemStack(Material.EMERALD);
                break;
        }
        lastSpawn = delay;
    }

    @Override
    @Deprecated(since = "1.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
    public ITeam getBwt() {
        return bwt;
    }
    @Override
    public ITeam getBedWarsTeam() {
        return bwt;
    }

    @Override
    public GeneratorHolder getHologramHolder() {
        return item;
    }

    @Override
    public GeneratorType getType() {
        return type;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public double getDelay() {
        return delay;
    }

    @Override
    public double getNextSpawn() {
        return lastSpawn;
    }

    @Override
    public int getSpawnLimit() {
        return spawnLimit;
    }

    @Override
    public void setNextSpawn(double nextSpawn) {
        this.lastSpawn = nextSpawn;
    }

    @Override
    public void setStack(boolean stack) {
        this.stack = stack;
    }

    @Override
    public boolean isStack() {
        return stack;
    }

    @Override
    public void setType(GeneratorType type) {
        this.type = type;
    }

    public void destroyData() {
        rotation.remove(this);
        location = null;
        arena = null;
        ore = null;
        bwt = null;
        holograms = null;
        item = null;
    }
}
