package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class LavagerArrowEntity extends ArrowEntity {
    private static final float EXPLOSION_POWER = 2.5F; // 💥 explosion strength (2.5 = creeper-like)

    // Basic constructor (for world-only spawn)
    public LavagerArrowEntity(EntityType<? extends LavagerArrowEntity> type, World world) {
        super(type, world);
        this.setFireTicks(120);
    }

    // Constructor for fired arrows
    public LavagerArrowEntity( World world, LivingEntity owner) {
        super(world, owner); // ✅ Correct constructor for 1.20.2
        this.setFireTicks(120);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        target.setOnFireFor(5);
        explode();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            World world = this.getWorld();

            // Ember trail
            for (int i = 0; i < 2; i++) {
                double offsetX = this.getX() + (this.random.nextDouble() - 0.5) * 0.2;
                double offsetY = this.getY() + 0.1 + (this.random.nextDouble() - 0.5) * 0.2;
                double offsetZ = this.getZ() + (this.random.nextDouble() - 0.5) * 0.2;

                if (this.getWorld().isClient && this.isOnFire()) {
                    this.getWorld().addParticle(ParticleTypes.SMALL_FLAME,
                            this.getX(), this.getY(), this.getZ(),
                            0, 0, 0);

                    world.addParticle(ParticleTypes.SMALL_FLAME, offsetX, offsetY, offsetZ, 0, 0, 0);
                    world.addParticle(ParticleTypes.LAVA, offsetX, offsetY, offsetZ, 0, 0, 0);
                }
            }
        }
    }


    @Override
    protected void onBlockHit(BlockHitResult hit) {
        super.onBlockHit(hit);
        explode();

        if (this.getWorld().isClient) return;
        World world = this.getWorld();

        // place fire in front of the hit face if air & mobGriefing allows
        BlockPos pos = hit.getBlockPos().offset(hit.getSide());
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)
                && world.isAir(pos)
                && Blocks.FIRE.getDefaultState().canPlaceAt(world, pos)) {
            world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
        }
    }

    private void explode() {
        if (this.getWorld().isClient) return;
        ServerWorld serverWorld = (ServerWorld) this.getWorld();

        // 💥 Explosion (damage + fire + block break)
        boolean mobGrief = serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
        serverWorld.createExplosion(
                this,                           // cause
                this.getX(), this.getY(), this.getZ(),
                EXPLOSION_POWER,                // power
                true,                           // set fire
                mobGrief ? World.ExplosionSourceType.MOB : World.ExplosionSourceType.NONE
        );

        // 🔊 Sound feedback
        serverWorld.playSound(null, this.getBlockPos(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE,
                1.2F, 1.0F + this.random.nextFloat() * 0.2F);

        // 💨 Visual feedback (extra ash + smoke)
        serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE,
                this.getX(), this.getY(), this.getZ(),
                12, 0.4, 0.3, 0.4, 0.02);

        this.discard(); // remove the arrow post-explosion
    }
}

