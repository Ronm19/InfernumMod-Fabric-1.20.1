package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.ronm19.infernummod.entity.ai.goals.ember_serpent.EmberSerpentAttackGoal;

public class EmberSerpentEntity extends HostileEntity implements Monster {

    // ---------------- ANIMATION STATES ----------------
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState swimAnimationState = new AnimationState();
    private int swimAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public EmberSerpentEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 10;
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createEmberSerpentAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 34.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D);
    }

    // ---------------- IMMUNITIES ----------------
    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean canBreatheInWater() {
        return false;
    }

    // ---------------- ANIMATION LOGIC ----------------
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else idleAnimationTimeout--;

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 25;
            attackAnimationState.start(this.age);
        } else attackAnimationTimeout--;

        if (this.isInLava() && swimAnimationTimeout <= 0) {
            swimAnimationTimeout = 40;
            swimAnimationState.start(this.age);
        } else swimAnimationTimeout--;

        if (!this.isInLava()) swimAnimationState.stop();
        if (!this.isAttacking()) attackAnimationState.stop();
    }

    // ---------------- MOVEMENT ----------------
    @Override
    public void travel(Vec3d movementInput) {
        if (this.isInLava()) {
            this.updateVelocity(0.03F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.8));
        } else {
            super.travel(movementInput);
        }
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
    // ---------------- GOALS ----------------
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EmberSerpentAttackGoal(this, 1.1D, true));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.8D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new GoBackToLavaGoal(this, 1.2D));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, TameableEntity.class, true));
    }

    // ---------------- COMBAT ----------------
    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof PlayerEntity player) {
            player.setOnFireFor(5);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
            this.playSound(SoundEvents.ENTITY_PHANTOM_BITE, 1.0F, 1.2F);
        }
        return success;
    }

    // ---------------- PARTICLES & VISUALS ----------------
    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();

        if (this.getWorld().isClient && this.random.nextFloat() < 0.12F) {
            double dx = this.getX() + (this.random.nextDouble() - 0.5D) * this.getWidth() * 1.5;
            double dy = this.getY() + this.random.nextDouble() * this.getHeight();
            double dz = this.getZ() + (this.random.nextDouble() - 0.5D) * this.getWidth() * 1.5;
            this.getWorld().addParticle(ParticleTypes.LAVA, dx, dy, dz, 0, 0.01, 0);
        }
    }

    // ---------------- SOUNDS ----------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BLOCK_BASALT_STEP, 0.5F, 0.9F);
    }

    // ---------------- LOOT ----------------
    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        if (causedByPlayer) {
            if (this.random.nextFloat() < 0.4F) this.dropItem(Items.MAGMA_CREAM);
            if (this.random.nextFloat() < 0.15F) this.dropItem(Items.BLAZE_POWDER);
        }
    }

    // ---------------- PATHFINDING ----------------
    static class GoBackToLavaGoal extends MoveToTargetPosGoal {
        private final EmberSerpentEntity serpent;

        GoBackToLavaGoal(EmberSerpentEntity serpent, double speed) {
            super(serpent, speed, 8, 2);
            this.serpent = serpent;
        }

        public boolean canStart() {
            return !this.serpent.isInLava() && super.canStart();
        }

        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            return world.getBlockState(pos).isOf(Blocks.LAVA);
        }

        public static boolean canSpawn( EntityType<EmberSerpentEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
            return world.getBlockState(pos.down()).isOf(Blocks.LAVA);
        }
    }
}
