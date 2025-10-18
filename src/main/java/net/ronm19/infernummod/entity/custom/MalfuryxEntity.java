package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.AttackingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static net.ronm19.infernummod.entity.custom.DemonEntity.FIRE_DAMAGE;

public class MalfuryxEntity extends HostileEntity implements AttackingEntity {
    // --- TRACKED DATA ---
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(MalfuryxEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    // --- ANIMATION STATES (client‐only) ---
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;

    // --- ITEM LISTS ---
    private static final String[] PROHIBITED_ITEMS = {
            "minecraft:nether_star",
            "minecraft:blaze_rod",
            "minecraft:ghast_tear",
            "minecraft:wither_skeleton_skull"
    };

    private static final String[] FORGIVENESS_ITEMS = {
            "minecraft:diamond",
            "minecraft:netherite_ingot",
            "minecraft:nether_ruby"
    };

    // Temporarily forgiven players
    private final Map<UUID, Integer> forgivenPlayers = new HashMap<>();

    public MalfuryxEntity( EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    // --- ATTRIBUTES ---
    public static DefaultAttributeContainer.Builder createMalfuryxAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.3)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2);
    }

    // --- DATA TRACKER ---
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    @Override
    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    // --- GOALS & TARGETING ---
    @Override
    protected void initGoals() {
        // Movement & idle
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        // Only target players carrying prohibited items
        Predicate<PlayerEntity> forbiddenPredicate = this::isPlayerCarryingProhibited;
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false,
                entity -> entity instanceof PlayerEntity player && isPlayerCarryingProhibited(player)));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    @Override
    public boolean cannotDespawn() {
        // Prevents despawn in normal conditions, but monsters still despawn on peaceful
        return this.getType().getSpawnGroup() != SpawnGroup.MONSTER || this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
    }

    // --- TICK LOGIC ---
    @Override
    public void tick() {
        super.tick();

        // Client‐side: just run animations
        if (this.getWorld().isClient) {
            runAnimationStates();
            return;
        }

        // Server‐side: decrement forgiveness timers
        forgivenPlayers.entrySet().removeIf(e -> e.getValue() - 1 <= 0);
        forgivenPlayers.replaceAll((uuid, ticks) -> ticks - 1);

        // Check all nearby players instead of only current target
        for (PlayerEntity player : this.getWorld().getEntitiesByClass(PlayerEntity.class,
                this.getBoundingBox().expand(16.0D),
                p -> !p.isCreative() && !p.isSpectator())) {

            UUID id = player.getUuid();

            // Skip forgiven players
            if (forgivenPlayers.containsKey(id)) continue;

            if (isPlayerCarryingProhibited(player)) {
                // Hostile & debuff
                this.setAttacking(true);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
            }
        }

        // If no valid target left, reset attacking flag
        if (!(this.getTarget() instanceof PlayerEntity)) {
            this.setAttacking(false);
        }
    }


    // --- ANIMATION HELPERS (client) ---
    private void runAnimationStates() {
        // Idle
        if (idleAnimationTimeout-- <= 0) {
            idleAnimationTimeout = this.random.nextInt(40) + 80;
            idleAnimationState.start(this.age);
        }
        // Attack
        if (isAttacking()) {
            if (attackAnimationTimeout-- <= 0) {
                attackAnimationTimeout = 40;
                attackAnimationState.start(this.age);
            }
        } else {
            attackAnimationState.stop();
        }
    }

    @Override
    public boolean damage( DamageSource source, float amount ) {
        if (source.isIn(FIRE_DAMAGE)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    protected void updateLimbs(float delta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(delta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);
    }

    // --- INTERACTION: APPEAL FOR FORGIVENESS ---
    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isEmpty() && isForgivenessItem(stack)) {
            // Forgive for 5 minutes (6000 ticks)
            forgivenPlayers.put(player.getUuid(), 6000);
            if (!player.isCreative()) stack.decrement(1);
            getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_VILLAGER_YES, this.getSoundCategory(), 1f, 1f);
            return ActionResult.success(getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    // --- HOSTILITY CHECKS ---
    private boolean isPlayerCarryingProhibited(PlayerEntity player) {
        // Skip creative/spectator
        if (player.isCreative() || player.isSpectator()) return false;

        // Hands first
        for (Hand hand : Hand.values()) {
            ItemStack s = player.getStackInHand(hand);
            if (isProhibitedItem(s)) return true;
        }
        // Then inventory
        for (ItemStack s : player.getInventory().main) {
            if (isProhibitedItem(s)) return true;
        }
        return false;
    }

    // --- HOSTILITY PREDICATE ---
    private final Predicate<LivingEntity> forbiddenPredicate = entity -> {
        if (entity instanceof PlayerEntity player) {
            return isPlayerCarryingProhibited(player);
        }
        return false;
    };


    private boolean isProhibitedItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String id = stack.getItem().toString();
        for (String forbid : PROHIBITED_ITEMS) {
            if (id.contains(forbid)) return true;
        }
        return false;
    }

    private boolean isForgivenessItem(ItemStack stack) {
        String id = stack.getItem().toString();
        for (String allow : FORGIVENESS_ITEMS) {
            if (id.contains(allow)) return true;
        }
        return false;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        // Ignore Infernum itself
        if (target instanceof InfernumEntity) {
            return false;
        }

        // Ignore Creative / Spectator players
        if (target instanceof PlayerEntity player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        // Otherwise, follow normal targeting rules
        return super.canTarget(target);
    }

    // --- COMBAT & SOUNDS ---
    @Override
    public boolean isFireImmune() {
        return true;
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

    @Override
    public void attack( LivingEntity pEnemy ) {

    }
}