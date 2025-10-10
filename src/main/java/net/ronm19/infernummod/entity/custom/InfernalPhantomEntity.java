package net.ronm19.infernummod.entity.custom;


import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.FlyingMobUtil;
import net.ronm19.infernummod.api.interfaces.MountableEntity;
import net.ronm19.infernummod.entity.ai.ProtectOwnerGoal;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.UUID;

public class InfernalPhantomEntity extends TameableEntity implements FlyingMobUtil, RangedAttackMob, MountableEntity, Mount {

    private int portalTime;
    private int portalCooldown = 300; // 15 seconds (same as players)


    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public InfernalPhantomEntity( EntityType<? extends TameableEntity> entityType, World world ) {
        super(entityType, world);
        this.setNoGravity(true);
    }


    // ---------------- ATTRIBUTES & GOALS ----------------
    public static DefaultAttributeContainer.Builder createInfernalPhantomAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 70.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.19f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.19f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 120.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.6)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void initGoals() {
        // Use your attack goal and others
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
        this.goalSelector.add(3, new SitGoal(this));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new SwimGoal(this));
        this.goalSelector.add(6, new TrackOwnerAttackerGoal(this));
        this.goalSelector.add(7, new AttackWithOwnerGoal(this));
        this.goalSelector.add(8, new ProtectOwnerGoal(this));
        this.goalSelector.add(9, new WanderAroundFarGoal(this, 2.0D, 1.0F));
        this.goalSelector.add(10, new FollowParentGoal(this, 2.0D));
        this.goalSelector.add(11, new TemptGoal(this, 2.0D, Ingredient.ofItems(ModItems.INFERNAL_BEAST_HORN), false));
        this.goalSelector.add(12, new AnimalMateGoal(this, 2.0D));
        this.goalSelector.add(13, new ProjectileAttackGoal(this, 1.0D, 10, 20.0F) {
            @Override
            public boolean canStart() {
                return super.canStart() && InfernalPhantomEntity.this.getTarget() != null
                        && InfernalPhantomEntity.this.squaredDistanceTo(InfernalPhantomEntity.this.getTarget()) >= 9.0D;
            }
        });


        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, false));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, SlimeEntity.class, false));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, ObsidianGhastEntity.class, false));
        this.targetSelector.add(3, new RevengeGoal(this));
    }

    // ========= ANIMATIONS =========
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
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

    // ========= TICK =========

    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();
    }

    // ========= IMMUNITIES =========

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean isImmuneToExplosion() {
        return true;
    }

    @Override
    public boolean damage( DamageSource source, float amount ) {
        Entity attacker = source.getAttacker();
        // If the attacker is the controlling passenger, ignore
        if (attacker != null && this.hasPassenger(attacker)) {
            return false;
        }
        return super.damage(source, amount);
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

        Item itemForTaming = ModItems.BLAZE_HEART;

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
    protected void tickPortal() {
        // Prevent vanilla dismount logic
        if (this.hasPassengers()) {
            Entity rider = this.getFirstPassenger();

            // handle portal cooldowns
            if (this.portalTime++ >= this.getMaxNetherPortalTime()) {
                this.portalTime = this.getMaxNetherPortalTime();
                this.timeUntilRegen = this.getPortalCooldown(); // portal cooldown like vanilla

                if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
                    ServerWorld destination = serverWorld.getServer()
                            .getWorld(this.getWorld().getRegistryKey() == World.NETHER ? World.OVERWORLD : World.NETHER);

                    if (destination != null) {
                        // transfer both the wyrn and its rider
                        Entity teleported = this.moveToWorld(destination);
                        if (teleported instanceof PyerlingWyrnEntity newWyrn) {
                            if (rider != null && !rider.hasVehicle()) {
                                Entity newRider = rider.moveToWorld(destination);
                                if (newRider instanceof PlayerEntity player) {
                                    player.requestTeleport(
                                            newWyrn.getX(), newWyrn.getY() + newWyrn.getMountedHeightOffset(), newWyrn.getZ());
                                    player.startRiding(newWyrn, true);
                                } else if (newRider != null) {
                                    newRider.requestTeleport(
                                            newWyrn.getX(), newWyrn.getY() + newWyrn.getMountedHeightOffset(), newWyrn.getZ());
                                    newRider.startRiding(newWyrn, true);
                                }
                            }
                        }
                    }
                }


            }
        } else {
            super.tickPortal(); // normal portal behaviour if not ridden
        }
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
    public @Nullable UUID getOwnerUuid() {
        return super.getOwnerUuid();
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (!this.getWorld().isClient) {
            this.dropStack(new ItemStack(Items.BLAZE_POWDER, 1));
        }
    }


    @Override
    public @Nullable PassiveEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return null;
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
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

    private LivingEntity findNearestHostile(double radius) {
        return this.getWorld()
                .getEntitiesByClass(HostileEntity.class, this.getBoundingBox().expand(radius), e -> e.isAlive())
                .stream()
                .min(Comparator.comparingDouble(this::squaredDistanceTo))
                .orElse(null);
    }

    /* ------------ SOUNDS ------------ */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.BLOCK_BASALT_STEP, 0.4F, 1.2F);
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
    private static final double SEAT_SIDE = 0.06; // centered left/right
    private static final double SEAT_BACK = -0.08; // was 1.05 → move forward (closer to shoulders)
    private static final double SEAT_HEIGHT = 0.35; // was 0.60 → drop a bit lower


    // Seat offset in local space, rotated by BODY yaw (not head)
    @Override
    public Vec3d getPassengerAttachmentPoint( Entity passenger, EntityDimensions dims, float tickDelta) {
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
            // --------- Riding logic ---------
            this.setYaw(rider.getYaw());
            this.setPitch(rider.getPitch() * 0.5F);
            this.bodyYaw = this.getYaw();
            this.headYaw = this.bodyYaw;

            // ----- Horizontal responsiveness -----
            float strafe  = rider.sidewaysSpeed * 0.8F;   // faster strafing
            float forward = rider.forwardSpeed * 1.2F;    // faster forward
            if (forward <= 0.0F) forward *= 0.25F;

            float baseSpeed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            this.setMovementSpeed(rider.isSprinting() ? baseSpeed * 2.5F : baseSpeed * 1.5F);

            // Vanilla-style horizontal control (no Y input)
            super.travel(new Vec3d(strafe, 0.0D, forward));

            // --------- Elytra-like Hover Vertical Control ---------
            double climbRate = 0.20D;
            double diveRate  = 0.18D;
            double vyTarget;

            float pitch = rider.getPitch(); // -90 = look up, +90 = look down
            if (pitch < -10F) {                  // looking up
                vyTarget = +climbRate;
            } else if (pitch > 25F) {            // looking down
                vyTarget = -diveRate;
            } else {
                vyTarget = 0.0D;                 // hover flat — no drift
            }

            Vec3d v = this.getVelocity();
            double blendedY = v.y * 0.5D + vyTarget;  // smooth out transitions
            double maxVy = 0.5D;
            if (blendedY >  maxVy) blendedY =  maxVy;
            if (blendedY < -maxVy) blendedY = -maxVy;

            // When hovering (neutral pitch), freeze altitude
            if (vyTarget == 0.0D) blendedY = 0.0D;

            this.setVelocity(v.x, blendedY, v.z);
        } else {
            // --------- Free-flying AI movement ---------
            handleFlyingTravel(this, input, this.getVelocityAffectingPos());
        }

        // --------- Lava walking logic ---------
        BlockPos blockPos = this.getBlockPos().down();
        FluidState below = this.getWorld().getFluidState(blockPos);

        if (below.isIn(FluidTags.LAVA) && this.canWalkOnFluid(below)) {
            this.setOnGround(true);
            this.setVelocity(this.getVelocity().x, 0.0D, this.getVelocity().z);

            double lavaSurface = blockPos.getY() + 1.0D;
            if (this.getY() < lavaSurface) {
                this.setPos(this.getX(), lavaSurface, this.getZ());
            }

            this.updateLimbs(false);
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

    // ---------------- FLYING ----------------- //

    protected float getActiveEyeHeight( EntityPose pose, EntityDimensions dimensions ) {
        return 2.6F;
    }

    static class InfernalPhantomMoveControl extends MoveControl {
        private final InfernalPhantomEntity infernalPhantomEntity;
        private int collisionCheckCooldown;

        public InfernalPhantomMoveControl( InfernalPhantomEntity infernalPhantomEntity ) {
            super(infernalPhantomEntity);
            this.infernalPhantomEntity = infernalPhantomEntity;
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                if (this.collisionCheckCooldown-- <= 0) {
                    this.collisionCheckCooldown += this.infernalPhantomEntity.getRandom().nextInt(5) + 2;
                    Vec3d vec3d = new Vec3d(this.targetX - this.infernalPhantomEntity.getX(), this.targetY - this.infernalPhantomEntity.getY(), this.targetZ - this.infernalPhantomEntity.getZ());
                    double d = vec3d.length();
                    vec3d = vec3d.normalize();
                    if (this.willCollide(vec3d, MathHelper.ceil(d))) {
                        this.infernalPhantomEntity.setVelocity(this.infernalPhantomEntity.getVelocity().add(vec3d.multiply(0.1)));
                    } else {
                        this.state = State.WAIT;
                    }
                }

            }
        }

        static class FlyRandomlyGoal extends Goal {
            private final InfernalPhantomEntity infernalPhantomEntity;

            public FlyRandomlyGoal(InfernalPhantomEntity infernalPhantomEntity) {
                this.infernalPhantomEntity = infernalPhantomEntity;
                this.setControls(EnumSet.of(Control.MOVE));
            }

            public boolean canStart() {
                MoveControl moveControl = this.infernalPhantomEntity.getMoveControl();
                if (!moveControl.isMoving()) {
                    return true;
                } else {
                    double d = moveControl.getTargetX() - this.infernalPhantomEntity.getX();
                    double e = moveControl.getTargetY() - this.infernalPhantomEntity.getY();
                    double f = moveControl.getTargetZ() - this.infernalPhantomEntity.getZ();
                    double g = d * d + e * e + f * f;
                    return g < (double)1.0F || g > (double)3600.0F;
                }
            }

            public boolean shouldContinue() {
                return false;
            }

            public void start() {
                Random random = this.infernalPhantomEntity.getRandom();
                double d = this.infernalPhantomEntity.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                double e = this.infernalPhantomEntity.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                double f = this.infernalPhantomEntity.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                this.infernalPhantomEntity.getMoveControl().moveTo(d, e, f, (double)1.0F);
            }
        }

        static class LookAtTargetGoal extends Goal {
            private final InfernalPhantomEntity infernalPhantomEntity;

            public LookAtTargetGoal(InfernalPhantomEntity infernalPhantomEntity) {
                this.infernalPhantomEntity = infernalPhantomEntity;
                this.setControls(EnumSet.of(Control.LOOK));
            }

            public boolean canStart() {
                return true;
            }

            public boolean shouldRunEveryTick() {
                return true;
            }

            public void tick() {
                if (this.infernalPhantomEntity.getTarget() == null) {
                    Vec3d vec3d = this.infernalPhantomEntity.getVelocity();
                    this.infernalPhantomEntity.setYaw(-((float)MathHelper.atan2(vec3d.x, vec3d.z)) * (180F / (float)Math.PI));
                    this.infernalPhantomEntity.bodyYaw = this.infernalPhantomEntity.getYaw();
                } else {
                    LivingEntity livingEntity = this.infernalPhantomEntity.getTarget();
                    double d = (double)64.0F;
                    if (livingEntity.squaredDistanceTo(this.infernalPhantomEntity) < (double)4096.0F) {
                        double e = livingEntity.getX() - this.infernalPhantomEntity.getX();
                        double f = livingEntity.getZ() - this.infernalPhantomEntity.getZ();
                        this.infernalPhantomEntity.setYaw(-((float)MathHelper.atan2(e, f)) * (180F / (float)Math.PI));
                        this.infernalPhantomEntity.bodyYaw = this.infernalPhantomEntity.getYaw();
                    }
                }

            }
        }

        private boolean willCollide( Vec3d direction, int steps ) {
            Box box = this.infernalPhantomEntity.getBoundingBox();

            for (int i = 1; i < steps; ++i) {
                box = box.offset(direction);
                if (!this.infernalPhantomEntity.getWorld().isSpaceEmpty(this.infernalPhantomEntity, box)) {
                    return false;
                }
            }

            return true;
        }
    }

    protected void fall( double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {

    }

    public boolean isClimbing() {
        return false;
    }

    public int getLimitPerChunk() {
        return 1;
    }
}