package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MagmaCreeperEntity extends CreeperEntity implements Monster {

    private static final TrackedData<Integer> FUSE_SPEED =
            DataTracker.registerData(MagmaCreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IGNITED =
            DataTracker.registerData(MagmaCreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int lastFuseTime;
    private int currentFuseTime;
    private final int fuseTime = 20;

    // ------------------- ANIMATION STATES -------------------
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public MagmaCreeperEntity(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
        this.isFireImmune();
    }

    // ------------------- ATTRIBUTES -------------------
    public static DefaultAttributeContainer.Builder createMagmaCreeperAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.27D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D);
    }

    // ------------------- GOALS -------------------
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new CreeperIgniteGoal(this));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, PyerlingWyrnEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, EmberHundEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, TameableEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(4, new RevengeGoal(this));
    }

    // ------------------- DATA TRACKER -------------------
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FUSE_SPEED, -1);
        this.dataTracker.startTracking(IGNITED, false);
    }

    // ------------------- ANIMATION LOGIC -------------------
    @Override
    public void tick() {
        super.tick();

        // Client-side: particles + animations
        if (this.getWorld().isClient()) {
            setupAnimationStates();
            spawnLavaTrail();
        }

        // Fuse logic
        if (this.isAlive()) {
            this.lastFuseTime = this.currentFuseTime;

            if (this.isIgnited()) {
                this.setFuseSpeed(1);
            }

            int i = this.getFuseSpeed();
            if (i > 0 && this.currentFuseTime == 0) {
                this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
                this.emitGameEvent(GameEvent.PRIME_FUSE);
            }

            this.currentFuseTime += i;
            this.currentFuseTime = Math.max(0, this.currentFuseTime);

            if (this.currentFuseTime >= this.fuseTime) {
                this.currentFuseTime = this.fuseTime;
                this.explode();
            }
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfNotRunning(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private boolean isMoving() {
        return this.getVelocity().horizontalLengthSquared() > 1.0E-6;
    }

    // ------------------- LAVA PARTICLE TRAIL -------------------
    private void spawnLavaTrail() {
        if (this.isMoving()) {
            if (this.random.nextFloat() < 0.3F) {
                this.getWorld().addParticle(
                        ParticleTypes.LAVA,
                        this.getParticleX(0.5D),
                        this.getY() + 0.1D,
                        this.getParticleZ(0.5D),
                        0.0D, 0.02D, 0.0D
                );
            }
            if (this.random.nextFloat() < 0.15F) {
                this.getWorld().addParticle(
                        ParticleTypes.FLAME,
                        this.getParticleX(0.3D),
                        this.getY() + 0.2D,
                        this.getParticleZ(0.3D),
                        0.0D, 0.01D, 0.0D
                );
            }
        }
    }

    // ------------------- CUSTOM EXPLOSION -------------------
    private void explode() {
        if (!this.getWorld().isClient) {
            float scale = this.shouldRenderOverlay() ? 2.0F : 1.0F;
            this.dead = true;

            // Main explosion
            int explosionRadius = 7;
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(),
                    (float) explosionRadius * scale, World.ExplosionSourceType.MOB);

            // Fire & molten terrain effect
            BlockPos origin = this.getBlockPos();
            int radius = 6;
            Random random = this.getWorld().getRandom();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = origin.add(x, y, z);
                        double distance = Math.sqrt(x * x + y * y + z * z);
                        if (distance <= radius) {
                            if (this.getWorld().isAir(pos) && random.nextFloat() < 0.5F) {
                                this.getWorld().setBlockState(pos, Blocks.FIRE.getDefaultState());
                            } else {
                                Block block = this.getWorld().getBlockState(pos).getBlock();
                                if (block == Blocks.STONE && random.nextFloat() < 0.2F) {
                                    this.getWorld().setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState());
                                } else if (block == Blocks.DIRT && random.nextFloat() < 0.3F) {
                                    this.getWorld().setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
                                } else if (block == Blocks.SAND && random.nextFloat() < 0.15F) {
                                    this.getWorld().setBlockState(pos, Blocks.LAVA.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }

            this.spawnEffectsCloud();
            this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.5F, 1.0F);
            this.discard();
        }
    }

    // ------------------- EFFECT CLOUD -------------------
    private void spawnEffectsCloud() {
        Collection<StatusEffectInstance> effects = this.getStatusEffects();
        if (!effects.isEmpty()) {
            AreaEffectCloudEntity cloud = createEffectCloud();
            for (StatusEffectInstance effect : effects) {
                cloud.addEffect(new StatusEffectInstance(effect));
            }
            this.getWorld().spawnEntity(cloud);
        }
    }

    private @NotNull AreaEffectCloudEntity createEffectCloud() {
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
        cloud.setRadius(2.5F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(cloud.getDuration() / 2);
        cloud.setRadiusGrowth(-cloud.getRadius() / (float) cloud.getDuration());
        return cloud;
    }

    // ------------------- DAMAGE / FALL -------------------
    @Override
    public int getSafeFallDistance() {
        return this.getTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        boolean bl = super.handleFallDamage(fallDistance, damageMultiplier, source);
        this.currentFuseTime += (int) (fallDistance * 1.5F);
        if (this.currentFuseTime > this.fuseTime - 5) {
            this.currentFuseTime = this.fuseTime - 5;
        }
        return bl;
    }

    // ------------------- PLAYER INTERACTION -------------------
    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isIn(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent sound = stack.isOf(Items.FIRE_CHARGE)
                    ? SoundEvents.ITEM_FIRECHARGE_USE
                    : SoundEvents.ITEM_FLINTANDSTEEL_USE;

            this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                    sound, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);

            if (!this.getWorld().isClient) {
                this.ignite();
                if (!stack.isDamageable()) {
                    stack.decrement(1);
                } else {
                    stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
                }
            }
            return ActionResult.success(this.getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    // ------------------- GETTERS / SETTERS -------------------
    public float getClientFuseTime(float delta) {
        return MathHelper.lerp(delta, (float) this.lastFuseTime, (float) this.currentFuseTime) / (float) (this.fuseTime - 2);
    }

    public int getFuseSpeed() {
        return this.dataTracker.get(FUSE_SPEED);
    }

    public void setFuseSpeed(int speed) {
        this.dataTracker.set(FUSE_SPEED, speed);
    }

    public boolean isIgnited() {
        return this.dataTracker.get(IGNITED);
    }

    public void ignite() {
        this.dataTracker.set(IGNITED, true);
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        // Ignore Infernum itself
        if (target instanceof InfernumEntity) {
            return false;
        }

        // Ignore Creative / Spectator players
        if (target instanceof PlayerEntity player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        // Otherwise, follow normal targeting rules
        return super.canTarget(target);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    // ------------------- SOUNDS -------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CREEPER_DEATH;
    }
}
