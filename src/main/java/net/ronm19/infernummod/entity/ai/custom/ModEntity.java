package net.ronm19.infernummod.entity.ai.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import net.minecraft.advancement.criterion.Criteria;

import java.util.Optional;
import java.util.UUID;

public abstract class ModEntity extends AnimalEntity {
    // --- Constants ---
    protected static final int BREEDING_COOLDOWN = 6000;
    protected static final int LOVE_DURATION = 600;

    // --- Breeding system fields ---
    private int loveTicks;
    private UUID lovingPlayer;

    protected ModEntity( EntityType<? extends AnimalEntity> entityType, World world ) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    // --- FlyingEntity-like fall ---
    @Override
    protected void fall( double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition ) {
    }

    // --- FlyingEntity-like travel ---
    @Override
    public void travel( Vec3d movementInput ) {
        if (!this.isLogicalSideForUpdatingMovement()) return;

        if (this.isTouchingWater()) {
            this.updateVelocity(0.02F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.8F));
        } else if (this.isInLava()) {
            this.updateVelocity(0.02F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.5F));
        } else {
            float friction = 0.91F;
            if (this.isOnGround()) {
                friction = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.91F;
            }

            float g = 0.16277137F / (friction * friction * friction);
            this.updateVelocity(this.isOnGround() ? 0.1F * g : 0.02F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(friction));
        }

        this.updateLimbs(false);
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    // --- Breeding logic ---
    @Override
    protected void mobTick() {
        if (this.getBreedingAge() != 0) loveTicks = 0;
        super.mobTick();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getBreedingAge() != 0) loveTicks = 0;

        if (loveTicks > 0) {
            --loveTicks;
            if (loveTicks % 10 == 0) spawnHeartParticles(1);
        }
    }

    private void spawnHeartParticles( int count ) {
        for (int i = 0; i < count; i++) {
            double dx = random.nextGaussian() * 0.02;
            double dy = random.nextGaussian() * 0.02;
            double dz = random.nextGaussian() * 0.02;
            this.getWorld().addParticle(ParticleTypes.HEART,
                    this.getParticleX(1.0F),
                    this.getRandomBodyY() + 0.5F,
                    this.getParticleZ(1.0F),
                    dx, dy, dz);
        }
    }

    @Override
    public boolean damage( DamageSource source, float amount ) {
        if (this.isInvulnerableTo(source)) return false;
        loveTicks = 0;
        return super.damage(source, amount);
    }

    public boolean isBreedingItem( ItemStack stack ) {
        return stack.isOf(Items.WHEAT);
    }

    @Override
    public ActionResult interactMob( PlayerEntity player, Hand hand ) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack != null && this.isBreedingItem(stack)) {
            int age = this.getBreedingAge();
            if (!this.getWorld().isClient && age == 0 && this.canEat()) {
                this.eat(player, hand, stack);
                this.lovePlayer(player);
                return ActionResult.SUCCESS;
            }
            if (this.isBaby()) {
                this.eat(player, hand, stack);
                this.growUp(toGrowUpAge(-age), true);
                return ActionResult.success(this.getWorld().isClient);
            }
            if (this.getWorld().isClient) return ActionResult.CONSUME;
        }
        return super.interactMob(player, hand);
    }

    protected void eat( PlayerEntity player, Hand hand, ItemStack stack ) {
        if (!player.getAbilities().creativeMode) stack.decrement(1);
    }

    public boolean canEat() {
        return loveTicks <= 0;
    }

    public void lovePlayer( PlayerEntity player ) {
        loveTicks = LOVE_DURATION;
        if (player != null) lovingPlayer = player.getUuid();
        this.getWorld().sendEntityStatus(this, (byte) 18);
    }

    public boolean isInLove() {
        return loveTicks > 0;
    }

    public void resetLoveTicks() {
        loveTicks = 0;
    }

    public boolean canBreedWith( ModEntity other ) {
        return other != this && other.getClass() == this.getClass() && this.isInLove() && other.isInLove();
    }

    public void breed( ServerWorld world, ModEntity other ) {
        PassiveEntity baby = this.createChild(world, other);
        if (baby != null) {
            baby.setBaby(true);
            baby.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            world.spawnEntityAndPassengers(baby);

            // Handle stats, experience, and reset love ticks
            Optional.ofNullable(this.getLovingPlayer())
                    .or(() -> Optional.ofNullable(other.getLovingPlayer()))
                    .ifPresent(player -> {
                        player.incrementStat(Stats.ANIMALS_BRED);
                        Criteria.BRED_ANIMALS.trigger(player, this, other, baby);
                    });

            this.setBreedingAge(BREEDING_COOLDOWN);
            other.setBreedingAge(BREEDING_COOLDOWN);
            this.resetLoveTicks();
            other.resetLoveTicks();
            world.sendEntityStatus(this, (byte) 18);

            if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.random.nextInt(7) + 1));
            }
        }
    }


    protected void finishBreeding( ServerWorld world, ModEntity mate, PassiveEntity baby ) {
        Optional.ofNullable(getLovingPlayer()).or(() -> Optional.ofNullable(mate.getLovingPlayer()))
                .ifPresent(player -> {
                    player.incrementStat(Stats.ANIMALS_BRED);
                    Criteria.BRED_ANIMALS.trigger(player, this, mate, baby);
                });

        this.setBreedingAge(BREEDING_COOLDOWN);
        mate.setBreedingAge(BREEDING_COOLDOWN);
        this.resetLoveTicks();
        mate.resetLoveTicks();
        world.sendEntityStatus(this, (byte) 18);

        if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.random.nextInt(7) + 1));
        }
    }

    public ServerPlayerEntity getLovingPlayer() {
        if (lovingPlayer == null) return null;
        PlayerEntity player = this.getWorld().getPlayerByUuid(lovingPlayer);
        return player instanceof ServerPlayerEntity ? (ServerPlayerEntity) player : null;
    }

    @Override
    public void handleStatus( byte status ) {
        if (status == 18) spawnHeartParticles(7);
        else super.handleStatus(status);
    }

    @Override
    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("InLove", loveTicks);
        if (lovingPlayer != null) nbt.putUuid("LoveCause", lovingPlayer);
    }

    @Override
    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        loveTicks = nbt.getInt("InLove");
        lovingPlayer = nbt.containsUuid("LoveCause") ? nbt.getUuid("LoveCause") : null;
    }

    // --- PathAwareEntity-like pathfinding ---
    public float getPathfindingFavor( BlockPos pos ) {
        return this.getPathfindingFavor(pos, this.getWorld());
    }

    public float getPathfindingFavor( BlockPos pos, WorldView world ) {
        return 0.0F;
    }

    public boolean canSpawn( WorldAccess world, SpawnReason spawnReason ) {
        return getPathfindingFavor(this.getBlockPos(), world) >= 0.0F;
    }

    public boolean isNavigating() {
        return !this.getNavigation().isIdle();
    }

    public boolean isPanicking() {
        return (this.brain.hasMemoryModule(MemoryModuleType.IS_PANICKING) &&
                this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_PANICKING).isPresent()) ||
                this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof EscapeDangerGoal);
    }

    @Override
    protected void updateLeash() {
        super.updateLeash();
        Entity entity = this.getHoldingEntity();
        if (entity == null || entity.getWorld() != this.getWorld()) return;

        this.setPositionTarget(entity.getBlockPos(), 5);
        float distance = this.distanceTo(entity);

        updateForLeashLength(distance);

        if (distance > 10.0F) {
            this.detachLeash(true, true);
            this.goalSelector.disableControl(Goal.Control.MOVE);
        } else if (distance > 6.0F) {
            double dx = (entity.getX() - this.getX()) / distance;
            double dy = (entity.getY() - this.getY()) / distance;
            double dz = (entity.getZ() - this.getZ()) / distance;
            this.setVelocity(this.getVelocity().add(
                    Math.copySign(dx * dx * 0.4, dx),
                    Math.copySign(dy * dy * 0.4, dy),
                    Math.copySign(dz * dz * 0.4, dz)
            ));
            this.limitFallDistance();
        } else if (shouldFollowLeash() && !isPanicking()) {
            this.goalSelector.enableControl(Goal.Control.MOVE);
            Vec3d vec = new Vec3d(entity.getX() - this.getX(),
                    entity.getY() - this.getY(),
                    entity.getZ() - this.getZ()).normalize().multiply(Math.max(distance - 2.0F, 0.0F));
            this.getNavigation().startMovingTo(this.getX() + vec.x, this.getY() + vec.y, this.getZ() + vec.z, getFollowLeashSpeed());
        }
    }

    protected boolean shouldFollowLeash() {
        return true;
    }

    protected double getFollowLeashSpeed() {
        return 1.0F;
    }

    protected void updateForLeashLength( float leashLength ) {
    }
}