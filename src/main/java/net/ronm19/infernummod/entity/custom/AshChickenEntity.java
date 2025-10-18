package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class AshChickenEntity extends ChickenEntity {
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(
            Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS,
            Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD
    );

    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flapSpeed = 1.0F;
    public int eggLayTime;
    public boolean hasJockey;

    public AshChickenEntity(EntityType<? extends ChickenEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 3;
        this.eggLayTime = this.random.nextInt(3000) + 1000;

        // Heat-friendly navigation
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createAshChickenAttributes() {
        return ChickenEntity.createChickenAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.4D)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1D);
    }

    // ---------------- GOALS ----------------
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4D));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, BREEDING_INGREDIENT, false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    // ---------------- PARTICLES ----------------
    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.getWorld().isClient) {
            // Wing sparks
            if (this.random.nextInt(12) == 0 && !this.isOnGround()) {
                this.getWorld().addParticle(
                        ParticleTypes.SMALL_FLAME,
                        this.getX() + (random.nextDouble() - 0.5D) * 0.3D,
                        this.getY() + 0.4D,
                        this.getZ() + (random.nextDouble() - 0.5D) * 0.3D,
                        0.0D, 0.02D, 0.0D);
            }

            // Lava droplet footsteps
            if (this.isOnGround() && this.random.nextInt(25) == 0) {
                this.getWorld().addParticle(
                        ParticleTypes.LAVA,
                        this.getX() + (random.nextDouble() - 0.5D) * 0.4D,
                        this.getY() + 0.1D,
                        this.getZ() + (random.nextDouble() - 0.5D) * 0.4D,
                        0.0D, 0.02D, 0.0D);
            }

            // Wing animation physics
            this.prevFlapProgress = this.flapProgress;
            this.prevMaxWingDeviation = this.maxWingDeviation;
            this.maxWingDeviation += (this.isOnGround() ? -1.0F : 4.0F) * 0.3F;
            this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
            if (!this.isOnGround() && this.flapSpeed < 1.0F) {
                this.flapSpeed = 1.0F;
            }

            this.flapSpeed *= 0.92F;
            Vec3d velocity = this.getVelocity();

            if (!this.isOnGround() && velocity.y < 0.0D) {
                this.setVelocity(velocity.multiply(1.0D, 0.6D, 1.0D));
            }

            this.flapProgress += this.flapSpeed * 2.0F;
        }

        // Egg laying (server side)
        if (!this.getWorld().isClient && this.isAlive() && !this.isBaby() && !this.hasJockey() && --this.eggLayTime <= 0) {
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(ModItems.ASH_EGG);
            this.emitGameEvent(GameEvent.ENTITY_PLACE);
            this.eggLayTime = this.random.nextInt(3000) + 1000;
        }
    }

    // ---------------- FIRE IMMUNITY ----------------
    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE)) {
            return false;
        }
        return super.damage(source, amount);
    }

    // ---------------- DROPS ----------------
    @Override
    protected void dropInventory() {
        if (!this.isBaby()) {
            this.dropStack(new ItemStack(Items.COOKED_CHICKEN));
        }
        super.dropInventory();
    }

    // ---------------- SOUNDS ----------------
    @Override
    protected void playStepSound( BlockPos pos, net.minecraft.block.BlockState state) {
        this.playSound(SoundEvents.BLOCK_LAVA_POP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CHICKEN_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    // --------------- OTHER ------------------
    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return this.isBaby() ? dimensions.height * 0.85F : dimensions.height * 0.92F;
    }

    @Nullable
    @Override
    public AshChickenEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return ModEntities.ASH_CHICKEN.create(serverWorld);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    @Override
    public int getXpToDrop() {
        return this.hasJockey ? 10 : super.getXpToDrop();
    }

    // ---------------- JOCKEY ----------------
    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return this.hasJockey;
    }

    public boolean hasJockey() {
        return this.hasJockey;
    }

    public void setHasJockey(boolean hasJockey) {
        this.hasJockey = hasJockey;
    }
}