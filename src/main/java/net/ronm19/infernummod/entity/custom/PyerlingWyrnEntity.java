package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.ronm19.infernummod.entity.ai.ProtectOwnerGoal;
import net.ronm19.infernummod.entity.ai.PyerlingWyrnAttackGoal;
import net.ronm19.infernummod.item.ModItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;

public class PyerlingWyrnEntity extends TameableEntity implements Mount, RangedAttackMob, Tameable, MountableEntity {

    private static final Logger LOGGER = LogManager.getLogger("InfernumMod:PyerlingWyrn");

    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(PyerlingWyrnEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;

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
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createPyerlingWyrnAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.19f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 120.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.6)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
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
        for (Entity passenger : this.getPassengerList()) {
            if (passenger instanceof LivingEntity living) {
                living.setFireTicks(0);
                if (this.getWorld().isClient()) {
                    this.setupAnimationStates();

                    if (!this.getWorld().isClient) {
                        LivingEntity target = findNearestHostile(16.0D); // 16 blocks range

                        if (target != null && this.age % 40 == 0) { // every ~2 seconds
                            shootAt(target, 1.0F);
                        }
                            if (passenger instanceof PlayerEntity player) {
                                player.setFireTicks(0); // extra safety
                                player.addStatusEffect(new StatusEffectInstance(
                                        StatusEffects.FIRE_RESISTANCE,
                                        10, // duration in ticks (0.5 second)
                                        0,  // amplifier (level 1)
                                        true,  // ambient (small particles)
                                        false  // showParticles
                                ));
                            }
                        }


                        // Debug throttle decrement
                        if (this.debugThrottle > 0) this.debugThrottle--;
                    }
                }
            }
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
        this.goalSelector.add(8, new FollowMobGoal(this, PyerlingWyrnEntity.class.getModifiers(), 2.0F, 15.0F));
        this.goalSelector.add(9, new FollowOwnerGoal(this, 2D, 2, 19, false));
        this.goalSelector.add(10, new ProtectOwnerGoal(this));
        this.goalSelector.add(11, new TrackOwnerAttackerGoal(this));
        this.goalSelector.add(12, new AttackWithOwnerGoal(this));
        this.goalSelector.add(13, new MeleeAttackGoal(this, 1.2D, false) {
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
    public boolean doesRenderOnFire() {
        return false; // hide fire overlay
    }

    @Override
    public boolean canWalkOnFluid( FluidState state ) {
        return state.isIn(FluidTags.LAVA);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity attacker = source.getAttacker();
        // If the attacker is the controlling passenger, ignore
        if (attacker != null && this.hasPassenger(attacker)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
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
            if (!player.isSneaking()) {
                setRiding(player);
            } else {
                boolean sitting = !isSitting();
                setSitting(sitting);
                setInSittingPose(sitting);
            }

            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }


    @Override
    public @Nullable UUID getOwnerUuid() {
        return super.getOwnerUuid();
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
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
    public void shootAt(LivingEntity target, float pullProgress) {
        if (!this.getWorld().isClient) {
            double dX = target.getX() - this.getX();
            double dY = target.getBodyY(0.5) - this.getBodyY(0.5);
            double dZ = target.getZ() - this.getZ();

            SmallFireballEntity fireball = new SmallFireballEntity(
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

    private LivingEntity findNearestHostile(double radius) {
        return this.getWorld()
                .getEntitiesByClass(HostileEntity.class, this.getBoundingBox().expand(radius), e -> e.isAlive())
                .stream()
                .min(Comparator.comparingDouble(this::squaredDistanceTo))
                .orElse(null);
    }


    // ---------------- RIDING METHODS ----------------

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity first = this.getFirstPassenger();
        return first instanceof LivingEntity ? (LivingEntity) first : null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().isEmpty() && passenger instanceof LivingEntity;
    }

    private void setRiding(PlayerEntity pPlayer) {
        this.setInSittingPose(false);
        // align at mount time; don't keep forcing it afterward
        pPlayer.setYaw(this.getYaw());
        pPlayer.setPitch(this.getPitch());
        pPlayer.startRiding(this);
    }

    /**
     * Return an offset in the MOUNT'S LOCAL SPACE (X = right, Y = up from feet,
     * Z = forward). We'll rotate it by the BODY yaw so head turns don't shift the seat.
     */
    // --- tuning knobs (updated) ---
    private static final double SEAT_SIDE = 0.12; // centered left/right
    private static final double SEAT_BACK = -0.12; // was 1.05 → move forward (closer to shoulders)
    private static final double SEAT_HEIGHT = 0.23; // was 0.60 → drop a bit lower


    // Seat offset in local space, rotated by BODY yaw (not head)
    @Override
    public Vec3d getPassengerAttachmentPoint(Entity passenger, EntityDimensions dims, float tickDelta) {
        double localX = SEAT_SIDE;
        double localY = getMountedHeightOffset() + passenger.getRidingOffset(this);
        double localZ = -SEAT_BACK;
        float yawRad = (float) Math.toRadians(this.bodyYaw);
        double rx = localX * Math.cos(yawRad) - localZ * Math.sin(yawRad);
        double rz = localX * Math.sin(yawRad) + localZ * Math.cos(yawRad);
        return new Vec3d(rx, localY, rz);
    }

    // Seat baseline (height)
    @Override
    public double getMountedHeightOffset() {
        return this.getHeight() * SEAT_HEIGHT;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater updater) {
        Vec3d a = getPassengerAttachmentPoint(passenger, passenger.getDimensions(passenger.getPose()), 1.0F);
        updater.accept(passenger, this.getX() + a.x, this.getY() + a.y, this.getZ() + a.z);
        if (passenger instanceof LivingEntity living) {
            living.bodyYaw = this.bodyYaw; // keep torso aligned with spine
        }
    }


    @Override
    public void travel(Vec3d input) {
        LivingEntity ctrl = getControllingPassenger();
        if (this.hasPassengers() && ctrl instanceof PlayerEntity rider) {
            // steer by rider
            this.setYaw(rider.getYaw());
            this.setPitch(rider.getPitch() * 0.5F);
            this.bodyYaw = this.getYaw();
            this.headYaw = this.bodyYaw;

            float strafe = rider.sidewaysSpeed * 0.5F;
            float forward = rider.forwardSpeed;
            if (forward <= 0.0F) forward *= 0.25F;

            float baseSpeed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            this.setMovementSpeed(rider.isSprinting() ? baseSpeed * 2.0F : baseSpeed); // no MinecraftClient here

            super.travel(new Vec3d(strafe, input.y, forward));
        } else {
            super.travel(input);
        }

        BlockPos blockPos = this.getBlockPos().down(); // block below feet
        FluidState below = this.getWorld().getFluidState(blockPos);

        if (below.isIn(FluidTags.LAVA) && this.canWalkOnFluid(below)) {
            // Walk *on* lava
            this.setOnGround(true);
            this.setVelocity(this.getVelocity().x, 0.0D, this.getVelocity().z);

            // Snap the entity just above the lava surface
            double lavaSurface = blockPos.getY() + 1.0D; // top of block
            if (this.getY() < lavaSurface) {
                this.setPos(this.getX(), lavaSurface, this.getZ());
            }
        }
    }


    @Override
    public Vec3d updatePassengerForDismount( LivingEntity passenger ) {
        Direction direction = this.getMovementDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.updatePassengerForDismount(passenger);
        }
        int[][] is = Dismounting.getDismountOffsets(direction);
        BlockPos blockPos = this.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (EntityPose entityPose : passenger.getPoses()) {
            Box box = passenger.getBoundingBox(entityPose);
            for (int[] js : is) {
                mutable.set(blockPos.getX() + js[0], blockPos.getY(), blockPos.getZ() + js[1]);
                double d = this.getWorld().getDismountHeight(mutable);
                if (!Dismounting.canDismountInBlock(d)) continue;
                Vec3d vec3d = Vec3d.ofCenter(mutable, d);
                if (!Dismounting.canPlaceEntityAt(this.getWorld(), passenger, box.offset(vec3d))) continue;
                passenger.setPose(entityPose);
                return vec3d;
            }
        }
        return super.updatePassengerForDismount(passenger);
    }
}

// keep your existing getMountedHeightOffset() method as-is
