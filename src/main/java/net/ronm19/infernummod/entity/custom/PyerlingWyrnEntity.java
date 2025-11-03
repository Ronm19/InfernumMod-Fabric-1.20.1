package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
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
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.MountableEntity;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.ai.goals.ProtectOwnerGoal;
import net.ronm19.infernummod.entity.ai.goals.pyerling_wyrn.PyerlingWyrnAttackGoal;
import net.ronm19.infernummod.item.ModItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;


public class PyerlingWyrnEntity extends TameableEntity implements RangedAttackMob {

    private static final Logger LOGGER = LogManager.getLogger("InfernumMod:PyerlingWyrn");

    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(PyerlingWyrnEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> TAMED =
            DataTracker.registerData(PyerlingWyrnEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OWNER_UUID =
            DataTracker.registerData(PyerlingWyrnEntity.class, TrackedDataHandlerRegistry.STRING);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;

    private int portalTime;
    private int portalCooldown = 300; // 15 seconds (same as players)


    // Debug toggle for rider coordinates (set true while testing only)
    private boolean debugRiderPositions = false;
    private int debugThrottle = 0; // simple throttle to avoid spam

    public PyerlingWyrnEntity( EntityType<? extends TameableEntity> entityType, World world ) {
        super(entityType, world);
    }

    // ---------------- ATTACKING ----------------
    public void setAttacking( boolean attacking ) {
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
        this.dataTracker.startTracking(TAMED, false);
        this.dataTracker.startTracking(OWNER_UUID, "");
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createPyerlingWyrnAttributes() {
        return TameableEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 110.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.19f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 120.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.6)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5)
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, 1.5f);
    }

    // ---------------- ANIMATIONS ----------------
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

    // ---------------- TICK ----------------
    @Override
    public void tick() {
        super.tick();
        this.setFireTicks(0);
        this.setupAnimationStates();

        // 🔥 Handle rider protection and lava/fire logic
        for (Entity passenger : this.getPassengerList()) {
            if (passenger instanceof PlayerEntity living) {
                living.setFireTicks(0);

                // Fire immunity always
                living.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.FIRE_RESISTANCE,
                        10,  // duration
                        0,   // amplifier
                        true,
                        false
                ));

                // 💥 Explosion damage nullifier
                DamageSource recent = living.getRecentDamageSource();
                if (recent != null && (
                        recent.isOf(DamageTypes.EXPLOSION)
                                || recent.isOf(DamageTypes.PLAYER_EXPLOSION)
                                || recent.getType().msgId().contains("explosion")
                )) {

                    // Cancel explosion damage effects immediately
                    living.hurtTime = 0;
                    living.timeUntilRegen = 0;
                    living.setHealth(living.getHealth());
                }

                // Safety: prevent fall damage accumulation while mounted
                living.fallDistance = 0.0F;

                // Extra: suppress knockback from explosions
                if (living.getVelocity().lengthSquared() > 0) {
                    living.setVelocity(living.getVelocity().multiply(0.5D, 1.0D, 0.5D));
                }
            }
        }

        // 🧠 Server-side combat AI
        if (!this.getWorld().isClient) {
            LivingEntity target = findNearestHostile(16.0D);
            if (target != null && this.age % 40 == 0) { // attack every 2 seconds
                shootAt(target, 1.0F);
            }
        }

        // 🧩 Debug throttle logic
        if (this.debugThrottle > 0) this.debugThrottle--;
    }


    // ---------------- BREEDING ----------------
    @Override
    public PyerlingWyrnEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return ModEntities.PYERLING_WYRN.create(world);
    }

    // ---------------- GOALS ----------------
    @Override
    protected void initGoals() {
        // Use your attack goal and others
        this.goalSelector.add(0, new PyerlingWyrnAttackGoal(this, 2D, 1, 3, 18f));
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(4, new TemptGoal(this, 2.0D, Ingredient.ofItems(ModItems.NETHER_RUBY), false));
        this.goalSelector.add(5, new SwimGoal(this));
        this.goalSelector.add(6, new FollowParentGoal(this, 1D));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1D));
        this.goalSelector.add(8, new TrackOwnerAttackerGoal(this));
        this.goalSelector.add(9, new AttackWithOwnerGoal(this));
        this.goalSelector.add(10, new FollowOwnerGoal(this, 2.0D, 1F, 29F, false));
        this.goalSelector.add(10, new ProtectOwnerGoal(this));
        this.goalSelector.add(11, new FollowMobGoal(this, PyerlingWyrnEntity.class.getModifiers(), 2.0F, 15.0F));
        this.goalSelector.add(12, new MeleeAttackGoal(this, 1.2D, false) {
            @Override
            public boolean canStart() {
                return super.canStart() && PyerlingWyrnEntity.this.getTarget() != null
                        && PyerlingWyrnEntity.this.squaredDistanceTo(PyerlingWyrnEntity.this.getTarget()) < 9.0D;
            }
        });

        this.goalSelector.add(14, new ProjectileAttackGoal(this, 1.0D, 10, 20.0F) {
            @Override
            public boolean canStart() {
                return super.canStart() && PyerlingWyrnEntity.this.getTarget() != null
                        && PyerlingWyrnEntity.this.squaredDistanceTo(PyerlingWyrnEntity.this.getTarget()) >= 9.0D;
            }
        });

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, HostileEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ObsidianGhastEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, SlimeEntity.class, false));

        // call integrator hook if you have more AI to append
        integrateAICode();
    }

    /**
     * Hook for your other AI code (AICodeIntegrator)
     */
    private void integrateAICode() {
        // PLACEHOLDER: if you have other AI behaviors to attach, do it here.
        // e.g. this.goalSelector.add(...);
    }

    // ---------------- IMMUNITIES ----------------
    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean isImmuneToExplosion() {
        return true;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false; // hide fire overlay
    }

    @Override
    public boolean canWalkOnFluid( FluidState state ) {
        return state.isIn(FluidTags.LAVA);
    }

    @Override
    public boolean damage( DamageSource source, float amount ) {
        // Rider protection — don't take explosion damage if being ridden
        if (this.hasPassengers()) {
            Entity rider = this.getFirstPassenger();
            if (rider instanceof PlayerEntity player) {
                // Block explosion damage for both rider and mount
                if (source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.PLAYER_EXPLOSION)) {
                    return false;
                }
            }
        }

        // Ignore explosion damage for the wyrn itself too
        if (source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.PLAYER_EXPLOSION)) {
            return false;
        }

        return super.damage(source, amount);
    }

    @Override
    public int getPortalCooldown() {
        return this.portalCooldown;
    }

    @Override
    public int getMaxNetherPortalTime() {
        return 80; // how long entity must stay in portal before teleporting
    }


    @Override
    public EntityDimensions getDimensions( EntityPose pose ) {
        if (this.hasPassengers()) {
            return super.getDimensions(pose).scaled(0.8F, 0.9F); // 80% width, 90% height
        }
        return super.getDimensions(pose);
    }

    // ---------------- TAMEABLE ----------------
    @Override
    public ActionResult interactMob( PlayerEntity player, Hand hand ) {
        ItemStack itemstack = player.getStackInHand(hand);
        Item item = itemstack.getItem();

        Item itemForTaming = ModItems.FIRERITE;

        if (item == itemForTaming && !isTamed()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }

                super.setOwner(player);
                this.navigation.recalculatePath();
                this.setTarget(null);
                this.getWorld().sendEntityStatus(this, (byte) 7);
                setSitting(true);
                setInSittingPose(true);

                return ActionResult.SUCCESS;
            }
        }

        if (isTamed() && hand == Hand.MAIN_HAND && item != itemForTaming) {
            boolean sitting = !isSitting();
            setSitting(sitting);
            setInSittingPose(sitting);

            return ActionResult.SUCCESS;
        }


        return super.interactMob(player, hand);
    }


    // ✅ Owner tracking
    public void setOwnerUuid( UUID uuid ) {
        this.dataTracker.set(OWNER_UUID, uuid.toString());
    }

    public UUID getOwnerUuid() {
        try {
            String s = this.dataTracker.get(OWNER_UUID);
            return s.isEmpty() ? null : UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    public boolean isOwner( PlayerEntity player ) {
        UUID ownerUuid = this.getOwnerUuid();
        return ownerUuid != null && ownerUuid.equals(player.getUuid());
    }

    public boolean isTame() {
        return this.dataTracker.get(TAMED);
    }

    public void setTame( boolean tame ) {
        this.dataTracker.set(TAMED, tame);
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

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.4F, 0.8F + this.random.nextFloat() * 0.4F);
    }

    // ---------------- RANGED ATTACK ----------------
    @Override
    public void shootAt( LivingEntity target, float pullProgress ) {
        if (!this.getWorld().isClient) {
            double dX = target.getX() - this.getX();
            double dY = target.getBodyY(0.5) - this.getBodyY(0.5);
            double dZ = target.getZ() - this.getZ();

            ExplosiveFireballEntity fireball = new ExplosiveFireballEntity(
                    this.getWorld(),
                    this,
                    dX + this.random.nextGaussian() * 0.1,
                    dY,
                    dZ + this.random.nextGaussian() * 0.1
            );

            fireball.setPos(this.getX(), this.getBodyY(0.5) + 0.5, this.getZ());
            this.getWorld().spawnEntity(fireball);

            this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F,
                    0.8F + this.random.nextFloat() * 0.4F);
        }
    }

    private LivingEntity findNearestHostile( double radius ) {
        return this.getWorld()
                .getEntitiesByClass(HostileEntity.class, this.getBoundingBox().expand(radius), e -> e.isAlive())
                .stream()
                .min(Comparator.comparingDouble(this :: squaredDistanceTo))
                .orElse(null);
    }


    // -------------------------- NBT -------------------------

    @Override
    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("TamedByPlayer", this.isTame());
        UUID uuid = this.getOwnerUuid();
        if (uuid != null) nbt.putString("Owner", uuid.toString());
    }

    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TamedByPlayer")) this.setTame(nbt.getBoolean("TamedByPlayer"));
        if (nbt.contains("Owner")) this.setOwnerUuid(UUID.fromString(nbt.getString("Owner")));
    }
}