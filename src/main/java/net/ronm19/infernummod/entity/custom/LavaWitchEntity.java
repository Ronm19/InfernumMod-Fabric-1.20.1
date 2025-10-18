package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.ronm19.infernummod.entity.ai.goals.lava_witch.ThrowLavaPotionGoal;
import net.ronm19.infernummod.item.ModItems;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class LavaWitchEntity extends WitchEntity implements Monster, RangedAttackMob {
    private static final UUID DRINKING_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final EntityAttributeModifier DRINKING_SPEED_PENALTY_MODIFIER;
    private static final TrackedData<Boolean> DRINKING;
    private int drinkTimeLeft;
    private RaidGoal<RaiderEntity> raidGoal;
    private DisableableFollowTargetGoal<PlayerEntity> attackPlayerGoal;

    public LavaWitchEntity( EntityType<? extends WitchEntity> entityType, World world ) {
        super(entityType, world);
    }

    // ---------------- GOALS -----------------

    protected void initGoals() {
        super.initGoals();
        this.raidGoal = new RaidGoal<>(this, RaiderEntity.class, true, ( entity) -> entity != null && this.hasActiveRaid() && entity.getType() != EntityType.WITCH);
        this.attackPlayerGoal = new DisableableFollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, (Predicate)null);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ThrowLavaPotionGoal(this));
        this.goalSelector.add(3, new ProjectileAttackGoal(this, (double)1.0F, 60, 10.0F));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, (double)1.0F));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this, new Class[]{RaiderEntity.class}));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, TameableEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(4, this.raidGoal);
        this.targetSelector.add(5, this.attackPlayerGoal);
    }

    // ---------------- ATTRIBUTES -----------------------

    public static DefaultAttributeContainer.Builder createLavaWitchAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 3.0D);
    }



    // --------------------- DATA TRACKER ----------------

    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(DRINKING, false);
    }

    public void setDrinking(boolean drinking) {
        this.getDataTracker().set(DRINKING, drinking);
    }

    public boolean isDrinking() {
        return (Boolean)this.getDataTracker().get(DRINKING);
    }


    // ---------------------- SOUNDS ---------------------

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITCH_AMBIENT;
    }

    protected SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_WITCH_CELEBRATE;
    }

    // ---------------------- TICK MOVEMENT ---------------------

    public void tickMovement() {
        if (!this.getWorld().isClient && this.isAlive()) {
            this.raidGoal.decreaseCooldown();
            if (this.raidGoal.getCooldown() <= 0) {
                this.attackPlayerGoal.setEnabled(true);
            } else {
                this.attackPlayerGoal.setEnabled(false);
            }

            if (this.isDrinking()) {
                if (this.drinkTimeLeft-- <= 0) {
                    this.setDrinking(false);
                    ItemStack itemStack = this.getMainHandStack();
                    this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (itemStack.isOf(Items.POTION)) {
                        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
                        if (list != null) {
                            for(StatusEffectInstance statusEffectInstance : list) {
                                this.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
                            }
                        }
                    }

                    this.emitGameEvent(GameEvent.DRINK);
                    this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(DRINKING_SPEED_PENALTY_MODIFIER.getId());
                }
            } else {
                Potion potion = null;
                if (this.random.nextFloat() < 0.15F && this.isSubmergedIn(FluidTags.WATER) && !this.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                    potion = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15F && (this.isOnFire() || this.getRecentDamageSource() != null && this.getRecentDamageSource().isIn(DamageTypeTags.IS_FIRE)) && !this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                    potion = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    potion = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5F && this.getTarget() != null && !this.hasStatusEffect(StatusEffects.SPEED) && this.getTarget().squaredDistanceTo(this) > (double)121.0F) {
                    potion = Potions.SWIFTNESS;
                }

                if (potion != null) {
                    this.equipStack(EquipmentSlot.MAINHAND, PotionUtil.setPotion(new ItemStack(Items.POTION), potion));
                    this.drinkTimeLeft = this.getMainHandStack().getMaxUseTime();
                    this.setDrinking(true);
                    if (!this.isSilent()) {
                        this.getWorld().playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }

                    EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                    entityAttributeInstance.removeModifier(DRINKING_SPEED_PENALTY_MODIFIER.getId());
                    entityAttributeInstance.addTemporaryModifier(DRINKING_SPEED_PENALTY_MODIFIER);
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.getWorld().sendEntityStatus(this, (byte)15);
            }
        }

        super.tickMovement();
    }

    // --------------------------- TICK ------------------------- //

    @Override
    public void tick() {
        super.tick();

        if (this.isWet() || this.isTouchingWater()) {
            this.damage(this.getDamageSources().drown(), 1.0F);
        }

        if (this.getWorld().isClient) {
            // Lava aura particles
            for (int i = 0; i < 1; ++i) {
                this.getWorld().addParticle(
                        net.minecraft.particle.ParticleTypes.LAVA,
                        this.getX() + (this.random.nextDouble() - 0.2D) * 0.2D,
                        this.getY() + 0.6D,
                        this.getZ() + (this.random.nextDouble() - 0.2D) * 0.2D,
                        0.0D, 0.01D, 0.0D);
            }
        }
    }

    //  ---------------------------- IMMUNITIES ----------------------//

    @Override
    public boolean isFireImmune() {
        return true;
    }

    // ---------------------------- OTHER ---------------------- //


    public void handleStatus(byte status) {
        if (status == 15) {
            for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.getWorld().addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double)0.13F, this.getBoundingBox().maxY + (double)0.5F + this.random.nextGaussian() * (double)0.13F, this.getZ() + this.random.nextGaussian() * (double)0.13F, (double)0.0F, (double)0.0F, (double)0.0F);
            }
        } else {
            super.handleStatus(status);
        }

    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);

        if (this.getWorld().isClient) return; // prevent double drops

        Random random = this.getRandom();
        World world = this.getWorld();

        // 🔥 Always drop: Infernal Beast Horn (guaranteed thematic drop)
        this.dropItem(ModItems.INFERNAL_BEAST_HORN);

        // 💨 Common: Blaze Powder (2–4)
        this.dropStack(new ItemStack(Items.BLAZE_POWDER, 2 + random.nextInt(3)));

        // ❤️ Rare: Blaze Heart (custom item)
        if (random.nextFloat() < 0.35F) { // 35% chance
            this.dropItem(ModItems.BLAZE_HEART);
        }

        // 🍷 Very Rare: Potion (randomized)
        if (random.nextFloat() < 0.15F) { // 15% chance
            ItemStack potion = new ItemStack(Items.POTION);
            potion.setCustomName(Text.literal("Molten Brew"));
            this.dropStack(potion);
        }

        // 💎 Ultra Rare: Nether Ruby (5% chance)
        if (random.nextFloat() < 0.05F) {
            this.dropItem(ModItems.NETHER_RUBY);
        }
    }


    protected float modifyAppliedDamage(DamageSource source, float amount) {
        amount = super.modifyAppliedDamage(source, amount);
        if (source.getAttacker() == this) {
            amount = 0.0F;
        }

        if (source.isIn(DamageTypeTags.WITCH_RESISTANT_TO)) {
            amount *= 0.15F;
        }

        return amount;
    }

    public void shootAt( LivingEntity target, float pullProgress) {
        if (!this.isDrinking()) {
            Vec3d vec3d = target.getVelocity();
            double d = target.getX() + vec3d.x - this.getX();
            double e = target.getEyeY() - (double)1.1F - this.getY();
            double f = target.getZ() + vec3d.z - this.getZ();
            double g = Math.sqrt(d * d + f * f);
            Potion potion = Potions.HARMING;
            if (target instanceof RaiderEntity) {
                if (target.getHealth() <= 4.0F) {
                    potion = Potions.HEALING;
                } else {
                    potion = Potions.REGENERATION;
                }

                this.setTarget((LivingEntity)null);
            } else if (g >= (double)8.0F && !target.hasStatusEffect(StatusEffects.SLOWNESS)) {
                potion = Potions.SLOWNESS;
            } else if (target.getHealth() >= 8.0F && !target.hasStatusEffect(StatusEffects.POISON)) {
                potion = Potions.POISON;
            } else if (g <= (double)3.0F && !target.hasStatusEffect(StatusEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                potion = Potions.WEAKNESS;
            }

            PotionEntity potionEntity = new PotionEntity(this.getWorld(), this);
            potionEntity.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
            potionEntity.setPitch(potionEntity.getPitch() - -20.0F);
            potionEntity.setVelocity(d, e + g * 0.2, f, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.getWorld().playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }

            this.getWorld().spawnEntity(potionEntity);
        }
    }

    protected float getActiveEyeHeight( EntityPose pose, EntityDimensions dimensions) {
        return 1.62F;
    }

    protected Vector3f getPassengerAttachmentPos( Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, dimensions.height + 0.3125F * scaleFactor, 0.0F);
    }

    public void addBonusForWave(int wave, boolean unused) {
    }

    public boolean canLead() {
        return false;
    }

    static {
        DRINKING_SPEED_PENALTY_MODIFIER = new EntityAttributeModifier(DRINKING_SPEED_PENALTY_MODIFIER_ID, "Drinking speed penalty", (double)-0.25F, EntityAttributeModifier.Operation.ADDITION);
        DRINKING = DataTracker.registerData(LavaWitchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
