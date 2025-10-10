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
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ai.infernal_hoarde.FollowGroupGoal;
import net.ronm19.infernummod.entity.ai.infernal_hoarde.InfernalHoardeAttackGoal;
import net.ronm19.infernummod.entity.ai.inferno_zombie.InfernoZombieAttackGoal;
import net.ronm19.infernummod.sound.ModSounds;

public class InfernoZombieEntity extends HostileEntity implements Monster {

    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(InfernoZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public InfernoZombieEntity( EntityType<? extends HostileEntity> entityType, World world ) {
        super(entityType, world);
    }

    // Attributes
    public static DefaultAttributeContainer.Builder createInfernoZombieAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 85.0) // 40 hearts
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0) // moderate damage
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4) // slightly fast
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 65.0) // can spot player from mid-range
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.3)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.4);

    }

    @Override
    protected void initGoals() {
        // Basic hostile mob goals
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new InfernoZombieAttackGoal(this, 1D, true));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, false)); // Attack player with melee
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D));    // Wander when idle
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // Look at nearby players
        this.goalSelector.add(6, new LookAroundGoal(this)); // Idle looking around

        // Targeting goals
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true, true)); // Aggro on players
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, VillagerEntity.class, true, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, TameableEntity.class, true, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, IronGolemEntity.class, true, true));
        this.targetSelector.add(5, new RevengeGoal(this).setGroupRevenge(InfernoZombieEntity.class)); // Attack back if hurt
    }

    /* ------------------- ANIMATIONS ------------------- */
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

        if (!this.isAttacking()) {
            attackAnimationState.stop();
        }
    }

    @Override
    protected void updateLimbs( float v ) {
        float f;
        if (this.getPose() == EntityPose.STANDING) {
            f = Math.min(v * 6.0F, 1.0F);
        } else {
            f = 0.0F;
        }
        this.limbAnimator.updateLimbs(f, 0.2F);
    }


    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();
    }

    /* ------------------- DESPAWN RULE ------------------- */
    @Override
    public boolean cannotDespawn() {
        // Prevents despawn in normal conditions, but monsters still despawn on peaceful
        return this.getType().getSpawnGroup() != SpawnGroup.MONSTER
                || this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
    }

    /* ------------------- DATA TRACKER ------------------- */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
    }

    /* ------------------- ATTACKING ------------------- */
    public void setAttacking( boolean attacking ) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);

    }

    /* ------------------- SOUNDS ------------------- */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.4F, 1.0F);
    }

    /* ------------------- IMMUNITIES ------------------- */

    @Override
    public boolean isFireImmune() {
        return true;
    }

    /* ------------------- OTHER ------------------- */

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!this.getWorld().isClient) {
            this.dropStack(new ItemStack(Items.COAL, this.random.nextBetween(1, 3)));
        }
    }

    @Override
    public boolean canTarget( LivingEntity target) {
        // All infernum creatures ignore their god
        return !(target instanceof InfernumEntity);
    }
}
