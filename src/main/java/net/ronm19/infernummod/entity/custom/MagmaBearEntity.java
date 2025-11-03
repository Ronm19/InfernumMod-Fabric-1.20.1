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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.ronm19.infernummod.api.interfaces.MountableEntity;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class MagmaBearEntity extends PolarBearEntity implements MountableEntity, Tameable {
    private static final TrackedData<Boolean> WARNING;
    private static final TrackedData<Boolean> TAMED = DataTracker.registerData(MagmaBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OWNER_UUID = DataTracker.registerData(MagmaBearEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> ACCEPTED_PLAYER =
            DataTracker.registerData(MagmaBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final float field_30352 = 6.0F;
    private float lastWarningAnimationProgress;
    private float warningAnimationProgress;
    private int warningSoundCooldown;
    private static final UniformIntProvider ANGER_TIME_RANGE;
    private int angerTime;
    @Nullable
    private UUID angryAt;

    // ---- NEW: soft state to prevent AI flicker ----
    private static final int TRUST_DURATION_TICKS = 200;         // 10s
    private static final int RETARGET_COOLDOWN_TICKS = 60;        // 3s
    private int trustUntilTick = 0;       // when temporary trust expires
    private int nextRetargetTick = 0;     // earliest tick we can acquire a new target

    public MagmaBearEntity(EntityType<? extends PolarBearEntity> entityType, World world) {
        super(entityType, world);
        this.setTame(false);
        this.isFireImmune();
    }

    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return (PassiveEntity) ModEntities.MAGMA_BEAR.create(world);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MagmaBearEntity.AttackGoal(this));
        this.goalSelector.add(1, new MagmaBearEntity.MagmaBearEscapeDangerGoal(this));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new MagmaBearEntity.MagmaBearRevengeGoal(this));
        this.targetSelector.add(2, new MagmaBearEntity.ProtectBabiesGoal(this));
        this.targetSelector.add(3, new PlayerIntruderTargetGoal(this));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, FoxEntity.class, 10, true, true, null));
        this.targetSelector.add(5, new UniversalAngerGoal<>(this, false));
    }

    public static DefaultAttributeContainer.Builder createMagmaBearAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0D);
    }

    public static boolean canSpawn(EntityType<PolarBearEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        RegistryEntry<Biome> registryEntry = world.getBiome(pos);
        if (!registryEntry.isIn(BiomeTags.POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS)) {
            return isValidNaturalSpawn(type, world, spawnReason, pos, random);
        } else {
            return isLightLevelValidForNaturalSpawn(world, pos)
                    && world.getBlockState(pos.down()).isIn(BlockTags.POLAR_BEARS_SPAWNABLE_ON_ALTERNATE);
        }
    }

    private boolean isPlayerTooClose(PlayerEntity player) {
        double distSq = this.squaredDistanceTo(player);
        return distSq < 36.0D; // within 6 blocks
    }

    // ---- helpers for clean AI state ----
    private static boolean isCreativeOrSpectator(PlayerEntity p) {
        return p.isSpectator() || (p.getAbilities() != null && p.getAbilities().creativeMode);
    }
    public boolean isTrustedNow() { return this.hasAcceptedPlayer() && this.age < this.trustUntilTick; }
    public void grantTemporaryTrust(int durationTicks) {
        setAcceptedPlayer(true);
        trustUntilTick = this.age + durationTicks;
        this.setTarget(null);
        this.setAttacking(false);
        this.getNavigation().stop();
    }
    public boolean canAcquireNewTarget() { return this.age >= this.nextRetargetTick; }
    public void markRetargetCooldown() { this.nextRetargetTick = this.age + RETARGET_COOLDOWN_TICKS; }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.getWorld(), nbt);
        if (nbt.contains("TamedByPlayer")) this.setTame(nbt.getBoolean("TamedByPlayer"));
        if (nbt.contains("Owner")) this.setOwnerUuid(UUID.fromString(nbt.getString("Owner")));
        if (nbt.contains("TrustUntil")) this.trustUntilTick = nbt.getInt("TrustUntil");
        if (nbt.contains("NextRetarget")) this.nextRetargetTick = nbt.getInt("NextRetarget");
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
        nbt.putBoolean("TamedByPlayer", this.isTame());
        UUID uuid = this.getOwnerUuid();
        if (uuid != null) nbt.putString("Owner", uuid.toString());
        nbt.putInt("TrustUntil", this.trustUntilTick);
        nbt.putInt("NextRetarget", this.nextRetargetTick);
    }

    public void chooseRandomAngerTime() { this.setAngerTime(ANGER_TIME_RANGE.get(this.random)); }
    public void setAngerTime(int angerTime) { this.angerTime = angerTime; }
    public int getAngerTime() { return this.angerTime; }
    public void setAngryAt(@Nullable UUID angryAt) { this.angryAt = angryAt; }
    @Nullable public UUID getAngryAt() { return this.angryAt; }

    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEvents.ENTITY_BLAZE_AMBIENT;
    }
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ENTITY_POLAR_BEAR_HURT; }
    protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_POLAR_BEAR_DEATH; }
    protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F); }

    protected void playWarningSound() {
        if (this.warningSoundCooldown <= 0) {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.getSoundPitch());
            this.warningSoundCooldown = 40;
        }
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(WARNING, false);
        this.dataTracker.startTracking(TAMED, false);
        this.dataTracker.startTracking(OWNER_UUID, "");
        this.dataTracker.startTracking(ACCEPTED_PLAYER, false);
    }

    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            if (this.warningAnimationProgress != this.lastWarningAnimationProgress) this.calculateDimensions();
            this.lastWarningAnimationProgress = this.warningAnimationProgress;
            if (this.isWarning()) this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress + 1.0F, 0.0F, 6.0F);
            else this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress - 1.0F, 0.0F, 6.0F);
        }

        if (this.warningSoundCooldown > 0) --this.warningSoundCooldown;
        if (!this.getWorld().isClient) this.tickAngerLogic((ServerWorld) this.getWorld(), true);

        // trust expiry
        if (this.hasAcceptedPlayer() && !this.isTrustedNow()) setAcceptedPlayer(false);

        // clear illegal targets (creative/spectator/trusted/tamed)
        if (!this.getWorld().isClient && this.getTarget() instanceof PlayerEntity p) {
            if (isCreativeOrSpectator(p) || this.isTame() || this.isTrustedNow()) {
                this.setTarget(null);
                this.setAttacking(false);
                this.getNavigation().stop();
            }
        }

        if (!this.getWorld().isClient && !this.isTame()) {
            List<PlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(
                    PlayerEntity.class,
                    this.getBoundingBox().expand(8.0D),
                    p -> !isCreativeOrSpectator(p)
            );

            for (PlayerEntity player : nearbyPlayers) {
                if (isPlayerTooClose(player) && !this.isTrustedNow()) {
                    if (this.getTarget() == null) this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.2F, 0.8F);

                    ((ServerWorld) this.getWorld()).spawnParticles(
                            ParticleTypes.LAVA,
                            this.getX(), this.getBodyY(0.5), this.getZ(),
                            8, 0.5, 0.3, 0.5, 0.02
                    );

                    // don’t spam retarget; respect cooldown
                    if (this.canAcquireNewTarget() && this.age % 40 == 0) {
                        this.setTarget(player);
                        this.markRetargetCooldown();
                    }
                }
            }
        }
    }

    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.warningAnimationProgress > 0.0F) {
            float f = this.warningAnimationProgress / 6.0F;
            float g = 1.0F + f;
            return super.getDimensions(pose).scaled(1.0F, g);
        } else {
            return super.getDimensions(pose);
        }
    }

    public boolean hasAcceptedPlayer() { return this.dataTracker.get(ACCEPTED_PLAYER); }
    public void setAcceptedPlayer(boolean value) { this.dataTracker.set(ACCEPTED_PLAYER, value); }

    public boolean tryAttack(Entity target) {
        boolean bl = target.damage(this.getDamageSources().mobAttack(this),
                (float) ((int) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)));
        if (bl) this.applyDamageEffects(this, target);
        return bl;
    }

    public boolean isWarning() { return this.dataTracker.get(WARNING); }
    public void setWarning(boolean warning) { this.dataTracker.set(WARNING, warning); }

    public float getWarningAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastWarningAnimationProgress, this.warningAnimationProgress) / 6.0F;
    }

    protected float getBaseMovementSpeedMultiplier() { return 0.98F; }

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (entityData == null) entityData = new PassiveEntity.PassiveData(1.0F);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    static {
        WARNING = DataTracker.registerData(MagmaBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    }

    public void setOwnerUuid(UUID uuid) { this.dataTracker.set(OWNER_UUID, uuid.toString()); }

    @Override
    public UUID getOwnerUuid() {
        try {
            String s = this.dataTracker.get(OWNER_UUID);
            return s.isEmpty() ? null : UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isOwner(PlayerEntity player) {
        UUID ownerUuid = this.getOwnerUuid();
        return ownerUuid != null && ownerUuid.equals(player.getUuid());
    }

    public boolean isTame() { return this.dataTracker.get(TAMED); }
    public void setTame(boolean tame) { this.dataTracker.set(TAMED, tame); }

    @Override public boolean isFireImmune() { return true; }
    @Override public EntityView method_48926() { return this.getWorld(); }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // --- Temporary trust if player feeds a baby ---
        if (!this.isTame() && this.isBaby() && stack.isOf(ModItems.MAGMA_FISH)) {
            List<MagmaBearEntity> nearbyAdults = this.getWorld().getEntitiesByClass(
                    MagmaBearEntity.class,
                    this.getBoundingBox().expand(8.0D),
                    bear -> !bear.isBaby() && !bear.isTame()
            );
            for (MagmaBearEntity adult : nearbyAdults) {
                adult.setTarget(null);
                adult.setWarning(false);
                adult.playSound(SoundEvents.ENTITY_POLAR_BEAR_AMBIENT, 1.0F, 0.8F);
                adult.grantTemporaryTrust(TRUST_DURATION_TICKS);
            }
        }

        // --- Player offers Magma Fish to tame ---
        if (!this.isTame() && stack.isOf(ModItems.MAGMA_FISH)) {
            double distance = this.squaredDistanceTo(player);
            if (distance > 9.0D) {
                player.sendMessage(Text.literal("You need to get closer to offer the Magma Fish!"), true);
                return ActionResult.FAIL;
            }

            if (!player.getAbilities().creativeMode) stack.decrement(1);
            this.setTame(true);
            this.setOwnerUuid(player.getUuid());
            this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 0.9F);
            player.sendMessage(Text.literal("🔥 The Magma Bear accepts your offering."), true);
            return ActionResult.SUCCESS;
        }

        // --- Allow trusted rider ---
        if (this.isTame() && this.isOwner(player)) {
            if (!player.hasVehicle()) {
                player.startRiding(this);
                this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 1.0F, 1.0F);
            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    class MagmaBearRevengeGoal extends RevengeGoal {
        public MagmaBearRevengeGoal(MagmaBearEntity magmaBearEntity) {
            super(magmaBearEntity, new Class[0]);
        }

        public void start() {
            super.start();
            if (MagmaBearEntity.this.isBaby()) {
                this.callSameTypeForRevenge();
                this.stop();
            }
        }

        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
            if (mob instanceof PolarBearEntity && !mob.isBaby()) {
                super.setMobEntityTarget(mob, target);
            }
        }
    }

    class ProtectBabiesGoal extends ActiveTargetGoal<PlayerEntity> {
        public ProtectBabiesGoal(MagmaBearEntity magmaBearEntity) {
            super(magmaBearEntity, PlayerEntity.class, 20, true, true, null);
        }

        @Override
        public boolean canStart() {
            if (MagmaBearEntity.this.isBaby()) return false;
            if (MagmaBearEntity.this.isTame() || MagmaBearEntity.this.isTrustedNow()) return false;
            if (!MagmaBearEntity.this.canAcquireNewTarget()) return false;

            if (super.canStart()) {
                for (MagmaBearEntity nearby : MagmaBearEntity.this.getWorld().getNonSpectatingEntities(
                        MagmaBearEntity.class,
                        MagmaBearEntity.this.getBoundingBox().expand(8.0D, 4.0D, 8.0D))) {
                    if (nearby.isBaby()) return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            MagmaBearEntity.this.markRetargetCooldown();
        }

        @Override
        public boolean shouldContinue() {
            if (MagmaBearEntity.this.isTame() || MagmaBearEntity.this.isTrustedNow()) return false;
            return super.shouldContinue();
        }

        @Override
        protected double getFollowRange() {
            return super.getFollowRange() * 0.5F;
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(MagmaBearEntity magmaBearEntity) {
            super(magmaBearEntity, 1.25D, true);
        }

        protected void attack(LivingEntity target) {
            if (target instanceof PlayerEntity p) {
                if (isCreativeOrSpectator(p) || MagmaBearEntity.this.isTame() || MagmaBearEntity.this.isTrustedNow()) {
                    this.stop();
                    return;
                }
            }
            if (this.canAttack(target)) {
                this.resetCooldown();
                this.mob.tryAttack(target);
                MagmaBearEntity.this.setWarning(false);
            } else if (this.mob.squaredDistanceTo(target) < (target.getWidth() + 3.0F) * (target.getWidth() + 3.0F)) {
                if (this.getCooldown() <= 10) {
                    MagmaBearEntity.this.setWarning(true);
                    MagmaBearEntity.this.playWarningSound();
                }
            } else {
                this.resetCooldown();
                MagmaBearEntity.this.setWarning(false);
            }
        }

        public void stop() {
            MagmaBearEntity.this.setWarning(false);
            super.stop();
        }
    }

    static class MagmaBearEscapeDangerGoal extends EscapeDangerGoal {
        public MagmaBearEscapeDangerGoal(MagmaBearEntity magmaBearEntity) {
            super(magmaBearEntity, 2.0D);
        }

        protected boolean isInDanger() {
            return this.mob.getAttacker() != null && this.mob.isBaby() || this.mob.isOnFire();
        }
    }

    // Inner class
    class PlayerIntruderTargetGoal extends ActiveTargetGoal<PlayerEntity> {
        private final MagmaBearEntity bear;

        PlayerIntruderTargetGoal(MagmaBearEntity bear) {
            super(bear, PlayerEntity.class, 10, true, false,
                    player -> !bear.isTame()
                            && !bear.isTrustedNow()
                            && player instanceof PlayerEntity p
                            && !isCreativeOrSpectator(p));
            this.bear = bear;
        }

        @Override
        public boolean canStart() {
            if (bear.isTame() || bear.isTrustedNow()) return false;
            if (!bear.canAcquireNewTarget()) return false;
            return super.canStart();
        }

        @Override
        public void start() {
            super.start();
            bear.markRetargetCooldown();
        }

        @Override
        public boolean shouldContinue() {
            if (bear.isTame() || bear.isTrustedNow()) return false;
            return super.shouldContinue();
        }
    }

    // ---------------- RIDING METHODS ----------------

    public boolean canBeRidden() {
        return this.isTame();
    }

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
        pPlayer.setYaw(this.getYaw());
        pPlayer.setPitch(this.getPitch());
        pPlayer.startRiding(this);
    }

    // --- tuning knobs (updated) ---
    private static final double SEAT_SIDE = -0.02; // centered left/right
    private static final double SEAT_BACK = 0.20;  // closer to shoulders
    private static final double SEAT_HEIGHT = 1.40;

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

    @Override
    public double getMountedHeightOffset() {
        return this.getHeight() * SEAT_HEIGHT;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater updater) {
        Vec3d a = getPassengerAttachmentPoint(passenger, passenger.getDimensions(passenger.getPose()), 1.0F);
        updater.accept(passenger, this.getX() + a.x, this.getY() + a.y, this.getZ() + a.z);
        if (passenger instanceof LivingEntity living) {
            living.bodyYaw = this.bodyYaw;
        }
    }

    @Override
    public void travel(Vec3d input) {
        LivingEntity ctrl = getControllingPassenger();
        if (this.hasPassengers() && ctrl instanceof PlayerEntity rider) {
            this.setYaw(rider.getYaw());
            this.setPitch(rider.getPitch() * 0.5F);
            this.bodyYaw = this.getYaw();
            this.headYaw = this.bodyYaw;

            float strafe = rider.sidewaysSpeed * 0.5F;
            float forward = rider.forwardSpeed;
            if (forward <= 0.0F) forward *= 0.25F;

            float baseSpeed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            this.setMovementSpeed(rider.isSprinting() ? baseSpeed * 2.0F : baseSpeed);

            super.travel(new Vec3d(strafe, input.y, forward));
        } else {
            super.travel(input);
        }

        BlockPos blockPos = this.getBlockPos().down();
        FluidState below = this.getWorld().getFluidState(blockPos);

        if (below.isIn(FluidTags.LAVA) && this.canWalkOnFluid(below)) {
            this.setOnGround(true);
            this.setVelocity(this.getVelocity().x, 0.0D, this.getVelocity().z);

            double lavaSurface = blockPos.getY() + 1.0D;
            if (this.getY() < lavaSurface) {
                this.setPos(this.getX(), lavaSurface, this.getZ());
            }
        }
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
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
