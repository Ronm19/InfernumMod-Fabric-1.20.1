package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.ronm19.infernummod.api.interfaces.DayLightBurnImmuneEntity;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class InfernoEndermanEntity extends EndermanEntity implements Monster, DayLightBurnImmuneEntity {
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST;
    private static final int field_30462 = 400;
    private static final int field_30461 = 600;
    private static final TrackedData<Optional<BlockState>> CARRIED_BLOCK;
    private static final TrackedData<Boolean> ANGRY;
    private static final TrackedData<Boolean> PROVOKED;
    private int lastAngrySoundAge = Integer.MIN_VALUE;
    private int ageWhenTargetSet;
    private static final UniformIntProvider ANGER_TIME_RANGE;
    private int angerTime;
    @Nullable
    private UUID angryAt;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public InfernoEndermanEntity( EntityType<? extends EndermanEntity> entityType, World world ) {
        super(entityType, world);
        this.experiencePoints = 10;
        this.setStepHeight(1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }

    // ---------- ATTRIBUTES ----------
    public static DefaultAttributeContainer.Builder createInfernoEndermanAttributes() {
        return EndermanEntity.createEndermanAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)      // twice as tanky
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)  // slightly faster
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0D)    // stronger hits
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    // ---------- IMMUNITIES ----------
    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean burnsInDaylight() {
        return false;
    }

    // ---------- ATTACK EFFECT ----------
    @Override
    public boolean tryAttack( Entity target ) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof LivingEntity living) {
            // Ignite and blind victim briefly
            living.setOnFireFor(4);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
            this.playSound(SoundEvents.ENTITY_BLAZE_HURT, 1.0F, 1.2F);
        }
        return success;
    }

    // ---------- SOUNDS ----------
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }

    // ---------- GOALS ----------
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new InfernoEndermanEntity.ChasePlayerGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, (double)1.0F, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, (double)1.0F, 0.0F));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(10, new InfernoEndermanEntity.PlaceBlockGoal(this));
        this.goalSelector.add(11, new InfernoEndermanEntity.PickUpBlockGoal(this));

        this.targetSelector.add(1, new InfernoEndermanEntity.TeleportTowardsPlayerGoal(this, this::shouldAngerAt));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, EndermiteEntity.class, true, false));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, true, false));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, TameableEntity.class, true, false));
        this.targetSelector.add(6, new UniversalAngerGoal<>(this, false));

        // Keep the Enderman’s built-in stare and teleport goals
        super.initGoals();
    }

    /* ------------- ANIMATIONS --------------- */

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

    // ---------- CUSTOM BEHAVIOR ----------
    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();


        // Small ambient ember effect while alive (reduce count to avoid lag)
        if (this.getWorld().isClient && this.random.nextFloat() < 0.15F) {
            this.getWorld().addParticle(
                    net.minecraft.particle.ParticleTypes.LAVA,
                    this.getX() + (this.random.nextDouble() - 0.5D) * this.getWidth(),
                    this.getY() + this.random.nextDouble() * this.getHeight(),
                    this.getZ() + (this.random.nextDouble() - 0.5D) * this.getWidth(),
                    0.0D, 0.02D, 0.0D
            );
        }
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        if (causedByPlayer) {
            this.dropItem(Items.ENDER_PEARL);
            if (this.random.nextFloat() < 0.5F) this.dropItem(Items.BLAZE_POWDER);
            if (this.random.nextFloat() < 0.15F) this.dropItem(Items.BLAZE_ROD);
            if (this.random.nextFloat() < 0.25F) this.dropItem(ModItems.BLAZE_HEART);
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (this.getWorld().isClient) return;
        ServerWorld world = (ServerWorld) this.getWorld();
        BlockPos pos = this.getBlockPos();

        boolean inInfernal = world.getBiome(pos).getKey().isPresent()
                && world.getBiome(pos).getKey().get().getValue().toString().equals("infernummod:infernal_biome");

        int particleCount = inInfernal ? 80 : 35;

        for (int i = 0; i < particleCount; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 2.5;
            double offsetY = this.random.nextDouble() * 2.0;
            double offsetZ = (this.random.nextDouble() - 0.5) * 2.5;

            // --- Main Infernal Burst ---
            world.spawnParticles(
                    ParticleTypes.LAVA,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0
            );

            // --- Secondary FX ---
            if (this.random.nextFloat() < (inInfernal ? 0.8F : 0.4F)) {
                world.spawnParticles(
                        ParticleTypes.FLAME,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        1, 0, 0, 0, 0
                );
            }

            if (inInfernal && this.random.nextFloat() < 0.6F) {
                world.spawnParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        1, 0, 0, 0, 0
                );
            }

            if (this.random.nextFloat() < 0.25F) {
                world.spawnParticles(
                        ParticleTypes.SMOKE,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        1, 0, 0, 0, 0
                );
            }
        }

        // --- Sound Variation ---
        world.playSound(
                null,
                pos,
                inInfernal ? SoundEvents.ENTITY_BLAZE_SHOOT : SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.HOSTILE,
                inInfernal ? 2.2F : 1.2F,
                inInfernal ? 0.5F + this.random.nextFloat() * 0.3F : 1.3F + this.random.nextFloat() * 0.4F
        );

        // --- Optional Infernal flash effect ---
        if (inInfernal) {
            world.spawnParticles(
                    ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(),
                    this.getY() + 1.0,
                    this.getZ(),
                    1, 0, 0, 0, 0
            );
        }
    }

    public void setTarget( @Nullable LivingEntity target ) {
        super.setTarget(target);
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.dataTracker.set(ANGRY, false);
            this.dataTracker.set(PROVOKED, false);
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST.getId());
        } else {
            this.ageWhenTargetSet = this.age;
            this.dataTracker.set(ANGRY, true);
            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        // Ignore Infernum itself
        if (target instanceof InfernumEntity) {
            return false;
        }

        // Ignore Creative / Spectator players
        if (target instanceof PlayerEntity player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        // Otherwise, follow normal targeting rules
        return super.canTarget(target);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CARRIED_BLOCK, Optional.empty());
        this.dataTracker.startTracking(ANGRY, false);
        this.dataTracker.startTracking(PROVOKED, false);
    }

    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Nullable
    public UUID getAngryAt() {
        return this.angryAt;
    }

    public void playAngrySound() {
        if (this.age >= this.lastAngrySoundAge + 400) {
            this.lastAngrySoundAge = this.age;
            if (!this.isSilent()) {
                this.getWorld().playSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
            }
        }

    }

    public void onTrackedDataSet( TrackedData<?> data) {
        if (ANGRY.equals(data) && this.isProvoked() && this.getWorld().isClient) {
            this.playAngrySound();
        }

        super.onTrackedDataSet(data);
    }

    public void writeCustomDataToNbt( NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        BlockState blockState = this.getCarriedBlock();
        if (blockState != null) {
            nbt.put("carriedBlockState", NbtHelper.fromBlockState(blockState));
        }

        this.writeAngerToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        BlockState blockState = null;
        if (nbt.contains("carriedBlockState", 10)) {
            blockState = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("carriedBlockState"));
            if (blockState.isAir()) {
                blockState = null;
            }
        }

        this.setCarriedBlock(blockState);
        this.readAngerFromNbt(this.getWorld(), nbt);
    }

    boolean isPlayerStaring(PlayerEntity player) {
        ItemStack itemStack = (ItemStack)player.getInventory().armor.get(3);
        if (itemStack.isOf(Blocks.CARVED_PUMPKIN.asItem())) {
            return false;
        } else {
            Vec3d vec3d = player.getRotationVec(1.0F).normalize();
            Vec3d vec3d2 = new Vec3d(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
            double d = vec3d2.length();
            vec3d2 = vec3d2.normalize();
            double e = vec3d.dotProduct(vec3d2);
            return e > (double)1.0F - 0.025 / d ? player.canSee(this) : false;
        }
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.55F;
    }

    protected Vector3f getPassengerAttachmentPos( Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, dimensions.height - 0.09375F * scaleFactor, 0.0F);
    }

    public void tickMovement() {
        if (this.getWorld().isClient) {
            for(int i = 0; i < 2; ++i) {
                this.getWorld().addParticle(ParticleTypes.PORTAL, this.getParticleX((double)0.5F), this.getRandomBodyY() - (double)0.25F, this.getParticleZ((double)0.5F), (this.random.nextDouble() - (double)0.5F) * (double)2.0F, -this.random.nextDouble(), (this.random.nextDouble() - (double)0.5F) * (double)2.0F);
            }
        }

        this.jumping = false;
        if (!this.getWorld().isClient) {
            this.tickAngerLogic((ServerWorld)this.getWorld(), true);
        }

        super.tickMovement();
    }

    protected void mobTick() {
        if (this.getWorld().isDay() && this.age >= this.ageWhenTargetSet + 600) {
            float f = this.getBrightnessAtEyes();
            if (f > 0.5F && this.getWorld().isSkyVisible(this.getBlockPos()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setTarget((LivingEntity)null);
                this.teleportRandomly();
            }
        }

        super.mobTick();
    }

    protected boolean teleportRandomly() {
        if (!this.getWorld().isClient() && this.isAlive()) {
            double d = this.getX() + (this.random.nextDouble() - (double)0.5F) * (double)64.0F;
            double e = this.getY() + (double)(this.random.nextInt(64) - 32);
            double f = this.getZ() + (this.random.nextDouble() - (double)0.5F) * (double)64.0F;
            return this.teleportTo(d, e, f);
        } else {
            return false;
        }
    }

    boolean teleportTo(Entity entity) {
        Vec3d vec3d = new Vec3d(this.getX() - entity.getX(), this.getBodyY((double)0.5F) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3d = vec3d.normalize();
        double d = (double)16.0F;
        double e = this.getX() + (this.random.nextDouble() - (double)0.5F) * (double)8.0F - vec3d.x * (double)16.0F;
        double f = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3d.y * (double)16.0F;
        double g = this.getZ() + (this.random.nextDouble() - (double)0.5F) * (double)8.0F - vec3d.z * (double)16.0F;
        return this.teleportTo(e, f, g);
    }

    private boolean teleportTo(double x, double y, double z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while(mutable.getY() > this.getWorld().getBottomY() && !this.getWorld().getBlockState(mutable).blocksMovement()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.getWorld().getBlockState(mutable);
        boolean bl = blockState.blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2) {
            Vec3d vec3d = this.getPos();
            boolean bl3 = this.teleport(x, y, z, true);
            if (bl3) {
                this.getWorld().emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(this));
                if (!this.isSilent()) {
                    this.getWorld().playSound((PlayerEntity)null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return bl3;
        } else {
            return false;
        }
    }

    public void setCarriedBlock(@Nullable BlockState state) {
        this.dataTracker.set(CARRIED_BLOCK, Optional.ofNullable(state));
    }

    @Nullable
    public BlockState getCarriedBlock() {
        return (BlockState)((Optional)this.dataTracker.get(CARRIED_BLOCK)).orElse((Object)null);
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            boolean bl = source.getSource() instanceof PotionEntity;
            if (!source.isIn(DamageTypeTags.IS_PROJECTILE) && !bl) {
                boolean bl2 = super.damage(source, amount);
                if (!this.getWorld().isClient() && !(source.getAttacker() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
                    this.teleportRandomly();
                }

                return bl2;
            } else {
                boolean bl2 = bl && this.damageFromPotion(source, (PotionEntity)source.getSource(), amount);

                for(int i = 0; i < 64; ++i) {
                    if (this.teleportRandomly()) {
                        return true;
                    }
                }

                return bl2;
            }
        }
    }

    private boolean damageFromPotion(DamageSource source, PotionEntity potion, float amount) {
        ItemStack itemStack = potion.getStack();
        Potion potion2 = PotionUtil.getPotion(itemStack);
        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
        boolean bl = potion2 == Potions.WATER && list.isEmpty();
        return bl ? super.damage(source, amount) : false;
    }

    public boolean isAngry() {
        return (Boolean)this.dataTracker.get(ANGRY);
    }

    public boolean isProvoked() {
        return (Boolean)this.dataTracker.get(PROVOKED);
    }

    public void setProvoked() {
        this.dataTracker.set(PROVOKED, true);
    }

    public boolean cannotDespawn() {
        return super.cannotDespawn() || this.getCarriedBlock() != null;
    }

    static {
        ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", (double)0.15F, EntityAttributeModifier.Operation.ADDITION);
        CARRIED_BLOCK = DataTracker.registerData(InfernoEndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
        ANGRY = DataTracker.registerData(InfernoEndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PROVOKED = DataTracker.registerData(InfernoEndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    }

    static class TeleportTowardsPlayerGoal extends ActiveTargetGoal<PlayerEntity> {
        private final InfernoEndermanEntity infernoEnderman;
        @Nullable
        private PlayerEntity targetPlayer;
        private int lookAtPlayerWarmup;
        private int ticksSinceUnseenTeleport;
        private final TargetPredicate staringPlayerPredicate;
        private final TargetPredicate validTargetPredicate = TargetPredicate.createAttackable().ignoreVisibility();
        private final Predicate<LivingEntity> angerPredicate;

        public TeleportTowardsPlayerGoal(InfernoEndermanEntity infernoEnderman, @Nullable Predicate<LivingEntity> targetPredicate) {
            super(infernoEnderman, PlayerEntity.class, 10, false, false, targetPredicate);
            this.infernoEnderman = infernoEnderman;
            this.angerPredicate = (playerEntity) -> (infernoEnderman.isPlayer() || infernoEnderman.shouldAngerAt(playerEntity)) && !infernoEnderman.hasPassengerDeep(playerEntity);
            this.staringPlayerPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(this.angerPredicate);
        }

        public boolean canStart() {
            this.targetPlayer = this.infernoEnderman.getWorld().getClosestPlayer(this.staringPlayerPredicate, this.infernoEnderman);
            return this.targetPlayer != null;
        }

        public void start() {
            this.lookAtPlayerWarmup = this.getTickCount(5);
            this.ticksSinceUnseenTeleport = 0;
            this.infernoEnderman.setProvoked();
        }

        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }

        public boolean shouldContinue() {
            if (this.targetPlayer != null) {
                if (!this.angerPredicate.test(this.targetPlayer)) {
                    return false;
                } else {
                    this.infernoEnderman.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
                    return true;
                }
            } else {
                if (this.targetEntity != null) {
                    if (this.infernoEnderman.hasPassengerDeep(this.targetEntity)) {
                        return false;
                    }

                    if (this.validTargetPredicate.test(this.infernoEnderman, this.targetEntity)) {
                        return true;
                    }
                }

                return super.shouldContinue();
            }
        }

        public void tick() {
            if (this.infernoEnderman.getTarget() == null) {
                super.setTargetEntity((LivingEntity)null);
            }

            if (this.targetPlayer != null) {
                if (--this.lookAtPlayerWarmup <= 0) {
                    this.targetEntity = this.targetPlayer;
                    this.targetPlayer = null;
                    super.start();
                }
            } else {
                if (this.targetEntity != null && !this.infernoEnderman.hasVehicle()) {
                    if (this.infernoEnderman.isPlayerStaring((PlayerEntity)this.targetEntity)) {
                        if (this.targetEntity.squaredDistanceTo(this.infernoEnderman) < (double)16.0F) {
                            this.infernoEnderman.teleportRandomly();
                        }

                        this.ticksSinceUnseenTeleport = 0;
                    } else if (this.targetEntity.squaredDistanceTo(this.infernoEnderman) > (double)256.0F && this.ticksSinceUnseenTeleport++ >= this.getTickCount(30) && this.infernoEnderman.teleportTo(this.targetEntity)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }

                super.tick();
            }

        }
    }

    static class ChasePlayerGoal extends Goal {
        private final InfernoEndermanEntity infernoEnderman;
        @Nullable
        private LivingEntity target;

        public ChasePlayerGoal(InfernoEndermanEntity infernoEnderman) {
            this.infernoEnderman = infernoEnderman;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            this.target = this.infernoEnderman.getTarget();
            if (!(this.target instanceof PlayerEntity)) {
                return false;
            } else {
                double d = this.target.squaredDistanceTo(this.infernoEnderman);
                return d > (double)256.0F ? false : this.infernoEnderman.isPlayerStaring((PlayerEntity)this.target);
            }
        }

        public void start() {
            this.infernoEnderman.getNavigation().stop();
        }

        public void tick() {
            this.infernoEnderman.getLookControl().lookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    static class PlaceBlockGoal extends Goal {
        private final InfernoEndermanEntity infernoEnderman;

        public PlaceBlockGoal(InfernoEndermanEntity infernoEnderman) {
            this.infernoEnderman = infernoEnderman;
        }

        public boolean canStart() {
            if (this.infernoEnderman.getCarriedBlock() == null) {
                return false;
            } else if (!this.infernoEnderman.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return false;
            } else {
                return this.infernoEnderman.getRandom().nextInt(toGoalTicks(2000)) == 0;
            }
        }

        public void tick() {
            Random random = this.infernoEnderman.getRandom();
            World world = this.infernoEnderman.getWorld();
            int i = MathHelper.floor(this.infernoEnderman.getX() - (double)1.0F + random.nextDouble() * (double)2.0F);
            int j = MathHelper.floor(this.infernoEnderman.getY() + random.nextDouble() * (double)2.0F);
            int k = MathHelper.floor(this.infernoEnderman.getZ() - (double)1.0F + random.nextDouble() * (double)2.0F);
            BlockPos blockPos = new BlockPos(i, j, k);
            BlockState blockState = world.getBlockState(blockPos);
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState2 = world.getBlockState(blockPos2);
            BlockState blockState3 = this.infernoEnderman.getCarriedBlock();
            if (blockState3 != null) {
                blockState3 = Block.postProcessState(blockState3, this.infernoEnderman.getWorld(), blockPos);
                if (this.canPlaceOn(world, blockPos, blockState3, blockState, blockState2, blockPos2)) {
                    world.setBlockState(blockPos, blockState3, 3);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this.infernoEnderman, blockState3));
                    this.infernoEnderman.setCarriedBlock((BlockState)null);
                }

            }
        }

        private boolean canPlaceOn(World world, BlockPos posAbove, BlockState carriedState, BlockState stateAbove, BlockState state, BlockPos pos) {
            return stateAbove.isAir() && !state.isAir() && !state.isOf(Blocks.BEDROCK) && state.isFullCube(world, pos) && carriedState.canPlaceAt(world, posAbove) && world.getOtherEntities(this.infernoEnderman, Box.from(Vec3d.of(posAbove))).isEmpty();
        }
    }

    static class PickUpBlockGoal extends Goal {
        private final InfernoEndermanEntity infernoEnderman;

        public PickUpBlockGoal(InfernoEndermanEntity infernoEnderman) {
            this.infernoEnderman = infernoEnderman;
        }

        public boolean canStart() {
            if (this.infernoEnderman.getCarriedBlock() != null) {
                return false;
            } else if (!this.infernoEnderman.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return false;
            } else {
                return this.infernoEnderman.getRandom().nextInt(toGoalTicks(20)) == 0;
            }
        }

        public void tick() {
            Random random = this.infernoEnderman.getRandom();
            World world = this.infernoEnderman.getWorld();
            int i = MathHelper.floor(this.infernoEnderman.getX() - (double)2.0F + random.nextDouble() * (double)4.0F);
            int j = MathHelper.floor(this.infernoEnderman.getY() + random.nextDouble() * (double)3.0F);
            int k = MathHelper.floor(this.infernoEnderman.getZ() - (double)2.0F + random.nextDouble() * (double)4.0F);
            BlockPos blockPos = new BlockPos(i, j, k);
            BlockState blockState = world.getBlockState(blockPos);
            Vec3d vec3d = new Vec3d((double)this.infernoEnderman.getBlockX() + (double)0.5F, (double)j + (double)0.5F, (double)this.infernoEnderman.getBlockZ() + (double)0.5F);
            Vec3d vec3d2 = new Vec3d((double)i + (double)0.5F, (double)j + (double)0.5F, (double)k + (double)0.5F);
            BlockHitResult blockHitResult = world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this.infernoEnderman));
            boolean bl = blockHitResult.getBlockPos().equals(blockPos);
            if (blockState.isIn(BlockTags.ENDERMAN_HOLDABLE) && bl) {
                world.removeBlock(blockPos, false);
                world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Emitter.of(this.infernoEnderman, blockState));
                this.infernoEnderman.setCarriedBlock(blockState.getBlock().getDefaultState());
            }

        }
    }
}