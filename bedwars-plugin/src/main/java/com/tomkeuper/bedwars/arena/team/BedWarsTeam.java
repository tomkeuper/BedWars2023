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

package com.tomkeuper.bedwars.arena.team;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.generator.IGenHolo;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.arena.shop.ShopHolo;
import com.tomkeuper.bedwars.api.arena.team.IBedHolo;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.arena.team.TeamColor;
import com.tomkeuper.bedwars.api.arena.team.TeamEnchant;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.events.player.PlayerFirstSpawnEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.region.Cuboid;
import com.tomkeuper.bedwars.api.upgrades.EnemyBaseEnterTrap;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.OreGenerator;
import com.tomkeuper.bedwars.configuration.Sounds;
import com.tomkeuper.bedwars.listeners.chat.ChatFormatting;
import com.tomkeuper.bedwars.shop.ShopCache;
import com.tomkeuper.bedwars.support.paper.PaperSupport;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.tomkeuper.bedwars.BedWars.*;
import static com.tomkeuper.bedwars.api.language.Language.getMsg;

@SuppressWarnings("WeakerAccess")
public class BedWarsTeam implements ITeam {

    @Getter
    private List<Player> members = new ArrayList<>();

    @Getter
    private TeamColor color;

    @Getter
    private Location spawn, bed, shop, teamUpgrades;

    // private IGenerator ironGenerator = null, goldGenerator = null, emeraldGenerator = null;
    @Getter
    private String name;

    @Getter
    private Arena arena;

    @Getter
    private boolean bedDestroyed = false;
    private boolean shopSpawned = false;
    private Player bedDestroyer = null;
    private Vector killDropsLoc = null;

    // team generators
    private final List<IGenerator> generators = new ArrayList<>();

    // team upgrade name, tier
    private final ConcurrentHashMap<String, Integer> teamUpgradeList = new ConcurrentHashMap<>();

    // Potion effects for teammates from the upgrades
    @Getter
    private List<PotionEffect> teamEffects = new ArrayList<>();

    // Potion effects for teammates on base only
    private List<PotionEffect> base = new ArrayList<>();

    // Enchantments for bows
    @Getter
    private List<TeamEnchant> bowsEnchantments = new ArrayList<>();

    // Enchantments for swords
    @Getter
    private List<TeamEnchant> swordsEnchantments = new ArrayList<>();

    // Enchantments for armors
    @Getter
    private List<TeamEnchant> armorsEnchantments = new ArrayList<>();

    // Used for show/ hide bed hologram
    @Getter
    private final HashMap<String, IBedHolo> beds = new HashMap<>();

    // Queued traps
    private final LinkedList<EnemyBaseEnterTrap> enemyBaseEnterTraps = new LinkedList<>();

    // Amount of dragons for Sudden Death phase
    @Getter
    private int dragonAmount = 1;

    // Player cache, used for losers stats and rejoin
    private List<EnderDragon> dragonEntities = new ArrayList<>();

    // Player cache, used for losers stats and rejoin
    @Getter
    private List<Player> membersCache = new ArrayList<>();

    // Invulnerability at re-spawn
    // Fall invulnerability when teammates respawn
    public static HashMap<UUID, Long> reSpawnInvulnerability = new HashMap<>();

    public BedWarsTeam(String name, TeamColor color, Location spawn, Location bed, Location shop, Location teamUpgrades, Arena arena) {
        if (arena == null) return;
        this.name = name;
        this.color = color;
        this.spawn = spawn;
        this.bed = bed;
        this.arena = arena;
        this.shop = shop;
        this.teamUpgrades = teamUpgrades;
        Language.saveIfNotExists(ConfigPath.TEAM_NAME_PATH.replace("{arena}", getArena().getArenaName()).replace("{team}", getName()), name);
        arena.getRegionsList().add(new Cuboid(spawn, arena.getConfig().getInt(ConfigPath.ARENA_SPAWN_PROTECTION), true));

        Location drops = getArena().getConfig().getArenaLoc("Team." + getName() + "." + ConfigPath.ARENA_TEAM_KILL_DROPS_LOC);
        if (drops != null) {
            setKillDropsLocation(drops);
        }
    }

    public int getSize() {
        return members.size();
    }

    /**
     * Add a new member to the team
     */
    public void addPlayers(Player... players) {
        if (players == null) return;
        for (Player p : players) {
            if (p == null) continue;
            String iso = Language.getPlayerLanguage(p).getIso();
            members.removeIf(player -> player.getUniqueId().equals(p.getUniqueId()));
            members.add(p);

            membersCache.removeIf(player -> player.getUniqueId().equals(p.getUniqueId()));
            membersCache.add(p);

            IBedHolo holo = beds.get(iso);
            if (holo == null) holo = new BedHolo(iso, arena, this);
            else holo.show(p);

            if (holo.getHologram() != null) holo.getHologram().addPlayer(p);
        }
    }

    /**
     * first spawn
     */
    public void firstSpawn(Player p) {
        if (p == null) return;
        PaperSupport.teleportC(p, spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
        p.setGameMode(GameMode.SURVIVAL);
        p.setCanPickupItems(true);
        nms.setCollide(p, getArena(), true);
        sendDefaultInventory(p, true);
        Bukkit.getPluginManager().callEvent(new PlayerFirstSpawnEvent(p, getArena(), this));
    }

    /**
     * Spawn shopkeepers for target team (if enabled).
     */
    public void spawnNPCs() {
        if (getMembers().isEmpty() && getArena().getConfig().getBoolean(ConfigPath.ARENA_DISABLE_NPCS_FOR_EMPTY_TEAMS)) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            nms.colorBed(this);
            nms.spawnShop(arena.getConfig().getArenaLoc("Team." + getName() + ".Upgrade"), (arena.getMaxInTeam() > 1 ? Messages.NPC_NAME_TEAM_UPGRADES.replace("%group%", arena.getGroup()) : Messages.NPC_NAME_SOLO_UPGRADES.replace("%group%", arena.getGroup())), arena.getPlayers(), arena);
            nms.spawnShop(arena.getConfig().getArenaLoc("Team." + getName() + ".Shop"), (arena.getMaxInTeam() > 1 ? Messages.NPC_NAME_TEAM_SHOP.replace("%group%", arena.getGroup()) : Messages.NPC_NAME_SOLO_SHOP.replace("%group%", arena.getGroup())), arena.getPlayers(), arena);
            nms.spawnShopHologram(arena.getConfig().getArenaLoc("Team." + getName() + ".Upgrade"), (arena.getMaxInTeam() > 1 ? Messages.NPC_NAME_TEAM_UPGRADES.replace("%group%", arena.getGroup()) : Messages.NPC_NAME_SOLO_UPGRADES.replace("%group%", arena.getGroup())), arena.getPlayers(), this);
            nms.spawnShopHologram(arena.getConfig().getArenaLoc("Team." + getName() + ".Shop"), (arena.getMaxInTeam() > 1 ? Messages.NPC_NAME_TEAM_SHOP.replace("%group%", arena.getGroup()) : Messages.NPC_NAME_SOLO_SHOP).replace("%group%", arena.getGroup()), arena.getPlayers(), this);
        }, 20L);

        Cuboid c1 = new Cuboid(arena.getConfig().getArenaLoc("Team." + getName() + ".Upgrade"), arena.getConfig().getInt(ConfigPath.ARENA_UPGRADES_PROTECTION), true);
        c1.setMinY(c1.getMinY() - 1);
        c1.setMaxY(c1.getMaxY() + 4);
        arena.getRegionsList().add(c1);

        Cuboid c2 = new Cuboid(arena.getConfig().getArenaLoc("Team." + getName() + ".Shop"), arena.getConfig().getInt(ConfigPath.ARENA_SHOP_PROTECTION), true);
        c2.setMinY(c2.getMinY() - 1);
        c2.setMaxY(c2.getMaxY() + 4);
        arena.getRegionsList().add(c2);
        shopSpawned = true;
    }

    /**
     * Rejoin a team
     */
    public void reJoin(@NotNull Player p) {
        reJoin(p, BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_REJOIN_RE_SPAWN_COUNTDOWN));
    }

    public void reJoin(@NotNull Player p, int respawnTime) {
        addPlayers(p);
        arena.startReSpawnSession(p, respawnTime);
    }

    /**
     * Gives the start inventory
     */
    public void sendDefaultInventory(Player p, boolean clean) {
        if (clean) p.getInventory().clear();
        String path = config.getYml().get(ConfigPath.GENERAL_CONFIGURATION_DEFAULT_ITEMS + "." + arena.getGroup()) == null ?
                ConfigPath.GENERAL_CONFIGURATION_DEFAULT_ITEMS + ".Default" : ConfigPath.GENERAL_CONFIGURATION_DEFAULT_ITEMS + "." + arena.getGroup();
        for (String s : config.getYml().getStringList(path)) {
            String[] parm = s.split(",");
            if (parm.length != 0) {
                try {
                    ItemStack i;
                    if (parm.length > 1) {
                        try {
                            Integer.parseInt(parm[1]);
                        } catch (Exception ex) {
                            plugin.getLogger().severe(parm[1] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        i = new ItemStack(Material.valueOf(parm[0]), Integer.parseInt(parm[1]));
                    } else {
                        i = new ItemStack(Material.valueOf(parm[0]));
                    }
                    if (parm.length > 2) {
                        try {
                            Integer.parseInt(parm[2]);
                        } catch (Exception ex) {
                            plugin.getLogger().severe(parm[2] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        i.setAmount(Integer.parseInt(parm[2]));
                    }
                    ItemMeta im = i.getItemMeta();
                    if (parm.length > 3) {
                        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', parm[3]));
                    }
                    nms.setUnbreakable(im);
                    i.setItemMeta(im);
                    i = nms.addCustomData(i, "DEFAULT_ITEM");

                    if (BedWars.nms.isSword(i)) {
                        boolean hasSword = false;
                        for (ItemStack item : p.getInventory().getContents()) {
                            if (item == null) continue;
                            if (item.getType() == Material.AIR) continue;
                            if (BedWars.nms.isSword(item)) {
                                hasSword = true;
                                break;
                            }
                        }
                        if (!hasSword) {
                            p.getInventory().addItem(i);
                        }
                    } else if (BedWars.nms.isBow(i)) {
                        boolean hasBow = false;
                        for (ItemStack item : p.getInventory().getContents()) {
                            if (item == null) continue;
                            if (item.getType() == Material.AIR) continue;
                            if (BedWars.nms.isBow(item)) {
                                hasBow = true;
                                break;
                            }
                        }
                        if (!hasBow) {
                            p.getInventory().addItem(i);
                        }
                    } else {
                        p.getInventory().addItem(i);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        sendArmor(p);
    }

    public void defaultSword(Player p, boolean sword) {
        if (!sword) return;
        String path = config.getYml().get(ConfigPath.GENERAL_CONFIGURATION_DEFAULT_ITEMS + "." + arena.getGroup()) == null ?
                ConfigPath.GENERAL_CONFIGURATION_DEFAULT_ITEMS + ".Default" : ConfigPath.GENERAL_CONFIGURATION_DEFAULT_ITEMS + "." + arena.getGroup();
        for (String s : config.getYml().getStringList(path)) {
            String[] parm = s.split(",");
            if (parm.length != 0) {
                try {
                    ItemStack i;
                    if (parm.length > 1) {
                        try {
                            Integer.parseInt(parm[1]);
                        } catch (Exception ex) {
                            plugin.getLogger().severe(parm[1] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        i = new ItemStack(Material.valueOf(parm[0]), Integer.parseInt(parm[1]));
                    } else {
                        i = new ItemStack(Material.valueOf(parm[0]));
                    }
                    if (parm.length > 2) {
                        try {
                            Integer.parseInt(parm[2]);
                        } catch (Exception ex) {
                            plugin.getLogger().severe(parm[2] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        i.setAmount(Integer.parseInt(parm[2]));
                    }
                    ItemMeta im = i.getItemMeta();
                    if (parm.length > 3) {
                        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', parm[3]));
                    }
                    for (TeamEnchant e : getSwordsEnchantments()) {
                        im.addEnchant(e.getEnchantment(), e.getAmplifier(),true);
                    }
                    nms.setUnbreakable(im);
                    i.setItemMeta(im);

                    i = nms.addCustomData(i, "DEFAULT_ITEM");

                    if (BedWars.nms.isSword(i)) {
                        p.getInventory().addItem(i);
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Spawn iron and gold generators
     */
    public void spawnGenerators() {
        for (String type : new String[]{"Iron", "Gold"}) {
            GeneratorType gt = GeneratorType.valueOf(type.toUpperCase());
            List<Location> locs = new ArrayList<>();
            Object o = getArena().getConfig().getYml().get("Team." + getName() + "." + type);
            if (o instanceof String) {
                locs.add(getArena().getConfig().getArenaLoc("Team." + getName() + "." + type));
            } else {
                locs = getArena().getConfig().getArenaLocations("Team." + getName() + "." + type);
            }
            for (Location loc : locs) {
                IGenerator gen = new OreGenerator(loc, getArena(), gt, this, false);
                //getArena().getOreGenerators().add(gen);
                generators.add(gen);
            }
        }
    }

    /**
     * Respawn a member
     */
    public void respawnMember(@NotNull Player p) {
        if (reSpawnInvulnerability.containsKey(p.getUniqueId())) {
            reSpawnInvulnerability.replace(p.getUniqueId(), System.currentTimeMillis() + config.getInt(ConfigPath.GENERAL_CONFIGURATION_RE_SPAWN_INVULNERABILITY));
        } else {
            reSpawnInvulnerability.put(p.getUniqueId(), System.currentTimeMillis() + config.getInt(ConfigPath.GENERAL_CONFIGURATION_RE_SPAWN_INVULNERABILITY));
        }
        p.setFallDistance(0f);
        PaperSupport.teleportC(p, getSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        p.setVelocity(new Vector(0, 0, 0));
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        nms.setCollide(p, arena, true);
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setHealth(20);
        p.setFireTicks(0);

        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            getArena().getRespawnSessions().remove(p); //Fixes https://github.com/andrei1058/BedWars1058/issues/669

            for (Player inGame : arena.getPlayers()){
                if (inGame.equals(p)) continue;
                BedWars.nms.spigotShowPlayer(p, inGame);
                BedWars.nms.spigotShowPlayer(inGame, p);
            }
            for (Player spectator : arena.getSpectators()){
                BedWars.nms.spigotShowPlayer(p, spectator);
            }
        }, 8L);

        nms.sendTitle(p, getMsg(p, Messages.PLAYER_DIE_RESPAWNED_TITLE), "", 0, 20, 10);
        BedWars.plugin.adventure().player(p).sendMessage(ChatFormatting.parseLegacyMini(getMsg(p, Messages.PLAYER_DIE_RESPAWNED_TEXT)));

        sendDefaultInventory(p, false);
        ShopCache sc = ShopCache.getInstance().getShopCache(p.getUniqueId());
        if (sc != null) {
            sc.managePermanentsAndDowngradables(getArena());
        }
        p.setHealth(20);
        if (!getBaseEffects().isEmpty()) {
            for (PotionEffect ef : getBaseEffects()) {
                p.addPotionEffect(ef, true);
            }
        }
        if (!getTeamEffects().isEmpty()) {
            for (PotionEffect ef : getTeamEffects()) {
                p.addPotionEffect(ef, true);
            }
        }
        if (!getBowsEnchantments().isEmpty()) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null) continue;
                if (i.getType() == Material.BOW) {
                    ItemMeta im = i.getItemMeta();
                    for (TeamEnchant e : getBowsEnchantments()) {
                        im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                    }
                    i.setItemMeta(im);
                }
                p.updateInventory();
            }
        }
        if (!getSwordsEnchantments().isEmpty()) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null) continue;
                if (nms.isSword(i)) {
                    ItemMeta im = i.getItemMeta();
                    for (TeamEnchant e : getSwordsEnchantments()) {
                        im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                    }
                    i.setItemMeta(im);
                }
                p.updateInventory();
            }
        }
        if (!getArmorsEnchantments().isEmpty()) {
            for (ItemStack i : p.getInventory().getArmorContents()) {
                if (i == null) continue;
                if (nms.isArmor(i)) {
                    ItemMeta im = i.getItemMeta();
                    for (TeamEnchant e : getArmorsEnchantments()) {
                        im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                    }
                    i.setItemMeta(im);
                }
                p.updateInventory();
            }
        }
        Bukkit.getPluginManager().callEvent(new PlayerReSpawnEvent(p, getArena(), this));
        nms.sendPlayerSpawnPackets(p, getArena());
        for (Player aPlayer : arena.getWorld().getPlayers()) {
            BedWars.nms.spigotShowPlayer(p, aPlayer);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (getArena() != null) {
                nms.sendPlayerSpawnPackets(p, getArena());

                // #274
                for (Player on : getArena().getShowTime().keySet()) {
                    BedWars.nms.hideArmor(on, p);
                }
            }
            //
        }, 10L);


        String iso = Language.getPlayerLanguage(p).getIso();
        List<ShopHolo> shopHolos = getArena().getShopHolograms(iso);
        if (shopHolos != null) {
            for (ShopHolo holo : getArena().getShopHolograms(iso)) {
                holo.update(p);
            }
        }

        for (IGenerator gen : getArena().getOreGenerators()) {
            IGenHolo h = gen.getLanguageHolograms().get(iso);
            if (h != null) h.update(p);
        }
        IBedHolo bedHolo = getBedHologram(iso);
        if (bedHolo != null) bedHolo.show(p);

        Sounds.playSound("player-re-spawn", p);
        for (Player worldPlayer : arena.getWorld().getPlayers()) {
            if (worldPlayer == p) continue;
            if (getArena().isReSpawning(worldPlayer)) continue;
            if (getArena().getShowTime().get(worldPlayer) != null) continue;
            if (getArena().isSpectator(worldPlayer)) continue;
            BedWars.nms.spigotShowPlayer(worldPlayer, p);
        }

        for (Player worldPlayer : arena.getWorld().getPlayers()) {
            if (worldPlayer == p) continue;
            BedWars.nms.spigotShowPlayer(p, worldPlayer);
        }
    }

    /**
     * Create a leather armor with team's color
     */
    private ItemStack createArmor(Material material) {
        ItemStack i = new ItemStack(material);
        LeatherArmorMeta lam = (LeatherArmorMeta) i.getItemMeta();
        lam.setColor(color.bukkitColor());
        nms.setUnbreakable(lam);
        i.setItemMeta(lam);
        return i;
    }

    /**
     * Equip a player with default armor
     */
    public void sendArmor(Player p) {
        if (p.getInventory().getHelmet() == null || p.getInventory().getHelmet().getType() == Material.AIR)
            p.getInventory().setHelmet(createArmor(Material.LEATHER_HELMET));
        if (p.getInventory().getChestplate() == null || p.getInventory().getChestplate().getType() == Material.AIR)
            p.getInventory().setChestplate(createArmor(Material.LEATHER_CHESTPLATE));
        if (p.getInventory().getLeggings() == null || p.getInventory().getLeggings().getType() == Material.AIR)
            p.getInventory().setLeggings(createArmor(Material.LEATHER_LEGGINGS));
        if (p.getInventory().getBoots() == null || p.getInventory().getBoots().getType() == Material.AIR)
            p.getInventory().setBoots(createArmor(Material.LEATHER_BOOTS));
    }

    /**
     * Creates a hologram on the team bed's per player
     */
    @SuppressWarnings("WeakerAccess")
    public class BedHolo implements IBedHolo {

        private final String iso;

        private final ITeam team;
        private IHologram h;
        private IHoloLine line;
        @Getter
        private Arena arena;
        @Getter
        private boolean hidden = false;

        public BedHolo(@NotNull String iso, @NotNull Arena arena, @NotNull ITeam team) {
            this.iso = iso;
            this.arena = arena;
            this.team = team;
            create();
        }

        public void create() {
            if (!arena.getConfig().getBoolean(ConfigPath.ARENA_USE_BED_HOLO)) return;
            // Note: Getting location after retrieving the block will make sure the hologram location will always be relative to the block instead of the actual config entry.
            h = BedWars.hologramManager.createHologram(team.getMembers(), getBed().getBlock().getLocation().clone().add(0.5, -0.3, 0.5), "");
            line = h.getLine(0);

            Language lang = Language.getLang(iso);
            if (line == null) return;
            if (isBedDestroyed()) {
                line.setText(lang.m(Messages.BED_HOLOGRAM_DESTROYED));
                bedDestroyed = true;
            } else line.setText(lang.m(Messages.BED_HOLOGRAM_DEFEND));
            beds.put(iso, this);
        }

        public void hide() {
            if (!arena.getConfig().getBoolean(ConfigPath.ARENA_USE_BED_HOLO)) return;
            hidden = true;
            line.remove();
        }

        @Override
        public void hide(Player player) {
            if (!arena.getConfig().getBoolean(ConfigPath.ARENA_USE_BED_HOLO)) return;
            hidden = true;
            line.remove(player);
        }

        public void destroy() {
            if (!arena.getConfig().getBoolean(ConfigPath.ARENA_USE_BED_HOLO)) return;
            h.remove();
            beds.remove(iso);
        }

        @Override
        public void remove(Player player) {
            if (!arena.getConfig().getBoolean(ConfigPath.ARENA_USE_BED_HOLO)) return;
            h.removePlayer(player);
        }

        public void show() {
            if (!arena.getConfig().getBoolean(ConfigPath.ARENA_USE_BED_HOLO)) return;
            hidden = false;
            line.reveal();

            Language lang = Language.getLang(iso);
            if (isBedDestroyed()) line.setText(lang.m(Messages.BED_HOLOGRAM_DESTROYED));
            else line.setText(lang.m(Messages.BED_HOLOGRAM_DEFEND));
        }

        @Override
        public void show(Player player) {
            if (!arena.getConfig().getBoolean(ConfigPath.ARENA_USE_BED_HOLO)) return;
            hidden = false;
            line.reveal(player);

            Language lang = Language.getLang(iso);
            if (isBedDestroyed()) line.setText(lang.m(Messages.BED_HOLOGRAM_DESTROYED));
            else line.setText(lang.m(Messages.BED_HOLOGRAM_DEFEND));
        }

        @Override
        public void update() {
            h.update();
        }

        @Override
        public void update(Player player) {
            h.update(player);
        }

        public IHologram getHologram() {
            return h;
        }

        public boolean isBedDestroyed()  {
            return team.isBedDestroyed();
        }

    }

    /**
     * Used when someone buys a new potion effect with apply == members
     */
    public void addTeamEffect(PotionEffectType pef, int amp, int duration) {
        getTeamEffects().add(new PotionEffect(pef, duration, amp));
        for (Player p : getMembers()) {
            p.addPotionEffect(new PotionEffect(pef, duration, amp), true);
        }
    }

    /**
     * Used when someone buys a new potion effect with apply == base
     */
    public void addBaseEffect(PotionEffectType pef, int amp, int duration) {
        getBaseEffects().add(new PotionEffect(pef, duration, amp));
        for (Player p : new ArrayList<>(getMembers())) {
            if (p.getLocation().distance(getBed()) <= getArena().getIslandRadius()) {
                for (PotionEffect e : getBaseEffects()) {
                    p.addPotionEffect(e, true);
                }
            }
        }
    }

    /**
     * Used when someone buys a bew enchantment with apply == bow
     */
    public void addBowEnchantment(Enchantment e, int a) {
        getBowsEnchantments().add(new Enchant(e, a));
        for (Player p : getMembers()) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null) continue;
                if (i.getType() == Material.BOW) {
                    ItemMeta im = i.getItemMeta();
                    im.addEnchant(e, a, true);
                    i.setItemMeta(im);
                }
            }
            p.updateInventory();
        }
    }

    /**
     * Used when someone buys a new enchantment with apply == sword
     */
    public void addSwordEnchantment(Enchantment e, int a) {
        getSwordsEnchantments().add(new Enchant(e, a));
        for (Player p : getMembers()) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null) continue;
                if (nms.isSword(i) || nms.isAxe(i)) {
                    ItemMeta im = i.getItemMeta();
                    im.addEnchant(e, a, true);
                    i.setItemMeta(im);
                }
            }
            p.updateInventory();
        }
    }

    /**
     * Used when someone buys a new enchantment with apply == armor
     */
    public void addArmorEnchantment(Enchantment e, int a) {
        getArmorsEnchantments().add(new Enchant(e, a));
        for (Player p : getMembers()) {
            for (ItemStack i : p.getInventory().getArmorContents()) {
                if (i == null) continue;
                if (nms.isArmor(i)) {
                    ItemMeta im = i.getItemMeta();
                    im.addEnchant(e, a, true);
                    i.setItemMeta(im);
                }
            }
            p.updateInventory();
        }

        // #274
        Bukkit.getScheduler().runTaskLater(BedWars.plugin, () -> {
            for (Player m : getMembers()) {
                if (m.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    for (Player p : getArena().getPlayers()) {
                        BedWars.nms.hideArmor(m, p);
                    }
                    for (Player p : getArena().getSpectators()) {
                        BedWars.nms.hideArmor(m, p);
                    }
                }
            }
        }, 20L);
    }

    /**
     * Enchantments for bows, swords and armors from the team upgrades
     */
    @Getter
    public static class Enchant implements TeamEnchant {
        Enchantment enchantment;
        int amplifier;

        public Enchant(Enchantment enchantment, int amplifier) {
            this.enchantment = enchantment;
            this.amplifier = amplifier;
        }

    }

    public boolean isMember(Player u) {
        if (u == null) return false;
        return members.contains(u);
    }

    @Override
    public boolean isShopSpawned() {
        return shopSpawned;
    }

    /**
     * Getter, setter etc.
     */
    public boolean wasMember(UUID u) {
        if (u == null) return false;
        for (Player p : membersCache) {
            if (p.getUniqueId().equals(u)) return true;
        }
        return false;
    }

    @Override
    public String getDisplayName(Language language) {
        String m = language.m(ConfigPath.TEAM_NAME_PATH.replace("{arena}", getArena().getArenaName()).replace("{team}", getName()));
        return m == null ? getName() : m;
    }

    @Override
    public ConcurrentHashMap<String, Integer> getTeamUpgradeTiers() {
        return teamUpgradeList;
    }

    @Override
    public IBedHolo getBedHologram(@NotNull String iso) {
        return beds.get(iso);
    }

    /**
     * Destroy the bed for a team.
     */
    public void setBedDestroyed(boolean bedDestroyed) {
        this.bedDestroyed = bedDestroyed;
        if (!bedDestroyed) {
            if (!getBed().getBlock().getType().toString().contains("BED")) {
                BedWars.plugin.getLogger().severe("Bed not set for team: " + getName() + " in arena: " + getArena().getArenaName());
                return;
            }
            nms.colorBed(this);
        } else {
            bed.getBlock().setType(Material.AIR);
        }
        for (IBedHolo bh : beds.values()) {
            bh.hide();
            bh.show();
        }

    }

    @Override
    public List<IGenerator> getGenerators() {
        return generators;
    }


    public List<PotionEffect> getBaseEffects() {
        return base;
    }

    @Override
    public void setDragonAmount(int amount) {
        this.dragonAmount = amount;
    }

    /**
     * Add assign a dragon to the team.
     */
    @Override
    public void addDragon(EnderDragon dragon){
        dragonEntities.add(dragon);
    }

    /**
     * Add assign a dragon to the team.
     */
    @Override
    public List<EnderDragon> getDragons(){
        return dragonEntities;
    }


    public void destroyData() {
        members = null;
        spawn = null;
        bed = null;
        shop = null;
        teamUpgrades = null;
        for (IGenerator ig : new ArrayList<>(generators)) {
            ig.destroyData();
        }
        arena = null;
        teamEffects = null;
        base = null;
        bowsEnchantments = null;
        swordsEnchantments = null;
        armorsEnchantments = null;
        enemyBaseEnterTraps.clear();
        membersCache = null;
        dragonEntities = null;
        bedDestroyer = null;
    }

    @Override
    public void destroyBedHolo(@NotNull Player player) {
        String iso = Language.getPlayerLanguage(player).getIso();
        IBedHolo bedHolo = getBedHologram(iso);
        if (bedHolo != null) bedHolo.remove(player);
    }

    @Override
    public void destroyBedHolo(String iso) {
        IBedHolo bedHolo = getBedHologram(iso);
        if (bedHolo != null) bedHolo.destroy();
    }

    @Override
    public LinkedList<EnemyBaseEnterTrap> getActiveTraps() {
        return enemyBaseEnterTraps;
    }

    @Override
    public Vector getKillDropsLocation() {
        if (killDropsLoc == null) {
            List<IGenerator> gen = generators.stream().filter(p -> (p.getType() == GeneratorType.IRON || p.getType() == GeneratorType.GOLD)).collect(Collectors.toList());
            if (gen.isEmpty()) return new Vector(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ());
            return new Vector(gen.get(0).getLocation().getX(), gen.get(0).getLocation().getY(), gen.get(0).getLocation().getZ());
        }
        return killDropsLoc;
    }

    @Override
    public void setKillDropsLocation(Vector loc) {
        if (loc == null) {
            this.killDropsLoc = null;
            return;
        }
        this.killDropsLoc = new Vector(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
    }

    public void setKillDropsLocation(Location loc) {
        if (loc == null) {
            this.killDropsLoc = null;
            return;
        }
        this.killDropsLoc = new Vector(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
    }

    @Override
    public boolean isBed(@NotNull Location location) {
        for (int x = location.getBlockX() - 1; x < location.getBlockX() + 1; x++) {
            for (int z = location.getBlockZ() - 1; z < location.getBlockZ() + 1; z++) {
                if (getBed().getBlockX() == x && getBed().getBlockY() == location.getBlockY() && getBed().getBlockZ() == z) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setBedDestroyer(Player bedDestroyer) {
        this.bedDestroyer = bedDestroyer;
    }

    @Override
    public Player getBedDestroyer() {
        return bedDestroyer;
    }
}
