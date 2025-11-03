package net.ronm19.infernummod.entity.custom;

import com.google.common.collect.Sets;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.ronm19.infernummod.event.InfernalTransformationHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class InfernalLightningEntity extends LightningEntity {
    private int ambientTick = 2;
    private int remainingActions;
    private boolean cosmetic;
    @Nullable private ServerPlayerEntity channeler;
    private final Set<Entity> struckEntities = Sets.newHashSet();
    private int blocksSetOnFire;

    public InfernalLightningEntity(EntityType<? extends LightningEntity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
        this.seed = this.random.nextLong();
        this.remainingActions = this.random.nextInt(3) + 1;
    }

    // ------------------------------------------------------------
    //                       TICK LOGIC
    // ------------------------------------------------------------
    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient() || !(this.getWorld() instanceof ServerWorld serverWorld)) return;

        // ---------------- First Tick: Infernal Strike ----------------
        if (this.age == 1) {
            // 🔥 Transform nearby non-infernal entities into Infernal variants
            for (Entity e : serverWorld.getOtherEntities(this, this.getBoundingBox().expand(3.5))) {
                if (e instanceof LivingEntity living && !living.getType().getTranslationKey().contains("infernal")) {
                    InfernalTransformationHandler.tryTransform(living, serverWorld);
                }
            }

            // Tag transformed entities to prevent instant re-striking
            serverWorld.getOtherEntities(this, this.getBoundingBox().expand(3.5)).forEach(e -> {
                if (e instanceof LivingEntity le) le.addCommandTag("infernal_just_transformed");
            });

            // Effects & sound remain unchanged
            serverWorld.spawnParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 20, 0.5, 0.5, 0.5, 0.02);
            serverWorld.spawnParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 1, this.getZ(), 10, 0.3, 0.3, 0.3, 0.02);
            serverWorld.spawnParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 8, 0.4, 0.3, 0.4, 0.01);
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT,
                    SoundCategory.WEATHER, 1.5F, 0.6F + this.random.nextFloat() * 0.2F);

            // Fire ignition + vanilla effects
            BlockPos strikePos = this.getBlockPos();
            if (serverWorld.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)
                    && serverWorld.getBlockState(strikePos).isAir()) {
                serverWorld.setBlockState(strikePos, Blocks.FIRE.getDefaultState());
            }

            this.powerLightningRod();
            cleanOxidation(this.getWorld(), this.getAffectedBlockPos());
            this.emitGameEvent(GameEvent.LIGHTNING_STRIKE);
        }



        // ---------------- Vanilla-like Lightning Behavior ----------------
        if (this.ambientTick == 2) {
            if (this.getWorld().isClient()) {
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER,
                        10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER,
                        2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            } else {
                Difficulty difficulty = this.getWorld().getDifficulty();
                if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
                    this.spawnFire(4);
                }
                cleanOxidation(this.getWorld(), getAffectedBlockPos());
                this.emitGameEvent(GameEvent.LIGHTNING_STRIKE);
            }
        }

        this.ambientTick--;

        // ---------------- Cleanup Phase ----------------
        if (this.ambientTick < 0) {
            if (this.remainingActions == 0) {
                if (this.getWorld() instanceof ServerWorld world) {
                    List<Entity> list = world.getOtherEntities(this,
                            new Box(this.getX() - 15, this.getY() - 15, this.getZ() - 15,
                                    this.getX() + 15, this.getY() + 6 + 15, this.getZ() + 15),
                            e -> e.isAlive() && !this.struckEntities.contains(e));
                    for (ServerPlayerEntity player : world.getPlayers(p -> p.distanceTo(this) < 256.0F)) {
                        Criteria.LIGHTNING_STRIKE.trigger(player, this, list);
                    }
                }
                this.discard();
            } else if (this.ambientTick < -this.random.nextInt(10)) {
                this.remainingActions--;
                this.ambientTick = 1;
                this.seed = this.random.nextLong();
                this.setFireTicks(0);
            }
        }

        // ---------------- Re-strike Nearby Entities ----------------
        if (this.ambientTick >= 0 && !this.cosmetic) {
            List<Entity> list = this.getWorld().getOtherEntities(this,
                    new Box(this.getX() - 3, this.getY() - 3, this.getZ() - 3,
                            this.getX() + 3, this.getY() + 6 + 3, this.getZ() + 3),
                    e -> e.isAlive() && !(e instanceof LivingEntity le && (
                            le.age <= 2 || le.getCommandTags().contains("infernal_just_transformed"))));

            for (Entity entity : list) {
                entity.onStruckByLightning(serverWorld, this);
            }

            this.struckEntities.addAll(list);
            if (this.channeler != null) Criteria.CHANNELED_LIGHTNING.trigger(this.channeler, list);
        }
    }



    // ------------------------------------------------------------
    //                    FIRE & OXIDATION HELPERS
    // ------------------------------------------------------------
    private BlockPos getAffectedBlockPos() {
        Vec3d vec3d = this.getPos();
        return BlockPos.ofFloored(vec3d.x, vec3d.y - 1.0E-6, vec3d.z);
    }

    private void spawnFire(int spreadAttempts) {
        if (!this.cosmetic && !this.getWorld().isClient
                && this.getWorld().getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            BlockPos base = this.getBlockPos();
            BlockState fire = AbstractFireBlock.getState(this.getWorld(), base);

            if (this.getWorld().getBlockState(base).isAir() && fire.canPlaceAt(this.getWorld(), base)) {
                this.getWorld().setBlockState(base, fire);
                this.blocksSetOnFire++;
            }

            for (int i = 0; i < spreadAttempts; ++i) {
                BlockPos pos = base.add(this.random.nextInt(3) - 1,
                        this.random.nextInt(3) - 1,
                        this.random.nextInt(3) - 1);
                fire = AbstractFireBlock.getState(this.getWorld(), pos);
                if (this.getWorld().getBlockState(pos).isAir() && fire.canPlaceAt(this.getWorld(), pos)) {
                    this.getWorld().setBlockState(pos, fire);
                    this.blocksSetOnFire++;
                }
            }
        }
    }

    private static void cleanOxidation(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        BlockPos basePos;
        BlockState targetState;
        if (blockState.isOf(Blocks.LIGHTNING_ROD)) {
            basePos = pos.offset(blockState.get(LightningRodBlock.FACING).getOpposite());
            targetState = world.getBlockState(basePos);
        } else {
            basePos = pos;
            targetState = blockState;
        }

        if (targetState.getBlock() instanceof Oxidizable) {
            world.setBlockState(basePos, Oxidizable.getUnaffectedOxidationState(targetState));
            BlockPos.Mutable mutable = pos.mutableCopy();
            int count = world.random.nextInt(3) + 3;
            for (int i = 0; i < count; i++) {
                cleanOxidationAround(world, basePos, mutable, world.random.nextInt(8) + 1);
            }
        }
    }

    private static void cleanOxidationAround(World world, BlockPos pos, BlockPos.Mutable mutable, int count) {
        mutable.set(pos);
        for (int i = 0; i < count; ++i) {
            Optional<BlockPos> optional = cleanOxidationAround(world, mutable);
            if (optional.isEmpty()) break;
            mutable.set(optional.get());
        }
    }

    private static Optional<BlockPos> cleanOxidationAround(World world, BlockPos pos) {
        for (BlockPos target : BlockPos.iterateRandomly(world.random, 10, pos, 1)) {
            BlockState state = world.getBlockState(target);
            if (state.getBlock() instanceof Oxidizable) {
                Oxidizable.getDecreasedOxidationState(state).ifPresent(s -> world.setBlockState(target, s));
                world.syncWorldEvent(3002, target, -1);
                return Optional.of(target);
            }
        }
        return Optional.empty();
    }

    private void powerLightningRod() {
        BlockPos blockPos = this.getAffectedBlockPos();
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        if (blockState.isOf(Blocks.LIGHTNING_ROD)) {
            ((LightningRodBlock)blockState.getBlock()).setPowered(blockState, this.getWorld(), blockPos);
        }

    }

    // ------------------------------------------------------------
    //                       DATA HANDLING
    // ------------------------------------------------------------
    @Override protected void initDataTracker() {}
    @Override protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override protected void writeCustomDataToNbt(NbtCompound nbt) {}

    public int getBlocksSetOnFire() { return this.blocksSetOnFire; }
    public Stream<Entity> getStruckEntities() { return this.struckEntities.stream().filter(Entity::isAlive); }
}
