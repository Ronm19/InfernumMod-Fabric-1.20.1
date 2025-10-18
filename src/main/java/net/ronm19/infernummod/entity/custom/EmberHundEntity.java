package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.ai.goals.ProtectOwnerGoal;
import net.ronm19.infernummod.entity.ai.goals.ember_hund.EmberHundAttackGoal;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;


public class EmberHundEntity extends WolfEntity implements Angerable {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState sitAnimationState = new AnimationState();


    public EmberHundEntity( EntityType<? extends WolfEntity> entityType, World world ) {
        super(entityType, world);
    }

    private static final TrackedData<Boolean> SITTING =
            DataTracker.registerData(EmberHundEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(EmberHundEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    // ========= ANIMATIONS =========
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

        if(isInSittingPose()) {
            sitAnimationState.startIfNotRunning(this.age);
        } else {
            sitAnimationState.stop();
        }
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }

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
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    public static DefaultAttributeContainer.Builder createEmberHundAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 75.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.9D)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.2D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 120.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.7);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setSitting( boolean sitting ) {
        this.dataTracker.set(SITTING, sitting);
    }

    @Override
    protected void initGoals() {
        // Use your attack goal and others
        this.goalSelector.add(0, new EmberHundAttackGoal(this, 1.0D, true));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
        this.goalSelector.add(3, new SitGoal(this));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new SwimGoal(this));
        this.goalSelector.add(6, new FollowMobGoal(this, EmberHundEntity.class.getModifiers(), 3.0F, 28.0F));
        this.goalSelector.add(7, new TrackOwnerAttackerGoal(this));
        this.goalSelector.add(8, new AttackWithOwnerGoal(this));
        this.goalSelector.add(9, new ProtectOwnerGoal(this));
        this.goalSelector.add(10, new WanderAroundFarGoal(this, 2.0D, 1.0F));
        this.goalSelector.add(11, new FollowParentGoal(this, 2.0D));
        this.goalSelector.add(12, new TemptGoal(this, 2.0D, Ingredient.ofItems(ModItems.FIRERITE), false));
        this.goalSelector.add(13, new AnimalMateGoal(this, 2.0D));


        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, false));
        this.targetSelector.add(2, new RevengeGoal(this).setGroupRevenge(EmberHundEntity.class));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        Item item = itemstack.getItem();

        Item itemForTaming = ModItems.INFERNUM_BONE;

        if(item == itemForTaming && !isTamed()) {
            if(this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }

                super.setOwner(player);
                this.navigation.recalculatePath();
                this.setTarget(null);
                this.getWorld().sendEntityStatus(this, (byte)7);

// start standing so it will follow right after taming
                setSitting(false);
                setInSittingPose(false);


                return ActionResult.SUCCESS;
            }
        }

        if(isTamed() && hand == Hand.MAIN_HAND) {
            boolean sitting = !isSitting();
            setSitting(sitting);
            setInSittingPose(sitting);

            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(ATTACKING, false);
    }

    public void setAttacking( boolean attacking ) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);

    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    public @Nullable EmberHundEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return ModEntities.EMBER_HUND.create(world);
    }

    /* ------------ SOUNDS ------------ */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.6F, 1.0F);
    }
}
