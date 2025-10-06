package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ai.infernal_hoarde.FollowGroupGoal;
import net.ronm19.infernummod.entity.ai.infernal_hoarde.InfernalHoardeAttackGoal;


public class InfernalHoardeEntity extends HostileEntity implements Monster {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public InfernalHoardeEntity( EntityType<? extends HostileEntity> entityType, World world ) {
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

        if (!this.isAttacking()) {
            attackAnimationState.stop();
        }
    }

    @Override
    protected void updateLimbs( float posDelta ) {
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
    public static DefaultAttributeContainer.Builder createInfernalHoardeAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0) // 20 hearts
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.5) // moderate damage
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3) // slightly fast
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 45.0) // can spot player from mid-range
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.3)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2);

    }

    @Override
    protected void initGoals() {
        // Basic hostile mob goals
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new InfernalHoardeAttackGoal(this, 1D, true));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, false)); // Attack player with melee
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D));    // Wander when idle
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // Look at nearby players
        this.goalSelector.add(5, new LookAroundGoal(this)); // Idle looking around
        this.goalSelector.add(6, new FollowGroupGoal(this, 2D, 4.0F, 16.0F));

        // Targeting goals
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true, true)); // Aggro on players
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, VillagerEntity.class, true, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PyerlingWyrnEntity.class, true, true));
        this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge(InfernalHoardeEntity.class)); // Attack back if hurt
    }

    @Override
    public boolean tryAttack( Entity target) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof LivingEntity living) {
            living.setOnFireFor(4); // burns for 4 seconds
        }
        return success;
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
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_BLAZE_HURT; // guttural hurt sound
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH; // big demonic death roar
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.BLOCK_NETHERRACK_STEP, 0.25F, 1.0F);
        // heavy footsteps, quieter volume
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        // All infernum creatures ignore their god
        return !(target instanceof InfernumEntity);
    }
}
