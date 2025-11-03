package net.ronm19.infernummod.entity.custom;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.ronm19.infernummod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class MagmaGolemEntity extends IronGolemEntity {
    protected static final TrackedData<Byte> MAGMA_GOLEM_FLAGS;
    private static final int HEALTH_PER_MAGMA = 25;
    private int attackTicksLeft;
    private int lookingAtVillagerTicksLeft;
    private static final UniformIntProvider ANGER_TIME_RANGE;
    private int angerTime;
    @Nullable
    private UUID angryAt;

    public MagmaGolemEntity( EntityType<? extends IronGolemEntity> entityType, World world ) {
        super(entityType, world);
        this.setStepHeight(1.0F);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, (double)1.0F, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9, 32.0F));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, ( entity) -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
        this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MAGMA_GOLEM_FLAGS, (byte)0);
    }

    public static DefaultAttributeContainer.Builder createMagmaGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, (double)150.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, (double)0.28F)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, (double)1.4F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, (double)20.0F);
    }

    protected int getNextAirUnderwater(int air) {
        return air;
    }

    protected void pushAway( Entity entity) {
        if (entity instanceof Monster && this.getRandom().nextInt(20) == 0) {
            this.setTarget((LivingEntity)entity);
        }

        super.pushAway(entity);
    }

    public void tickMovement() {
        super.tickMovement();
        if (this.attackTicksLeft > 0) {
            --this.attackTicksLeft;
        }

        if (this.lookingAtVillagerTicksLeft > 0) {
            --this.lookingAtVillagerTicksLeft;
        }

        if (!this.getWorld().isClient) {
            this.tickAngerLogic((ServerWorld)this.getWorld(), true);
        }

    }

    public boolean shouldSpawnSprintingParticles() {
        return this.getVelocity().horizontalLengthSquared() > (double)2.5000003E-7F && this.random.nextInt(5) == 0;
    }

    public boolean canTarget(EntityType<?> type) {
        if (this.isPlayerCreated() && type == EntityType.PLAYER) {
            return false;
        } else {
            return type != ModEntities.EMBER_HUND && super.canTarget(type);
        }
    }

    public void writeCustomDataToNbt( NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("PlayerCreated", this.isPlayerCreated());
        this.writeAngerToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setPlayerCreated(nbt.getBoolean("PlayerCreated"));
        this.readAngerFromNbt(this.getWorld(), nbt);
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

    @Override
    public boolean isFireImmune() {
        return true;
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    public boolean tryAttack(Entity target) {
        this.attackTicksLeft = 10;
        this.getWorld().sendEntityStatus(this, (byte)4);
        float f = this.getAttackDamage();
        float g = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
        boolean bl = target.damage(this.getDamageSources().mobAttack(this), g);
        if (bl) {
            double var10000;
            if (target instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)target;
                var10000 = livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
            } else {
                var10000 = (double)0.0F;
            }

            double d = var10000;
            double e = Math.max((double)0.0F, (double)1.0F - d);
            target.setVelocity(target.getVelocity().add((double)0.0F, (double)0.4F * e, (double)0.0F));
            this.applyDamageEffects(this, target);
        }

        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return bl;
    }

    public boolean damage( DamageSource source, float amount) {
        IronGolemEntity.Crack crack = this.getCrack();
        boolean bl = super.damage(source, amount);
        if (bl && this.getCrack() != crack) {
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }

        return bl;
    }

    public IronGolemEntity.Crack getCrack() {
        return IronGolemEntity.Crack.from(this.getHealth() / this.getMaxHealth());
    }

    public void handleStatus(byte status) {
        if (status == 4) {
            this.attackTicksLeft = 10;
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else if (status == 11) {
            this.lookingAtVillagerTicksLeft = 400;
        } else if (status == 34) {
            this.lookingAtVillagerTicksLeft = 0;
        } else {
            super.handleStatus(status);
        }

    }

    public int getAttackTicksLeft() {
        return this.attackTicksLeft;
    }

    public void setLookingAtVillager(boolean lookingAtVillager) {
        if (lookingAtVillager) {
            this.lookingAtVillagerTicksLeft = 400;
            this.getWorld().sendEntityStatus(this, (byte)11);
        } else {
            this.lookingAtVillagerTicksLeft = 0;
            this.getWorld().sendEntityStatus(this, (byte)34);
        }

    }

    protected SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }

    protected ActionResult interactMob( PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Blocks.MAGMA_BLOCK.asItem())) {
            return ActionResult.PASS;
        } else {
            float f = this.getHealth();
            this.heal(25.0F);
            if (this.getHealth() == f) {
                return ActionResult.PASS;
            } else {
                float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0F, g);
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                return ActionResult.success(this.getWorld().isClient);
            }
        }
    }

    protected void playStepSound( BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    public int getLookingAtVillagerTicks() {
        return this.lookingAtVillagerTicksLeft;
    }

    public boolean isPlayerCreated() {
        return ((Byte)this.dataTracker.get(MAGMA_GOLEM_FLAGS) & 1) != 0;
    }

    public void setPlayerCreated(boolean playerCreated) {
        byte b = (Byte)this.dataTracker.get(MAGMA_GOLEM_FLAGS);
        if (playerCreated) {
            this.dataTracker.set(MAGMA_GOLEM_FLAGS, (byte)(b | 1));
        } else {
            this.dataTracker.set(MAGMA_GOLEM_FLAGS, (byte)(b & -2));
        }

    }

    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
    }

    public boolean canSpawn( WorldView world) {
        BlockPos blockPos = this.getBlockPos();
        BlockPos blockPos2 = blockPos.down();
        BlockState blockState = world.getBlockState(blockPos2);
        if (!blockState.hasSolidTopSurface(world, blockPos2, this)) {
            return false;
        } else {
            for(int i = 1; i < 3; ++i) {
                BlockPos blockPos3 = blockPos.up(i);
                BlockState blockState2 = world.getBlockState(blockPos3);
                if (!SpawnHelper.isClearForSpawn(world, blockPos3, blockState2, blockState2.getFluidState(), ModEntities.MAGMA_GOLEM)) {
                    return false;
                }
            }

            return SpawnHelper.isClearForSpawn(world, blockPos, world.getBlockState(blockPos), Fluids.EMPTY.getDefaultState(), ModEntities.MAGMA_GOLEM) && world.doesNotIntersectEntities(this);
        }
    }

    public Vec3d getLeashOffset() {
        return new Vec3d((double)0.0F, (double)(0.875F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
    }

    static {
        MAGMA_GOLEM_FLAGS = DataTracker.registerData(MagmaGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    }

    public static enum Crack {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.5F),
        HIGH(0.25F);

        private static final List<Crack> VALUES = (List) Stream.of(values()).sorted(Comparator.comparingDouble(( crack) -> (double)crack.maxHealthFraction)).collect(ImmutableList.toImmutableList());
        private final float maxHealthFraction;

        private Crack(float maxHealthFraction) {
            this.maxHealthFraction = maxHealthFraction;
        }

        public static Crack from( float healthFraction) {
            for(MagmaGolemEntity.Crack crack : VALUES) {
                if (healthFraction < crack.maxHealthFraction) {
                    return crack;
                }
            }

            return NONE;
        }
    }
}
