package net.ronm19.infernummod.entity.custom;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.*;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.UUID;

public class InfernalZombilagerEntity extends ZombieEntity implements Monster, VillagerDataContainer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final TrackedData<Boolean> CONVERTING;
    private static final TrackedData<VillagerData> VILLAGER_DATA;
    private static final int BASE_CONVERSION_DELAY = 3600;
    private static final int field_30520 = 6000;
    private static final int field_30521 = 14;
    private static final int field_30522 = 4;
    private int conversionTimer;
    @Nullable
    private UUID converter;
    @Nullable
    private NbtElement gossipData;
    @Nullable
    private NbtCompound offerData;
    private int xp;

    public InfernalZombilagerEntity( EntityType<? extends ZombieEntity> entityType, World world ) {
        super(entityType, world);
        Registries.VILLAGER_PROFESSION.getRandom(this.random).ifPresent(( profession) -> this.setVillagerData(this.getVillagerData().withProfession((VillagerProfession)profession.value())));
    }

    // --------------------------------- DATAs -------------------------------------

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CONVERTING, false);
        this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    public void setOfferData( @Nullable NbtCompound offerData) {
        this.offerData = offerData;
    }

    public void setGossipData( @Nullable NbtElement gossipData) {
        this.gossipData = gossipData;
    }

    public void setVillagerData(VillagerData villagerData) {
        VillagerData villagerData2 = this.getVillagerData();
        if (villagerData2.getProfession() != villagerData.getProfession()) {
            this.offerData = null;
        }

        this.dataTracker.set(VILLAGER_DATA, villagerData);
    }

    public VillagerData getVillagerData() {
        return (VillagerData)this.dataTracker.get(VILLAGER_DATA);
    }

    // -------------------------- ATTRIBUTES & GOALs ----------------------- //

    // Attributes
    public static DefaultAttributeContainer.Builder createInfernalZombilagerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 75.0) // 40 hearts
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0) // moderate damage
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5) // slightly fast
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 55.0) // can spot player from mid-range
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.2)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.3);

    }

    @Override
    protected void initGoals() {
        // Basic hostile mob goals
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, false)); // Attack player with melee
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));    // Wander when idle
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // Look at nearby players
        this.goalSelector.add(4,new LookAroundGoal(this)); // Idle looking around

        // Targeting goals
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true, true)); // Aggro on players
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, VillagerEntity.class, true, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, TameableEntity.class, true, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, IronGolemEntity.class, true, true));
        this.targetSelector.add(5, new RevengeGoal(this).setGroupRevenge(InfernalZombilagerEntity.class)); // Attack back if hurt
    }



    // ---------------------------- NBT ------------------------------------------------

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        // 🧠 Encode VillagerData safely and log any issues clearly
        VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData())
                .resultOrPartial(message -> LOGGER.error("Failed to encode VillagerData: {}", message))
                .ifPresent(nbtElement -> nbt.put("VillagerData", nbtElement));

        // 💰 Trade offers
        if (this.offerData != null) {
            nbt.put("Offers", this.offerData);
        }

        // 🗣 Gossip data
        if (this.gossipData != null) {
            nbt.put("Gossips", this.gossipData);
        }

        // 🧪 Conversion data
        nbt.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
        if (this.converter != null) {
            nbt.putUuid("ConversionPlayer", this.converter);
        }

        // 🧩 Experience points
        nbt.putInt("Xp", this.xp);


        if (this.gossipData != null) {
            nbt.put("Gossips", this.gossipData);
        }

        nbt.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
        if (this.converter != null) {
            nbt.putUuid("ConversionPlayer", this.converter);
        }

        nbt.putInt("Xp", this.xp);
    }


    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataResult = VillagerData.CODEC.parse(new Dynamic(NbtOps.INSTANCE, nbt.get("VillagerData")));
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            dataResult.resultOrPartial(var10001::error).ifPresent(this::setVillagerData);
        }

        if (nbt.contains("Offers", 10)) {
            this.offerData = nbt.getCompound("Offers");
        }

        if (nbt.contains("Gossips", 9)) {
            this.gossipData = nbt.getList("Gossips", 10);
        }

        if (nbt.contains("ConversionTime", 99) && nbt.getInt("ConversionTime") > -1) {
            this.setConverting(nbt.containsUuid("ConversionPlayer") ? nbt.getUuid("ConversionPlayer") : null, nbt.getInt("ConversionTime"));
        }

        if (nbt.contains("Xp", 3)) {
            this.xp = nbt.getInt("Xp");
        }

    }

    // ----------------------- TICK --------------------------------------
    public void tick() {
        if (!this.getWorld().isClient && this.isAlive() && this.isConverting()) {
            int i = this.getConversionRate();
            this.conversionTimer -= i;
            if (this.conversionTimer <= 0) {
                this.finishConversion((ServerWorld)this.getWorld());
            }
        }

        super.tick();
    }

    public ActionResult interactMob( PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.GOLDEN_APPLE)) {
            if (this.hasStatusEffect(StatusEffects.WEAKNESS)) {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (!this.getWorld().isClient) {
                    this.setConverting(player.getUuid(), this.random.nextInt(2401) + 3600);
                }

                return ActionResult.SUCCESS;
            } else {
                return ActionResult.CONSUME;
            }
        } else {
            return super.interactMob(player, hand);
        }
    }

    // -------------------- IMMUNITIES --------------------- //


    @Override
    public boolean isFireImmune() {
        return true;
    }

    // ------------------------ OTHER ----------------------- //

    protected boolean canConvertInWater() {
        return false;
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isConverting() && this.xp == 0;
    }

    public boolean isConverting() {
        return (Boolean)this.getDataTracker().get(CONVERTING);
    }

    private void setConverting(@Nullable UUID uuid, int delay) {
        this.converter = uuid;
        this.conversionTimer = delay;
        this.getDataTracker().set(CONVERTING, true);
        this.removeStatusEffect(StatusEffects.WEAKNESS);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.getWorld().getDifficulty().getId() - 1, 0)));
        this.getWorld().sendEntityStatus(this, (byte)16);
    }


    public void handleStatus(byte status) {
        if (status == 16) {
            if (!this.isSilent()) {
                this.getWorld().playSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

        } else {
            super.handleStatus(status);
        }
    }

    private void finishConversion(ServerWorld world) {
        VillagerEntity villagerEntity = (VillagerEntity)this.convertTo(EntityType.VILLAGER, false);

        for(EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            if (!itemStack.isEmpty()) {
                if (EnchantmentHelper.hasBindingCurse(itemStack)) {
                    villagerEntity.getStackReference(equipmentSlot.getEntitySlotId() + 300).set(itemStack);
                } else {
                    double d = (double)this.getDropChance(equipmentSlot);
                    if (d > (double)1.0F) {
                        this.dropStack(itemStack);
                    }
                }
            }
        }

        villagerEntity.setVillagerData(this.getVillagerData());
        if (this.gossipData != null) {
            villagerEntity.readGossipDataNbt(this.gossipData);
        }

        if (this.offerData != null) {
            villagerEntity.setOffers(new TradeOfferList(this.offerData));
        }

        villagerEntity.setExperience(this.xp);
        villagerEntity.initialize(world, world.getLocalDifficulty(villagerEntity.getBlockPos()), SpawnReason.CONVERSION, (EntityData)null, (NbtCompound)null);
        villagerEntity.reinitializeBrain(world);
        if (this.converter != null) {
            PlayerEntity playerEntity = world.getPlayerByUuid(this.converter);
            if (playerEntity instanceof ServerPlayerEntity) {
                Criteria.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayerEntity)playerEntity, this, villagerEntity);
                world.handleInteraction(EntityInteraction.ZOMBIE_VILLAGER_CURED, playerEntity, villagerEntity);
            }
        }

        villagerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
        if (!this.isSilent()) {
            world.syncWorldEvent((PlayerEntity)null, 1027, this.getBlockPos(), 0);
        }

    }

    private int getConversionRate() {
        int i = 1;
        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for(int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; ++k) {
                for(int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; ++l) {
                    for(int m = (int)this.getZ() - 4; m < (int)this.getZ() + 4 && j < 14; ++m) {
                        BlockState blockState = this.getWorld().getBlockState(mutable.set(k, l, m));
                        if (blockState.isOf(Blocks.IRON_BARS) || blockState.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    public int getXp() {
        return this.xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    protected Vector3f getPassengerAttachmentPos( Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, dimensions.height + 0.175F * scaleFactor, 0.0F);
    }

// ------------------------ SOUNDS --------------------------------

    public float getSoundPitch() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
    }

    public SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
    }

    public SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
    }

    public SoundEvent getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
    }

    // ---------------------- SPAWN SETUP ------------------------------ //

    @Nullable
    public EntityData initialize( ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(world.getBiome(this.getBlockPos()))));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    static {
        CONVERTING = DataTracker.registerData(InfernalZombilagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        VILLAGER_DATA = DataTracker.registerData(InfernalZombilagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
    }
}
