package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.ItemEquippable;
import net.ronm19.infernummod.entity.ai.DemonAttackGoal;
import net.ronm19.infernummod.item.ModItems;

import java.util.Random;

public class DemonEntity extends HostileEntity implements Monster, ItemEquippable {

    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(DemonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;


    public DemonEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 40;
            attackAnimationState.start(this.age);
        } else {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()){
            attackAnimationState.stop();
        }
    }

    @Override
    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }
    }

    // Attributes
    public static DefaultAttributeContainer.Builder createDemonAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0) // 20 hearts
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0) // moderate damage
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3) // slightly fast
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0) // can spot player from mid-range
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.2)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1);
    }

    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
    }

    @Override
    public void initEquipment( Random random, LocalDifficulty difficulty, ServerWorld world, SpawnReason spawnReason ) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.NETHER_RUBY_SWORD));
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F); // 0% drop unless you want it to
    }


    @Override
    protected void initGoals() {
        // Basic hostile mob goals
        this.goalSelector.add(0, new SwimGoal(this));

        this.goalSelector.add(1, new DemonAttackGoal(this, 1D, true));

        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, false)); // Attack player with melee
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D));    // Wander when idle
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // Look at nearby players
        this.goalSelector.add(8, new LookAroundGoal(this)); // Idle looking around

        // Targeting goals
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true)); // Aggro on players
        this.targetSelector.add(3, new RevengeGoal(this)); // Attack back if hurt
    }

    @Override
    public boolean isFireImmune() {
        return true; // Demon is fire-resistant
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT; // eerie fire crackle/growl
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_HURT; // guttural hurt sound
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH; // big demonic death roar
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_STEP, 0.25F, 1.0F);
        // heavy footsteps, quieter volume
    }
}

