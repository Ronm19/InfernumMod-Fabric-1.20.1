package net.ronm19.infernummod.entity.ai.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class ModTameableAnimal extends TameableEntity implements Tameable {

    // --- Data tracking ---
    protected static final TrackedData<Byte> TAMEABLE_FLAGS =
            DataTracker.registerData(ModTameableAnimal.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Optional<UUID>> OWNER_UUID =
            DataTracker.registerData(ModTameableAnimal.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    protected ModTameableAnimal(EntityType<? extends ModTameableAnimal> type, World world) {
        super(type, world);
    }

    // --- Owner handling ---
    @Nullable
    @Override
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUuid();
        return uuid == null ? null : this.getWorld().getPlayerByUuid(uuid);
    }

    public void setOwner(PlayerEntity player) {
        this.setTamed(true);
        this.setOwnerUuid(player.getUuid());
        if (player instanceof ServerPlayerEntity serverPlayer) {
            Criteria.TAME_ANIMAL.trigger(serverPlayer, this);
        }
    }

    public boolean canTarget(LivingEntity target) {
        return !this.isOwner(target) && super.canTarget(target);
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    // --- Sitting ---
    public boolean isSitting() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
    }

    public void setSitting(boolean sit) {
        byte b = this.dataTracker.get(TAMEABLE_FLAGS);
        if (sit) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b | 1));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b & ~1));
        }
    }

    // --- Tame state ---
    public boolean isTamed() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
    }

    public void setTamed(boolean tamed) {
        byte b = this.dataTracker.get(TAMEABLE_FLAGS);
        this.dataTracker.set(TAMEABLE_FLAGS, tamed ? (byte) (b | 4) : (byte) (b & ~4));
    }

    protected void onTamedChanged() {}

    // --- NBT save/load ---
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID uuid = null;
        if (nbt.containsUuid("Owner")) {
            uuid = nbt.getUuid("Owner");
        } else if (nbt.contains("Owner")) {
            uuid = ServerConfigHandler.getPlayerUuidByName(this.getServer(), nbt.getString("Owner"));
        }

        if (uuid != null) {
            this.setOwnerUuid(uuid);
            this.setTamed(true);
        }

        if (nbt.contains("Sitting")) {
            this.setSitting(nbt.getBoolean("Sitting"));
        }
    }

    // --- Data tracker init ---
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TAMEABLE_FLAGS, (byte) 0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    // --- Taming helper ---
    public void tame(PlayerEntity player) {
        if (!this.isTamed()) {
            this.setTamed(true);
            this.setOwnerUuid(player.getUuid());
            this.setSitting(false);
            this.spawnTamingParticles(true);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                // Hook for advancements or custom events
            }
        }
    }

    protected abstract Ingredient getTamingIngredient();

    // --- Interact ---
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (this.getWorld().isClient) {
            return (!this.isTamed() && getTamingIngredient().test(stack))
                    ? ActionResult.CONSUME
                    : ActionResult.PASS;
        }

        // --- Taming ---
        if (!this.isTamed() && getTamingIngredient().test(stack)) {
            if (!player.getAbilities().creativeMode) stack.decrement(1);
            this.tame(player);
            this.getWorld().sendEntityStatus(this, (byte) 7); // hearts
            return ActionResult.SUCCESS;
        }

        // --- Owner controls ---
        if (this.isTamed() && this.isOwner(player)) {
            if (stack.isEmpty()) {
                this.setSitting(!this.isSitting()); // toggle sit/stand
                return ActionResult.SUCCESS;
            }
            if (stack.isFood() && this.getHealth() < this.getMaxHealth()) {
                this.heal(stack.getItem().getFoodComponent() != null
                        ? stack.getItem().getFoodComponent().getHunger()
                        : 4);
                stack.decrement(1);
                this.getWorld().sendEntityStatus(this, (byte) 6);
                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }

    // --- Particle effects ---
    protected void spawnTamingParticles(boolean positive) {
        ParticleEffect particle = positive ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int i = 0; i < 7; i++) {
            double dx = this.random.nextGaussian() * 0.02;
            double dy = this.random.nextGaussian() * 0.02;
            double dz = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticle(particle,
                    this.getParticleX(1.0),
                    this.getRandomBodyY() + 0.5,
                    this.getParticleZ(1.0),
                    dx, dy, dz);
        }
    }

    // --- Teleport to owner ---
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && this.isTamed() && !this.isSitting() && this.getOwner() != null) {
            if (this.squaredDistanceTo(this.getOwner()) >= 144.0) {
                BlockPos pos = this.getOwner().getBlockPos();
                this.refreshPositionAndAngles(
                        pos.getX() + 0.5,
                        pos.getY(),
                        pos.getZ() + 0.5,
                        this.getYaw(),
                        this.getPitch()
                );
            }
        }
    }

    // --- Team logic ---
    @Override
    public AbstractTeam getScoreboardTeam() {
        if (this.isTamed()) {
            LivingEntity owner = this.getOwner();
            if (owner != null) return owner.getScoreboardTeam();
        }
        return super.getScoreboardTeam();
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (this.isTamed()) {
            LivingEntity owner = this.getOwner();
            if (other == owner) return true;
            if (owner != null && owner.isTeammate(other)) return true;
        }
        return super.isTeammate(other);
    }

    // --- Death messages ---
    @Override
    public void onDeath(DamageSource source) {
        if (!this.getWorld().isClient && this.getWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
            LivingEntity owner = this.getOwner();
            if (owner instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(this.getDamageTracker().getDeathMessage());
            }
        }
        super.onDeath(source);
    }

    // --- Goals to be registered in subclasses ---
    @Override
    protected void initGoals() {
        super.initGoals();
        registerTameableGoals();
    }

    protected abstract void registerTameableGoals();
}
