package com.tomkeuper.bedwars.support.version.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

public class CustomExplosion extends Explosion {

    private Field sizeField;
    private Field posXField;
    private Field posYField;
    private Field posZField;
    private Field worldField;
    private Field blocksField;
    private Field kField; // the map for player knockback vectors
    private final Random random = new Random();

    private float size;
    private double posX, posY, posZ;
    private World world;
    private final List<BlockPosition> blocks = Lists.newArrayList();
    private final Map<EntityHuman, Vec3D> k = Maps.newHashMap();

    public final Entity source;
    private final boolean setFire, breakBlocks;

    public CustomExplosion(World world, Entity source, double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
        super(world, source, x, y, z, power, setFire, breakBlocks);
        this.world = world;
        this.source = source;
        this.size = (float)Math.max((double)power, (double)0.0F);
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.setFire = setFire;
        this.breakBlocks = breakBlocks;
    }


    @Override
    public void a() {
        Logger.getAnonymousLogger().warning("Custom Explosion a() called with size: " + this.size + ", pos: (" + this.posX + ", " + this.posY + ", " + this.posZ + ")");
        if (this.size < 0.1F) return;

        Set<BlockPosition> affectedPositions = Sets.newHashSet();
        int rays = 16;

        for (int k = 0; k < rays; ++k) {
            for (int i = 0; i < rays; ++i) {
                for (int j = 0; j < rays; ++j) {
                    if (k == 0 || k == rays - 1 || i == 0 || i == rays - 1 || j == 0 || j == rays - 1) {

                        double dx = (double) k / 15.0F * 2.0F - 1.0F;
                        double dy = (double) i / 15.0F * 2.0F - 1.0F;
                        double dz = (double) j / 15.0F * 2.0F - 1.0F;

                        double dLength = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        dx /= dLength;
                        dy /= dLength;
                        dz /= dLength;

                        float power = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double px = this.posX;
                        double py = this.posY;
                        double pz = this.posZ;

                        BlockPosition lastPos = null;

                        for (float step = 0.3F; power > 0.0F; power -= step * 0.75F) {
                            BlockPosition pos = new BlockPosition(px, py, pz);
                            IBlockData blockData = world.getType(pos);
                            Block block = blockData.getBlock();

                            // Only check if we entered a new block (avoids duplicate checks)
                            if (lastPos == null || !pos.equals(lastPos)) {

                                if (block.getMaterial() != Material.AIR) {
                                    if (isGlassLike(block)) {
                                        Logger.getAnonymousLogger().info("Blocked by glass at " + pos);
                                        break; // Stop the ray, it can't go through glass
                                    }

                                    float resistance = this.source != null ? this.source.a(this, this.world, pos, blockData) :
                                            block.a((Entity) null);


                                    power -= (resistance + 0.3F) * 0.3F;
                                }

                                if (power > 0.0F && pos.getY() >= 0 && pos.getY() < 256) {
                                    affectedPositions.add(pos);
                                }
                            }

                            lastPos = pos;

                            px += dx * 0.3F;
                            py += dy * 0.3F;
                            pz += dz * 0.3F;
                        }
                    }
                }
            }
        }
//        this.blocks.clear();
        this.blocks.addAll(affectedPositions);

        // Handle entity damage and knockback
        float explosionSize = this.size * 2.0F;
        int minX = MathHelper.floor(this.posX - explosionSize - 1.0D);
        int maxX = MathHelper.floor(this.posX + explosionSize + 1.0D);
        int minY = MathHelper.floor(this.posY - explosionSize - 1.0D);
        int maxY = MathHelper.floor(this.posY + explosionSize + 1.0D);
        int minZ = MathHelper.floor(this.posZ - explosionSize - 1.0D);
        int maxZ = MathHelper.floor(this.posZ + explosionSize + 1.0D);

        List<Entity> entities = this.world.getEntities(this.source, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
        Vec3D explosionCenter = new Vec3D(this.posX, this.posY, this.posZ);

        for (Entity entity : entities) {
            if (entity.aW()) continue; // skip dead

            double dist = entity.f(this.posX, this.posY, this.posZ) / explosionSize;
            if (dist > 1.0D) continue;

            double dx = entity.locX - this.posX;
            double dy = entity.locY + entity.getHeadHeight() - this.posY;
            double dz = entity.locZ - this.posZ;

            double len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            if (len == 0.0D) continue;

            dx /= len;
            dy /= len;
            dz /= len;

            double blockDensity = this.world.a(explosionCenter, entity.getBoundingBox());
            double impact = (1.0D - dist) * blockDensity;

            CraftEventFactory.entityDamage = this.source;
            boolean damaged = entity.damageEntity(DamageSource.explosion(null), (float) ((int) ((impact * impact + impact) / 2.0D * 8.0D * explosionSize + 1.0D)));
            CraftEventFactory.entityDamage = null;

            if (damaged || entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock || entity.forceExplosionKnockback) {
                double knockback = EnchantmentProtection.a(entity, impact);
                entity.motX += dx * knockback;
                entity.motY += dy * knockback;
                entity.motZ += dz * knockback;

                if (entity instanceof EntityHuman && !((EntityHuman) entity).abilities.isInvulnerable) {
                    this.k.put((EntityHuman) entity, new Vec3D(dx * impact, dy * impact, dz * impact));
                }
            }
        }
//        this.internalBlockList.clear();
//        this.blocks.addAll(affectedPositions);
    }

    @Override
    public void a(boolean showParticles) {
        Logger.getAnonymousLogger().info("Custom Explosion a(boolean showParticles) called with size: " + this.size + ", particles: " + showParticles + " breakBlocks: " + this.breakBlocks);
        world.makeSound(posX, posY, posZ, "random.explode", 4.0F,
                (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);

        if (size >= 2.0F && breakBlocks) {
            Logger.getAnonymousLogger().info("Spawning HUGE explosion particles at " + posX + ", " + posY + ", " + posZ);
            Logger.getAnonymousLogger().info("World: " + world.getWorld().getName());
//            this.world.addParticle(EnumParticle.EXPLOSION_HUGE, posX, posY, posZ, 1.0D, 0.0F, 0.0F, new int[0]);

            // particles are client side only, so we need to send them to players instead
            for (Player player : world.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(new Location(player.getWorld(), posX, posY, posZ)) < 64 * 64) {
                    sendExplosionType(player, posX, posY, posZ, EnumParticle.EXPLOSION_HUGE);
                }
            }


        } else {
            Logger.getAnonymousLogger().info("Spawning LARGE explosion particles at " + posX + ", " + posY + ", " + posZ);
//            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, posX, posY, posZ, 1.0D, 0.0D, 0.0D, new int[0]);
            for (Player player : world.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(new Location(player.getWorld(), posX, posY, posZ)) < 64 * 64) {
                    sendExplosionType(player, posX, posY, posZ, EnumParticle.EXPLOSION_LARGE);
                }
            }
        }

        if (!breakBlocks) return;

        // Bukkit explosion events
        org.bukkit.World bukkitWorld = world.getWorld();
        org.bukkit.entity.Entity bukkitEntity = source == null ? null : source.getBukkitEntity();
        org.bukkit.Location location = new org.bukkit.Location(bukkitWorld, posX, posY, posZ);
        List<org.bukkit.block.Block> blockList = Lists.newArrayList();

        for (BlockPosition pos : blocks) {
            org.bukkit.block.Block block = bukkitWorld.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            if (block.getType() != org.bukkit.Material.AIR) {
                blockList.add(block);
            }
        }

        float yield = 0.3F;
        boolean cancelled;
        List<org.bukkit.block.Block> finalList;

        if (bukkitEntity != null) {
            EntityExplodeEvent event = new EntityExplodeEvent(bukkitEntity, location, blockList, yield);
            world.getServer().getPluginManager().callEvent(event);
            cancelled = event.isCancelled();
            finalList = event.blockList();
            yield = event.getYield();
        } else {
            BlockExplodeEvent event = new BlockExplodeEvent(location.getBlock(), blockList, yield);
            world.getServer().getPluginManager().callEvent(event);
            cancelled = event.isCancelled();
            finalList = event.blockList();
            yield = event.getYield();
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("BedWars2023");
        bukkitEntity.setMetadata("custom-explosion", new FixedMetadataValue(plugin, false));
        // This prevents the default explosion behavior from being triggered again thus creating a loop

        Logger.getAnonymousLogger().info("is Cancelled: " + cancelled);
        if (cancelled) {
            Logger.getAnonymousLogger().severe("Explosion was canceled, clearing blocks.");
            this.clearBlocks();
            this.wasCanceled = true;
            return;
        }

        // Clear and re-fill the internal list
        blocks.clear();
        for (org.bukkit.block.Block block : finalList) {
            blocks.add(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        }

        // Damage blocks, spawn particles, drop items
        for (BlockPosition pos : blocks) {
            IBlockData blockData = world.getType(pos);
            net.minecraft.server.v1_8_R3.Block block = blockData.getBlock();

            world.spigotConfig.antiXrayInstance.updateNearbyBlocks(world, pos);
//            Logger.getAnonymousLogger().info("Damaging block at " + pos + " with data: " + blockData + ", particles: " + showParticles);

            if (showParticles) {
                double dX = pos.getX() + world.random.nextFloat();
                double dY = pos.getY() + world.random.nextFloat();
                double dZ = pos.getZ() + world.random.nextFloat();

                double motionX = dX - posX;
                double motionY = dY - posY;
                double motionZ = dZ - posZ;

                double motionScale = 0.5D / (Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) / size + 0.1D);
                motionScale *= random.nextFloat() * random.nextFloat() + 0.3F;

                motionX *= motionScale;
                motionY *= motionScale;
                motionZ *= motionScale;

                Logger.getAnonymousLogger().info("Showing particles at " + pos + " with motion: (" + motionX + ", " + motionY + ", " + motionZ + ")");


                for (Player player : world.getWorld().getPlayers()) {
                    if (player.getLocation().distanceSquared(new Location(player.getWorld(), posX, posY, posZ)) < 64 * 64) {

                        sendParticle(player, EnumParticle.EXPLOSION_NORMAL,
                                (dX + posX) / 2.0D,
                                (dY + posY) / 2.0D,
                                (dZ + posZ) / 2.0D,
                                motionX, motionY, motionZ,
                                0.1f, 1);

                        // Smoke particle at displaced location
                        sendParticle(player, EnumParticle.SMOKE_NORMAL,
                                dX, dY, dZ,
                                motionX, motionY, motionZ,
                                0.05f, 1);

                    }
                }

                Logger.getAnonymousLogger().info("Showing particles for block at " + pos + ".");
            }

            if (block.getMaterial() != Material.AIR) {
                if (block.a(this)) {
                    block.dropNaturally(world, pos, blockData, yield, 0);
                }

                world.setTypeAndData(pos, Blocks.AIR.getBlockData(), 3);
                block.wasExploded(world, pos, this);
            }
        }

        // Set fire if configured
        if (setFire) {
            for (BlockPosition pos : blocks) {
                if (world.getType(pos).getBlock().getMaterial() == Material.AIR &&
                        world.getType(pos.down()).getBlock().o() &&
                        random.nextInt(3) == 0 &&
                        !CraftEventFactory.callBlockIgniteEvent(world, pos.getX(), pos.getY(), pos.getZ(), this).isCancelled()) {

                    world.setTypeUpdate(pos, Blocks.FIRE.getBlockData());
                }
            }
        }
    }

    public void sendExplosionType(Player player, double x, double y, double z, EnumParticle particle) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                particle, // Particle type
                true,                        // Long distance (send far away)
                (float) x, (float) y, (float) z, // Coordinates
                0f, 0f, 0f,                  // Offset X, Y, Z
                0f,                          // Speed
                1                           // Count
        );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendParticle(Player player, EnumParticle particle, double x, double y, double z,
                             double offsetX, double offsetY, double offsetZ,
                             float speed, int count, int... data) {

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                particle,
                true, // long distance
                (float) x, (float) y, (float) z,     // position
                (float) offsetX, (float) offsetY, (float) offsetZ, // motion/offset
                speed,
                count,
                data
        );

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }


    private boolean isGlassLike(Block block) {
        return block == Blocks.GLASS ||
                block == Blocks.STAINED_GLASS ||
                block == Blocks.GLASS_PANE ||
                block == Blocks.STAINED_GLASS_PANE;
    }
}

