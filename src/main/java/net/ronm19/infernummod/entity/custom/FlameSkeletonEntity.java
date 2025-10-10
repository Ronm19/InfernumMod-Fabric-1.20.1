package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.DayLightBurnImmuneEntity;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class FlameSkeletonEntity extends SkeletonEntity implements DayLightBurnImmuneEntity, Monster {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public FlameSkeletonEntity( EntityType<? extends SkeletonEntity> entityType, World world ) {
        super(entityType, world);
    }

    /* ---------- ATTRIBUTES ---------- */
    public static DefaultAttributeContainer.Builder createFlameSkeletonAttributes() {
        return AbstractSkeletonEntity.createAbstractSkeletonAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)           // 20 hearts
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)         // melee fallback
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)       // normal skeleton speed
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);        // bow range
    }

    /* ---------- AI GOALS ---------- */
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, true)); // shoot bow every 20 ticks
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D)); // wander when idle
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new SwimGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this).setGroupRevenge(FlameSkeletonEntity.class));
    }

    /* ---------- EQUIPMENT ON SPAWN ---------- */
    @Override
    public EntityData initialize( ServerWorldAccess world, LocalDifficulty difficulty,
                                  SpawnReason spawnReason, @Nullable EntityData entityData,
                                  @Nullable NbtCompound entityNbt ) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        // Equip a bow
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.1F);

        if (this.random.nextFloat() < 0.20F) { // 20% chance for any armor piece
            if (this.random.nextFloat() < 0.50F) {
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            }
            if (this.random.nextFloat() < 0.25F) {
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            }
            if (this.random.nextFloat() < 0.15F) {
                this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
            }
            if (this.random.nextFloat() < 0.10F) {
                this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
            }

            // Optional: fiery enchantment
            this.getEquippedStack(EquipmentSlot.HEAD)
                    .addEnchantment(net.minecraft.enchantment.Enchantments.FIRE_PROTECTION, 2);
        }

// Lower drop chance so it feels special
        this.setEquipmentDropChance(EquipmentSlot.HEAD, 0.05F);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.05F);
        this.setEquipmentDropChance(EquipmentSlot.LEGS, 0.05F);
        this.setEquipmentDropChance(EquipmentSlot.FEET, 0.05F);

        return data;
    }

    /* ---------- SOUNDS ---------- */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.ENTITY_SKELETON_STEP, 0.15F, 1.0F);
    }

    /* ---------- FIRE IMMUNITY ---------- */
    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean burnsInDaylight() {
        return false;
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
        setupAnimationStates();
    }

    /* ------------- OTHERS --------------- */

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);

        if (allowDrops && this.random.nextFloat() < 0.4F + 0.05F * lootingMultiplier) {
            this.dropStack(new ItemStack(ModItems.INFERNUM_BONE, 1 + this.random.nextInt(2)));
        }

    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean hit = super.tryAttack(target);
        if (hit && target instanceof LivingEntity living) {
            // Option A: burn
            living.setOnFireFor(4); // 4 seconds

            // Option B: wither effect
            // living.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 0));
        }
        return hit;
    }

    @Override
    public boolean cannotDespawn() {
        // Prevents despawn in normal conditions, but monsters still despawn on peaceful
        return this.getType().getSpawnGroup() != SpawnGroup.MONSTER
                || this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
    }
}



