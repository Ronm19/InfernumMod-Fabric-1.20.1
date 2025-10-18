package net.ronm19.infernummod.entity.custom;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.*;
import net.ronm19.infernummod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class LavagerEntity extends PillagerEntity {
    private static final TrackedData<Boolean> CHARGING;
    private static final int field_30478 = 5;
    private static final int field_30476 = 300;
    private static final float CROSSBOW_SHOT_SPEED = 1.6F;
    private final SimpleInventory inventory = new SimpleInventory(5);


    public LavagerEntity( EntityType<? extends PillagerEntity> entityType, World world ) {
        super(entityType, world);
        this.experiencePoints = 10;
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        // Replace default crossbow goal with custom flaming one
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new CrossbowAttackGoal<>(this, (double) 1.0F, 8.0F));
        this.goalSelector.add(2, new RaiderEntity.PatrolApproachGoal(this, 10.0F));
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 15.0F, 1.0F));
        this.goalSelector.add(5, new LookAtEntityGoal(this, MobEntity.class, 15.0F));

        this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, TameableEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    // ------------- ATTRIBUTES -------------------- //

    public static DefaultAttributeContainer.Builder createLavagerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 36.0D)          // Tougher than a Pillager
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.34D)       // Slightly faster to keep range
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 30.0D)         // Spots players from afar
                .add(EntityAttributes.GENERIC_ARMOR, 6.0D)                 // Fire-hardened skin
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0D)         // Melee fallback damage
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5D)      // Minor pushback
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.25D); // Some stability in combat
    }


    // ---------------- FIRE IMMUNITY -------------- //

    @Override
    public boolean isFireImmune() {
        return true;
    }


    // ---------------- SOUNDS -------------- //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    // ---------------- TICK -------------- //

    @Override
    public void tick() {
        super.tick();

        if (getWorld().isClient()) return;
        ServerWorld world = (ServerWorld) getWorld();

        // Subtle ember particles
        if (this.age % 10 == 0) {
            world.spawnParticles(ParticleTypes.SMALL_FLAME,
                    getX(), getY() + 1.3, getZ(),
                    2, 0.15, 0.2, 0.15, 0.01);
        }
    }

    // -------------------- DATA ------------------- //

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHARGING, false);
    }

    public boolean isCharging() {
        return (Boolean) this.dataTracker.get(CHARGING);
    }

    public void setCharging( boolean charging ) {
        this.dataTracker.set(CHARGING, charging);
    }

    // ------------------ CROSSBOW SYSTEM ------------- //

    public boolean canUseRangedWeapon( RangedWeaponItem weapon ) {
        return weapon == Items.CROSSBOW;
    }

    public void postShoot() {
        this.despawnCounter = 0;
    }

    public IllagerEntity.State getState() {
        if (this.isCharging()) {
            return State.CROSSBOW_CHARGE;
        } else if (this.isHolding(Items.CROSSBOW)) {
            return State.CROSSBOW_HOLD;
        } else {
            return this.isAttacking() ? State.ATTACKING : State.NEUTRAL;
        }
    }

    // ------------------ NBT ------------- //

    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        this.writeInventory(nbt);
    }

    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        this.readInventory(nbt);
        this.setCanPickUpLoot(true);
    }

    // ------------------ OTHER --------------- //

    public float getPathfindingFavor( BlockPos pos, WorldView world ) {
        return 0.0F;
    }

    public int getLimitPerChunk() {
        return 1;
    }

    @Nullable
    public EntityData initialize( ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt ) {
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    protected void initEquipment( Random random, LocalDifficulty localDifficulty ) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    protected void enchantMainHandItem( Random random, float power ) {
        super.enchantMainHandItem(random, power);
        if (random.nextInt(300) == 0) {
            ItemStack itemStack = this.getMainHandStack();
            if (itemStack.isOf(Items.CROSSBOW)) {
                Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack);
                map.putIfAbsent(Enchantments.PIERCING, 1);
                EnchantmentHelper.set(map, itemStack);
                this.equipStack(EquipmentSlot.MAINHAND, itemStack);
            }
        }

    }

    public boolean isTeammate( Entity other ) {
        if (super.isTeammate(other)) {
            return true;
        } else if (other instanceof LivingEntity && ((LivingEntity) other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        } else {
            return false;
        }


    }

    public void shootAt(LivingEntity target, float pullProgress) {
        this.shoot(this, 1.6F);
    }

    public void shoot( LivingEntity target, ItemStack crossbow, ProjectileEntity unusedProjectile, float multishotSpray ) {
        World world = this.getWorld();
        if (world.isClient) return;

        // Create your custom lava arrow
        LavagerArrowEntity arrow = new LavagerArrowEntity(world, this);
        arrow.setOnFireFor(100);
        arrow.setCritical(true);
        arrow.setDamage(arrow.getDamage() + 2.0D);
        arrow.setPierceLevel((byte) 0);

        // Compute trajectory
        double dx = target.getX() - this.getX();
        double dy = target.getBodyY(0.3333333333333D) - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float velocity = 1.6F; // Crossbow base
        float inaccuracy = 14.0F - (world.getDifficulty().getId() * 4); // Harder diff = more accurate

        arrow.setVelocity(dx, dy + distXZ * 0.2D, dz, velocity, inaccuracy);

        // Play shoot sound (vanilla pillager crossbow sound)
        this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

        world.spawnEntity(arrow);

        // Optional tag for identification
        arrow.addCommandTag("lavager_arrow");

        // Trigger vanilla cooldown / animation
        this.postShoot();
    }

    // --------------- RAID METHODS --------------------- //

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (itemStack.getItem() instanceof BannerItem) {
            super.loot(item);
        } else if (this.isRaidCaptain(itemStack)) {
            this.triggerItemPickedUpByEntityCriteria(item);
            ItemStack itemStack2 = this.inventory.addStack(itemStack);
            if (itemStack2.isEmpty()) {
                item.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }

    }

    private boolean isRaidCaptain(ItemStack stack) {
        return this.hasActiveRaid() && stack.isOf(Items.WHITE_BANNER);
    }

    public StackReference getStackReference( int mappedIndex) {
        int i = mappedIndex - 300;
        return i >= 0 && i < this.inventory.size() ? StackReference.of(this.inventory, i) : super.getStackReference(mappedIndex);
    }

    public void addBonusForWave(int wave, boolean unused) {
        Raid raid = this.getRaid();
        boolean bl = this.random.nextFloat() <= raid.getEnchantmentChance();
        if (bl) {
            ItemStack itemStack = new ItemStack(Items.CROSSBOW);
            Map<Enchantment, Integer> map = Maps.newHashMap();
            if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
                map.put(Enchantments.QUICK_CHARGE, 2);
            } else if (wave > raid.getMaxWaves(Difficulty.EASY)) {
                map.put(Enchantments.QUICK_CHARGE, 1);
            }

            map.put(Enchantments.MULTISHOT, 1);
            EnchantmentHelper.set(map, itemStack);
            this.equipStack(EquipmentSlot.MAINHAND, itemStack);
        }

    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }

    static {
        CHARGING = DataTracker.registerData(LavagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
