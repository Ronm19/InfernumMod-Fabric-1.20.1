package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.variant.InfernalKnightVariant;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InfernalKnightEntity extends TameableEntity {

    // Command modes
    public enum CommandMode {
        FOLLOW(0), HOLD(1), PATROL(2), ATTACK(3);
        public final int id;

        CommandMode( int id ) {
            this.id = id;
        }

        public static CommandMode byId( int id ) {
            for (var v : values()) if (v.id == id) return v;
            return FOLLOW;
        }
    }

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static final TrackedData<Integer> COMMAND = DataTracker.registerData(InfernalKnightEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(InfernalKnightEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Boolean> SHIELDING = DataTracker.registerData(InfernalKnightEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    // Patrol center/radius (optional persisted)
    private BlockPos patrolCenter;
    private int patrolRadius = 20;

    public InfernalKnightEntity( EntityType<? extends TameableEntity> type, World world ) {
        super(type, world);
        this.setPathfindingPenalty(net.minecraft.entity.ai.pathing.PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(net.minecraft.entity.ai.pathing.PathNodeType.DAMAGE_FIRE, 0.0F);
        this.experiencePoints = 0;
        this.setAiDisabled(false);
        this.ignoreCameraFrustum = true;
        this.setPersistent();
        this.setCanPickUpLoot(false);
        this.setStepHeight(1.0f);
        this.isFireImmune();
    }

    public static DefaultAttributeContainer.Builder createKnightAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 150.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 19.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.9D)
                .add(EntityAttributes.GENERIC_ARMOR, 20.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    @Override
    public @Nullable PassiveEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return null;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COMMAND, CommandMode.FOLLOW.id);
        this.dataTracker.startTracking(SHIELDING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);// 0 = Melee by default
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

    /* ------------- TICK --------------- */

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStates();

            if (this.getVariant() == InfernalKnightVariant.ELITE && this.age % 10 == 0) {
                this.getWorld().addParticle(ParticleTypes.LAVA,
                        this.getX(), this.getEyeY() + 0.3, this.getZ(),
                        0.0, 0.01, 0.0);
            }
        }
    }


    // Fire/explosion handling
    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean damage( DamageSource source, float amount ) {
        if (source.isIn(DamageTypeTags.IS_FIRE)) return false;
        if (this.getVariant() == InfernalKnightVariant.ELITE && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            amount *= 0.3F; // reduce explosion damage heavily
        }
        if (isShielding()) amount *= 0.4f;
        return super.damage(source, amount);
    }


    @Override
    public boolean isInvulnerableTo( DamageSource damageSource ) {
        // explosions reduced if shielding; not fully invulnerable
        return super.isInvulnerableTo(damageSource);
    }

    // Commands
    public CommandMode getCommand() {
        return CommandMode.byId(this.dataTracker.get(COMMAND));
    }

    public void setCommand( CommandMode mode ) {
        this.dataTracker.set(COMMAND, mode.id);
    }

    public boolean isShielding() {
        return this.dataTracker.get(SHIELDING);
    }

    public void setShielding( boolean v ) {
        this.dataTracker.set(SHIELDING, v);
    }

    public void setPatrol( BlockPos center, int radius ) {
        this.patrolCenter = center;
        this.patrolRadius = Math.max(4, radius);
    }

    // Tame interaction: allow sitting as a “hold” pose via staff, but also hand-feed if you want later
    @Override
    public ActionResult interactMob( PlayerEntity player, Hand hand ) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();

        // Taming Item
        Item tamingItem = ModItems.INFERNAL_GEM;

        // Taming logic (if not tamed)
        if (!this.isTamed() && item == tamingItem) {
            if (!this.getWorld().isClient()) {
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                this.tameTo(player);
                this.navigation.stop();
                this.setTarget(null);
                this.getWorld().sendEntityStatus(this, (byte) 7); // heart particles

                // Standing right after taming
                setSitting(false);
                setInSittingPose(false);
            }
            return ActionResult.SUCCESS;
        }

        // If tamed: follow / sit toggle OR command execution
        if (this.isTamed() && player.getUuid().equals(this.getOwnerUuid())) {
            if (item == ModItems.INFERNAL_ROYAL_STAFF) {
                // You could add mode cycling here too later
                player.sendMessage(Text.literal("⚔ Command acknowledged, my liege."), true);
                return ActionResult.SUCCESS;
            }

            // Empty-hand toggle sit / follow
            if (stack.isEmpty() && hand == Hand.MAIN_HAND) {
                boolean sitting = !this.isSitting();
                setSitting(sitting);
                setInSittingPose(sitting);

                if (sitting) {
                    player.sendMessage(Text.literal("🛡 Infernal Knight: Holding position."), true);
                } else {
                    player.sendMessage(Text.literal("🔥 Infernal Knight: Following you."), true);
                }

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }


    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        // Randomize variant (25% chance for Elite)
        InfernalKnightVariant variant = this.random.nextFloat() < 0.25F
                ? InfernalKnightVariant.ELITE
                : InfernalKnightVariant.DEFAULT;
        this.setVariant(variant);

        // Common equipment drop rates
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.1F);
        this.setEquipmentDropChance(EquipmentSlot.HEAD, 0.05F);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.05F);
        this.setEquipmentDropChance(EquipmentSlot.LEGS, 0.05F);
        this.setEquipmentDropChance(EquipmentSlot.FEET, 0.05F);

        // Optional: armor chance (keep that awesome Nether Ruby armor look)
        if (this.random.nextFloat() < 0.20F) {
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(ModItems.NETHER_RUBY_HELMET));
            this.equipStack(EquipmentSlot.CHEST, new ItemStack(ModItems.NETHER_RUBY_CHESTPLATE));
            this.equipStack(EquipmentSlot.LEGS, new ItemStack(ModItems.NETHER_RUBY_LEGGINGS));
            this.equipStack(EquipmentSlot.FEET, new ItemStack(ModItems.NETHER_RUBY_BOOTS));
            this.getEquippedStack(EquipmentSlot.HEAD)
                    .addEnchantment(Enchantments.BLAST_PROTECTION, 2);
        }

        // --- NEW FIX ---
        // Always apply proper gear *before* the entity ticks
        this.setupDefaultLoadout();

        // Safety: ensure they actually render their weapon instantly
        this.setCurrentHand(Hand.MAIN_HAND);
        this.setAttacking(false);

        // Fully heal to max based on loadout attributes
        this.setHealth(this.getMaxHealth());

        return data;
    }

    public void setupDefaultLoadout() {
        // Always clear hands before re-equipping (prevents stacking bugs)
        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        // --- ELITE KNIGHT ---
        if (this.getVariant() == InfernalKnightVariant.ELITE) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.INFERNUM_SWORD));
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(ModItems.INFERNO_SHIELD));

            // Attributes
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(250.0D);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(28.0D);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(30.0D);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.33D);

            // --- NORMAL KNIGHT ---
        } else {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.FIRERITE_SWORD));
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(ModItems.INFERNO_SHIELD));

            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(150.0D);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(19.0D);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(20.0D);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.30D);
        }

        // --- Common finalization ---
        this.setHealth(this.getMaxHealth());
        this.setAttacking(false);
        this.handSwingProgress = 0.0F; // reset animation
    }



    @Override
    protected void initGoals() {


        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, true) {
            @Override
            public boolean canStart() {
                if (getCommand() == CommandMode.ATTACK) return super.canStart();
                if (getCommand() == CommandMode.HOLD) return false;
                return getTarget() != null && super.canStart();
            }
        });

        goalSelector.add(2, new ShieldWhenHurtGoal());
        goalSelector.add(3, new FollowOwnerIfAllowedGoal(this, 1.05D, 6.0F, 2.0F));
        goalSelector.add(4, new PatrolGoal());
        goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        goalSelector.add(9, new LookAroundGoal(this));

        targetSelector.add(1, new RevengeGoal(this));
        targetSelector.add(2, new ActiveTargetGoal<>(this, HostileEntity.class, 10, true, false,
                e -> getCommand() == CommandMode.ATTACK && e != getOwner() && !(e instanceof InfernalKnightEntity)));
        targetSelector.add(3, new FollowTargetGoal(this));
    }

    // FOLLOW: vanilla-like follow owner when not HOLD or PATROL
    public static class FollowOwnerIfAllowedGoal extends FollowOwnerGoal {
        private final InfernalKnightEntity knight;
        public FollowOwnerIfAllowedGoal(InfernalKnightEntity mob, double speed, float min, float max) {
            super(mob, speed, min, max, false);
            this.knight = mob;
        }
        @Override public boolean canStart() {
            if (!knight.isTamed()) return false;
            var mode = knight.getCommand();
            if (mode == CommandMode.HOLD || mode == CommandMode.PATROL || mode == CommandMode.ATTACK) return false;
            return super.canStart();
        }
    }

    // HOLD: stand ground & raise “shielding” briefly after being hit
    private class ShieldWhenHurtGoal extends Goal {
        private int ticks;
        ShieldWhenHurtGoal() { setControls(EnumSet.of(Control.MOVE, Control.LOOK)); }
        @Override public boolean canStart() {
            return getCommand() == CommandMode.HOLD;
        }
        @Override public void start() {
            getNavigation().stop();
        }
        @Override public void tick() {
            // idle but occasionally “shield”
            if (age % 40 == 0) setShielding(true);
            if (isShielding() && ++ticks > 20) { setShielding(false); ticks = 0; }
        }
    }

    // PATROL: stroll around patrolCenter
    private class PatrolGoal extends Goal {
        private static final int BASE_COOLDOWN = 40;

        private Vec3d pointA;
        private Vec3d pointB;
        private Vec3d currentTarget;
        private int cooldown;
        private boolean movingToB = true;

        PatrolGoal() {
            setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (getCommand() != CommandMode.PATROL) return false;
            if (patrolCenter == null) patrolCenter = getBlockPos();
            return true;
        }

        @Override
        public boolean shouldContinue() {
            return getCommand() == CommandMode.PATROL;
        }

        @Override
        public void start() {
            cooldown = 0;
            generatePatrolRoute();
            currentTarget = movingToB ? pointB : pointA;
            if (currentTarget != null) {
                getNavigation().startMovingTo(currentTarget.x, currentTarget.y, currentTarget.z, 1.0);
            }
        }

        @Override
        public void tick() {
            Random random = InfernalKnightEntity.this.getRandom();

            if (cooldown > 0) {
                cooldown--;
                return;
            }

            // If no current target or path is idle, resume movement
            if (currentTarget == null || getNavigation().isIdle()) {
                currentTarget = movingToB ? pointB : pointA;
                if (currentTarget != null) {
                    getNavigation().startMovingTo(currentTarget.x, currentTarget.y, currentTarget.z, 1.0);
                }
                cooldown = BASE_COOLDOWN + random.nextInt(40);
            }

            // Occasionally glance toward patrol center
            if (random.nextInt(200) == 0) {
                lookAt(Vec3d.ofCenter(patrolCenter));
            }

            // If close enough to current target, switch direction
            if (currentTarget != null && distanceTo(currentTarget) < 2.5) {
                movingToB = !movingToB;
                currentTarget = movingToB ? pointB : pointA;
                if (currentTarget != null) {
                    getNavigation().startMovingTo(currentTarget.x, currentTarget.y, currentTarget.z, 1.0);
                }
                cooldown = BASE_COOLDOWN + random.nextInt(60);
            }
        }

        /** Makes the knight look toward a point naturally */
        private void lookAt(Vec3d vec3d) {
            getLookControl().lookAt(vec3d.x, vec3d.y, vec3d.z);
        }

        /** Calculates distance from the knight to a Vec3d target */
        private double distanceTo(Vec3d target) {
            return InfernalKnightEntity.this.getPos().distanceTo(target);
        }

        /** Generates two opposite patrol points around the center */
        private void generatePatrolRoute() {
            Random random = InfernalKnightEntity.this.getRandom();

            // pick a random direction
            double angle = random.nextDouble() * Math.PI * 2;
            double offsetX = Math.cos(angle) * patrolRadius;
            double offsetZ = Math.sin(angle) * patrolRadius;

            Vec3d center = Vec3d.ofCenter(patrolCenter);
            pointA = findValidGround(center.add(offsetX, 0, offsetZ));
            pointB = findValidGround(center.add(-offsetX, 0, -offsetZ));

            // fallback: if both fail, default to center
            if (pointA == null) pointA = center;
            if (pointB == null) pointB = center;
        }

        /** Finds a valid ground position near a target point */
        @Nullable
        private Vec3d findValidGround(Vec3d pos) {
            BlockPos.Mutable mutable = BlockPos.ofFloored(pos).mutableCopy();
            World world = getWorld();

            while (mutable.getY() > world.getBottomY()) {
                BlockState state = world.getBlockState(mutable);
                if (state.isSolidBlock(world, mutable)) {
                    return Vec3d.ofCenter(mutable.up()); // place entity just above solid ground
                }
                mutable.move(Direction.DOWN);
            }
            return null;
        }
    }


    private static class FollowTargetGoal extends ActiveTargetGoal<LivingEntity> {
        private final InfernalKnightEntity knight;

        public FollowTargetGoal(InfernalKnightEntity knight) {
            super(knight, LivingEntity.class, true);
            this.knight = knight;
        }

        @Override
        public boolean canStart() {
            if (!knight.isTamed()) return false;

            LivingEntity owner = knight.getOwner();
            if (owner == null) return false;

            // Check if the owner's last attacker is valid
            LivingEntity threat = owner.getAttacker();

            // If none, check for attacks against allied entities
            if (threat == null) {
                // Find nearby allies that are tamed by same owner and attacked recently
                List<InfernalKnightEntity> allies = knight.getWorld()
                        .getEntitiesByClass(InfernalKnightEntity.class,
                                knight.getBoundingBox().expand(12.0D),
                                ally -> ally.isTamed() && owner.getUuid().equals(ally.getOwnerUuid()));

                for (InfernalKnightEntity ally : allies) {
                    if (ally.getAttacker() != null
                            && ally.getAttacker().isAlive()
                            && !(ally.getAttacker() instanceof InfernalKnightEntity)) {
                        threat = ally.getAttacker();
                        break;
                    }
                }
            }

            // No threats found
            if (threat == null || !threat.isAlive()) return false;

            // Do not target friendly entities or other Knights
            if (threat instanceof InfernalKnightEntity) return false;
            if (threat == owner) return false;

            this.targetEntity = threat;
            return true;
        }

        @Override
        public void start() {
            if (this.targetEntity != null) {
                knight.setTarget(this.targetEntity);
                knight.setCommand(CommandMode.ATTACK);
                knight.getWorld().playSound(null, knight.getBlockPos(),
                        SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.7F, 1.0F);
            }
            super.start();
        }
    }

    public static class InfernalBowAttackGoal extends Goal {
        private final InfernalKnightEntity knight;
        private final double speed;
        private int attackInterval;
        private final float squaredRange;
        private int cooldown = -1;
        private int targetVisibilityTicks;
        private boolean movingLeft;
        private boolean movingBack;
        private int combatTicks = -1;

        public InfernalBowAttackGoal(InfernalKnightEntity knight, double speed, int attackInterval, float range) {
            this.knight = knight;
            this.speed = speed;
            this.attackInterval = attackInterval;
            this.squaredRange = range * range;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        public void setAttackInterval(int attackInterval) {
            this.attackInterval = attackInterval;
        }

        @Override
        public boolean canStart() {
            return this.knight.getTarget() != null && this.isHoldingBow();
        }

        protected boolean isHoldingBow() {
            ItemStack stack = this.knight.getMainHandStack();
            return stack.getItem() instanceof net.minecraft.item.BowItem;
        }


        @Override
        public boolean shouldContinue() {
            return (this.canStart() || !this.knight.getNavigation().isIdle()) && this.isHoldingBow();
        }

        @Override
        public void start() {
            this.knight.setAttacking(true);
        }

        @Override
        public void stop() {
            this.knight.setAttacking(false);
            this.targetVisibilityTicks = 0;
            this.cooldown = -1;
            this.knight.clearActiveItem();
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.knight.getTarget();
            if (target == null) return;

            double distance = this.knight.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
            boolean canSee = this.knight.getVisibilityCache().canSee(target);

            if (canSee) ++this.targetVisibilityTicks;
            else --this.targetVisibilityTicks;

            if (distance <= (double) this.squaredRange && this.targetVisibilityTicks >= 20) {
                this.knight.getNavigation().stop();
                ++this.combatTicks;
            } else {
                this.knight.getNavigation().startMovingTo(target, this.speed);
                this.combatTicks = -1;
            }

            if (this.combatTicks >= 20) {
                if (this.knight.getRandom().nextFloat() < 0.3f) this.movingLeft = !this.movingLeft;
                if (this.knight.getRandom().nextFloat() < 0.3f) this.movingBack = !this.movingBack;
                this.combatTicks = 0;
            }

            if (this.combatTicks > -1) {
                if (distance > (double)(this.squaredRange * 0.75F)) this.movingBack = false;
                else if (distance < (double)(this.squaredRange * 0.25F)) this.movingBack = true;

                this.knight.getMoveControl().strafeTo(this.movingBack ? -0.5F : 0.5F,
                        this.movingLeft ? 0.5F : -0.5F);
            }

            this.knight.lookAtEntity(target, 30.0F, 30.0F);

            if (this.knight.isUsingItem()) {
                if (!canSee && this.targetVisibilityTicks < -60) {
                    this.knight.clearActiveItem();
                } else if (canSee) {
                    int i = this.knight.getItemUseTime();
                    if (i >= 20) {
                        this.knight.clearActiveItem();
                        ((RangedAttackMob) this.knight).shootAt(target, BowItem.getPullProgress(i));
                        this.cooldown = this.attackInterval;
                    }
                }
            } else if (--this.cooldown <= 0 && this.targetVisibilityTicks >= -60) {
                this.knight.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.knight, Items.BOW));
            }
        }
    }


    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }


    // Ownership helpers
    public void tameTo(PlayerEntity player) {
        this.setOwner(player);
        this.setTamed(true);
        this.setCommand(CommandMode.FOLLOW);
        this.navigation.stop();
        this.getWorld().sendEntityStatus(this, (byte)7);
}

    // Persistence
    @Override public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Command", getCommand().id);
        if (patrolCenter != null) nbt.putLong("PatrolCenter", patrolCenter.asLong());
        nbt.putInt("PatrolRadius", patrolRadius);
    }
    @Override public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setCommand(CommandMode.byId(nbt.getInt("Command")));
        patrolCenter = nbt.contains("PatrolCenter") ? BlockPos.fromLong(nbt.getLong("PatrolCenter")) : null;
        patrolRadius = nbt.getInt("PatrolRadius") == 0 ? 10 : nbt.getInt("PatrolRadius");
    }


    //* INFERNAL KNIGHT ARCHER VARIANT *//

    public InfernalKnightVariant getVariant() {
        return InfernalKnightVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant( InfernalKnightVariant variant ) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    /* ---------- SOUNDS ---------- */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.ENTITY_WARDEN_STEP, 0.15F, 1.0F);
    }
}
