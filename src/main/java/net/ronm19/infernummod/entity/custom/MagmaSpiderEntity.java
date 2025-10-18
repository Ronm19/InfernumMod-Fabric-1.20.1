package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.ai.pathing.MagmaSpiderNavigation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class MagmaSpiderEntity extends SpiderEntity implements Monster {

    private static final TrackedData<Byte> SPIDER_FLAGS;
    private static final float field_30498 = 0.1F;
    private int fireStepCooldown = 0;

    public MagmaSpiderEntity( EntityType<? extends SpiderEntity> entityType, World world ) {
        super(entityType, world);
        this.experiencePoints = 10;
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.isFireImmune();
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createMagmaSpiderAttributes() {
        return SpiderEntity.createSpiderAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 32.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2D);
    }

    // ---------------- GOALS ----------------
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(4, new SpiderData.AttackGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new SpiderData.TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.add(2, new SpiderData.TargetGoal<>(this, TameableEntity.class));
    }

    // ---------------- LIGHT EMISSION ----------------
    @Override
    public float getBrightnessAtEyes() {
        // Slight flicker: 8–10 brightness range (max 15)
        return 8 + random.nextInt(3);
    }



    // ------------- DATA -------------------

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPIDER_FLAGS, (byte) 0);
    }


    // ---------------- MAIN TICK ----------------
    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            this.setClimbingWall(this.horizontalCollision);
        }

        // Client particles
        if (this.getWorld().isClient) {
            if (this.isOnGround() && this.getVelocity().horizontalLengthSquared() > 0.001D) {
                this.getWorld().addParticle(ParticleTypes.LAVA,
                        this.getX() + (random.nextDouble() - 0.5D) * 0.5D,
                        this.getY() + 0.1D,
                        this.getZ() + (random.nextDouble() - 0.5D) * 0.5D,
                        0.0D, 0.02D, 0.0D);
            }

            if (this.age % 15 == 0) {
                this.getWorld().addParticle(ParticleTypes.FLAME,
                        this.getX() + (random.nextDouble() - 0.5D),
                        this.getY() + 0.6D,
                        this.getZ() + (random.nextDouble() - 0.5D),
                        0.0D, 0.02D, 0.0D);
            }
        }

        // Server fire trail logic
        if (!this.getWorld().isClient()) {
            if (fireStepCooldown > 0) fireStepCooldown--;

            if (this.isOnGround() && fireStepCooldown <= 0 && random.nextFloat() < 0.3F) {
                BlockPos below = this.getBlockPos().down();
                if (this.getWorld().getBlockState(below).isAir()) {
                    ((ServerWorld) this.getWorld()).setBlockState(below, net.minecraft.block.Blocks.FIRE.getDefaultState());
                }
                fireStepCooldown = 30; // ~1.5s between fire steps
            }
        }
    }

    // ---------------- FIRE BITE ----------------
    @Override
    public boolean tryAttack( Entity target ) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof LivingEntity living) {
            // Replace poison with burn effect
            living.setOnFireFor(5); // burns for 5 seconds
            this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 0.8F, 1.0F + (random.nextFloat() * 0.2F));
        }
        return success;
    }

    // ---------------- WATER WEAKNESS ----------------
    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.isWet()) {
            this.damage(this.getDamageSources().drown(), 1.0F);
            if (this.getWorld().isClient) {
                this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(),
                        0.0D, 0.02D, 0.0D);
            }
        }
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_LAVA_POP;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    // ------------------ OTHER ------------------------

    protected Vector3f getPassengerAttachmentPos( Entity passenger, EntityDimensions dimensions, float scaleFactor ) {
        return new Vector3f(0.0F, dimensions.height * 0.85F, 0.0F);
    }

    protected EntityNavigation createNavigation( World world ) {
        return new MagmaSpiderNavigation(this, world);
    }

    public boolean isClimbing() {
        return this.isClimbingWall();
    }

    public void slowMovement( BlockState state, Vec3d multiplier ) {
        if (!state.isOf(Blocks.COBWEB)) {
            super.slowMovement(state, multiplier);
        }

    }

    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    public boolean canHaveStatusEffect( StatusEffectInstance effect ) {
        return effect.getEffectType() == StatusEffects.POISON ? false : super.canHaveStatusEffect(effect);
    }

    public boolean isClimbingWall() {
        return ((Byte) this.dataTracker.get(SPIDER_FLAGS) & 1) != 0;
    }

    public void setClimbingWall( boolean climbing ) {
        byte b = (Byte) this.dataTracker.get(SPIDER_FLAGS);
        if (climbing) {
            b = (byte) (b | 1);
        } else {
            b = (byte) (b & -2);
        }

        this.dataTracker.set(SPIDER_FLAGS, b);
    }

    @Nullable
    public EntityData initialize( ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt ) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        Random random = world.getRandom();
        if (random.nextInt(100) == 0) {
            FlameSkeletonEntity flameSkeletonEntity = (FlameSkeletonEntity) ModEntities.FLAME_SKELETON.create(this.getWorld());
            if (flameSkeletonEntity != null) {
                flameSkeletonEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
                flameSkeletonEntity.initialize(world, difficulty, spawnReason, (EntityData) null, (NbtCompound) null);
                flameSkeletonEntity.startRiding(this);
            }
        }

        if (entityData == null) {
            entityData = new SpiderData();
            if (world.getDifficulty() == Difficulty.HARD && random.nextFloat() < 0.1F * difficulty.getClampedLocalDifficulty()) {
                ((SpiderData)entityData).setEffect(random);
            }
        }

        if (entityData instanceof SpiderData spiderData) {
            StatusEffect statusEffect = spiderData.effect;
            if (statusEffect != null) {
                this.addStatusEffect(new StatusEffectInstance(statusEffect, -1));
            }
        }

        return entityData;
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.65F;
    }

    protected float getUnscaledRidingOffset(Entity vehicle) {
        return vehicle.getWidth() <= this.getWidth() ? -0.3125F : 0.0F;
    }

    static {
        SPIDER_FLAGS = DataTracker.registerData(MagmaSpiderEntity.class, TrackedDataHandlerRegistry.BYTE);
    }

    public static class SpiderData implements EntityData {
        @Nullable
        public StatusEffect effect;

        public void setEffect(Random random) {
            int i = random.nextInt(5);
            if (i <= 1) {
                this.effect = StatusEffects.SPEED;
            } else if (i <= 2) {
                this.effect = StatusEffects.STRENGTH;
            } else if (i <= 3) {
                this.effect = StatusEffects.REGENERATION;
            } else if (i <= 4) {
                this.effect = StatusEffects.INVISIBILITY;
            }

        }

        // -------------------- INNER GOALS -------------------

        static class AttackGoal extends MeleeAttackGoal {
            public AttackGoal(MagmaSpiderEntity magmaSpider) {
                super(magmaSpider, (double)1.0F, true);
            }

            public boolean canStart() {
                return super.canStart() && !this.mob.hasPassengers();
            }

            public boolean shouldContinue() {
                float f = this.mob.getBrightnessAtEyes();
                if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
                    this.mob.setTarget((LivingEntity)null);
                    return false;
                } else {
                    return super.shouldContinue();
                }
            }
        }

        static class TargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
            public TargetGoal(MagmaSpiderEntity magmaSpider, Class<T> targetEntityClass) {
                super(magmaSpider, targetEntityClass, true);
            }

            public boolean canStart() {
                float f = this.mob.getBrightnessAtEyes();
                return f >= 0.5F ? false : super.canStart();
            }
        }
    }
}

