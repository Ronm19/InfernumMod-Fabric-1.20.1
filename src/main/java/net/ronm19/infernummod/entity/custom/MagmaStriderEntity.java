package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
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
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.ronm19.infernummod.entity.ai.magma_strider.MagmaStriderAttackGoal;
import net.ronm19.infernummod.item.ModItems;

public class MagmaStriderEntity extends HostileEntity implements Monster {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public MagmaStriderEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 8;
        this.intersectionChecked = true;

        // encourage pathing through hot stuff
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    // ---------- ATTRIBUTES ----------
    public static DefaultAttributeContainer.Builder createMagmaStriderAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.4D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 28.0D);
    }

    // ---------- IMMUNITIES ----------
    @Override public boolean isFireImmune() { return true; }
    @Override public boolean canBreatheInWater() { return false; }
    @Override public boolean isOnFire() { return false; } // never render on-fire overlay

    // ---------- ANIMATIONS ----------
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            idleAnimationTimeout--;
        }

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 30;
            attackAnimationState.start(this.age);
        } else {
            attackAnimationTimeout--;
        }

        if (!this.isAttacking()) attackAnimationState.stop();
    }

    @Override
    protected void updateLimbs(float v) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(v * 6.0F, 1.0F) : 0.0F;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    // ---------- TICK ----------
    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();
        this.updateFloating();
        this.checkBlockCollision();

        // light ember particles (kept modest to avoid visual noise)
        if (this.getWorld().isClient && this.random.nextFloat() < 0.08F) {
            double dx = this.getX() + (this.random.nextDouble() - 0.5D) * this.getWidth();
            double dy = this.getY() + this.random.nextDouble() * this.getHeight();
            double dz = this.getZ() + (this.random.nextDouble() - 0.5D) * this.getWidth();
            this.getWorld().addParticle(ParticleTypes.LAVA, dx, dy, dz, 0.0D, 0.03D, 0.0D);
        }
    }

    // ---------- GOALS ----------
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MagmaStriderAttackGoal(this, 1.1D, true));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.8D));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));
        this.goalSelector.add(6, new GoBackToLavaGoal(this, 1.2D));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, TameableEntity.class, true));
    }

    // ---------- COMBAT ----------
    @Override
    public boolean tryAttack( Entity target) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof LivingEntity living) {
            living.setOnFireFor(4);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0));
            this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F);
        }
        return success;
    }

    // ---------- MOVEMENT ----------
    @Override
    public void travel(Vec3d input) {
        if (this.isInLava()) {
            // lava “glide”
            this.updateVelocity(0.04F, input);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.8));
        } else {
            super.travel(input);
        }
    }

    // keep it “floating” on lava surface similarly to strider behavior
    private void updateFloating() {
        if (this.isInLava()) {
            ShapeContext ctx = ShapeContext.of(this);
            if (ctx.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true)
                    && !this.getWorld().getFluidState(this.getBlockPos().up()).isIn(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setVelocity(this.getVelocity().multiply(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        if (world.getBlockState(pos).getFluidState().isIn(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.6F;
    }

    // ---------- SOUNDS ----------
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_STRIDER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_STRIDER_DEATH; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BLOCK_BASALT_STEP, 0.6F, 0.9F);
    }

    // ---------- DROPS ----------
    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        if (causedByPlayer) {
            if (this.random.nextFloat() < 0.50F) this.dropItem(Items.MAGMA_CREAM);
            if (this.random.nextFloat() < 0.25F) this.dropItem(Items.BLAZE_POWDER);
            if (this.random.nextFloat() < 0.10F) this.dropItem(ModItems.BLAZE_HEART);
        }
    }

    // ---------- LEASH POS ----------
    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0D, 0.6D * this.getStandingEyeHeight(), this.getWidth() * 0.4D);
        // small forward offset so the leash doesn’t clip the head
    }

    // ---------- NAVIGATION (lava-walk aware) ----------
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new Navigation(this, world);
    }

    static class Navigation extends MobNavigation {
        Navigation(MagmaStriderEntity entity, World world) {
            super(entity, world);
        }

        public boolean canWalkOnFluid(FluidState state) {
            return state.isIn(FluidTags.LAVA) || state.isIn(FluidTags.WATER);
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new LandPathNodeMaker();
            this.nodeMaker.setCanEnterOpenDoors(true);
            return new PathNodeNavigator(this.nodeMaker, range);
        }

        @Override
        protected boolean canWalkOnPath(PathNodeType type) {
            if (type == PathNodeType.LAVA || type == PathNodeType.WATER || type == PathNodeType.DANGER_FIRE || type == PathNodeType.DAMAGE_FIRE) {
                return true; // treat as valid
            }
            return super.canWalkOnPath(type);
        }

        @Override
        public boolean isValidPosition(BlockPos pos) {
            // allow paths that include lava
            return this.world.getFluidState(pos).isIn(FluidTags.LAVA) | world.getFluidState(pos).isIn(FluidTags.WATER);
        }
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        this.checkBlockCollision();
        if (this.isInLava()) {
            this.onLanding();
        } else {
            super.fall(heightDifference, onGround, state, landedPosition);
        }
    }

    // ---------- AI: return to lava when out of it ----------
    static class GoBackToLavaGoal extends MoveToTargetPosGoal {
        private final MagmaStriderEntity mob;

        GoBackToLavaGoal(MagmaStriderEntity mob, double speed) {
            super(mob, speed, 8, 2);
            this.mob = mob;
        }

        @Override
        public boolean canStart() {
            return !mob.isInLava() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !mob.isInLava() && super.shouldContinue();
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            // any lava fluid source or flowing
            return world.getFluidState(pos).isIn(FluidTags.LAVA)
                    && world.getBlockState(pos.up()).canPathfindThrough(world, pos.up(), NavigationType.LAND);
        }

        // Optional helper you can reference from your SpawnRestriction if you like:
        public static boolean canSpawn(EntityType<MagmaStriderEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
            // ensure there’s air above lava column
            BlockPos.Mutable m = pos.mutableCopy();
            do {
                m.move(Direction.UP);
            } while (world.getFluidState(m).isIn(FluidTags.LAVA));
            return world.getBlockState(m).isAir();
        }
    }
}
