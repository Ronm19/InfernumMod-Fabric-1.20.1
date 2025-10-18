package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
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
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.ronm19.infernummod.api.interfaces.MountableEntity;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class InfernalHorseEntity extends ZombieHorseEntity implements MountableEntity {

    private static final TrackedData<Boolean> TAMED =
            DataTracker.registerData(InfernalHorseEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OWNER_UUID =
            DataTracker.registerData(InfernalHorseEntity.class, TrackedDataHandlerRegistry.STRING);

    public InfernalHorseEntity( EntityType<? extends ZombieHorseEntity> entityType, World world ) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    public static DefaultAttributeContainer.Builder createInfernalHorseAttributes() {
        return AbstractHorseEntity.createBaseHorseAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30D)
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, 0.75D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0);
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
        this.goalSelector.add(7, new TemptGoal(this, 2.0D, Ingredient.ofItems(ModItems.CURSED_FLINT), false));
        this.goalSelector.add(8, new FollowParentGoal(this, 1D));


    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TAMED, false);
        this.dataTracker.startTracking(OWNER_UUID, "");
    }

    @Override
    public void tick() {
        super.tick();

        // 🔥 Fire/smoke visuals
        if (this.getWorld().isClient) {
            double motion = this.getVelocity().horizontalLength();

            // --- Aura when idle ---
            if (motion < 0.05 && this.age % 20 == 0) {
                this.getWorld().addParticle(ParticleTypes.SMALL_FLAME,
                        this.getX(), this.getY() + 1.2, this.getZ(), 0, 0.01, 0);
            }

            // --- Fiery hoof trail when running ---
            if (motion > 0.1) {
                for (int i = 0; i < 3; i++) {
                    double ox = (this.random.nextDouble() - 0.5D) * 0.8D;
                    double oy = 0.1D + this.random.nextDouble() * 0.3D;
                    double oz = (this.random.nextDouble() - 0.5D) * 0.8D;

                    this.getWorld().addParticle(ParticleTypes.FLAME,
                            this.getX() + ox, this.getY() + oy, this.getZ() + oz,
                            0.0, 0.02, 0.0);

                    if (this.random.nextInt(6) == 0) {
                        this.getWorld().addParticle(ParticleTypes.LAVA,
                                this.getX() + ox, this.getY() + oy, this.getZ() + oz,
                                0.0, 0.02, 0.0);
                    }
                }
            }

            // --- Jump sparks ---
            if (this.isOnGround() && this.age % 15 == 0 && motion > 0.2) {
                for (int i = 0; i < 4; i++) {
                    double ox = (this.random.nextDouble() - 0.5D) * 1.2D;
                    double oz = (this.random.nextDouble() - 0.5D) * 1.2D;
                    this.getWorld().addParticle(ParticleTypes.SMOKE,
                            this.getX() + ox, this.getY() + 0.1D, this.getZ() + oz,
                            0.0, 0.02, 0.0);
                }
            }
        }

        // 💧 Water weakness
        if (this.isTouchingWaterOrRain()) {
            this.damage(this.getDamageSources().drown(), 0.5F);
            this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 0, 0.05, 0);
        }

        // 🔊 Occasional crackle sound
        if (this.age % 80 == 0 && this.random.nextInt(4) == 0) {
            this.playSound(this.random.nextBoolean()
                    ? SoundEvents.BLOCK_LAVA_POP
                    : SoundEvents.BLOCK_FIRE_AMBIENT, 0.5F, 1.0F + this.random.nextFloat() * 0.4F);
        }
    }


    // 🔥 Taming behavior — Blaze Powder
    @Override
    public ActionResult interactMob( PlayerEntity player, Hand hand ) {
        ItemStack stack = player.getStackInHand(hand);

        if (!this.isTame() && stack.isOf(Items.BLAZE_POWDER)) {
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            this.setTame(true);
            this.setOwnerUuid(player.getUuid());
            this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
            player.sendMessage(Text.literal("🔥 The Infernal Horse accepts your flame!"), true);
            return ActionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwner(player)) {
            player.startRiding(this);
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

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.ENTITY_HORSE_STEP, 0.25F, 1.0F);
        // heavy footsteps, quieter volume
    }


    public boolean canBeRidden() {
        return this.isTame();
    }

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

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    protected float getPassengerAttachmentY( EntityDimensions dimensions, float scaleFactor) {
        return dimensions.height - (this.isBaby() ? 0.03125F : 0.28125F) * scaleFactor;
    }

    @Override
    public boolean canWalkOnFluid( FluidState state ) {
        return state.isIn(FluidTags.LAVA);
    }

    // ---------------- BREEDING ----------------
    @Override
    public InfernalHorseEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return ModEntities.INFERNAL_HORSE.create(world);
    }

    // ---------------- IMMUNITIES ----------------

    @Override
    public boolean isFireImmune() {
        return true;
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