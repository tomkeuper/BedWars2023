package com.tomkeuper.bedwars.support.version.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

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
    public void a(boolean spawnParticles) {
        this.world.makeSound(this.posX, this.posY, this.posZ, "random.explode", 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);

        if (this.size >= 2.0F && this.breakBlocks) {
            this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, new int[0]);
        } else {
            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, new int[0]);
        }

        if (this.breakBlocks) {
            for (BlockPosition pos : this.blocks) {
                IBlockData blockData = this.world.getType(pos);
                Block block = blockData.getBlock();

                if (block.getMaterial() != Material.AIR) {
                    if (block.a(this)) {
                        block.dropNaturally(this.world, pos, blockData, this.size, 0);
                    }

                    this.world.setTypeAndData(pos, Blocks.AIR.getBlockData(), 3);
                    block.wasExploded(this.world, pos, this);
                }
            }
        }

        if (this.setFire) {
            for (BlockPosition pos : this.blocks) {
                if (this.world.getType(pos).getBlock().getMaterial() == Material.AIR &&
                        this.world.getType(pos.down()).getBlock().o() &&
                        this.random.nextInt(3) == 0) {

                    this.world.setTypeUpdate(pos, Blocks.FIRE.getBlockData());
                }
            }
        }
    }



    private boolean isGlassLike(Block block) {
        return block == Blocks.GLASS ||
                block == Blocks.STAINED_GLASS ||
                block == Blocks.GLASS_PANE ||
                block == Blocks.STAINED_GLASS_PANE;
    }
}

