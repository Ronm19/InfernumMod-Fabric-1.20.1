package net.ronm19.infernummod.entity.custom;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumSet;

public class LavaSlimeEntity extends SlimeEntity implements Monster {

    // -----------------------------------------------------
    // ------------------- DATA TRACKERS -------------------
    // -----------------------------------------------------
    private static final TrackedData<Integer> SLIME_SIZE;
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;

    // -----------------------------------------------------
    // --------------------- VARIABLES ---------------------
    // -----------------------------------------------------
    public float targetStretch;
    public float stretch;
    public float lastStretch;
    private boolean onGroundLastTick;

    // -----------------------------------------------------
    // --------------------- CONSTRUCTOR -------------------
    // -----------------------------------------------------
    public LavaSlimeEntity(EntityType<? extends SlimeEntity> entityType, World world) {
        super(entityType, world);
        this.reinitDimensions();
        this.moveControl = new LavaSlimeMoveControl(this);
        this.isFireImmune();
    }

    public float getPublicJumpSoundPitch() {
        float f = this.isSmall() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }



    // -----------------------------------------------------
    // ---------------------- GOALS ------------------------
    // -----------------------------------------------------
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SwimmingGoal(this));
        this.goalSelector.add(2, new FaceTowardTargetGoal(this));
        this.goalSelector.add(3, new RandomLookGoal(this));
        this.goalSelector.add(4, new MoveGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(
                this, PlayerEntity.class, 10, true, false,
                (LivingEntity livingEntity) -> Math.abs(livingEntity.getY() - this.getY()) <= 4.0D
        ));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, TameableEntity.class, true));
    }

    // -----------------------------------------------------
    // -------------------- ATTRIBUTES ---------------------
    // -----------------------------------------------------

    public static DefaultAttributeContainer.Builder createLavaSlimeAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2D);
    }



    // -----------------------------------------------------
    // -------------------- BASIC DATA ---------------------
    // -----------------------------------------------------
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SLIME_SIZE, 1);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @VisibleForTesting
    public void setSize(int size, boolean heal) {
        int i = MathHelper.clamp(size, 1, 127);
        this.dataTracker.set(SLIME_SIZE, i);
        this.refreshPosition();
        this.calculateDimensions();
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .setBaseValue(i * i);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(0.2F + 0.1F * i);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .setBaseValue(i);
        if (heal) this.setHealth(this.getMaxHealth());
        this.experiencePoints = i;
    }

    public int getSize() {
        return this.dataTracker.get(SLIME_SIZE);
    }

    // -----------------------------------------------------
    // ---------------------- NBT --------------------------
    // -----------------------------------------------------
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Size", this.getSize() - 1);
        nbt.putBoolean("wasOnGround", this.onGroundLastTick);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.setSize(nbt.getInt("Size") + 1, false);
        super.readCustomDataFromNbt(nbt);
        this.onGroundLastTick = nbt.getBoolean("wasOnGround");
    }

    // -----------------------------------------------------
    // -------------------- CORE TICK ----------------------
    // -----------------------------------------------------
    @Override
    public void tick() {
        super.tick();

        if (getWorld() instanceof ServerWorld serverWorld) {
            // Lava particles
            if (this.age % 3 == 0) {
                serverWorld.spawnParticles(ParticleTypes.LAVA, getX(), getY(), getZ(),
                        2, 0.2, 0.3, 0.2, 0.02);
                serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 0.2, getZ(),
                        1, 0.1, 0.1, 0.1, 0.01);
            }
        }

        // Steam damage in water
        if (this.isTouchingWaterOrRain()) {
            this.damage(this.getDamageSources().magic(), 1.0F);
            if (getWorld() instanceof ServerWorld serverWorld && this.age % 5 == 0) {
                serverWorld.spawnParticles(ParticleTypes.CLOUD, getX(), getY() + 0.5, getZ(),
                        2, 0.1, 0.1, 0.1, 0.02);
                getWorld().playSound(null, getBlockPos(),
                        SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 1.2F);
            }
        }

        // Bounce visuals
        this.stretch += (this.targetStretch - this.stretch) * 0.5F;
        this.lastStretch = this.stretch;

        if (this.isOnGround() && !this.onGroundLastTick) {
            int i = this.getSize();
            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * ((float) Math.PI * 2F);
                float g = this.random.nextFloat() * 0.5F + 0.5F;
                float h = MathHelper.sin(f) * i * 0.5F * g;
                float k = MathHelper.cos(f) * i * 0.5F * g;
                this.getWorld().addParticle(this.getParticles(),
                        getX() + h, getY(), getZ() + k, 0, 0, 0);
            }
            this.playSound(this.getSquishSound(), this.getSoundVolume(),
                    ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetStretch = -0.5F;
        } else if (!this.isOnGround() && this.onGroundLastTick) {
            this.targetStretch = 1.0F;
        }

        this.onGroundLastTick = this.isOnGround();
        this.updateStretch();
    }

    protected void updateStretch() {
        this.targetStretch *= 0.6F;
    }

    // -----------------------------------------------------
    // -------------------- DAMAGE LOGIC -------------------
    // -----------------------------------------------------
    @Override
    protected void damage(LivingEntity target) {
        super.damage(target);
        target.setOnFireFor(3);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.IS_FIRE) || source.isOf(DamageTypes.LAVA)) return false;
        return super.damage(source, amount);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    // -----------------------------------------------------
    // --------------------- SOUNDS ------------------------
    // -----------------------------------------------------
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_DEATH;
    }

    @Override
    protected SoundEvent getSquishSound() {
        return isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_SQUISH;
    }

    @Override
    protected SoundEvent getJumpSound() {
        return isSmall() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_JUMP;
    }

    // -----------------------------------------------------
    // ------------------ INNER CLASSES --------------------
    // -----------------------------------------------------
    static class LavaSlimeMoveControl extends MoveControl {
        private float targetYaw;
        private int ticksUntilJump;
        private final LavaSlimeEntity lavaSlimeEntity;
        private boolean jumpOften;

        public LavaSlimeMoveControl(LavaSlimeEntity lavaSlimeEntity) {
            super(lavaSlimeEntity);
            this.lavaSlimeEntity = lavaSlimeEntity;
            this.targetYaw = 180.0F * lavaSlimeEntity.getYaw() / (float) Math.PI;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            this.speed = speed;
            this.state = State.MOVE_TO;
        }

        public void tick() {
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), this.targetYaw, 90.0F));
            this.entity.headYaw = this.entity.getYaw();
            this.entity.bodyYaw = this.entity.getYaw();
            if (this.state != State.MOVE_TO) {
                this.entity.setForwardSpeed(0.0F);
            } else {
                this.state = State.WAIT;
                if (this.entity.isOnGround()) {
                    this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                    if (this.ticksUntilJump-- <= 0) {
                        this.ticksUntilJump = this.lavaSlimeEntity.getTicksUntilNextJump();
                        if (this.jumpOften) {
                            this.ticksUntilJump /= 3;
                        }

                        this.lavaSlimeEntity.getJumpControl().setActive();
                        if (this.lavaSlimeEntity.makesJumpSound()) {
                            this.lavaSlimeEntity.playSound(this.lavaSlimeEntity.getJumpSound(), this.lavaSlimeEntity.getSoundVolume(), this.lavaSlimeEntity.getPublicJumpSoundPitch());
                        }
                    } else {
                        this.lavaSlimeEntity.sidewaysSpeed = 0.0F;
                        this.lavaSlimeEntity.forwardSpeed = 0.0F;
                        this.entity.setMovementSpeed(0.0F);
                    }
                } else {
                    this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                }

            }
        }
    }

    // Face Target Goal
    static class FaceTowardTargetGoal extends Goal {
        private final LavaSlimeEntity lavaSlimeEntity;
        private int ticksLeft;

        public FaceTowardTargetGoal(LavaSlimeEntity lavaSlimeEntity) {
            this.lavaSlimeEntity = lavaSlimeEntity;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        public boolean canStart() {
            LivingEntity livingEntity = this.lavaSlimeEntity.getTarget();
            return livingEntity != null && this.lavaSlimeEntity.canTarget(livingEntity);
        }

        public void start() {
            this.ticksLeft = toGoalTicks(300);
        }

        public boolean shouldContinue() {
            LivingEntity livingEntity = this.lavaSlimeEntity.getTarget();
            return livingEntity != null && this.lavaSlimeEntity.canTarget(livingEntity) && --this.ticksLeft > 0;
        }

        public void tick() {
            LivingEntity livingEntity = this.lavaSlimeEntity.getTarget();
            if (livingEntity != null) this.lavaSlimeEntity.lookAtEntity(livingEntity, 10.0F, 10.0F);
        }
    }

    // Random Look Goal
    static class RandomLookGoal extends Goal {
        private final LavaSlimeEntity lavaSlimeEntity;
        private float targetYaw;
        private int timer;

        public RandomLookGoal(LavaSlimeEntity lavaSlimeEntity) {
            this.lavaSlimeEntity = lavaSlimeEntity;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        public boolean canStart() {
            return this.lavaSlimeEntity.getTarget() == null && this.lavaSlimeEntity.getMoveControl() instanceof LavaSlimeMoveControl;
        }

        public void tick() {
            if (--this.timer <= 0) {
                this.timer = this.getTickCount(40 + this.lavaSlimeEntity.getRandom().nextInt(60));
                this.targetYaw = this.lavaSlimeEntity.getRandom().nextInt(360);
            }
        }
    }

    // Swimming Goal
    static class SwimmingGoal extends Goal {
        private final LavaSlimeEntity lavaSlimeEntity;

        public SwimmingGoal(LavaSlimeEntity lavaSlimeEntity) {
            this.lavaSlimeEntity = lavaSlimeEntity;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            return (this.lavaSlimeEntity.isTouchingWater() || this.lavaSlimeEntity.isInLava());
        }

        public void tick() {
            if (this.lavaSlimeEntity.getRandom().nextFloat() < 0.8F)
                this.lavaSlimeEntity.getJumpControl().setActive();
        }
    }

    // Move Goal
    static class MoveGoal extends Goal {
        private final LavaSlimeEntity lavaSlimeEntity;

        public MoveGoal(LavaSlimeEntity lavaSlimeEntity) {
            this.lavaSlimeEntity = lavaSlimeEntity;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            return !this.lavaSlimeEntity.hasVehicle();
        }

        public void tick() {
            MoveControl move = this.lavaSlimeEntity.getMoveControl();
            if (move instanceof LavaSlimeMoveControl lavaSlimeMoveControl)
                lavaSlimeMoveControl.move(1.0D);
        }
    }

    static {
        SLIME_SIZE = DataTracker.registerData(LavaSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
