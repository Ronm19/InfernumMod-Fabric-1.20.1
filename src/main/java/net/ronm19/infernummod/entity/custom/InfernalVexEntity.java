package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.util.ModDamageTags;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumSet;

public class InfernalVexEntity extends VexEntity implements Monster {
    public static final float field_30502 = 45.836624F;
    public static final int field_28645 = MathHelper.ceil(3.9269907F);
    protected static final TrackedData<Byte> INFERNAL_VEX_FLAGS;
    private static final int CHARGING_FLAG = 1;
    @Nullable
    MobEntity owner;
    @Nullable
    private BlockPos bounds;
    private boolean alive;
    private int lifeTicks;


    public InfernalVexEntity( EntityType<? extends VexEntity> entityType, World world ) {
        super(entityType, world);
        this.moveControl = new InfernalVexMoveControl(this, this);
        this.experiencePoints = 3;
    }

    public static DefaultAttributeContainer.Builder createInfernalVexAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0D)         // tougher than normal vex (14 → 28 HP)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0D)       // solid melee damage with fire effect
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 1.2D)        // slightly faster strikes
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45D)     // quick and agile in flight
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.55D)       // faster aerial movement
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)       // can chase targets further away
                .add(EntityAttributes.GENERIC_ARMOR, 4.0D)               // mild resistance (like light armor)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3D); // resists slight knockback
    }


    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ChargeTargetGoal(this));
        this.goalSelector.add(2, new LookAtTargetGoal(this));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(4, new LookAtEntityGoal(this, MobEntity.class, 8.0F));

        this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new TrackOwnerTargetGoal(this, this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, TameableEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        // 🔥 Leave a fiery trail
        if (this.getWorld().isClient) {
            for (int i = 0; i < 2; ++i) {
                double ox = (this.random.nextDouble() - 0.5D) * 0.3D;
                double oy = (this.random.nextDouble() - 0.5D) * 0.3D;
                double oz = (this.random.nextDouble() - 0.5D) * 0.3D;
                this.getWorld().addParticle(ParticleTypes.FLAME,
                        this.getX() + ox, this.getY() + oy, this.getZ() + oz, 0.0, 0.02, 0.0);
                this.getWorld().addParticle(ParticleTypes.SMOKE,
                        this.getX() + ox, this.getY() + oy, this.getZ() + oz, 0.0, 0.01, 0.0);
            }

            this.noClip = true;
            super.tick();
            this.noClip = false;
            this.setNoGravity(true);
            if (this.alive && --this.lifeTicks <= 0) {
                this.lifeTicks = 20;
                this.damage(this.getDamageSources().starve(), 1.0F);
            }
        }

        // 🌧 Water weakness
        if (this.isWet() || this.isTouchingWater()) {
            this.damage(this.getDamageSources().drown(), 1.0F);
        }
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(INFERNAL_VEX_FLAGS, (byte) 0);
    }


    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo( DamageSource source ) {
        // Still immune to fire and explosions — but NOT arrows/tridents
        return super.isInvulnerableTo(source)
                || source.isIn(DamageTypeTags.IS_FIRE);
    }


    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("BoundX")) {
            this.bounds = new BlockPos(nbt.getInt("BoundX"), nbt.getInt("BoundY"), nbt.getInt("BoundZ"));
        }

        if (nbt.contains("LifeTicks")) {
            this.setLifeTicks(nbt.getInt("LifeTicks"));
        }

    }

    @Override
    public boolean tryAttack( Entity target ) {
        boolean result = super.tryAttack(target);
        if (result && target instanceof LivingEntity living)
            living.setOnFireFor(3);
        return result;
    }

    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        if (this.bounds != null) {
            nbt.putInt("BoundX", this.bounds.getX());
            nbt.putInt("BoundY", this.bounds.getY());
            nbt.putInt("BoundZ", this.bounds.getZ());
        }

        if (this.alive) {
            nbt.putInt("LifeTicks", this.lifeTicks);
        }

    }

    @Nullable
    public MobEntity getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBounds() {
        return this.bounds;
    }

    public void setBounds( @Nullable BlockPos bounds ) {
        this.bounds = bounds;
    }

    private boolean areFlagsSet( int mask ) {
        int i = (Byte) this.dataTracker.get(INFERNAL_VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag( int mask, boolean value ) {
        int i = (Byte) this.dataTracker.get(INFERNAL_VEX_FLAGS);
        if (value) {
            i |= mask;
        } else {
            i &= ~mask;
        }

        this.dataTracker.set(INFERNAL_VEX_FLAGS, (byte) (i & 255));
    }

    public boolean isCharging() {
        return this.areFlagsSet(1);
    }

    public void setCharging( boolean charging ) {
        this.setVexFlag(1, charging);
    }

    public void setOwner( MobEntity owner ) {
        this.owner = owner;
    }

    public void setLifeTicks( int lifeTicks ) {
        this.alive = true;
        this.lifeTicks = lifeTicks;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    @Nullable
    public EntityData initialize( ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt ) {
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    protected void initEquipment( Random random, LocalDifficulty localDifficulty ) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    protected float getUnscaledRidingOffset( Entity vehicle ) {
        return 0.04F;
    }

    protected Vector3f getPassengerAttachmentPos( Entity passenger, EntityDimensions dimensions, float scaleFactor ) {
        return new Vector3f(0.0F, dimensions.height - 0.0625F * scaleFactor, 0.0F);
    }

    static {
        INFERNAL_VEX_FLAGS = DataTracker.registerData(InfernalVexEntity.class, TrackedDataHandlerRegistry.BYTE);
    }

    class InfernalVexMoveControl extends MoveControl {
        public InfernalVexMoveControl( InfernalVexEntity infernalVexEntity, InfernalVexEntity owner ) {
            super(owner);
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.targetX - InfernalVexEntity.this.getX(), this.targetY - InfernalVexEntity.this.getY(), this.targetZ - InfernalVexEntity.this.getZ());
                double d = vec3d.length();
                if (d < InfernalVexEntity.this.getBoundingBox().getAverageSideLength()) {
                    this.state = State.WAIT;
                    InfernalVexEntity.this.setVelocity(InfernalVexEntity.this.getVelocity().multiply((double) 0.5F));
                } else {
                    InfernalVexEntity.this.setVelocity(InfernalVexEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                    if (InfernalVexEntity.this.getTarget() == null) {
                        Vec3d vec3d2 = InfernalVexEntity.this.getVelocity();
                        InfernalVexEntity.this.setYaw(-((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * (180F / (float) Math.PI));
                        InfernalVexEntity.this.bodyYaw = InfernalVexEntity.this.getYaw();
                    } else {
                        double e = InfernalVexEntity.this.getTarget().getX() - InfernalVexEntity.this.getX();
                        double f = InfernalVexEntity.this.getTarget().getZ() - InfernalVexEntity.this.getZ();
                        InfernalVexEntity.this.setYaw(-((float) MathHelper.atan2(e, f)) * (180F / (float) Math.PI));
                        InfernalVexEntity.this.bodyYaw = InfernalVexEntity.this.getYaw();
                    }
                }

            }
        }
    }

    class ChargeTargetGoal extends Goal {
        public ChargeTargetGoal( InfernalVexEntity infernalVexEntity ) {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            LivingEntity livingEntity = InfernalVexEntity.this.getTarget();
            if (livingEntity != null && livingEntity.isAlive() && !InfernalVexEntity.this.getMoveControl().isMoving() && InfernalVexEntity.this.random.nextInt(toGoalTicks(7)) == 0) {
                return InfernalVexEntity.this.squaredDistanceTo(livingEntity) > (double) 4.0F;
            } else {
                return false;
            }
        }

        public boolean shouldContinue() {
            return InfernalVexEntity.this.getMoveControl().isMoving() && InfernalVexEntity.this.isCharging() && InfernalVexEntity.this.getTarget() != null && InfernalVexEntity.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingEntity = InfernalVexEntity.this.getTarget();
            if (livingEntity != null) {
                Vec3d vec3d = livingEntity.getEyePos();
                InfernalVexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, (double) 1.0F);
            }

            InfernalVexEntity.this.setCharging(true);
            InfernalVexEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
        }

        public void stop() {
            InfernalVexEntity.this.setCharging(false);
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = InfernalVexEntity.this.getTarget();
            if (livingEntity != null) {
                if (InfernalVexEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                    InfernalVexEntity.this.tryAttack(livingEntity);
                    InfernalVexEntity.this.setCharging(false);
                } else {
                    double d = InfernalVexEntity.this.squaredDistanceTo(livingEntity);
                    if (d < (double) 9.0F) {
                        Vec3d vec3d = livingEntity.getEyePos();
                        InfernalVexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, (double) 1.0F);
                    }
                }

            }
        }
    }

    class LookAtTargetGoal extends Goal {
        public LookAtTargetGoal( InfernalVexEntity infernalVexEntity ) {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            return !InfernalVexEntity.this.getMoveControl().isMoving() && InfernalVexEntity.this.random.nextInt(toGoalTicks(7)) == 0;
        }

        public boolean shouldContinue() {
            return false;
        }

        public void tick() {
            BlockPos blockPos = InfernalVexEntity.this.getBounds();
            if (blockPos == null) {
                blockPos = InfernalVexEntity.this.getBlockPos();
            }

            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.add(InfernalVexEntity.this.random.nextInt(15) - 7, InfernalVexEntity.this.random.nextInt(11) - 5, InfernalVexEntity.this.random.nextInt(15) - 7);
                if (InfernalVexEntity.this.getWorld().isAir(blockPos2)) {
                    InfernalVexEntity.this.moveControl.moveTo((double) blockPos2.getX() + (double) 0.5F, (double) blockPos2.getY() + (double) 0.5F, (double) blockPos2.getZ() + (double) 0.5F, (double) 0.25F);
                    if (InfernalVexEntity.this.getTarget() == null) {
                        InfernalVexEntity.this.getLookControl().lookAt((double) blockPos2.getX() + (double) 0.5F, (double) blockPos2.getY() + (double) 0.5F, (double) blockPos2.getZ() + (double) 0.5F, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        // Ignore self and owner
        if (target == this || target == this.owner) {
            return false;
        }

        // Ignore Infernum allies
        if (target instanceof InfernumEntity) {
            return false;
        }

        // Ignore creative / spectator players
        if (target instanceof PlayerEntity player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        // Otherwise, use vanilla targeting logic
        return super.canTarget(target);
    }

    class TrackOwnerTargetGoal extends TrackTargetGoal {
        private final TargetPredicate targetPredicate = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();

        public TrackOwnerTargetGoal( InfernalVexEntity infernalVexEntity, PathAwareEntity mob ) {
            super(mob, false);

        }

        public boolean canStart() {
            return InfernalVexEntity.this.owner != null && InfernalVexEntity.this.owner.getTarget() != null && this.canTrack(InfernalVexEntity.this.owner.getTarget(), this.targetPredicate);
        }

        public boolean canInfernalTarget(LivingEntity target) {
            if (target instanceof InfernumEntity) return false;
            if (target instanceof PlayerEntity player) {
                return !player.isCreative() && !player.isSpectator();
            }
            return target.isAlive() && target.canTakeDamage();
        }
    }
}
