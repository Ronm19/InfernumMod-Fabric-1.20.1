package net.ronm19.infernummod.entity.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.IntFunction;

public class InfernalRabbitEntity extends RabbitEntity {
    private int jumpTicks;
    private int jumpDuration;
    private boolean lastOnGround;
    private int ticksUntilJump;
    int moreCarrotTicks;


    public InfernalRabbitEntity( EntityType<? extends RabbitEntity> entityType, World world ) {
        super(entityType, world);
        this.jumpControl = new InfernalRabbitJumpControl(this);
        this.moveControl = new InfernalRabbitMoveControl(this);
        this.setSpeed((double)0.0F);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new PowderSnowJumpGoal(this, this.getWorld()));
        this.goalSelector.add(1, new InfernalRabbitEntity.EscapeDangerGoal(this, 2.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(3, new TemptGoal(this, (double)1.0F, Ingredient.ofItems(new ItemConvertible[]{Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION, ModItems.INFERNAL_APPLE}), false));
        this.goalSelector.add(4, new InfernalRabbitEntity.FleeGoal<>(this, PlayerEntity.class, 8.0F, 2.2, 2.2));
        this.goalSelector.add(4, new InfernalRabbitEntity.FleeGoal<>(this, WolfEntity.class, 10.0F, 2.2, 2.2));
        this.goalSelector.add(4, new InfernalRabbitEntity.FleeGoal<>(this, HostileEntity.class, 4.0F, 2.2, 2.2));
        this.goalSelector.add(5, new InfernalRabbitEntity.EatCarrotCropGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
    }

    protected float getJumpVelocity() {
        float f = 0.3F;
        if (this.horizontalCollision || this.moveControl.isMoving() && this.moveControl.getTargetY() > this.getY() + (double)0.5F) {
            f = 0.5F;
        }

        Path path = this.navigation.getCurrentPath();
        if (path != null && !path.isFinished()) {
            Vec3d vec3d = path.getNodePosition(this);
            if (vec3d.y > this.getY() + (double)0.5F) {
                f = 0.5F;
            }
        }

        if (this.moveControl.getSpeed() <= 0.6) {
            f = 0.2F;
        }

        return f + this.getJumpBoostVelocityModifier();
    }

    protected void jump() {
        super.jump();
        double d = this.moveControl.getSpeed();
        if (d > (double)0.0F) {
            double e = this.getVelocity().horizontalLengthSquared();
            if (e < 0.01) {
                this.updateVelocity(0.1F, new Vec3d((double)0.0F, (double)0.0F, (double)1.0F));
            }
        }

        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)1);
        }

    }

    public float getJumpProgress(float delta) {
        return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + delta) / (float)this.jumpDuration;
    }

    public void setSpeed(double speed) {
        this.getNavigation().setSpeed(speed);
        this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), speed);
    }

    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
        if (jumping) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }

    }

    public void startJump() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    protected void initDataTracker() {
        super.initDataTracker();
    }

    public void mobTick() {
        if (this.ticksUntilJump > 0) {
            --this.ticksUntilJump;
        }

        if (this.moreCarrotTicks > 0) {
            this.moreCarrotTicks -= this.random.nextInt(3);
            if (this.moreCarrotTicks < 0) {
                this.moreCarrotTicks = 0;
            }
        }

        if (this.isOnGround()) {
            if (!this.lastOnGround) {
                this.setJumping(false);
                this.scheduleJump();
            }

            InfernalRabbitEntity.InfernalRabbitJumpControl inferalRabbitJumpControl = (InfernalRabbitJumpControl) this.jumpControl;
            if (!inferalRabbitJumpControl.isActive()) {
                if (this.moveControl.isMoving() && this.ticksUntilJump == 0) {
                    Path path = this.navigation.getCurrentPath();
                    Vec3d vec3d = new Vec3d(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ());
                    if (path != null && !path.isFinished()) {
                        vec3d = path.getNodePosition(this);
                    }

                    this.lookTowards(vec3d.x, vec3d.z);
                    this.startJump();
                }
            } else if (!inferalRabbitJumpControl.canJump()) {
                this.enableJump();
            }
        }

        this.lastOnGround = this.isOnGround();
    }

    public boolean shouldSpawnSprintingParticles() {
        return false;
    }

    private void lookTowards(double x, double z) {
        this.setYaw((float)(MathHelper.atan2(z - this.getZ(), x - this.getX()) * (double)(180F / (float)Math.PI)) - 90.0F);
    }

    private void enableJump() {
        ((InfernalRabbitEntity.InfernalRabbitJumpControl)this.jumpControl).setCanJump(true);
    }

    private void disableJump() {
        ((InfernalRabbitEntity.InfernalRabbitJumpControl)this.jumpControl).setCanJump(false);
    }

    private void doScheduleJump() {
        if (this.moveControl.getSpeed() < 2.2) {
            this.ticksUntilJump = 10;
        } else {
            this.ticksUntilJump = 1;
        }

    }

    private void scheduleJump() {
        this.doScheduleJump();
        this.disableJump();
    }

    public void tickMovement() {
        super.tickMovement();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }

    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    public static DefaultAttributeContainer.Builder createInfernalRabbitAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, (double)20.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, (double)0.3F);
    }

    public void writeCustomDataToNbt( NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("MoreCarrotTicks", this.moreCarrotTicks);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.moreCarrotTicks = nbt.getInt("MoreCarrotTicks");
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_RABBIT_JUMP;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    protected SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    private static boolean isTempting( ItemStack stack) {
        return stack.isOf(Items.CARROT) || stack.isOf(Items.GOLDEN_CARROT) || stack.isOf(ModItems.INFERNAL_APPLE)|| stack.isOf(Blocks.DANDELION.asItem());
    }

    @Override
    public @Nullable InfernalRabbitEntity createChild( ServerWorld serverWorld, PassiveEntity passiveEntity ) {
        return ModEntities.INFERNAL_RABBIT.create(serverWorld);
    }

    public boolean isBreedingItem( ItemStack stack) {
        return isTempting(stack);
    }


    boolean wantsCarrots() {
        return this.moreCarrotTicks <= 0;
    }

    public void handleStatus(byte status) {
        if (status == 1) {
            this.spawnSprintingParticles();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.handleStatus(status);
        }

    }

    public Vec3d getLeashOffset() {
        return new Vec3d((double)0.0F, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
    }

    public static class InfernalRabbitJumpControl extends JumpControl {
        private final InfernalRabbitEntity rabbit;
        private boolean canJump;

        public InfernalRabbitJumpControl(InfernalRabbitEntity rabbit) {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public boolean isActive() {
            return this.active;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean canJump) {
            this.canJump = canJump;
        }

        public void tick() {
            if (this.active) {
                this.rabbit.startJump();
                this.active = false;
            }

        }
    }



    static class InfernalRabbitMoveControl extends MoveControl {
        private final InfernalRabbitEntity rabbit;
        private double rabbitSpeed;

        public InfernalRabbitMoveControl(InfernalRabbitEntity owner) {
            super(owner);
            this.rabbit = owner;
        }

        public void tick() {
            if (this.rabbit.isOnGround() && !this.rabbit.jumping && !((InfernalRabbitEntity.InfernalRabbitJumpControl)this.rabbit.jumpControl).isActive()) {
                this.rabbit.setSpeed((double)0.0F);
            } else if (this.isMoving()) {
                this.rabbit.setSpeed(this.rabbitSpeed);
            }

            super.tick();
        }

        public void moveTo(double x, double y, double z, double speed) {
            if (this.rabbit.isTouchingWater()) {
                speed = (double)1.5F;
            }

            super.moveTo(x, y, z, speed);
            if (speed > (double)0.0F) {
                this.rabbitSpeed = speed;
            }

        }
    }

    static class FleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
        private final RabbitEntity rabbit;

        public FleeGoal(RabbitEntity rabbit, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
            super(rabbit, fleeFromType, distance, slowSpeed, fastSpeed);
            this.rabbit = rabbit;
        }

        public boolean canStart() {
            return this.rabbit.getVariant() != InfernalRabbitEntity.RabbitType.EVIL && super.canStart();
        }
    }

    static class EatCarrotCropGoal extends MoveToTargetPosGoal {
        private final InfernalRabbitEntity rabbit;
        private boolean wantsCarrots;
        private boolean hasTarget;

        public EatCarrotCropGoal(InfernalRabbitEntity rabbit) {
            super(rabbit, (double)0.7F, 16);
            this.rabbit = rabbit;
        }

        public boolean canStart() {
            if (this.cooldown <= 0) {
                if (!this.rabbit.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    return false;
                }

                this.hasTarget = false;
                this.wantsCarrots = this.rabbit.wantsCarrots();
            }

            return super.canStart();
        }

        public boolean shouldContinue() {
            return this.hasTarget && super.shouldContinue();
        }

        public void tick() {
            super.tick();
            this.rabbit.getLookControl().lookAt((double)this.targetPos.getX() + (double)0.5F, (double)(this.targetPos.getY() + 1), (double)this.targetPos.getZ() + (double)0.5F, 10.0F, (float)this.rabbit.getMaxLookPitchChange());
            if (this.hasReached()) {
                World world = this.rabbit.getWorld();
                BlockPos blockPos = this.targetPos.up();
                BlockState blockState = world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (this.hasTarget && block instanceof CarrotsBlock) {
                    int i = (Integer)blockState.get(CarrotsBlock.AGE);
                    if (i == 0) {
                        world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
                        world.breakBlock(blockPos, true, this.rabbit);
                    } else {
                        world.setBlockState(blockPos, (BlockState)blockState.with(CarrotsBlock.AGE, i - 1), 2);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(this.rabbit));
                        world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(blockState));
                    }

                    this.rabbit.moreCarrotTicks = 40;
                }

                this.hasTarget = false;
                this.cooldown = 10;
            }

        }

        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            BlockState blockState = world.getBlockState(pos);
            if (blockState.isOf(Blocks.FARMLAND) && this.wantsCarrots && !this.hasTarget) {
                blockState = world.getBlockState(pos.up());
                if (blockState.getBlock() instanceof CarrotsBlock && ((CarrotsBlock)blockState.getBlock()).isMature(blockState)) {
                    this.hasTarget = true;
                    return true;
                }
            }

            return false;
        }
    }

    static class EscapeDangerGoal extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
        private final InfernalRabbitEntity rabbit;

        public EscapeDangerGoal(InfernalRabbitEntity rabbit, double speed) {
            super(rabbit, speed);
            this.rabbit = rabbit;
        }

        public void tick() {
            super.tick();
            this.rabbit.setSpeed(this.speed);
        }
    }
}
