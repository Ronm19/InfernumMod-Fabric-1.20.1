package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BedBlock;
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
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ai.goals.volcarnis.VolcarnisAttackGoal;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class VolcarnisEntity extends CatEntity {
    public static final double CROUCHING_SPEED = 0.6;
    public static final double NORMAL_SPEED = 0.8;
    public static final double SPRINTING_SPEED = 1.33;

    private static final Ingredient TAMING_INGREDIENT;
    private static final TrackedData<Boolean> IN_SLEEPING_POSE;
    private static final TrackedData<Boolean> HEAD_DOWN;
    private static final TrackedData<Integer> COLLAR_COLOR;
    private static final TrackedData<Boolean> SITTING =
            DataTracker.registerData(VolcarnisEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(VolcarnisEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Nullable private VolcarinsFleeGoal<PlayerEntity> fleeGoal;
    @Nullable private TemptGoal temptGoal;

    // vanilla animation states
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    public int attackAnimationTimeout = 0;

    private float sleepAnimation;
    private float prevSleepAnimation;
    private float tailCurlAnimation;
    private float prevTailCurlAnimation;
    private float headDownAnimation;
    private float prevHeadDownAnimation;

    public VolcarnisEntity(EntityType<? extends CatEntity> entityType, World world) {
        super(entityType, world);
        this.isFireImmune();
    }

    // ------------------- ATTRIBUTES -------------------
    public static DefaultAttributeContainer.Builder createVolcarnisAttributes() {
        return CatEntity.createCatAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 44.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 0.1);
    }

    // ------------------- GOALS -------------------
    @Override
    protected void initGoals() {
        this.temptGoal = new TemptGoal(this, 0.6, TAMING_INGREDIENT, true);
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new VolcarnisAttackGoal(this, 1.1D, true));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
        this.goalSelector.add(4, new AttackWithOwnerGoal(this));
        this.goalSelector.add(5, new SleepWithOwnerGoal(this));
        this.goalSelector.add(6, new GoToBedAndSleepGoal(this, 1.1, 8));
        this.goalSelector.add(7, this.temptGoal);
        this.goalSelector.add(8, new CatSitOnBlockGoal(this, 0.8));
        this.goalSelector.add(9, new PounceAtTargetGoal(this, 0.3F));
        this.goalSelector.add(10, new AttackGoal(this));
        this.goalSelector.add(11, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(12, new WanderAroundFarGoal(this, 0.8, 1.0000001E-5F));
        this.goalSelector.add(13, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));

        this.targetSelector.add(1, new UntamedActiveTargetGoal<>(this, LavaFishEntity.class, false, null));
        this.targetSelector.add(2, new UntamedActiveTargetGoal<>(this, MagmaStriderEntity.class, false, null));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, HostileEntity.class, false, null));
        this.targetSelector.add(4, new RevengeGoal(this));
    }

    // ------------------- DATA -------------------
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IN_SLEEPING_POSE, false);
        this.dataTracker.startTracking(HEAD_DOWN, false);
        this.dataTracker.startTracking(COLLAR_COLOR, DyeColor.RED.getId());
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(ATTACKING, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("CollarColor", (byte) this.getCollarColor().getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(nbt.getInt("CollarColor")));
        }
    }

    static {
        TAMING_INGREDIENT = Ingredient.ofItems(ModItems.LAVA_FISH, ModItems.FIRE_FISH, Items.BLAZE_POWDER,  Items.FIRE_CHARGE, Items.COOKED_SALMON);
        IN_SLEEPING_POSE = DataTracker.registerData(VolcarnisEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        HEAD_DOWN = DataTracker.registerData(VolcarnisEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        COLLAR_COLOR = DataTracker.registerData(VolcarnisEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    // ------------------- TICK / ANIM -------------------
    @Override
    public void tick() {
        super.tick();
        updateSleepAnimation();
        updateHeadDownAnimation();

        if (this.getWorld().isClient()) {
            setupAnimationStates();
            spawnFlameParticles();
        }

        if (this.temptGoal != null && this.temptGoal.isActive() && !this.isTamed() && this.age % 100 == 0) {
            this.playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
        }

        if (this.isTouchingWater()) {
            this.damage(this.getWorld().getDamageSources().drown(), 0.5F);
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfNotRunning(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.attackAnimationTimeout > 0) {
            this.attackAnimationTimeout--;
        } else if (!this.isAttacking()) {
            this.attackAnimationState.stop();
        }

        if(isInSittingPose()) {
            sitAnimationState.startIfNotRunning(this.age);
        } else {
            sitAnimationState.stop();
        }
    }

    private void updateSleepAnimation() {
        this.prevSleepAnimation = this.sleepAnimation;
        this.prevTailCurlAnimation = this.tailCurlAnimation;
        if (this.isInSleepingPose()) {
            this.sleepAnimation = Math.min(1.0F, this.sleepAnimation + 0.15F);
            this.tailCurlAnimation = Math.min(1.0F, this.tailCurlAnimation + 0.08F);
        } else {
            this.sleepAnimation = Math.max(0.0F, this.sleepAnimation - 0.22F);
            this.tailCurlAnimation = Math.max(0.0F, this.tailCurlAnimation - 0.13F);
        }
    }

    private void updateHeadDownAnimation() {
        this.prevHeadDownAnimation = this.headDownAnimation;
        if (this.isHeadDown()) {
            this.headDownAnimation = Math.min(1.0F, this.headDownAnimation + 0.1F);
        } else {
            this.headDownAnimation = Math.max(0.0F, this.headDownAnimation - 0.13F);
        }
    }

    public float getSleepAnimation(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevSleepAnimation, this.sleepAnimation);
    }

    public float getTailCurlAnimation(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevTailCurlAnimation, this.tailCurlAnimation);
    }

    public float getHeadDownAnimation(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevHeadDownAnimation, this.headDownAnimation);
    }

    public void setAttacking( boolean attacking ) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setSitting( boolean sitting ) {
        this.dataTracker.set(SITTING, sitting);
    }

    @Override
    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);
    }

    // ------------------- PARTICLES -------------------
    private void spawnFlameParticles() {
        if (this.random.nextFloat() < 0.2F) {
            this.getWorld().addParticle(
                    ParticleTypes.FLAME,
                    this.getParticleX(0.5D),
                    this.getY() + 0.5D,
                    this.getParticleZ(0.5D),
                    0.0D, 0.02D, 0.0D
            );
        }
        if (this.random.nextFloat() < 0.1F) {
            this.getWorld().addParticle(
                    ParticleTypes.SMALL_FLAME,
                    this.getParticleX(0.3D),
                    this.getY() + 0.6D,
                    this.getParticleZ(0.3D),
                    0.0D, 0.01D, 0.0D
            );
        }
    }

    // ------------------- ATTACK -------------------
    @Override
    public boolean tryAttack(Entity target) {
        // server: deal damage
        boolean damaged = false;
        if (!this.getWorld().isClient()) {
            damaged = target.damage(this.getDamageSources().mobAttack(this), getAttackDamage());
        }

        // client: play attack anim + particles
        if (damaged || this.getWorld().isClient()) {
            this.attackAnimationTimeout = 10;
            this.attackAnimationState.start(this.age);
            if (this.getWorld().isClient()) {
                for (int i = 0; i < 5; i++) {
                    this.getWorld().addParticle(
                            ParticleTypes.FLAME,
                            this.getParticleX(0.5D),
                            this.getY() + 0.5D,
                            this.getParticleZ(0.5D),
                            0.0D, 0.02D, 0.0D
                    );
                }
            }
        }
        return damaged;
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    // ------------------- BREEDING -------------------
    @Nullable
    @Override
    public VolcarnisEntity createChild(ServerWorld world, PassiveEntity mate) {
        VolcarnisEntity child = (VolcarnisEntity) this.getType().create(world);
        if (child != null && mate instanceof VolcarnisEntity other) {
            child.setVariant(this.random.nextBoolean() ? this.getVariant() : other.getVariant());
            if (this.isTamed()) {
                child.setOwnerUuid(this.getOwnerUuid());
                child.setTamed(true);
                child.setCollarColor(this.random.nextBoolean() ? this.getCollarColor() : other.getCollarColor());
            }
        }
        return child;
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (!(other instanceof VolcarnisEntity v)) return false;
        return this.isTamed() && v.isTamed() && super.canBreedWith(other);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return TAMING_INGREDIENT.test(stack);
    }

    // ------------------- MOB TICK (poses) -------------------
    @Override
    public void mobTick() {
        if (this.getMoveControl().isMoving()) {
            double speed = this.getMoveControl().getSpeed();
            if (speed == CROUCHING_SPEED) {
                this.setPose(EntityPose.CROUCHING);
                this.setSprinting(false);
            } else if (speed == SPRINTING_SPEED) {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(EntityPose.STANDING);
            this.setSprinting(false);
        }
    }

    // ------------------- TAME / INTERACT -------------------
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();

        if (this.getWorld().isClient) {
            if (this.isTamed() && this.isOwner(player)) return ActionResult.SUCCESS;
            return (this.isBreedingItem(stack) && (this.getHealth() < this.getMaxHealth() || !this.isTamed()))
                    ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        if (this.isTamed()) {
            if (this.isOwner(player)) {
                if (item instanceof DyeItem dye) {
                    if (dye.getColor() != this.getCollarColor()) {
                        this.setCollarColor(dye.getColor());
                        if (!player.getAbilities().creativeMode) stack.decrement(1);
                        this.setPersistent();
                        return ActionResult.CONSUME;
                    }
                } else if (item.isFood() && this.isBreedingItem(stack) && this.getHealth() < this.getMaxHealth()) {
                    this.eat(player, hand, stack);
                    this.heal(item.getFoodComponent().getHunger());
                    return ActionResult.CONSUME;
                } else {
                    ActionResult res = super.interactMob(player, hand);
                    if (!res.isAccepted() || this.isBaby()) this.setSitting(!this.isSitting());
                    return res;
                }
            }
        } else if (this.isBreedingItem(stack)) {
            this.eat(player, hand, stack);
            if (this.random.nextInt(3) == 0) {
                this.setOwner(player);
                this.setSitting(true);
                this.getWorld().sendEntityStatus(this, (byte)7);
            } else {
                this.getWorld().sendEntityStatus(this, (byte)6);
            }
            this.setPersistent();
            return ActionResult.CONSUME;
        }

        if(isTamed() && hand == Hand.MAIN_HAND) {
            boolean sitting = !isSitting();
            setSitting(sitting);
            setInSittingPose(sitting);

            return ActionResult.SUCCESS;
        }

        ActionResult res = super.interactMob(player, hand);
        if (res.isAccepted()) this.setPersistent();
        return res;


    }

    // ------------------- SOUNDS -------------------
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isTamed()) {
            return this.isInLove() ? SoundEvents.ENTITY_CAT_PURR
                    : (this.random.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT);
        }
        return SoundEvents.ENTITY_CAT_STRAY_AMBIENT;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }

    public void hiss() {
        this.playSound(SoundEvents.ENTITY_CAT_HISS, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
        if (this.isBreedingItem(stack)) this.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0F, 1.0F);
        super.eat(player, hand, stack);
    }

    // ------------------- FIRE/WATER / LIGHT -------------------
    @Override public boolean isFireImmune() { return true; }
    @Override public boolean canBreatheInWater() { return false; }
    @Override public float getBrightnessAtEyes() { return 1.0F; }

    public void setInSleepingPose(boolean sleeping) { this.dataTracker.set(IN_SLEEPING_POSE, sleeping); }
    public boolean isInSleepingPose() { return this.dataTracker.get(IN_SLEEPING_POSE); }
    public void setHeadDown(boolean headDown) { this.dataTracker.set(HEAD_DOWN, headDown); }
    public boolean isHeadDown() { return this.dataTracker.get(HEAD_DOWN); }

    @Override
    public boolean bypassesSteppingEffects() {
        return this.isInSneakingPose() || super.bypassesSteppingEffects();
    }

    @Override
    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dims, float scale) {
        return new Vector3f(0.0F, dims.height - 0.1875F * scale, 0.0F);
    }

    // ------------------- INNER GOALS -------------------
    static class VolcarinsFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
        private final VolcarnisEntity volcarnis;

        public VolcarinsFleeGoal(VolcarnisEntity volcarnis, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
            super(volcarnis, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
            this.volcarnis = volcarnis;
        }

        @Override public boolean canStart() { return !volcarnis.isTamed() && super.canStart(); }
        @Override public boolean shouldContinue() { return !volcarnis.isTamed() && super.shouldContinue(); }
    }

    static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal {
        @Nullable private PlayerEntity player;
        private final VolcarnisEntity volcarnis;

        public TemptGoal(VolcarnisEntity volcarnis, double speed, Ingredient food, boolean canBeScared) {
            super(volcarnis, speed, food, canBeScared);
            this.volcarnis = volcarnis;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.player == null && this.mob.getRandom().nextInt(this.getTickCount(600)) == 0) this.player = this.closestPlayer;
            else if (this.mob.getRandom().nextInt(this.getTickCount(500)) == 0) this.player = null;
        }

        @Override
        protected boolean canBeScared() {
            return (this.player == null || !this.player.equals(this.closestPlayer)) && super.canBeScared();
        }

        @Override
        public boolean canStart() { return super.canStart() && !volcarnis.isTamed(); }
    }

    static class SleepWithOwnerGoal extends Goal {
        private final VolcarnisEntity volcarnis;
        @Nullable private PlayerEntity owner;
        @Nullable private BlockPos bedPos;
        private int ticksOnBed;

        public SleepWithOwnerGoal(VolcarnisEntity volcarnis) { this.volcarnis = volcarnis; }

        @Override
        public boolean canStart() {
            if (!volcarnis.isTamed() || volcarnis.isSitting()) return false;
            LivingEntity living = volcarnis.getOwner();
            if (!(living instanceof PlayerEntity p)) return false;
            this.owner = p;
            if (!p.isSleeping()) return false;
            if (volcarnis.squaredDistanceTo(p) > 100.0F) return false;

            BlockPos pos = p.getBlockPos();
            BlockState state = volcarnis.getWorld().getBlockState(pos);
            if (state.isIn(BlockTags.BEDS)) {
                this.bedPos = state.getOrEmpty(BedBlock.FACING).map(dir -> pos.offset(dir.getOpposite())).orElse(pos);
                return cannotSleep();
            }
            return false;
        }

        private boolean cannotSleep() {
            for (VolcarnisEntity volcarnisEntity : volcarnis.getWorld().getNonSpectatingEntities(VolcarnisEntity.class, new Box(this.bedPos).expand(2.0))) {
                if (volcarnisEntity != volcarnis && (volcarnisEntity.isInSleepingPose() || volcarnisEntity.isHeadDown())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean shouldContinue() {
            return volcarnis.isTamed() && !volcarnis.isSitting() && owner != null && owner.isSleeping() && bedPos != null && cannotSleep();
        }

        @Override
        public void start() {
            if (bedPos != null) {
                volcarnis.setInSittingPose(false);
                volcarnis.getNavigation().startMovingTo(bedPos.getX(), bedPos.getY(), bedPos.getZ(), 1.1);
            }
        }

        @Override
        public void stop() {
            volcarnis.setInSleepingPose(false);
            float sky = volcarnis.getWorld().getSkyAngle(1.0F);
            if (owner != null && owner.getSleepTimer() >= 100 && sky > 0.77 && sky < 0.8 && volcarnis.getRandom().nextFloat() < 0.7F) {
                dropMorningGifts();
            }
            ticksOnBed = 0;
            volcarnis.setHeadDown(false);
            volcarnis.getNavigation().stop();
        }

        private void dropMorningGifts() {
            Random random = volcarnis.getRandom();
            BlockPos.Mutable m = new BlockPos.Mutable();
            m.set(volcarnis.isLeashed() ? volcarnis.getHoldingEntity().getBlockPos() : volcarnis.getBlockPos());
            volcarnis.teleport(m.getX() + random.nextInt(11) - 5, m.getY() + random.nextInt(5) - 2, m.getZ() + random.nextInt(11) - 5, false);
            m.set(volcarnis.getBlockPos());

            LootTable table = volcarnis.getWorld().getServer().getLootManager().getLootTable(LootTables.CAT_MORNING_GIFT_GAMEPLAY);
            LootContextParameterSet ctx = new LootContextParameterSet.Builder((ServerWorld) volcarnis.getWorld())
                    .add(LootContextParameters.ORIGIN, volcarnis.getPos())
                    .add(LootContextParameters.THIS_ENTITY, volcarnis)
                    .build(LootContextTypes.GIFT);

            for (ItemStack stack : table.generateLoot(ctx)) {
                volcarnis.getWorld().spawnEntity(new ItemEntity(
                        volcarnis.getWorld(),
                        m.getX() - MathHelper.sin(volcarnis.bodyYaw * ((float)Math.PI / 180F)),
                        m.getY(),
                        m.getZ() + MathHelper.cos(volcarnis.bodyYaw * ((float)Math.PI / 180F)),
                        stack
                ));
            }
        }

        @Override
        public void tick() {
            if (owner != null && bedPos != null) {
                volcarnis.setInSittingPose(false);
                volcarnis.getNavigation().startMovingTo(bedPos.getX(), bedPos.getY(), bedPos.getZ(), 1.1);
                if (volcarnis.squaredDistanceTo(owner) < 2.5F) {
                    ++ticksOnBed;
                    if (ticksOnBed > this.getTickCount(16)) {
                        volcarnis.setInSleepingPose(true);
                        volcarnis.setHeadDown(false);
                    } else {
                        volcarnis.lookAtEntity(owner, 45.0F, 45.0F);
                        volcarnis.setHeadDown(true);
                    }
                } else {
                    volcarnis.setInSleepingPose(false);
                }
            }
        }
    }
}
