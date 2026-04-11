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

package com.tomkeuper.bedwars.api.server;

import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.generator.IGeneratorAnimation;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.arena.team.TeamColor;
import com.tomkeuper.bedwars.api.entity.Despawnable;
import com.tomkeuper.bedwars.api.entity.GeneratorHolder;
import com.tomkeuper.bedwars.api.exceptions.InvalidEffectException;
import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class VersionSupport {

    /**
     * Map of entities that are going to despawn based on a timer.
     */
    private static final ConcurrentHashMap<UUID, Despawnable> despawnables = new ConcurrentHashMap<>();

    /**
     * Generic plugin tag key.
     */
    public static String PLUGIN_TAG_GENERIC_KEY = "BedWars2023";

    /**
     * Tier identifier tag key.
     */
    public static String PLUGIN_TAG_TIER_KEY = "tierIdentifier";

    /**
     * The name of the version.
     */
    private static String name2;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The egg bridge effect.
     */
    private Effect eggBridge;

    /**
     * Create a new version support instance.
     *
     * @param plugin      the plugin instance
     * @param versionName the version name
     */
    public VersionSupport(Plugin plugin, String versionName) {
        name2 = versionName;
        this.plugin = plugin;
    }

    /**
     * Get the version name.
     *
     * @return the version name
     */
    public static String getName() {
        return name2;
    }

    /**
     * Load default effects.
     */
    protected void loadDefaultEffects() {
        try {
            setEggBridgeEffect("MOBSPAWNER_FLAMES");
        } catch (InvalidEffectException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a new command as bukkit command
     */
    public abstract void registerCommand(String name, Command cmd);

    /**
     * Send title, subtitle. null for empty
     */
    public abstract void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut);

    /**
     * Send action-bar message
     */
    public abstract void playAction(Player p, String text);

    /**
     * Check if bukkit command is registered
     */
    public abstract boolean isBukkitCommandRegistered(String command);

    /**
     * Get in had item-stack
     */
    public abstract ItemStack getItemInHand(Player p);

    /**
     * Hide an entity
     */
    public abstract void hideEntity(Entity e, Player p);

    /**
     * Apply fake damage to a player (red flash and hurt sound)
     */
    public abstract void fakeDamagePlayer(Player e);

    /**
     * Check if item-stack is armor
     */
    public abstract boolean isArmor(ItemStack itemStack);

    /**
     * Check if item-stack is a tool
     */
    public abstract boolean isTool(ItemStack itemStack);

    /**
     * Check if item-stack is sword
     */
    public abstract boolean isSword(ItemStack itemStack);

    /**
     * Check if item-stack is axe
     */
    public abstract boolean isAxe(ItemStack itemStack);

    /**
     * Check if item-stack is bow
     */
    public abstract boolean isBow(ItemStack itemStack);

    /**
     * Check if itemstack is Projectile
     */
    public abstract boolean isProjectile(ItemStack itemStack);

    /**
     * Check if itemstack is Invisibility Potion
     */
    public abstract boolean isInvisibilityPotion(ItemStack itemStack);

    /**
     * Check if type is a Glass type material
     */
    public boolean isGlass(Material type) {
        return type != Material.AIR && (type == Material.GLASS || type.toString().contains("_GLASS"));
    }

    /**
     * Register custom entities
     */
    public abstract void registerEntities();

    /**
     * Spawn shop NPC
     */
    public abstract void spawnShop(Location loc, String name1, Iterable<Player> players, IArena arena);

    /**
     * Spawn shop hologram
     */
    public abstract void spawnShopHologram(Location loc, String name1, Iterable<Player> players, ITeam team);

    /**
     * Get item-stack damage amount
     */
    public abstract double getDamage(ItemStack i);

    /**
     * Spawn silverfish for a team
     */
    public abstract void spawnSilverfish(Location loc, ITeam team, double speed, double health, int despawn, double damage, int pathFindingTicks);

    /**
     * Spawn a iron-golem for a team
     */
    public abstract void spawnIronGolem(Location loc, ITeam team, double speed, double health, int despawn, int pathFindingTicks);

    /**
     * Is despawnable entity
     */
    public boolean isDespawnable(Entity e) {
        if (e == null) return false;
        return despawnables.get(e.getUniqueId()) != null;
    }

    /**
     * Change item amount
     */
    public abstract void minusAmount(Player p, ItemStack i, int amount);

    /**
     * Set tnt source
     */
    public abstract void setSource(TNTPrimed tnt, Player owner);

    /**
     * Void damage with cause
     */
    public abstract void voidKill(Player p);

    /**
     * Hide player armor to a player
     */
    public abstract void hideArmor(Player victim, Player receiver);

    /**
     * Show a player armor
     */
    public abstract void showArmor(Player victim, Player receiver);

    /**
     * Spawn ender dragon
     */
    public abstract EnderDragon spawnDragon(Location l, ITeam team);

    /**
     * Color a bed 1.12+
     */
    public abstract void colorBed(ITeam team);

    /**
     * Modify and register block blast resistance.
     */
    public abstract void registerTntWhitelist(float endStoneBlast, float glassBlast);

    /**
     * Get blast resistance of a block.
     * This will return the default blast resistance if not modified.
     *
     * @param block the block to get blast resistance for
     * @return the blast resistance of the block
     */
    public abstract float getBlastResistance(Block block);

    /**
     * Egg bridge particles
     */
    public Effect eggBridge() {
        return eggBridge;
    }

    @SuppressWarnings("WeakerAccess")
    public void setEggBridgeEffect(String eggBridge) throws InvalidEffectException {
        try {
            this.eggBridge = Effect.valueOf(eggBridge);
        } catch (Exception e) {
            throw new InvalidEffectException(eggBridge);
        }
    }

    /**
     * Set block data
     * For 1.13 support
     */
    public abstract void setBlockTeamColor(Block block, TeamColor teamColor);

    /**
     * Disable collisions in 1.9+
     */
    public abstract void setCollide(Player p, IArena a, boolean value);

    /**
     * Add custom data to an ItemStack.
     *
     * @param i    the item stack
     * @param data the data string
     * @return the modified item stack
     */
    public abstract ItemStack addCustomData(ItemStack i, String data);

    /**
     * Set a custom NBT tag.
     *
     * @param itemStack the item stack
     * @param key       the tag key
     * @param value     the tag value
     * @return the modified item stack
     */
    public abstract ItemStack setTag(ItemStack itemStack, String key, String value);

    /**
     * Get a custom item tag.
     *
     * @param itemStack the item stack
     * @param key       the tag key
     * @return the tag value or null if not present
     */
    @SuppressWarnings("unused")
    public abstract String getTag(ItemStack itemStack, String key);

    /**
     * Check if an item has a BedWars2023 NBTTag
     */
    public abstract boolean isCustomBedWarsItem(ItemStack i);

    /**
     * Get the NBTTag from a BedWars2023 item
     */
    public abstract String getCustomData(ItemStack i);

    /**
     * Color an item if possible with the team's color
     */
    public abstract ItemStack colourItem(ItemStack itemStack, ITeam bedWarsTeam);

    /**
     * Create an item stack.
     *
     * @param material the material name
     * @param amount   the amount
     * @param data     the data value (for legacy versions)
     * @return the item stack
     */
    public abstract ItemStack createItemStack(String material, int amount, short data);

    /**
     * Check if is a player head
     */
    public boolean isPlayerHead(String material, int data) {
        return material.equalsIgnoreCase("PLAYER_HEAD") || (material.equalsIgnoreCase("SKULL_ITEM") && data == 3);
    }

    /**
     * Get fireball material
     */
    public abstract Material materialFireball();

    /**
     * Player head material
     */
    public abstract Material materialPlayerHead();

    /**
     * Get snowball material
     */
    public abstract Material materialSnowball();

    /**
     * Get gold  helmet material
     */
    public abstract Material materialGoldenHelmet();

    /**
     * Get gold chest plate
     */
    public abstract Material materialGoldenChestPlate();

    /**
     * Get gold leggings
     */
    public abstract Material materialGoldenLeggings();

    /**
     * Get netherite  helmet material
     */
    public abstract Material materialNetheriteHelmet();

    /**
     * Get netherite chest plate
     */
    public abstract Material materialNetheriteChestPlate();

    /**
     * Get netherite leggings
     */
    public abstract Material materialNetheriteLeggings();

    /**
     * Get elytra - supports: 1.12.2+
     */
    public abstract Material materialElytra();

    /**
     * Cake material
     */
    public abstract Material materialCake();

    /**
     * Crafting table material
     */
    public abstract Material materialCraftingTable();

    /**
     * Enchanting table material
     */
    public abstract Material materialEnchantingTable();

    /**
     * End stone material.
     *
     * @return the end stone material
     */
    public abstract Material materialEndStone();

    /**
     * Check if bed
     */
    public boolean isBed(Material material) {
        return material.toString().contains("_BED");
    }

    /**
     * Item Data compare
     * This will always return true on versions major or equal 1.13
     */
    public boolean itemStackDataCompare(ItemStack i, short data) {
        return true;
    }

    /**
     * Set block data
     * For versions before 1.13
     */
    public void setJoinSignBackgroundBlockData(BlockState b, byte data) {

    }

    /**
     * Change the block behind the join sign.
     */
    public abstract void setJoinSignBackground(BlockState b, Material material);

    /**
     * Wool material
     */
    public abstract Material woolMaterial();

    /**
     * Red glass pane item stack
     *
     * @param amount the amount of the stack
     * @return the itemStack
     */
    public abstract ItemStack redGlassPane(int amount);

    /**
     * Green glass pane item stack
     *
     * @param amount the amount of the stack
     * @return the itemStack
     */
    public abstract ItemStack greenGlassPane(int amount);

    /**
     * Get an ItemStack identifier
     * will return null text if it does not have an identifier
     */
    public abstract String getShopUpgradeIdentifier(ItemStack itemStack);

    /**
     * Set an upgrade identifier
     */
    public abstract ItemStack setShopUpgradeIdentifier(ItemStack itemStack, String identifier);

    /**
     * Get player head with skin.
     *
     * @param player      the player
     * @param copyTagFrom will copy nbt tag from this item.
     * @return the player head item stack
     */
    public abstract ItemStack getPlayerHead(Player player, @Nullable ItemStack copyTagFrom);

    /**
     * This will send the player spawn packet after a player re-spawn.
     * <p>
     * Show the target player to players and spectators in the arena.
     */
    public abstract void sendPlayerSpawnPackets(Player player, IArena arena);

    /**
     * Get inventory name.
     */
    public abstract String getInventoryName(InventoryEvent e);

    /**
     * Make item unbreakable.
     */
    public abstract void setUnbreakable(ItemMeta itemMeta);

    /**
     * Get list of entities that are going to despawn based on a timer.
     */
    public ConcurrentHashMap<UUID, Despawnable> getDespawnablesList() {
        return despawnables;
    }

    /**
     * Get the major version.
     *
     * @return the major version (e.g., 8, 12, 16, 21)
     */
    public abstract int getVersion();

    /**
     * Get the plugin instance.
     *
     * @return the plugin instance
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Register version-specific listeners.
     */
    public abstract void registerVersionListeners();

    /**
     * Get main level name.
     */
    public abstract String getMainLevel();

    /**
     * Get compressed angle for packets.
     *
     * @param value the angle value
     * @return the compressed angle
     */
    public byte getCompressedAngle(float value) {
        return (byte) ((value * 256.0F) / 360.0F);
    }

    /**
     * Show a player to another player using spigot-specific methods if necessary.
     *
     * @param victim   the player to show
     * @param receiver the player who will see the victim
     */
    public void spigotShowPlayer(Player victim, Player receiver) {
        receiver.showPlayer(victim);
    }

    /**
     * Hide a player from another player using spigot-specific methods if necessary.
     *
     * @param victim   the player to hide
     * @param receiver the player who will no longer see the victim
     */
    public void spigotHidePlayer(Player victim, Player receiver) {
        receiver.hidePlayer(victim);
    }

    /**
     * Make fireball go straight.
     *
     * @param fireball fireball instance;
     * @param vector   fireball direction to normalize.
     * @return modified fireball.
     */
    public abstract Fireball setFireballDirection(Fireball fireball, Vector vector);

    /**
     * Play redstone dot particle at player location.
     *
     * @param player the player
     */
    public abstract void playRedStoneDot(Player player);

    /**
     * Clear arrows from player body.
     *
     * @param player the player
     */
    public abstract void clearArrowsFromPlayerBody(Player player);

    /**
     * Place tower blocks.
     *
     * @param b     the base block
     * @param a     the arena
     * @param color the team color
     * @param x     the x offset
     * @param y     the y offset
     * @param z     the z offset
     * @return the last placed block
     */
    public abstract Block placeTowerBlocks(Block b, IArena a, TeamColor color, int x, int y, int z);

    /**
     * Place a ladder.
     *
     * @param b          the block
     * @param x          the x coordinate
     * @param y          the y coordinate
     * @param z          the z coordinate
     * @param a          the arena
     * @param ladderdata the ladder data
     * @return the placed block
     */
    public abstract Block placeLadder(Block b, int x, int y, int z, IArena a, int ladderdata);

    /**
     * Play villager effect at location.
     *
     * @param player   the player
     * @param location the location
     */
    public abstract void playVillagerEffect(Player player, Location location);

    /**
     * Create a hologram.
     *
     * @param p        the player who can see the hologram
     * @param location the location
     * @param lines    the lines of text
     * @return the hologram instance
     */
    public abstract IHologram createHologram(Player p, Location location, String... lines);

    /**
     * Create a hologram.
     *
     * @param p        the player who can see the hologram
     * @param location the location
     * @param lines    the hologram lines
     * @return the hologram instance
     */
    public abstract IHologram createHologram(Player p, Location location, IHoloLine... lines);

    /**
     * Create a hologram.
     *
     * @param players  the players who can see the hologram
     * @param location the location
     * @param lines    the lines of text
     * @return the hologram instance
     */
    public abstract IHologram createHologram(Iterable<Player> players, Location location, String... lines);

    /**
     * Create a hologram.
     *
     * @param players  the players who can see the hologram
     * @param location the location
     * @param lines    the hologram lines
     * @return the hologram instance
     */
    public abstract IHologram createHologram(Iterable<Player> players, Location location, IHoloLine... lines);

    /**
     * Create a hologram line from text.
     *
     * @param text     the text
     * @param hologram the hologram instance
     * @return the hologram line
     */
    public abstract IHoloLine lineFromText(String text, @Nonnull IHologram hologram);

    /**
     * Create default generator animation.
     *
     * @param armorStand the armor stand
     * @return the generator animation instance
     */
    public abstract IGeneratorAnimation createDefaultGeneratorAnimation(ArmorStand armorStand);

    /**
     * Destroy packet armor stand.
     *
     * @param generatorHolder the generator holder
     * @param players         the players who can see the armor stand
     */
    public abstract void destroyPacketArmorStand(GeneratorHolder generatorHolder, Iterable<Player> players);

    /**
     * Create packet armor stand.
     *
     * @param loc     the location
     * @param players the players who can see the armor stand
     * @return the armor stand instance
     */
    public abstract ArmorStand createPacketArmorStand(@Nonnull Location loc, Iterable<Player> players);

    /**
     * Update packet armor stand.
     *
     * @param generatorHolder the generator holder
     * @param players         the players who can see the armor stand
     */
    public abstract void updatePacketArmorStand(GeneratorHolder generatorHolder, Iterable<Player> players);

    /**
     * Update packet armor stand equipment.
     *
     * @param generatorHolder the generator holder
     */
    public abstract void updatePacketArmorStandEquipment(GeneratorHolder generatorHolder);

    /**
     * Call player death event.
     *
     * @param player       the player
     * @param drops        the item drops
     * @param droppedExp   the dropped exp
     * @param newLevel     the new level
     * @param deathMessage the death message
     */
    public abstract void callPlayerDeathEvent(Player player, List<ItemStack> drops, int droppedExp, int newLevel, String deathMessage);

    /**
     * Returns the absorption health of a player.
     *
     * @param player The player to get the absorption health from.
     * @return The absorption health of the player.
     */
    public abstract float getAbsorption(Player player);
}
