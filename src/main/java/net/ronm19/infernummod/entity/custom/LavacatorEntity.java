package net.ronm19.infernummod.entity.custom;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Predicate;

public class LavacatorEntity extends VindicatorEntity implements Monster {
    private static final String LAVA_JOHNNY_KEY = "Infernal";
    static final Predicate<Difficulty> DIFFICULTY_ALLOWS_DOOR_BREAKING_PREDICATE = ( difficulty ) -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD;
    boolean lava_johnny;
    private int enragedTicks = 0;



    public LavacatorEntity( EntityType<? extends VindicatorEntity> entityType, World world ) {
        super(entityType, world);
    }

    // ---------------------------- GOALS --------------------------------------

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new BreakDoorGoal(this));
        this.goalSelector.add(2, new LongDoorInteractGoal(this));
        this.goalSelector.add(3, new PatrolApproachGoal(this, 10.0F));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, false));

        this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, TameableEntity.class, true));
        this.targetSelector.add(4, new TargetGoal(this));

        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
    }

    // -------------------------------- ATTRIBUTES --------------------------------------------------

    public static DefaultAttributeContainer.Builder createLavacatorAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    // ---------------------------- MOB TICK --------------------------------------------------------

    protected void mobTick() {
        if (!this.isAiDisabled() && NavigationConditions.hasMobNavigation(this)) {
            boolean bl = ((ServerWorld) this.getWorld()).hasRaidAt(this.getBlockPos());
            ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(bl);
        }

        super.mobTick();
    }

    // ---------------------------- NBT --------------------------------------------------------

    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        if (this.lava_johnny) {
            nbt.putBoolean("Infernal", true);
        }

    }

    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Infernal", 99)) {
            this.lava_johnny = nbt.getBoolean("Infernal");
        }

    }

    public void setCustomName( @Nullable Text name ) {
        super.setCustomName(name);
        if (!this.lava_johnny && name != null && name.getString().equals("Infernal")) {
            this.lava_johnny = true;
        }

    }

    // ---------------------------- STATE --------------------------------------------------------

    public State getState() {
        if (this.isAttacking()) {
            return State.ATTACKING;
        } else {
            return this.isCelebrating() ? State.CELEBRATING : State.CROSSED;
        }
    }

    // ---------------------------- SOUNDS --------------------------------------------------------

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_VINDICATOR_CELEBRATE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    //  ---------------------------- SPAWN SETUP --------------------------------------------------------
    @Nullable
    @Override
    public EntityData initialize( ServerWorldAccess world, LocalDifficulty difficulty,
                                                       SpawnReason spawnReason, @Nullable EntityData entityData,
                                                       @Nullable NbtCompound entityNbt ) {
        this.initEquipment(world.getRandom(), difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    //  ---------------------------- IMMUNITIES --------------------------------------------------------

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean damage( DamageSource source, float amount ) {
        // Optional: immune to magma or lava-based damage
        if (source.isOf(DamageTypes.LAVA) || source.isOf(DamageTypes.IN_FIRE)) {
            return false;
        }
        return super.damage(source, amount);
    }

    //  ---------------------------- TICK ---------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.getTarget();

        // Detect and sustain rage state
        if (target != null && this.canSee(target)) {
            enragedTicks = 60; // Stay enraged for 3 seconds
            this.setAttacking(true);
        } else if (enragedTicks > 0) {
            enragedTicks--;
            if (enragedTicks == 0) this.setAttacking(false);
        } else {
            this.setAttacking(false);
        }

        // 🔥 Add flame particles while enraged (client only)
        if (this.getWorld().isClient && this.isEnraged()) {
            this.getWorld().addParticle(ParticleTypes.FLAME,
                    this.getX() + (this.random.nextDouble() - 0.5D) * 0.4D,
                    this.getY() + 1.2D,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * 0.4D,
                    0, 0.02, 0);
        }
    }

    public boolean isEnraged() {
        return enragedTicks > 0;
    }



    //  ---------------------------- INNER GOALS --------------------------------------------------------

    static class BreakDoorGoal extends net.minecraft.entity.ai.goal.BreakDoorGoal {
        public BreakDoorGoal( MobEntity mobEntity ) {
            super(mobEntity, 6, LavacatorEntity.DIFFICULTY_ALLOWS_DOOR_BREAKING_PREDICATE);
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean shouldContinue() {
            LavacatorEntity lavacatorEntity = (LavacatorEntity) this.mob;
            return lavacatorEntity.hasActiveRaid() && super.shouldContinue();
        }

        public boolean canStart() {
            LavacatorEntity lavacatorEntity = (LavacatorEntity) this.mob;
            return lavacatorEntity.hasActiveRaid() && lavacatorEntity.random.nextInt(toGoalTicks(10)) == 0 && super.canStart();
        }

        public void start() {
            super.start();
            this.mob.setDespawnCounter(0);
        }
    }

    static class TargetGoal extends ActiveTargetGoal<LivingEntity> {
        public TargetGoal( LavacatorEntity lavacatorEntity ) {
            super(lavacatorEntity, LivingEntity.class, 0, true, true, LivingEntity :: isMobOrPlayer);
        }

        public boolean canStart() {
            return ((LavacatorEntity) this.mob).lava_johnny && super.canStart();
        }

        public void start() {
            super.start();
            this.mob.setDespawnCounter(0);
        }
    }

    //  ---------------------------- OTHERS --------------------------------------------------------

    @Override
    protected void initEquipment(Random random, LocalDifficulty difficulty) {
        ItemStack axe = new ItemStack(ModItems.NETHER_RUBY_AXE);
        axe.addEnchantment(Enchantments.FIRE_ASPECT, 2);
        this.equipStack(EquipmentSlot.MAINHAND, axe);
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.05F);
    }

    public float getAttackProgress(float tickDelta) {
        return this.handSwingProgress; // or your own timer
    }

    @Override
    public boolean tryAttack( Entity target ) {
        boolean result = super.tryAttack(target);
        if (result && target instanceof LivingEntity living) {
            living.setOnFireFor(4);
        }
        return result;
    }

    public void addBonusForWave( int wave, boolean unused ) {
        ItemStack itemStack = new ItemStack(Items.NETHERITE_AXE);
        Raid raid = this.getRaid();
        int i = 1;
        if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
            i = 2;
        }

        boolean bl = this.random.nextFloat() <= raid.getEnchantmentChance();
        if (bl) {
            Map<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.SHARPNESS, i);
            EnchantmentHelper.set(map, itemStack);
        }

        this.equipStack(EquipmentSlot.MAINHAND, itemStack);
    }

    public boolean isTeammate( Entity other ) {
        if (super.isTeammate(other)) {
            return true;
        } else if (other instanceof LivingEntity living && (living.getGroup() == EntityGroup.ILLAGER || other.getType() == ModEntities.INFERNAL_VEX)) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        // Ignore allied Infernum types
        if (target.getType() == ModEntities.INFERNUM_HEROBRINE
                || target.getType() == ModEntities.MALFURYX)
        {
            return false;
        }

        // Ignore Creative / Spectator players
        if (target instanceof PlayerEntity player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        return super.canTarget(target);
    }


    @Override
    public boolean canLead() { // sometimes be a captain
        return true;
    }

    @Override
    public boolean canJoinRaid() {
        return true;
    }
}
