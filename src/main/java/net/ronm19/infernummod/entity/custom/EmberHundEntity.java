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
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.ronm19.infernummod.entity.ai.custom.ModTameableAnimal;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class EmberHundEntity extends ModTameableAnimal {

    // ---------------- ANIMATIONS ----------------
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState sitAnimationState = new AnimationState();

    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(EmberHundEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public EmberHundEntity( EntityType<? extends ModTameableAnimal> type, World world ) {
        super(type, world);
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createEmberHundAttributes() {
        return TameableEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 70.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.4)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3);
    }

    // ---------------- ANIM LOGIC ----------------
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

        if (isInSittingPose()) {
            sitAnimationState.startIfNotRunning(this.age);
        } else {
            sitAnimationState.stop();
        }
    }

    @Override
    protected void updateLimbs( float v ) {
        float f = (this.getPose() == EntityPose.STANDING)
                ? Math.min(v * 6.0F, 1.0F)
                : 0.0F;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    // ---------------- TICK ----------------
    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }
    }

    // ---------------- DATA TRACKER ----------------
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
    }

    @Override
    protected Ingredient getTamingIngredient() {
        return Ingredient.ofItems(ModItems.NETHER_RUBY);
    }

    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    public void setAttacking( boolean attacking ) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    // ---------------- INTERACTION / TAMING ----------------
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // --- CLIENT SIDE PREVIEW ---
        if (this.getWorld().isClient) {
            boolean isTameItem = stack.isOf(ModItems.NETHER_RUBY);
            if (this.isTamed()) {
                // Owner can always interact → show hand swing
                return this.isOwner(player) ? ActionResult.SUCCESS : ActionResult.PASS;
            } else {
                // If not tamed yet, but holding Nether Ruby → show hand swing
                return isTameItem ? ActionResult.CONSUME : ActionResult.PASS;
            }
        }

        // --- SERVER SIDE LOGIC ---
        if (this.isTamed()) {
            if (this.isOwner(player)) {
                // Heal with Nether Ruby if hurt
                if (stack.isOf(ModItems.NETHER_RUBY) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                    this.heal(8.0F);
                    this.playSound(SoundEvents.ENTITY_FOX_EAT, 1.0F, 1.0F);
                    return ActionResult.SUCCESS;
                }

                // Empty hand toggles sit/stand
                if (stack.isEmpty()) {
                    boolean sitting = this.isSitting();
                    this.setSitting(sitting);
                    return ActionResult.SUCCESS;
                }
            }
            return super.interactMob(player, hand);
        } else {
            // Try to tame with Nether Ruby
            if (stack.isOf(ModItems.NETHER_RUBY)) {
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                if (this.random.nextInt(3) == 0) { // 33% chance
                    this.setOwner(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.getWorld().sendEntityStatus(this, (byte)7); // hearts
                } else {
                    this.getWorld().sendEntityStatus(this, (byte)6); // smoke
                }

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }

// ---------------- BREEDING ----------------
    @Override
    public boolean isBreedingItem( ItemStack stack ) {
        return stack.isOf(ModItems.NETHER_RUBY);
    }

    @Nullable
    @Override
    public PassiveEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return ModEntities.EMBER_HUND.create(world);
    }

    // ---------------- GOALS ----------------
    @Override
    protected void registerTameableGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.1D, 6.0F, 2.0F, false));
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(4, new TemptGoal(this, 1.2D, Ingredient.ofItems(ModItems.NETHER_RUBY), false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.goalSelector.add(8, new SwimGoal(this));

        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, HostileEntity.class, true));
    }

    // ---------------- IMMUNITIES ----------------
    @Override
    public boolean isFireImmune() {
        return true;
    }

    // ---------------- SOUND EVENTS ----------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WOLF_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.25F, 1.0F);
    }

    // ---------------- OTHER ----------------
    @Override
    public boolean canImmediatelyDespawn( double distanceSquared ) {
        return false; // Don’t despawn tamed fire pets
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return super.getOwner();
    }
}