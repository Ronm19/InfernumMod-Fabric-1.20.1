package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ai.infernum_herobrine.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InfernumHerobrineEntity extends HostileEntity implements RangedAttackMob {

    private int attackCooldown = 0;
    private boolean enragedTriggered = false;   // <— for 1-time effects

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;

    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(InfernumHerobrineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public InfernumHerobrineEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 500;
    }

    // ---------------- ATTACKING ----------------
    public void setAttacking(boolean attacking) { this.dataTracker.set(ATTACKING, attacking); }
    @Override public boolean isAttacking() { return this.dataTracker.get(ATTACKING); }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
    }

    @Override
    protected void initGoals() {
        // Core survival & phase logic
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new InfernumHerobrinePhaseGoal(this));      // <-- must run early
        this.goalSelector.add(3, new InfernumHerobrineAttackGoal(this));     // base ranged/melee logic

        // Combat mobility
        this.goalSelector.add(4, new HerobrineDashAttackGoal(this));          // dashes when in melee range
        this.goalSelector.add(5, new HerobrineStrafeShootGoal(this));         // strafes when shooting at range
        this.goalSelector.add(6, new HerobrineVolleyGoal(this));
        this.goalSelector.add(7, new HerobrineJumpStrikeGoal(this));
        this.goalSelector.add(8, new HerobrineFlameWaveGoal(this));
        this.goalSelector.add(9, new HerobrineKnockbackSwipeGoal(this));

        // Standard vanilla-style behaviors
        this.goalSelector.add(10, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(11, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 16.0F));
        this.goalSelector.add(13, new LookAroundGoal(this));
        this.goalSelector.add(14, new EscapeDangerGoal(this, 2D));

        // Targeting priorities
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, TameableEntity.class, true));
        this.targetSelector.add(3, new RevengeGoal(this));
    }


    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createInfernumHerobrineAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 450.0)        // beefier mini-boss
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)      // hits harder in melee
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.19f)    // base speed
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 120.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.6)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6);
    }


    // ---------------- ANIMATIONS ----------------
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else idleAnimationTimeout--;

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 40;
            attackAnimationState.start(this.age);
        } else attackAnimationTimeout--;

        if (!this.isAttacking()) attackAnimationState.stop();
    }

    protected void updateLimbs(float v) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(v * 6.0F, 1.0F) : 0.0F;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    // ---------------- ENRAGED CHECK ----------------
    public boolean isEnraged() { return this.getHealth() <= this.getMaxHealth() * 0.5F; }

    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();

        // --- 1. Rage-trigger burst (1-time) ---
        if (!this.getWorld().isClient && this.isEnraged() && !enragedTriggered) {
            enragedTriggered = true;
            this.getWorld().playSound(null, this.getBlockPos(),
                    SoundEvents.ENTITY_WITHER_SPAWN,
                    SoundCategory.HOSTILE, 2.0F, 0.6F);

            for (int i = 0; i < 80; i++) {
                double ox = (this.random.nextDouble() - 0.5) * this.getWidth() * 2.5;
                double oy = this.random.nextDouble() * this.getHeight() + 0.2;
                double oz = (this.random.nextDouble() - 0.5) * this.getWidth() * 2.5;
                ((ServerWorld) this.getWorld()).spawnParticles(
                        ParticleTypes.EXPLOSION,
                        this.getX() + ox, this.getY() + oy, this.getZ() + oz,
                        1, 0, 0, 0, 0.1
                );
            }
        }

        // --- 2. Aura damage around Herobrine ---
        this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(5),
                e -> e instanceof PlayerEntity).forEach(e -> {
            PlayerEntity player = (PlayerEntity) e;
            player.setOnFireFor(2);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40, 0));
        });

        // --- 3. Adjusted ranged attack cooldown ---
        if (attackCooldown > 0) attackCooldown--;
        else if (this.getTarget() != null && this.squaredDistanceTo(this.getTarget()) > 16) {
            this.shootAt(this.getTarget(), 1.0f);
            attackCooldown = this.isEnraged() ? 18 : 40; // enraged: much faster shots
        }

        // --- 4. Client-side fiery aura & trail cleanup ---
        if (this.isEnraged() && this.getWorld().isClient) {
            for (int i = 0; i < 6; i++) {
                double ox = (this.random.nextDouble() - 0.5) * this.getWidth() * 1.5;
                double oy = this.random.nextDouble() * this.getHeight();
                double oz = (this.random.nextDouble() - 0.5) * this.getWidth() * 1.5;
                this.getWorld().addParticle(ParticleTypes.FLAME,
                        this.getX() + ox, this.getY() + oy, this.getZ() + oz,
                        0, 0.01, 0);
                this.getWorld().addParticle(ParticleTypes.SMOKE,
                        this.getX() + ox, this.getY() + oy, this.getZ() + oz,
                        0, 0.01, 0);
                if (this.random.nextFloat() < 0.3F) {
                    this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                            this.getX() + ox, this.getY() + oy, this.getZ() + oz,
                            0, 0.02, 0);
                }
            }

            // clean up temporary fire trail blocks
            if (!fireTrailMap.isEmpty() && !this.getWorld().isClient) {
                fireTrailMap.entrySet().removeIf(entry -> {
                    BlockPos pos = entry.getKey();
                    int ticksLeft = entry.getValue() - 1;
                    if (ticksLeft <= 0) {
                        if (this.getWorld().getBlockState(pos).isOf(Blocks.FIRE)) {
                            this.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
                        }
                        return true; // remove entry
                    } else {
                        entry.setValue(ticksLeft);
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public boolean cannotDespawn() {
        // Prevents despawn in normal conditions, but monsters still despawn on peaceful
        return this.getType().getSpawnGroup() != SpawnGroup.MONSTER || this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
    }


    // ---------------- RANGED ATTACK ----------------
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (this.getWorld().isClient) return;

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dy = (target.getY() + target.getHeight() * 0.4) - this.getEyeY(); // aim at chest

        float velocity = this.isEnraged() ? 1.6F : 1.3F;      // higher speed if enraged
        float inaccuracy = this.isEnraged() ? 0.01F : 0.03F;  // more accurate when enraged

        if (this.isEnraged()) {
            WitherSkullEntity skull = new WitherSkullEntity(this.getWorld(), this, dx, dy, dz);
            skull.setPos(this.getX(), this.getEyeY() - 0.2, this.getZ());
            skull.setVelocity(dx, dy, dz, velocity, inaccuracy);
            skull.setCharged(true);
            this.getWorld().spawnEntity(skull);

            this.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 1.0F,
                    0.8F + this.random.nextFloat() * 0.4F);
        } else {
            ExplosiveFireballEntity skull = new ExplosiveFireballEntity(this.getWorld(), this, dx, dy, dz);
            skull.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ());
            skull.setVelocity(dx, dy, dz, velocity, inaccuracy);
            this.getWorld().spawnEntity(skull);

            this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F,
                    0.8F + this.random.nextFloat() * 0.4F);
        }
    }


    // ---------------- IMMUNITIES ----------------
    @Override public boolean isFireImmune() {
        return true;
    }

    @Override public boolean isImmuneToExplosion() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.PLAYER_EXPLOSION)
                || source.isOf(DamageTypes.WITHER) || source.isOf(DamageTypes.WITHER_SKULL)
                || source.isOf(DamageTypes.IN_FIRE)) return false;
        return super.damage(source, amount);
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        // All infernum creatures ignore their god
        return !(target instanceof InfernumEntity);
    }

    // ---------------- SOUNDS ----------------
    @Override protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }

    @Override protected SoundEvent getHurtSound(DamageSource src) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }
    @Override protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    // Track temporary fire trail blocks
    public final Map<BlockPos, Integer> fireTrailMap = new HashMap<>();

}


