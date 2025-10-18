package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.ai.goals.infernal_eye.InfernalEyeShootGoal;
import net.ronm19.infernummod.entity.ai.goals.ProtectOwnerGoal;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class InfernalEyeEntity extends TameableEntity implements Tameable, RangedAttackMob {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;

    private static final TrackedData<Optional<UUID>> OWNER =
            DataTracker.registerData(InfernalEyeEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Boolean> SITTING =
            DataTracker.registerData(InfernalEyeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SHOOTING =
            DataTracker.registerData(InfernalEyeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int skullStrength = 3;

    public InfernalEyeEntity( EntityType<? extends TameableEntity> type, World world ) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 10, true);
    }

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

    // === Attributes ===
    public static DefaultAttributeContainer.Builder createInfernalEyeAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 65.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.35D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 1200.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.7);
    }

    public static boolean canDestroy( BlockState block ) {
        return !block.isAir() && !block.isIn(BlockTags.WITHER_IMMUNE);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public @Nullable PassiveEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return ModEntities.INFERNAL_EYE.create(world);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        Item item = itemstack.getItem();

        Item itemForTaming = ModItems.INFERNIUM;

        if(item == itemForTaming && !isTamed()) {
            if(!this.getWorld().isClient()) {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }

                super.setOwner(player); // Use super.setOwner for the base method
                this.navigation.recalculatePath();
                this.setTarget(null);
                this.getWorld().sendEntityStatus(this, (byte)7);
                setSitting(true); // Call your setSitting method, which uses the data tracker
            }
            return ActionResult.SUCCESS;
        }

        if(isTamed() && hand == Hand.MAIN_HAND && isOwner(player)) {
            if(!this.getWorld().isClient()) {
                boolean sitting = !isSitting();
                setSitting(sitting);
            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }


    // === Data tracker ===
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OWNER, Optional.empty());
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(SHOOTING, false);
    }

    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }

    public void setShooting( boolean shooting ) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    public int getSkullStrength() {
        return skullStrength;
    }

    public void setSkullStrength( int strength ) {
        this.skullStrength = Math.max(0, strength);
    }

    // === Tameable implementation ===
    @Nullable
    @Override
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER).orElse(null);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    public void setOwnerUuid( @Nullable UUID uuid ) {
        this.dataTracker.set(OWNER, Optional.ofNullable(uuid));
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID id = this.getOwnerUuid();
        return id == null ? null : this.getWorld().getPlayerByUuid(id);
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
        this.goalSelector.add(1, new InfernalEyeShootGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(4, new SwimGoal(this));
        this.goalSelector.add(5, new FollowMobGoal(this, InfernalEyeEntity.class.getModifiers(), 2.0F, 16.0F));
        this.goalSelector.add(6, new ProjectileAttackGoal(this, 1.0D, 20, 1200.0F));
        this.goalSelector.add(5, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
        this.goalSelector.add(8, new TrackOwnerAttackerGoal(this));
        this.goalSelector.add(9, new AttackWithOwnerGoal(this));
        this.goalSelector.add(10, new ProtectOwnerGoal(this));
        this.goalSelector.add(11, new WanderAroundFarGoal(this, 2.0D, 1.0F));
        this.goalSelector.add(12, new FlyGoal(this, 2.0D));
        this.goalSelector.add(13, new FollowParentGoal(this, 2.0D));
        this.goalSelector.add(14, new TemptGoal(this, 2.0D, Ingredient.ofItems(ModItems.FIRERITE), false));
        this.goalSelector.add(15, new AnimalMateGoal(this, 2.0D));
        this.goalSelector.add(16, new SitGoal(this));


        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, false));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, ObsidianGhastEntity.class, false));
        this.targetSelector.add(3, new RevengeGoal(this));
    }

    // Ranged Attack //

    @Override
    public void shootAt( LivingEntity target, float pullProgress ) {
        if (!this.getWorld().isClient && this.isShooting()) {
            double dx = target.getX() - this.getX();
            double dy = target.getBodyY(0.5) - this.getBodyY(0.5);
            double dz = target.getZ() - this.getZ();

            InfernalSkullEntity skull = new InfernalSkullEntity(
                    this.getWorld(),
                    this,
                    dx, dy, dz
                    // <— now used here

            );
            // Maybe scale with health or difficulty
            if (this.getHealth() < this.getMaxHealth() / 2) {
                this.setSkullStrength(4); // stronger when weakened
            }


            skull.setPosition(this.getX(), this.getBodyY(0.5) + 0.5, this.getZ());
            skull.setVelocity(dx, dy, dz, 6.0F, 0.02F);


            this.getWorld().spawnEntity(skull);
            this.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 1.0F, 0.8F + this.random.nextFloat() * 0.4F);

            // reset shooting state after firing
            this.setShooting(false);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // Already invulnerable to this source? Bail out.
        if (this.isInvulnerableTo(source)) {
            return false;
        }

        // Immune to WITHER damage & effects
        if (source.isOf(DamageTypes.WITHER) || source.isOf(DamageTypes.WITHER_SKULL)) {
            return false;
        }

        // Immune to any explosions (mob or block caused)
        if (source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.PLAYER_EXPLOSION)) {
            return false;
        }

        // Immune to our custom InfernalSkull explosions / direct hits
        if (source.getSource() instanceof InfernalSkullEntity) {
            return false;
        }

        // Optional: also ignore damage if *attacker* is the same owner/friendly
        if (source.getAttacker() instanceof InfernalEyeEntity) {
            return false;
        }

        // Allow normal projectile damage (like arrows, tridents)
        if (source.getSource() instanceof PersistentProjectileEntity) {
            return super.damage(source, amount);
        }

        // For everything else, use default
        return super.damage(source, amount);
    }



    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();
    }

    // === Save/Load NBT ===
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("OwnerUuid", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("OwnerUuid")) {
            this.setOwnerUuid(nbt.getUuid("OwnerUuid"));
        }
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
    }

    @Override
    public void setInSittingPose(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
    }

    // ---------------- SOUND EVENTS ----------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    public void travel( Vec3d movementInput) {
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply((double)0.8F));
            } else if (this.isInLava()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply((double)0.5F));
            } else {
                float f = 0.91F;
                if (this.isOnGround()) {
                    f = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.91F;
                }

                float g = 0.16277137F / (f * f * f);
                f = 0.91F;
                if (this.isOnGround()) {
                    f = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.91F;
                }

                this.updateVelocity(this.isOnGround() ? 0.1F * g : 0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply((double)f));
            }
        }

        this.updateLimbs(false);
    }

    public boolean isClimbing() {
        return false;
    }
}
