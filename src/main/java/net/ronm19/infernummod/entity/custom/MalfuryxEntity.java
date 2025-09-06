package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;

import java.util.List;

public class MalfuryxEntity extends PathAwareEntity {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private int scanCooldown = 0;
    private int roarCooldown = 0;

    private final ServerBossBar bossBar =
            new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);

    public MalfuryxEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 500; // Big boss XP
    }

    // Attributes
    public static DefaultAttributeContainer.Builder createMalfuryxAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0)  // very tanky
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0) // high damage if melee
                .add(EntityAttributes.GENERIC_ARMOR, 10.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new WanderAroundGoal(this, 0.2));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));

        this.targetSelector.add(1, new RevengeGoal(this)); // Attack back if hurt
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);

    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }

        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {

            // Boss bar health update
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            // Scan forbidden items
            if (scanCooldown-- <= 0) {
                scanNearbyPlayers(serverWorld);
                scanCooldown = 100; // 5s
            }

            // Aura debuff: Weakness within 8 blocks
            applyAuraDebuff(serverWorld);

            // Roar ability every ~60s
            if (roarCooldown-- <= 0) {
                useRoarAbility(serverWorld);
                roarCooldown = 20 * 60; // 60 seconds
            }

            // Particles around him (always)
            if (serverWorld.getRandom().nextFloat() < 0.3f) {
                serverWorld.spawnParticles(
                        ParticleTypes.FLAME,
                        this.getX(), this.getY() + 1.5, this.getZ(),
                        4, 0.5, 0.8, 0.5, 0.01
                );
            }
        }
    }

    private void scanNearbyPlayers(ServerWorld world) {
        Box scanArea = this.getBoundingBox().expand(16);
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, scanArea, p -> true)) {
            if (player.isCreative()) continue;

            // Check if player carries forbidden items
            boolean carryingForbidden = player.getInventory().containsAny(stack ->
                    stack.getItem().toString().contains("netherite") ||
                            stack.getItem().toString().contains("blaze_rod") ||
                            stack.getItem().toString().contains("ghast_tear") ||
                            stack.getItem().toString().contains("wither_skull")
            );

            if (carryingForbidden) {
                applyJudgment(player);
            }
        }
    }

    private void applyJudgment(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200, 0));
        this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0f, 0.8f);
    }

    private void applyAuraDebuff(ServerWorld world) {
        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class,
                this.getBoundingBox().expand(8), p -> !p.isCreative());

        for (PlayerEntity player : players) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40, 0, true, false));
        }
    }

    private void useRoarAbility(ServerWorld world) {
        this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 0.6f);
        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class,
                this.getBoundingBox().expand(16), p -> !p.isCreative());

        for (PlayerEntity player : players) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1));
        }
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        return false; // Malfuryx does not chase/attack normally
    }

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

    // Boss Bar handling
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }
}
