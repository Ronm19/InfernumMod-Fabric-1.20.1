package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ai.goals.infernal_beast.InfernalBeastAttackGoal;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.sound.ModSounds;

public class InfernalBeastEntity extends HostileEntity implements Monster {

    public static final TrackedData<Boolean> ENRAGED =
            DataTracker.registerData(InfernalBeastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(InfernalBeastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public InfernalBeastEntity( EntityType<? extends HostileEntity> entityType, World world ) {
        super(entityType, world);
    }

    /* ------------------- IMMUNITIES ------------------- */
    @Override
    public boolean isFireImmune() {
        return true;       // Beast is always immune to fire & lava
    }

    /* ------------------- ANIMATIONS ------------------- */
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 40;
            attackAnimationState.start(this.age);
        } else {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()) {
            attackAnimationState.stop();
        }
    }

    @Override
    protected void updateLimbs( float v ) {
        float f;
        if (this.getPose() == EntityPose.STANDING) {
            f = Math.min(v * 6.0F, 1.0F);
        } else {
            f = 0.0F;
        }
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    /* ------------------- TICK ------------------- */
    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();

        // Enraged phase trigger at half health (120 HP)
        if (!this.getWorld().isClient() && !this.isEnraged() && this.getHealth() <= 120.0F) {
            enterEnragedPhase();
        }
    }

    /* ------------------- DESPAWN RULE ------------------- */
    @Override
    public boolean cannotDespawn() {
        // Prevents despawn in normal conditions, but monsters still despawn on peaceful
        return this.getType().getSpawnGroup() != SpawnGroup.MONSTER
                || this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
    }

    /* ------------------- DATA TRACKER ------------------- */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
        this.dataTracker.startTracking(ENRAGED, false);
    }

    /* ------------------- ATTACKING ------------------- */
    public void setAttacking( boolean attacking ) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);

    }

    /* ------------------- ATTRIBUTES ------------------- */
    public static DefaultAttributeContainer.Builder createInfernalBeastAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 240.0D)       // 120 hearts
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 36.0D);
    }

    /* ------------------- GOALS ------------------- */
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new InfernalBeastAttackGoal(this, 1.1D, true));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.1D, true)); // placeholder
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.8D));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 18.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, TameableEntity.class, true));
    }

    /* ------------------- SOUNDS ------------------- */
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.INFERNAL_BEAST_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.INFERNAL_BEAST_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(ModSounds.INFERNAL_BEAST_STEP, 0.4F, 1.0F);
    }

    /* ------------------- ENRAGED PHASE ------------------- */
    private void enterEnragedPhase() {
        this.dataTracker.set(ENRAGED, true);      // sync to clients

        this.playSound(ModSounds.INFERNAL_BEAST_ROAR, 1.2F, 0.8F);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(18.0D);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.40D);
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(48.0D);
        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.8D);
        // TODO: trigger particles/glow visuals

        if (this.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {

            // Core explosion puff
            serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.EXPLOSION,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    15, 0.5, 0.5, 0.5, 0.0);

            // Fiery burst: flames + lava chunks + smoke
            for (int i = 0; i < 80; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 3.0;
                double dy = this.random.nextDouble() * 2.0;
                double dz = (this.random.nextDouble() - 0.5) * 3.0;

                serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.FLAME,
                        this.getX() + dx, this.getY() + 0.8 + dy, this.getZ() + dz,
                        1, 0, 0, 0, 0.02);

                serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.LAVA,
                        this.getX() + dx, this.getY() + 0.5 + dy, this.getZ() + dz,
                        1, 0, 0, 0, 0.05);

                serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.LARGE_SMOKE,
                        this.getX() + dx, this.getY() + dy, this.getZ() + dz,
                        1, 0, 0, 0, 0.0);
            }
        }
    }

    public boolean isEnraged() {
        return this.dataTracker.get(ENRAGED);
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

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (!this.getWorld().isClient) {
            this.dropStack(new ItemStack(ModItems.INFERNAL_BEAST_HORN, 1));
        }
    }

}
