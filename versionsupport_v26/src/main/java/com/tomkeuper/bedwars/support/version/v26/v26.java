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

package com.tomkeuper.bedwars.support.version.v26;

import com.mojang.datafixers.util.Pair;
import com.saicone.rtag.RtagItem;
import com.saicone.rtag.util.OptionalType;
import com.saicone.rtag.util.SkullTexture;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import com.tomkeuper.bedwars.api.arena.shop.ShopHolo;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.arena.team.TeamColor;
import com.tomkeuper.bedwars.api.entity.Despawnable;
import com.tomkeuper.bedwars.api.entity.GeneratorHolder;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.api.server.VersionSupport;
import com.tomkeuper.bedwars.support.version.common.VersionCommon;
import com.tomkeuper.bedwars.support.version.v26.despawnable.DespawnableAttributes;
import com.tomkeuper.bedwars.support.version.v26.despawnable.DespawnableFactory;
import com.tomkeuper.bedwars.support.version.v26.despawnable.DespawnableType;
import com.tomkeuper.bedwars.support.version.v26.hologram.HoloLine;
import com.tomkeuper.bedwars.support.version.v26.hologram.Hologram;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.*;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

public final class v26 extends VersionSupport {

    private final DespawnableFactory despawnableFactory;

    public v26(Plugin plugin, String name) {
        super(plugin, name);
        loadDefaultEffects();
        this.despawnableFactory = new DespawnableFactory(this);
    }

    @Override
    public void registerCommand(String name, Command cmd) {
        ((CraftServer) getPlugin().getServer()).getCommandMap().register(name, cmd);
    }

    @Override
    public void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        p.sendTitle(title == null ? " " : title, subtitle == null ? " " : subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void playAction(Player p, String text) {
        p.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(ChatColor.translateAlternateColorCodes('&', text)
                )
        );
    }

    @Override
    public boolean isBukkitCommandRegistered(String name) {
        return ((CraftServer) getPlugin().getServer()).getCommandMap().getCommand(name) != null;
    }

    @Override
    public org.bukkit.inventory.ItemStack getItemInHand(@NotNull Player p) {
        return p.getInventory().getItemInMainHand();
    }

    @Override
    public void hideEntity(Entity e, Player p) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(e.getEntityId());
        sendPacket(p, packet);
    }

    @Override
    public void fakeDamagePlayer(Player e) {
        Location loc = e.getLocation();
        World world = e.getWorld();
        world.playSound(loc, Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
        ClientboundAnimatePacket anim = new ClientboundAnimatePacket(((CraftPlayer) e).getHandle(), 1);
        for (Player p : e.getWorld().getPlayers()) {
            sendPackets(p, anim);
        }
    }

    @Override
    public boolean isArmor(org.bukkit.inventory.ItemStack itemStack) {
        if(itemStack == null)
            return false;

        return Tag.ITEMS_HEAD_ARMOR.isTagged(itemStack.getType()) || Tag.ITEMS_CHEST_ARMOR.isTagged(itemStack.getType())
                || Tag.ITEMS_LEG_ARMOR.isTagged(itemStack.getType()) || Tag.ITEMS_FOOT_ARMOR.isTagged(itemStack.getType()) || itemStack.getType() == materialElytra();
    }

    @Override
    public boolean isTool(org.bukkit.inventory.ItemStack itemStack) {
        if(itemStack == null)
            return false;

        return itemStack.hasItemMeta();
    }

    @Override
    public boolean isSword(org.bukkit.inventory.ItemStack itemStack) {
        if(itemStack == null)
            return false;

        return Tag.ITEMS_SWORDS.isTagged(itemStack.getType());
    }

    @Override
    public boolean isAxe(org.bukkit.inventory.ItemStack itemStack) {
        var i = getItem(itemStack);
        if (null == i) return false;
        return i instanceof AxeItem;
    }

    @Override
    public boolean isBow(org.bukkit.inventory.ItemStack itemStack) {
        var i = getItem(itemStack);
        if (null == i) return false;
        return i instanceof BowItem;
    }


    @Override
    public boolean isProjectile(org.bukkit.inventory.ItemStack itemStack) {
        var i = getItem(itemStack);
        if (null == i) return false;
        return i instanceof ProjectileItem;
    }

    @Override
    public boolean isInvisibilityPotion(org.bukkit.inventory.@NotNull ItemStack itemStack) {
        if (!itemStack.getType().equals(org.bukkit.Material.POTION)) return false;

        org.bukkit.inventory.meta.PotionMeta pm = (org.bukkit.inventory.meta.PotionMeta) itemStack.getItemMeta();

        return pm != null && pm.hasCustomEffects() && pm.hasCustomEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
    }

    @Override
    public void registerEntities() {

    }

    @Override
    public void spawnShop(Location loc, String name1, Iterable<Player> players, IArena arena) {
        Location l = loc.clone();

        if (l.getWorld() == null) return;
        Villager vlg = (Villager) l.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        vlg.setAI(false);
        vlg.setRemoveWhenFarAway(false);
        vlg.setCollidable(false);
        vlg.setInvulnerable(true);
        vlg.setSilent(true);
    }

    @Override
    public void spawnShopHologram(Location loc, String name1, Iterable<Player> players, ITeam team) {
        HashMap<String, List<Player>> languagePlayers = new HashMap<>();

        for (Player p : players) {
            String iso = Language.getPlayerLanguage(p).getIso();
            if (!languagePlayers.containsKey(iso)) languagePlayers.put(iso, new ArrayList<>());
            languagePlayers.get(iso).add(p);
        }

        for (String iso : languagePlayers.keySet()) {
            Language lang = Language.getLang(iso);
            String[] text = (lang.l(name1) == null || lang.l(name1).isEmpty() ? lang.l(name1.replace(name1.split("\\.")[2], "default")) : lang.l(name1)).toArray(new String[0]);
            IHologram h = createHologram(languagePlayers.get(iso), loc, text);
            new ShopHolo(h, team, iso);
        }
    }

    @Override
    public double getDamage(org.bukkit.inventory.ItemStack i) {
        var tag = getTag(i);
        if (null == tag) {
            throw new RuntimeException("Provided item has no Tag");
        }
        return tag.getIntOr("generic.attackDamage", 0);
    }

    @Override
    public void spawnSilverfish(Location loc, ITeam bedWarsTeam, double speed, double health, int despawn, double damage, int pathFindingTicks) {
        var attr = new DespawnableAttributes(DespawnableType.SILVERFISH, speed, health, damage, despawn);
        var entity = despawnableFactory.spawn(attr, loc, bedWarsTeam, pathFindingTicks);

        new Despawnable(
                entity,
                bedWarsTeam, despawn,
                Messages.SHOP_UTILITY_NPC_SILVERFISH_NAME,
                PlayerKillEvent.PlayerKillCause.SILVERFISH_FINAL_KILL,
                PlayerKillEvent.PlayerKillCause.SILVERFISH
        );
    }

    @Override
    public void spawnIronGolem(Location loc, ITeam bedWarsTeam, double speed, double health, int despawn, int pathFindingTicks) {
        var attr = new DespawnableAttributes(DespawnableType.IRON_GOLEM, speed, health, 4, despawn);
        var entity = despawnableFactory.spawn(attr, loc, bedWarsTeam, pathFindingTicks);
        new Despawnable(
                entity,
                bedWarsTeam, despawn,
                Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME,
                PlayerKillEvent.PlayerKillCause.IRON_GOLEM_FINAL_KILL,
                PlayerKillEvent.PlayerKillCause.IRON_GOLEM
        );
    }

    @Override
    public void minusAmount(Player p, org.bukkit.inventory.@NotNull ItemStack i, int amount) {
        if (i.getAmount() - amount <= 0) {
            if (p.getInventory().getItemInOffHand().equals(i)) {
                p.getInventory().setItemInOffHand(null);
            } else {
                p.getInventory().removeItem(i);
            }
            return;
        }
        i.setAmount(i.getAmount() - amount);
        //noinspection UnstableApiUsage
        p.updateInventory();
    }

    @Override
    public void setSource(TNTPrimed tnt, Player owner) {
        net.minecraft.world.entity.LivingEntity nmsEntityLiving = (((CraftLivingEntity) owner).getHandle());
        PrimedTnt nmsTNT = (((CraftTNTPrimed) tnt).getHandle());
        nmsTNT.owner = nmsEntityLiving != null ? EntityReference.of(nmsEntityLiving) : null;
    }

    @Override
    public void voidKill(Player p) {
        p.damage(1000.0);
    }

    @Override
    public void hideArmor(@NotNull Player victim, Player receiver) {
        List<Pair<EquipmentSlot, ItemStack>> items = new ArrayList<>();
        items.add(new Pair<>(EquipmentSlot.HEAD, new ItemStack(Item.byId(0))));
        items.add(new Pair<>(EquipmentSlot.CHEST, new ItemStack(Item.byId(0))));
        items.add(new Pair<>(EquipmentSlot.LEGS, new ItemStack(Item.byId(0))));
        items.add(new Pair<>(EquipmentSlot.FEET, new ItemStack(Item.byId(0))));
        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(victim.getEntityId(), items);
        sendPacket(receiver, packet);
    }

    @Override
    public void showArmor(Player victim, Player receiver) {
        List<Pair<EquipmentSlot, ItemStack>> items = new ArrayList<>();
        items.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(victim.getInventory().getHelmet())));
        items.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(victim.getInventory().getChestplate())));
        items.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(victim.getInventory().getLeggings())));
        items.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(victim.getInventory().getBoots())));
        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(victim.getEntityId(), items);
        sendPacket(receiver, packet);
    }

    @Override
    public EnderDragon spawnDragon(Location l, ITeam team) {
        if (l == null || l.getWorld() == null) {
            getPlugin().getLogger().log(Level.WARNING, "Could not spawn Dragon. Location is null");
            return null;
        }
        EnderDragon ed = (EnderDragon) l.getWorld().spawnEntity(l, EntityType.ENDER_DRAGON);
        ed.setPhase(EnderDragon.Phase.CIRCLING);
        return ed;
    }

    @Override
    public void colorBed(ITeam team) {
        Location bedLoc = team.getBed();
        if (bedLoc == null) return;
        Material bedMaterial = team.getColor().bedMaterial();
        if (bedMaterial == null) return;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block bedBlock = bedLoc.clone().add(x, 0, z).getBlock();
                BlockData blockData = bedBlock.getBlockData();
                if (!(blockData instanceof org.bukkit.block.data.type.Bed)) continue;
                BlockFace facing = ((org.bukkit.block.data.type.Bed) blockData).getFacing();
                org.bukkit.block.data.type.Bed.Part part = ((org.bukkit.block.data.type.Bed) blockData).getPart();
                bedBlock.setType(bedMaterial, false);
                blockData = bedBlock.getBlockData();
                if (blockData instanceof org.bukkit.block.data.type.Bed bed) {
                    bed.setFacing(facing);
                    bed.setPart(part);
                    bedBlock.setBlockData(bed, false);
                }
            }
        }
    }

    @Override
    public void registerTntWhitelist(float endStoneBlast, float glassBlast) {
        try {
            // blast resistance
            Field field = BlockBehaviour.class.getDeclaredField("explosionResistance");
            field.setAccessible(true);
            // end stone
            field.set(Blocks.END_STONE, endStoneBlast);
            // obsidian
            field.set(Blocks.OBSIDIAN, glassBlast);
            // standard glass
            field.set(Blocks.GLASS, glassBlast);

            var coloredGlass = new net.minecraft.world.level.block.Block[]{
                    Blocks.WHITE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS,
                    Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS,
                    Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS,
                    Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS,

                    Blocks.GLASS,
            };

            Arrays.stream(coloredGlass).forEach(
                    glass -> {
                        try {
                            field.set(glass, glassBlast);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    @Override
    public float getBlastResistance(org.bukkit.block.Block bukkitBlock) {
        try {
            // Convert Bukkit block to NMS Block
            net.minecraft.world.level.block.Block nmsBlock = CraftMagicNumbers.getBlock(bukkitBlock.getType());

            // Access the 'durability' field
            Field durabilityField = BlockBehaviour.class.getDeclaredField("explosionResistance");
            durabilityField.setAccessible(true);

            return durabilityField.getFloat(nmsBlock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return 0; // Default if something fails
    }

    @Override
    public void setBlockTeamColor(Block block, TeamColor teamColor) {
        if (block.getType().toString().contains("STAINED_GLASS") || block.getType().toString().equals("GLASS")) {
            block.setType(teamColor.glassMaterial());
        } else if (block.getType().toString().contains("_TERRACOTTA")) {
            block.setType(teamColor.glazedTerracottaMaterial());
        } else if (block.getType().toString().contains("_WOOL")) {
            block.setType(teamColor.woolMaterial());
        }
    }

    @Override
    public void setCollide(Player p, IArena a, boolean value) {
        p.setCollidable(value);
    }

    @Override
    public org.bukkit.inventory.ItemStack addCustomData(org.bukkit.inventory.ItemStack i, String data) {
        return RtagItem.edit(i, tag -> {
            tag.set(data, VersionSupport.PLUGIN_TAG_GENERIC_KEY);
        });
    }

    @Override
    public org.bukkit.inventory.ItemStack setTag(org.bukkit.inventory.ItemStack itemStack, String key, String value) {
        return RtagItem.edit(itemStack, tag -> {
            tag.set(value, key);
        });
    }

    @Override
    public String getTag(org.bukkit.inventory.ItemStack itemStack, String key) {
        var tag = getTag(itemStack);
        return tag == null ? null : tag.contains(key) ? tag.getStringOr(key, null) : null;
    }

    @Override
    public boolean isCustomBedWarsItem(org.bukkit.inventory.ItemStack i) {
        if (i == null) return false;
        if (i.getType() == org.bukkit.Material.AIR) return false;
        RtagItem rtagItem = new RtagItem(i);
        OptionalType tag = rtagItem.getOptional(VersionSupport.PLUGIN_TAG_GENERIC_KEY);
        return tag.isNotEmpty();
    }

    @Override
    public String getCustomData(org.bukkit.inventory.ItemStack i) {
        RtagItem rtagItem = new RtagItem(i);
        OptionalType tag = rtagItem.getOptional(VersionSupport.PLUGIN_TAG_GENERIC_KEY);
        return (tag.isEmpty() || tag.isNotInstance(String.class)) ? null : tag.asString(null);
    }

    @Override
    public org.bukkit.inventory.ItemStack colourItem(org.bukkit.inventory.ItemStack itemStack, ITeam bedWarsTeam) {
        if (itemStack == null) return null;
        String type = itemStack.getType().toString();
        if (isBed(itemStack.getType())) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().bedMaterial(), itemStack.getAmount());
        } else if (type.contains("_STAINED_GLASS_PANE")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().glassPaneMaterial(), itemStack.getAmount());
        } else if (type.contains("STAINED_GLASS") || type.equals("GLASS")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().glassMaterial(), itemStack.getAmount());
        } else if (type.contains("_TERRACOTTA")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().glazedTerracottaMaterial(), itemStack.getAmount());
        } else if (type.contains("_WOOL")) {
            return new org.bukkit.inventory.ItemStack(bedWarsTeam.getColor().woolMaterial(), itemStack.getAmount());
        }
        return itemStack;
    }

    @Override
    public org.bukkit.inventory.ItemStack createItemStack(String material, int amount, short data) {
        org.bukkit.inventory.ItemStack i;
        try {
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.valueOf(material), amount);
        } catch (Exception ex) {
            getPlugin().getLogger().log(Level.WARNING, material + " is not a valid " + getName() + " material!");
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BEDROCK);
        }
        return i;
    }

    @Override
    public Material materialFireball() {
        return org.bukkit.Material.FIRE_CHARGE;
    }

    @Override
    public org.bukkit.Material materialPlayerHead() {
        return org.bukkit.Material.PLAYER_HEAD;
    }

    @Override
    public org.bukkit.Material materialSnowball() {
        return org.bukkit.Material.SNOWBALL;
    }

    @Override
    public org.bukkit.Material materialGoldenHelmet() {
        return org.bukkit.Material.GOLDEN_HELMET;
    }

    @Override
    public org.bukkit.Material materialGoldenChestPlate() {
        return org.bukkit.Material.GOLDEN_CHESTPLATE;
    }

    @Override
    public org.bukkit.Material materialGoldenLeggings() {
        return org.bukkit.Material.GOLDEN_LEGGINGS;
    }

    @Override
    public org.bukkit.Material materialNetheriteHelmet() {
        return Material.NETHERITE_HELMET;
    }

    @Override
    public org.bukkit.Material materialNetheriteChestPlate() {
        return Material.NETHERITE_CHESTPLATE;
    }

    @Override
    public org.bukkit.Material materialNetheriteLeggings() {
        return Material.NETHERITE_LEGGINGS;
    }

    @Override
    public org.bukkit.Material materialElytra() {
        return Material.ELYTRA;
    }

    @Override
    public org.bukkit.Material materialCake() {
        return org.bukkit.Material.CAKE;
    }

    @Override
    public org.bukkit.Material materialCraftingTable() {
        return org.bukkit.Material.CRAFTING_TABLE;
    }

    @Override
    public org.bukkit.Material materialEnchantingTable() {
        return org.bukkit.Material.ENCHANTING_TABLE;
    }

    @Override
    public org.bukkit.Material materialEndStone() {
        return Material.END_STONE;
    }

    @Override
    public org.bukkit.Material woolMaterial() {
        return org.bukkit.Material.WHITE_WOOL;
    }

    @Override
    public void setJoinSignBackground(BlockState b, Material material) {
        if (b.getBlockData() instanceof WallSign) {
            b.getBlock().getRelative(((WallSign) b.getBlockData()).getFacing().getOppositeFace()).setType(material);
        }
    }

    @Override
    public org.bukkit.inventory.ItemStack redGlassPane(int amount) {
        return new org.bukkit.inventory.ItemStack(Material.RED_STAINED_GLASS_PANE, amount);
    }

    @Override
    public org.bukkit.inventory.ItemStack greenGlassPane(int amount) {
        return new org.bukkit.inventory.ItemStack(Material.LIME_STAINED_GLASS_PANE, amount);
    }

    @Override
    public String getShopUpgradeIdentifier(org.bukkit.inventory.ItemStack itemStack) {
        RtagItem item = new RtagItem(itemStack);
        OptionalType tag = item.getOptional(VersionSupport.PLUGIN_TAG_TIER_KEY);
        return tag.isEmpty() ? "null" : tag.asString("null");
    }

    @Override
    public org.bukkit.inventory.ItemStack setShopUpgradeIdentifier(org.bukkit.inventory.ItemStack itemStack, String identifier) {
        RtagItem item = new RtagItem(itemStack);
        item.set(identifier, VersionSupport.PLUGIN_TAG_TIER_KEY);
        item.load();
        return item.getItem();
    }

    @Override
    public org.bukkit.inventory.ItemStack getPlayerHead(Player player, org.bukkit.inventory.ItemStack copyTagFrom) {
        org.bukkit.inventory.ItemStack head = SkullTexture.getTexturedHead(player.getUniqueId().toString());

        if (copyTagFrom != null) {
            var tag = getTag(copyTagFrom);
            RtagItem rtagItem = new RtagItem(head);
            rtagItem.set(tag, VersionSupport.PLUGIN_TAG_GENERIC_KEY);
            rtagItem.load();
            head = rtagItem.getItem();
        }

        return head;
    }

    @Override
    public void sendPlayerSpawnPackets(Player respawned, IArena arena) {
        if (respawned == null) return;
        if (arena == null) return;
        if (!arena.isPlayer(respawned)) return;

        // if method was used when the player was still in re-spawning screen
        if (arena.getRespawnSessions().containsKey(respawned)) return;

        ServerPlayer entityPlayer = getPlayer(respawned);
        ClientboundAddEntityPacket show = newAddEntityPacket(entityPlayer);
        ClientboundSetEntityMotionPacket playerVelocity = new ClientboundSetEntityMotionPacket(entityPlayer);
        // we send head rotation packet because sometimes on respawn others see him with bad rotation
        ClientboundRotateHeadPacket head = new ClientboundRotateHeadPacket(entityPlayer, getCompressedAngle(entityPlayer.getBukkitYaw()));

        // retrieve current armor and in-hand items
        // we send a packet later for timing issues where other players do not see them
        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> list = getPlayerEquipment(entityPlayer);


        for (Player p : arena.getPlayers()) {
            if (p == null) continue;
            if (p.equals(respawned)) continue;
            // if p is in re-spawning screen continue
            if (arena.getRespawnSessions().containsKey(p)) continue;

            ServerPlayer boundTo = getPlayer(p);
            if (p.getWorld().equals(respawned.getWorld())) {
                if (respawned.getLocation().distance(p.getLocation()) <= arena.getRenderDistance()) {

                    // send respawned player to regular players
                    sendPackets(
                            p, show, head, playerVelocity,
                            new ClientboundSetEquipmentPacket(respawned.getEntityId(), list)
                    );

                    // send nearby players to respawned player
                    // if the player has invisibility hide armor
                    if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        hideArmor(p, respawned);
                    } else {
                        ClientboundAddEntityPacket show2 = newAddEntityPacket(boundTo);

                        ClientboundSetEntityMotionPacket playerVelocity2 = new ClientboundSetEntityMotionPacket(boundTo);
                        ClientboundRotateHeadPacket head2 = new ClientboundRotateHeadPacket(boundTo, getCompressedAngle(boundTo.getBukkitYaw()));
                        sendPackets(respawned, show2, playerVelocity2, head2);
                        showArmor(p, respawned);
                    }
                }
            }
        }

        for (Player spectator : arena.getSpectators()) {
            if (spectator == null) continue;
            if (spectator.equals(respawned)) continue;
            respawned.hidePlayer(getPlugin(), spectator);
            if (spectator.getWorld().equals(respawned.getWorld())) {
                if (respawned.getLocation().distance(spectator.getLocation()) <= arena.getRenderDistance()) {

                    // send respawned player to spectator
                    sendPackets(
                            spectator, show, playerVelocity,
                            new ClientboundSetEquipmentPacket(respawned.getEntityId(), list),
                            new ClientboundRotateHeadPacket(entityPlayer, getCompressedAngle(entityPlayer.getBukkitYaw()))
                    );
                }
            }
        }
    }

    @Override
    public String getInventoryName(@NotNull InventoryEvent e) {
        return e.getView().getTitle();
    }

    @Override
    public void setUnbreakable(ItemMeta itemMeta) {
        itemMeta.setUnbreakable(true);
    }

    @Override
    public int getVersion() {
        return 14;
    }

    @Override
    public void registerVersionListeners() {
        new VersionCommon(this);
    }

    @Override
    public String getMainLevel() {
        //noinspection deprecation
        return ((DedicatedServer) MinecraftServer.getServer()).getProperties().levelName;
    }

    @Override
    public Fireball setFireballDirection(Fireball fireball, Vector vector) {
        fireball.setDirection(vector);
        return fireball;
    }

    @Override
    public void playRedStoneDot(Player player) {
        ClientboundLevelParticlesPacket particlePacket = new ClientboundLevelParticlesPacket(
                DustParticleOptions.REDSTONE,
                true,
                true,
                player.getLocation().getX(),
                player.getLocation().getY() + 2.6,
                player.getLocation().getZ(),
                0, 0, 0, 0, 0
        );
        for (Player p : player.getWorld().getPlayers()) {
            if (p.equals(player)) continue;
            sendPacket(p, particlePacket);
        }
    }

    @Override
    public void clearArrowsFromPlayerBody(Player player) {
        // minecraft clears them on death on newer version
    }

    @Override
    public Block placeTowerBlocks(@NotNull Block b, @NotNull IArena a, @NotNull TeamColor color, int x, int y, int z) {
        b.getRelative(x, y, z).setType(color.woolMaterial());
        a.addPlacedBlock(b.getRelative(x, y, z));
        return b.getRelative(x, y, z);
    }

    @Override
    public Block placeLadder(@NotNull Block b, int x, int y, int z, @NotNull IArena a, int ladderData) {
        Block block = b.getRelative(x, y, z);  //ladder block
        block.setType(Material.LADDER);
        if(block.getBlockData() instanceof Ladder ladder) {
            a.addPlacedBlock(block);
            switch (ladderData) {
                case 2 -> {
                    ladder.setFacing(BlockFace.NORTH);
                    block.setBlockData(ladder);
                }
                case 3 -> {
                    ladder.setFacing(BlockFace.SOUTH);
                    block.setBlockData(ladder);
                }
                case 4 -> {
                    ladder.setFacing(BlockFace.WEST);
                    block.setBlockData(ladder);
                }
                case 5 -> {
                    ladder.setFacing(BlockFace.EAST);
                    block.setBlockData(ladder);
                }
            }
        }
        return b;
    }

    @Override
    public void playVillagerEffect(Player player, Location location) {
        player.spawnParticle(Particle.HAPPY_VILLAGER, location, 1);
    }

    @Override
    public IHologram createHologram(Player p, Location location, String... lines) {
        List<String> linesList = new ArrayList<>(Arrays.asList(lines));
        // holograms are reversed, correcting that here
        Collections.reverse(linesList);
        return new Hologram(p, location, linesList);
    }

    @Override
    public IHologram createHologram(Player p, Location location, IHoloLine... lines) {
        List<IHoloLine> linesList = new ArrayList<>(Arrays.asList(lines));
        // holograms are reversed, correcting that here
        Collections.reverse(linesList);
        return new Hologram(p, linesList, location);
    }

    @Override
    public IHologram createHologram(Iterable<Player> players, Location location, String... lines) {
        List<String> linesList = new ArrayList<>(Arrays.asList(lines));
        // holograms are reversed, correcting that here
        Collections.reverse(linesList);
        return new Hologram(players, linesList, location);
    }

    @Override
    public IHologram createHologram(Iterable<Player> players, Location location, IHoloLine... lines) {
        List<IHoloLine> linesList = new ArrayList<>(Arrays.asList(lines));
        // holograms are reversed, correcting that here
        Collections.reverse(linesList);
        return new Hologram(players, location, linesList);
    }

    @Override
    public IHoloLine lineFromText(String text, @NotNull IHologram hologram) {
        return new HoloLine(text, hologram);
    }

    @Override
    public IGeneratorAnimation createDefaultGeneratorAnimation(ArmorStand armorStand) {
        return new DefaultGenAnimation(armorStand);
    }

    @Override
    public void updatePacketArmorStand(GeneratorHolder gh, Iterable<Player> players) {
        ArmorStand armorStand = gh.getArmorStand();
        net.minecraft.world.entity.decoration.ArmorStand nmsEntity = ((CraftArmorStand) armorStand).getHandle();
        ClientboundAddEntityPacket spawn = newAddEntityPacket(nmsEntity);
        ClientboundSetEntityDataPacket metadata = new ClientboundSetEntityDataPacket(armorStand.getEntityId(), ((CraftArmorStand) armorStand).getHandle().getEntityData().getNonDefaultValues());
        Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> equip = new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(gh.getHelmet()));
        ClientboundSetEquipmentPacket equipment = new ClientboundSetEquipmentPacket(armorStand.getEntityId(), Collections.singletonList(equip));

        for (Player p : players) {
            sendPackets(p, spawn, metadata, equipment);
        }
    }

    @Override
    public void updatePacketArmorStandEquipment(GeneratorHolder generatorHolder) {
        ArmorStand armorStand = generatorHolder.getArmorStand();
        World world = armorStand.getWorld();
        List<Pair<EquipmentSlot, ItemStack>> items = new ArrayList<>();
        items.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(generatorHolder.getHelmet())));
        ClientboundSetEquipmentPacket equipment = new ClientboundSetEquipmentPacket(armorStand.getEntityId(), items);
        ClientboundSetEntityDataPacket metadata = new ClientboundSetEntityDataPacket(armorStand.getEntityId(), ((CraftArmorStand) armorStand).getHandle().getEntityData().getNonDefaultValues());
        for (Player p : world.getPlayers()) {
            sendPackets(p, equipment, metadata);
        }
    }

    @Override
    public void callPlayerDeathEvent(Player player, List<org.bukkit.inventory.ItemStack> drops, int droppedExp, int newLevel, String deathMessage) {
        DamageSource ds = DamageSource.builder(DamageType.GENERIC).build();
        PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, ds, drops, droppedExp, newLevel, deathMessage);
        Bukkit.getPluginManager().callEvent(deathEvent);
    }

    @Override
    public float getAbsorption(Player player) {
        return (float) player.getAbsorptionAmount();
    }

    @Override
    public void destroyPacketArmorStand(GeneratorHolder generatorHolder, Iterable<Player> players) {
        ArmorStand armorStand = generatorHolder.getArmorStand();
        ClientboundRemoveEntitiesPacket destroy = new ClientboundRemoveEntitiesPacket(armorStand.getEntityId());
        for (Player p : players) {
            sendPacket(p, destroy);
        }
    }

    @Override
    public ArmorStand createPacketArmorStand(Location loc, Iterable<Player> players) {
        if (loc.getWorld() == null) throw new RuntimeException("World of a location should not be null.");
        net.minecraft.world.entity.decoration.ArmorStand nmsEntity = new net.minecraft.world.entity.decoration.ArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
        nmsEntity.setPos(loc.getX(), loc.getY(), loc.getZ());
        ClientboundAddEntityPacket spawn = newAddEntityPacket(nmsEntity);

        for (Player p : players) {
            sendPacket(p, spawn);
        }
        return new CraftArmorStand((CraftServer) getPlugin().getServer(), nmsEntity);
    }

    public static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public static void sendPackets(Player player, Packet<?> @NotNull ... packets) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        for (Packet<?> p : packets) {
            connection.send(p);
        }
    }

    /**
     * Gets the NMS Item from ItemStack
     */
    private @Nullable Item getItem(org.bukkit.inventory.ItemStack itemStack) {
        var i = CraftItemStack.asNMSCopy(itemStack);
        if (null == i) {
            return null;
        }
        return i.getItem();
    }

    private @Nullable CompoundTag getTag(@NotNull org.bukkit.inventory.ItemStack itemStack) {
        RtagItem rtagItem = new RtagItem(itemStack);
        return (CompoundTag) rtagItem.getTag();
    }

    public ServerPlayer getPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> getPlayerEquipment(@NotNull ServerPlayer entityPlayer) {
        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EquipmentSlot.MAINHAND, entityPlayer.getItemBySlot(EquipmentSlot.MAINHAND)));
        list.add(new Pair<>(EquipmentSlot.OFFHAND, entityPlayer.getItemBySlot(EquipmentSlot.OFFHAND)));
        list.add(new Pair<>(EquipmentSlot.HEAD, entityPlayer.getItemBySlot(EquipmentSlot.HEAD)));
        list.add(new Pair<>(EquipmentSlot.CHEST, entityPlayer.getItemBySlot(EquipmentSlot.CHEST)));
        list.add(new Pair<>(EquipmentSlot.LEGS, entityPlayer.getItemBySlot(EquipmentSlot.LEGS)));
        list.add(new Pair<>(EquipmentSlot.FEET, entityPlayer.getItemBySlot(EquipmentSlot.FEET)));

        return list;
    }

    public static ClientboundAddEntityPacket newAddEntityPacket(net.minecraft.world.entity.Entity nmsEntity) {
        return new ClientboundAddEntityPacket(
                nmsEntity.getId(),
                nmsEntity.getUUID(),
                nmsEntity.getX(),
                nmsEntity.getY(),
                nmsEntity.getZ(),
                nmsEntity.getXRot(),
                nmsEntity.getYRot(),
                nmsEntity.getType(),
                0,
                nmsEntity.getDeltaMovement(),
                nmsEntity.getYHeadRot()
        );
    }
}
