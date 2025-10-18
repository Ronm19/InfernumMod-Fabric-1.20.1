package net.ronm19.infernummod.entity.custom;


import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumSet;

public class EmberBoarEntity extends PigEntity {
    private static final TrackedData<Boolean> SADDLED;
    private static final TrackedData<Integer> BOOST_TIME;
    private static final Ingredient BREEDING_INGREDIENT;
    private final SaddledComponent saddledComponent;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    // -------------------- CONSTRUCTOR --------------------
    public EmberBoarEntity( EntityType<? extends PigEntity> entityType, World world ) {
        super(entityType, world);
        this.isFireImmune();
        this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
    }

    // -------------------- ATTRIBUTES --------------------
    public static DefaultAttributeContainer.Builder createEmberBoarAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 18.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0D);
    }

    // -------------------- GOALS --------------------
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, (double) 1.25F));
        this.goalSelector.add(2, new AnimalMateGoal(this, (double) 1.0F));
        this.goalSelector.add(3, new FleeWaterGoal(this));
        this.goalSelector.add(4, new TemptGoal(this, 1.2, Ingredient.ofItems(new ItemConvertible[]{ModItems.INFERNAL_APPLE}), false));
        this.goalSelector.add(5, new TemptGoal(this, 1.2, BREEDING_INGREDIENT, false));
        this.goalSelector.add(6, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, (double) 1.0F));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
    }

    // -------------------- TICK --------------------

    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();

        if (getWorld().isClient()) return; // Run logic only on the server
        ServerWorld serverWorld = (ServerWorld) getWorld();

        // 🔥 Ambient ember + smoke effect
        if (this.age % 15 == 0) {
            double x = getX();
            double y = getY() + 0.7;
            double z = getZ();

            // Subtle smoke puff
            serverWorld.spawnParticles(ParticleTypes.SMOKE, x, y, z, 2, 0.25, 0.1, 0.25, 0.01);

            // Occasional ember burst
            if (this.random.nextFloat() < 0.4F) {
                serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, x, y, z, 3, 0.2, 0.1, 0.2, 0.02);
                serverWorld.spawnParticles(ParticleTypes.LAVA, x, y, z, 1, 0.1, 0.1, 0.1, 0.03);
            }
        }

        // 💨 Steam reaction when touching water or rain
        if (this.isTouchingWaterOrRain()) {
            if (this.age % 8 == 0) {
                double x = getX();
                double y = getY() + 0.6;
                double z = getZ();

                serverWorld.spawnParticles(ParticleTypes.CLOUD, x, y, z, 4, 0.3, 0.2, 0.3, 0.02);
                serverWorld.playSound(
                        null,
                        this.getBlockPos(),
                        SoundEvents.BLOCK_FIRE_EXTINGUISH,
                        SoundCategory.NEUTRAL,
                        0.7F,
                        1.2F + this.random.nextFloat() * 0.2F
                );
            }

            // Slight burn damage to simulate “cooling lava flesh”
            this.damage(this.getDamageSources().magic(), 1.0F);
        }

        // 🌋 Ember trail when walking on land
        if (this.isOnGround() && this.isMoving() && this.age % 6 == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.6;
            double y = getY() + 0.05;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.6;
            serverWorld.spawnParticles(ParticleTypes.FLAME, x, y, z, 1, 0.02, 0.01, 0.02, 0.001);
        }
    }

    /**
     * Keeps idle animation states looping naturally.
     */
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfNotRunning(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    /**
     * Utility to check if the boar is walking or running.
     */
    private boolean isMoving() {
        Vec3d vel = this.getVelocity();
        return Math.abs(vel.x) > 0.05 || Math.abs(vel.z) > 0.05;
    }

    // --------------------- DATA -------------------------

    public void onTrackedDataSet( TrackedData<?> data ) {
        if (BOOST_TIME.equals(data) && this.getWorld().isClient) {
            this.saddledComponent.boost();
        }

        super.onTrackedDataSet(data);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SADDLED, false);
        this.dataTracker.startTracking(BOOST_TIME, 0);
    }

    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        this.saddledComponent.writeNbt(nbt);
    }

    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        this.saddledComponent.readNbt(nbt);
    }

    // -------------------- INTERACTION --------------------

    public ActionResult interactMob( PlayerEntity player, Hand hand ) {
        boolean bl = this.isBreedingItem(player.getStackInHand(hand));
        if (!bl && this.isSaddled() && !this.hasPassengers() && !player.shouldCancelInteraction()) {
            if (!this.getWorld().isClient) {
                player.startRiding(this);
            }

            return ActionResult.success(this.getWorld().isClient);
        } else {
            ActionResult actionResult = super.interactMob(player, hand);
            if (!actionResult.isAccepted()) {
                ItemStack itemStack = player.getStackInHand(hand);
                return itemStack.isOf(Items.SADDLE) ? itemStack.useOnEntity(player, this, hand) : ActionResult.PASS;
            } else {
                return actionResult;
            }
        }
    }

    // -------------------- DROPS --------------------
    @Override
    protected void dropLoot( DamageSource source, boolean causedByPlayer ) {
        this.dropStack(new ItemStack(Items.COOKED_PORKCHOP, 1 + random.nextInt(2)));
        if (random.nextFloat() < 0.15F) {
            this.dropStack(new ItemStack(ModItems.EMBER_ASH));
        }
    }

    // -------------------- SOUNDS --------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_LAVA_POP;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.BLOCK_LAVA_POP, 0.15F, 1.2F);
    }

    // -------------------- CUSTOM GOAL --------------------
    static class FleeWaterGoal extends Goal {
        private final EmberBoarEntity boar;
        private int timer;

        public FleeWaterGoal( EmberBoarEntity boar ) {
            this.boar = boar;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return boar.isTouchingWaterOrRain();
        }

        @Override
        public void tick() {
            if (--this.timer <= 0) {
                this.timer = 20;
                Vec3d currentPos = boar.getPos();
                Vec3d escape = NoWaterTargeting.find(boar, 12, 3, 0, currentPos, 1.6D);
                if (escape != null) {
                    boar.getNavigation().startMovingTo(escape.x, escape.y, escape.z, 1.4);
                }
            }
        }
    }


        // -------------------------- BREEDING -----------------------------------------

    @Nullable
    public EmberBoarEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return (EmberBoarEntity)ModEntities.EMBER_BOAR.create(serverWorld);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }



    // ---------------------------- BOAR RIDING METHODS ----------------------------

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            Entity var2 = this.getFirstPassenger();
            if (var2 instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)var2;
                if (playerEntity.isHolding(Items.CARROT_ON_A_STICK)) {
                    return playerEntity;
                }
            }
        }

        return super.getControllingPassenger();
    }

    public boolean canBeSaddled() {
        return this.isAlive() && !this.isBaby();
    }

    protected void dropInventory() {
        super.dropInventory();
        if (this.isSaddled()) {
            this.dropItem(Items.SADDLE);
        }

    }

    public boolean isSaddled() {
        return this.saddledComponent.isSaddled();
    }

    public void saddle(@Nullable SoundCategory sound) {
        this.saddledComponent.setSaddled(true);
        if (sound != null) {
            this.getWorld().playSoundFromEntity((PlayerEntity)null, this, SoundEvents.ENTITY_PIG_SADDLE, sound, 0.5F, 1.0F);
        }

    }

    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Direction direction = this.getMovementDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.updatePassengerForDismount(passenger);
        } else {
            int[][] is = Dismounting.getDismountOffsets(direction);
            BlockPos blockPos = this.getBlockPos();
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            UnmodifiableIterator var6 = passenger.getPoses().iterator();

            while(var6.hasNext()) {
                EntityPose entityPose = (EntityPose)var6.next();
                Box box = passenger.getBoundingBox(entityPose);

                for(int[] js : is) {
                    mutable.set(blockPos.getX() + js[0], blockPos.getY(), blockPos.getZ() + js[1]);
                    double d = this.getWorld().getDismountHeight(mutable);
                    if (Dismounting.canDismountInBlock(d)) {
                        Vec3d vec3d = Vec3d.ofCenter(mutable, d);
                        if (Dismounting.canPlaceEntityAt(this.getWorld(), passenger, box.offset(vec3d))) {
                            passenger.setPose(entityPose);
                            return vec3d;
                        }
                    }
                }
            }

            return super.updatePassengerForDismount(passenger);
        }
    }

    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        super.tickControlled(controllingPlayer, movementInput);
        this.setRotation(controllingPlayer.getYaw(), controllingPlayer.getPitch() * 0.5F);
        this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
        this.saddledComponent.tickBoost();
    }

    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
        return new Vec3d((double)0.0F, (double)0.0F, (double)1.0F);
    }

    protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
        return (float)(this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.225 * (double)this.saddledComponent.getMovementSpeedMultiplier());
    }

    public boolean consumeOnAStickItem() {
        return this.saddledComponent.boost(this.getRandom());
    }

    public Vec3d getLeashOffset() {
        return new Vec3d((double)0.0F, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
    }

    protected Vector3f getPassengerAttachmentPos( Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, dimensions.height - 0.03125F * scaleFactor, 0.0F);
    }





    // ------------------------ OTHER ---------------------- //

    public void onStruckByLightning(ServerWorld world, InfernalLightningEntity lightning) {
        if (lightning instanceof InfernalLightningEntity && (this.age <= 5 || this.getCommandTags().contains("infernal_just_transformed"))) {
            return;
        }
        if (lightning instanceof InfernalLightningEntity && world.getDifficulty() != Difficulty.PEACEFUL) {
            DemonEntity demonEntity = (DemonEntity) ModEntities.DEMON.create(world);
            if (demonEntity != null) {
                demonEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.NETHER_RUBY_SWORD));
                demonEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
                demonEntity.setAiDisabled(this.isAiDisabled());
                demonEntity.setBaby(this.isBaby());
                if (this.hasCustomName()) {
                    demonEntity.setCustomName(this.getCustomName());
                    demonEntity.setCustomNameVisible(this.isCustomNameVisible());
                }

                demonEntity.setPersistent();
                world.spawnEntity(demonEntity);
                this.discard();
            } else {
                super.onStruckByLightning(world, lightning);
            }
        } else {
            super.onStruckByLightning(world, lightning);
        }

    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    static {
        SADDLED = DataTracker.registerData(EmberBoarEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BOOST_TIME = DataTracker.registerData(EmberBoarEntity.class, TrackedDataHandlerRegistry.INTEGER);
        BREEDING_INGREDIENT = Ingredient.ofItems(new ItemConvertible[]{Items.CARROT, Items.POTATO, Items.BEETROOT, ModItems.INFERNAL_APPLE});
    }
}
