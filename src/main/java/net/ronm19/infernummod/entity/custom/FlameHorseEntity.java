package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
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
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.MountableEntity;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FlameHorseEntity extends AbstractHorseEntity implements MountableEntity {

    private static final TrackedData<Boolean> TAMED =
            DataTracker.registerData(FlameHorseEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OWNER_UUID =
            DataTracker.registerData(FlameHorseEntity.class, TrackedDataHandlerRegistry.STRING);

    public FlameHorseEntity( EntityType<? extends AbstractHorseEntity> entityType, World world ) {
        super(entityType, world);
        this.setTame(false);
        this.isFireImmune();
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isClient && getWorld() instanceof ServerWorld serverWorld) {
            // Flame aura
            if (this.age % 5 == 0) {
                serverWorld.spawnParticles(ParticleTypes.FLAME, getX(), getY() + 1.0, getZ(),
                        2, 0.2, 0.2, 0.2, 0.01);
            }
        }

        // Buff rider
        if (this.hasPassengers() && this.getFirstPassenger() instanceof PlayerEntity rider) {
            rider.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 40, 0, true, false));
            rider.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 0, true, false));
        }
    }

    public static DefaultAttributeContainer.Builder createFlameHorseAttributes() {
        return HorseEntity.createBaseHorseAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45D)
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, 1.2D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.2D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.9D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(6, new AnimalMateGoal(this, 1D));
        this.goalSelector.add(7, new TemptGoal(this, 2.0D, Ingredient.ofItems(Items.BONE), false));
        this.goalSelector.add(8, new FollowParentGoal(this, 1D));
    }

    // ---------------- DATA -------------------------


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TAMED, false);
        this.dataTracker.startTracking(OWNER_UUID, "");
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

    protected float getBaseMovementSpeedMultiplier() {
        return 0.96F;
    }

    // ---------------- TAMING LOGIC ----------------
    @Override
    public ActionResult interactMob( PlayerEntity player, Hand hand ) {
        ItemStack stack = player.getStackInHand(hand);

        if (!this.isTame() && stack.isOf(ModItems.INFERNAL_APPLE)) {
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            this.setTame(true);
            this.setOwnerUuid(player.getUuid());
            this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
            player.sendMessage(Text.literal("🔥 The Flame Horse accepts you!"), true);
            return ActionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwner(player)) {
            player.startRiding(this);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    // -------------------------- NBT -------------------------

    @Override
    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("TamedByPlayer", this.isTame());
        UUID uuid = this.getOwnerUuid();
        if (uuid != null) nbt.putString("Owner", uuid.toString());
    }

    @Override
    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TamedByPlayer")) this.setTame(nbt.getBoolean("TamedByPlayer"));
        if (nbt.contains("Owner")) this.setOwnerUuid(UUID.fromString(nbt.getString("Owner")));
    }

    protected float getPassengerAttachmentY( EntityDimensions dimensions, float scaleFactor) {
        return dimensions.height - (this.isBaby() ? 0.03125F : 0.28125F) * scaleFactor;
    }

    @Override
    public boolean canWalkOnFluid( FluidState state ) {
        return state.isIn(FluidTags.LAVA);
    }



    // Fire & lava immunity
    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean damage( DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.IS_FIRE) || source.isOf(DamageTypes.LAVA)) {
            return false; // ignore all fire/lava damage
        }
        return super.damage(source, amount);
    }


    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    @Nullable
    protected SoundEvent getEatSound() {
        return SoundEvents.ENTITY_HORSE_EAT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    protected SoundEvent getAngrySound() {
        return SoundEvents.ENTITY_HORSE_ANGRY;
    }

    // Fun step sound over lava-ish terrain (optional flair)
    @Override
    protected void playStepSound( BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BLOCK_LAVA_POP, 0.3F, 1.0F);
    }

    @Override
    public @Nullable PassiveEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return ModEntities.FLAME_HORSE.create(world);
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
    private static final double SEAT_SIDE = -0.03; // centered left/right
    private static final double SEAT_BACK = 0.10; // was 1.05 → move forward (closer to shoulders)
    private static final double SEAT_HEIGHT = 0.90; // was 0.60 → drop a bit lower


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
