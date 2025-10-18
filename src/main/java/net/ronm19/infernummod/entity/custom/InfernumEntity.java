package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.sound.ModSounds;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class InfernumEntity extends WardenEntity implements Monster {

    /* ------------ DATA TRACKERS ------------ */
    private static final TrackedData<Boolean> ENRAGED =
            DataTracker.registerData(InfernumEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(InfernumEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private int attackAnimationTimeout = 0;

    /* ------------ BOSS BAR ------------ */
    private final ServerBossBar bossBar =
            new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);

    public InfernumEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar.setDragonMusic(true);
    }

    /* ------------ IMMUNITIES ------------ */
    @Override
    public boolean isFireImmune() {
        return true;
    }

    /* ------------------- ATTACKING ------------------- */
    public void setAttacking( boolean attacking ) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);

    }

    /* ------------ ATTRIBUTES ------------ */
    public static DefaultAttributeContainer.Builder createInfernumAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 900.0D)       // 500 ♥
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 24.0D)     // hits harder
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.34D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 60.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 16.0D)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 8.0D);


    }


    /* ------------ GOALS ------------ */
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true)); // will replace with custom later
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.7D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 24.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, TameableEntity.class, true));
    }

    /* ------------ DATA TRACKER ------------ */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ENRAGED, false);
        this.dataTracker.startTracking(ATTACKING, false);
    }

    /* ------------ ANIMATIONS ------------ */
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && this.attackAnimationTimeout <= 0) {
            this.attackAnimationTimeout = 40;
            this.attackAnimationState.start(this.age);
        } else {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()) {
            this.attackAnimationState.stop();
        }
    }

    /* ------------- SPAWNING SET UP ------------ */

    @Override
    public EntityData initialize( ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                  @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.getBrain().remember(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);

        // Only play emerging animation when summoned by statue
        if (spawnReason == SpawnReason.TRIGGERED) {
            this.setPose(EntityPose.EMERGING);
            this.getBrain().remember(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, (long) WardenBrain.EMERGE_DURATION);
            this.playSound(SoundEvents.ENTITY_WARDEN_AGITATED, 5.0F, 1.0F);
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }


    /* ------------ TICK ------------ */
    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();

        if (!this.getWorld().isClient) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }

        // Phase 2 trigger (rage)
        if (!this.getWorld().isClient && !this.isEnraged() && this.getHealth() <= 200.0F) {
            enterEnragedPhase();
        }
    }

    /* ------------ ENRAGED PHASE ------------ */
    private void enterEnragedPhase() {
        this.dataTracker.set(ENRAGED, true);
        this.playSound(ModSounds.INFERNAL_BEAST_ROAR, 1.5F, 0.8F);

        // Buffed stats
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(36.0D);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.40D);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20*9999, 1)); // ~20% reduction
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(60.0D);
        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0D);


        if (this.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {

            /* === Flame pillar burst === */
            for (int i = 0; i < 8; i++) {
                double angle = (Math.PI * 2 / 8) * i;
                double radius = 4.0 + this.random.nextDouble() * 2.0;
                double px = this.getX() + Math.cos(angle) * radius;
                double pz = this.getZ() + Math.sin(angle) * radius;

                for (int j = 0; j < 15; j++) {
                    double py = this.getY() + j * 0.2;
                    serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.FLAME,
                            px, py, pz,
                            2, 0.1, 0.0, 0.1, 0.02);
                    serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.SMOKE,
                            px, py, pz,
                            1, 0.05, 0.0, 0.05, 0.0);
                }
            }

            /* === Summon Enraged Infernal Beasts === */
            for (int i = 0; i < 3; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 8.0;
                double offsetZ = (this.random.nextDouble() - 0.5) * 8.0;

                InfernalBeastEntity beast = new InfernalBeastEntity(ModEntities.INFERNAL_BEAST, serverWorld);
                beast.refreshPositionAndAngles(
                        this.getX() + offsetX,
                        this.getY(),
                        this.getZ() + offsetZ,
                        this.getYaw(), 0.0F);

                serverWorld.spawnEntity(beast);
                beast.getDataTracker().set(InfernalBeastEntity.ENRAGED, true);  // make them spawn enraged
                beast.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(18.0D);
                beast.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.40D);
            }
        }
    }


    public boolean isEnraged() {
        return this.dataTracker.get(ENRAGED);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // Cap extreme modded-weapon hits
        float capped = Math.min(amount, 12.0F);
        return super.damage(source, capped);
    }


    /* ------------ BOSS BAR HANDLING ------------ */
    @Override
    public void onStartedTrackingBy(net.minecraft.server.network.ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(net.minecraft.server.network.ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    /* ------------ SOUNDS ------------ */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.INFERNAL_BEAST_STEP, 0.6F, 1.0F);
    }

    /* ------------ DESPAWN RULE ------------ */
    @Override
    public boolean cannotDespawn() {
        return this.getType().getSpawnGroup() != SpawnGroup.MONSTER
                || this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
    }

    /* ------------ TARGET ------------ */

    @Override
    public boolean canTarget( LivingEntity target) {
        // Ignore creative/spectator players
        if (target instanceof PlayerEntity player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        // Ignore other hostile mobs unless enraged
        if (target instanceof HostileEntity && !(target instanceof InfernumEntity)) {
            if (!this.isEnraged()) {
                return false;
            }
        }

        // Ignore self or allies
        if (target == this) return false;

        return super.canTarget(target);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!this.getWorld().isClient) {
            this.dropStack(new ItemStack(ModItems.INFERNAL_GEM, this.random.nextBetween(1, 3)));
        }
    }

}