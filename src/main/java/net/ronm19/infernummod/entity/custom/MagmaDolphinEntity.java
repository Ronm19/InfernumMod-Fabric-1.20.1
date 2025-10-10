package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ronm19.infernummod.effect.ModEffects;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class MagmaDolphinEntity extends DolphinEntity {

    // --- Dolphin Data ---
    private static TrackedData<BlockPos> TREASURE_POS;
    private static TrackedData<Boolean> HAS_FISH;
    private static TrackedData<Integer> MOISTNESS;

    static TargetPredicate CLOSE_PLAYER_PREDICATE;
    public static final int MAX_AIR = 4800;
    private static final int MAX_MOISTNESS = 2400;
    public static Predicate<ItemEntity> CAN_TAKE;

    public MagmaDolphinEntity(EntityType<? extends DolphinEntity> type, World world) {
        super(type, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.1F, false);
        this.navigation = new SwimNavigation(this, world);
    }

    // ---------------- Attributes ----------------

    public static DefaultAttributeContainer.Builder createMagmaDolphinAttributes() {
        return DolphinEntity.createDolphinAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D) // tougher
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.5D) // faster
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BreatheAirGoal(this));
        this.goalSelector.add(1, new MoveIntoWaterGoal(this));
        this.goalSelector.add(2, new PlayWithItemsGoal.SwimWithPlayerGoal(this, 4.0D));
        this.goalSelector.add(3, new DolphinJumpGoal(this, 12));   // Slightly stronger jump
        this.goalSelector.add(4, new SwimAroundGoal(this, 1.0D, 10));
        this.goalSelector.add(5, new LookAroundGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.add(8, new PlayWithItemsGoal(this));
        this.goalSelector.add(9, new ChaseBoatGoal(this));
        this.goalSelector.add(10, new FleeEntityGoal<>(this, DrownedEntity.class, 8.0F, 1.0D, 1.0D));

        this.targetSelector.add(1, new RevengeGoal(this, new Class[]{DrownedEntity.class}).setGroupRevenge());
    }

    static {
        CLOSE_PLAYER_PREDICATE = TargetPredicate.createNonAttackable()
                .setBaseMaxDistance(10.0D)
                .ignoreVisibility();
    }

    // ---------------- Behavior Overrides ----------------

    @Override
    public boolean isTouchingWater() {
        return super.isTouchingWater() || this.isInLava();  // Treat lava as water
    }

    @Override
    public boolean canBreatheInWater() {
        return true; // No drowning
    }

    @Override
    public boolean isFireImmune() {
        return true; // Fire-proof dolphin
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    @Override
    public void tick() {
        super.tick();

        // Spawn fewer particles for performance & visibility
        if (this.getWorld().isClient && this.isTouchingWater() && this.getVelocity().lengthSquared() > 0.01) {

            // Only spawn particles every 10 ticks (~0.5s)
            if (this.age % 10 == 0) {
                double dx = (random.nextDouble() - 0.5) * 0.4;
                double dy = random.nextDouble() * 0.4 + 0.1;
                double dz = (random.nextDouble() - 0.5) * 0.4;

                this.getWorld().addParticle(
                        ParticleTypes.LAVA,
                        this.getX() + dx,
                        this.getY() + dy,
                        this.getZ() + dz,
                        0.0, 0.02, 0.0
                );

                this.getWorld().addParticle(
                        ParticleTypes.SMOKE,
                        this.getX() + dx,
                        this.getY() + dy,
                        this.getZ() + dz,
                        0.0, 0.01, 0.0
                );
            }
        }
    }


    @Override
    public boolean tryAttack(Entity target) {
        boolean success = target.damage(this.getDamageSources().mobAttack(this),
                (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (success) {
            this.applyDamageEffects(this, target);
            this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0F, 1.0F);
        }
        return success;
    }

    @Override
    protected void playSwimSound() {
        this.playSound(SoundEvents.BLOCK_LAVA_POP, 0.3F, 1.0F);
    }

    // ---------------- Sounds ----------------

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_DOLPHIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_DOLPHIN_DEATH;
    }

    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_DOLPHIN_SPLASH;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_DOLPHIN_SWIM;
    }

    // ---------------- Interaction ----------------

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isEmpty() && itemStack.isIn(ItemTags.FISHES)) {
            if (!this.getWorld().isClient) {
                this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, 1.0F, 1.0F);
            }

            this.setHasFish(true);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            return ActionResult.success(this.getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return true;
    }

    // ---------------- Inner Goals ----------------

    class PlayWithItemsGoal extends Goal {
        private int nextPlayingTime;

        PlayWithItemsGoal(MagmaDolphinEntity magmaDolphinEntity) {
        }

        @Override
        public boolean canStart() {
            if (this.nextPlayingTime > MagmaDolphinEntity.this.age) {
                return false;
            } else {
                List<ItemEntity> list = MagmaDolphinEntity.this.getWorld()
                        .getEntitiesByClass(ItemEntity.class,
                                MagmaDolphinEntity.this.getBoundingBox().expand(8.0F),
                                DolphinEntity.CAN_TAKE);
                return !list.isEmpty() || !MagmaDolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
            }
        }

        @Override
        public void start() {
            List<ItemEntity> list = MagmaDolphinEntity.this.getWorld()
                    .getEntitiesByClass(ItemEntity.class,
                            MagmaDolphinEntity.this.getBoundingBox().expand(8.0F),
                            DolphinEntity.CAN_TAKE);
            if (!list.isEmpty()) {
                MagmaDolphinEntity.this.getNavigation().startMovingTo(list.get(0), 1.2F);
                MagmaDolphinEntity.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0F, 1.0F);
            }
            this.nextPlayingTime = 0;
        }

        @Override
        public void stop() {
            ItemStack itemStack = MagmaDolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                spitOutItem(itemStack);
                MagmaDolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                this.nextPlayingTime = MagmaDolphinEntity.this.age + MagmaDolphinEntity.this.random.nextInt(100);
            }
        }

        @Override
        public void tick() {
            List<ItemEntity> list = MagmaDolphinEntity.this.getWorld()
                    .getEntitiesByClass(ItemEntity.class,
                            MagmaDolphinEntity.this.getBoundingBox().expand(8.0F),
                            DolphinEntity.CAN_TAKE);
            ItemStack itemStack = MagmaDolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                spitOutItem(itemStack);
                MagmaDolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else if (!list.isEmpty()) {
                MagmaDolphinEntity.this.getNavigation().startMovingTo(list.get(0), 1.2F);
            }
        }

        private void spitOutItem(ItemStack stack) {
            if (!stack.isEmpty()) {
                double d = MagmaDolphinEntity.this.getEyeY() - 0.3F;
                ItemEntity itemEntity = new ItemEntity(MagmaDolphinEntity.this.getWorld(),
                        MagmaDolphinEntity.this.getX(), d,
                        MagmaDolphinEntity.this.getZ(), stack);
                itemEntity.setPickupDelay(40);
                itemEntity.setThrower(MagmaDolphinEntity.this.getUuid());

                float g = MagmaDolphinEntity.this.random.nextFloat() * ((float) Math.PI * 2F);
                float h = 0.02F * MagmaDolphinEntity.this.random.nextFloat();
                itemEntity.setVelocity(
                        (0.3F * -MathHelper.sin(MagmaDolphinEntity.this.getYaw() * ((float) Math.PI / 180F))
                                * MathHelper.cos(MagmaDolphinEntity.this.getPitch() * ((float) Math.PI / 180F))
                                + MathHelper.cos(g) * h),
                        (0.3F * MathHelper.sin(MagmaDolphinEntity.this.getPitch() * ((float) Math.PI / 180F)) * 1.5F),
                        (0.3F * MathHelper.cos(MagmaDolphinEntity.this.getYaw() * ((float) Math.PI / 180F))
                                * MathHelper.cos(MagmaDolphinEntity.this.getPitch() * ((float) Math.PI / 180F))
                                + MathHelper.sin(g) * h)
                );

                MagmaDolphinEntity.this.getWorld().spawnEntity(itemEntity);
            }
        }

        // ---- Inner Swim-With-Player Goal ----
        static class SwimWithPlayerGoal extends Goal {
            private final MagmaDolphinEntity magmaDolphin;
            private final double speed;
            @Nullable
            private PlayerEntity closestPlayer;

            SwimWithPlayerGoal(MagmaDolphinEntity magmaDolphin, double speed) {
                this.magmaDolphin = magmaDolphin;
                this.speed = speed;
                this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
            }

            @Override
            public boolean canStart() {
                this.closestPlayer = this.magmaDolphin.getWorld().getClosestPlayer(
                        MagmaDolphinEntity.CLOSE_PLAYER_PREDICATE, this.magmaDolphin);
                return this.closestPlayer != null
                        && this.closestPlayer.isSwimming()
                        && this.magmaDolphin.getTarget() != this.closestPlayer;
            }

            @Override
            public boolean shouldContinue() {
                return this.closestPlayer != null
                        && this.closestPlayer.isSwimming()
                        && this.magmaDolphin.squaredDistanceTo(this.closestPlayer) < 256.0F;
            }

            @Override
            public void start() {
                this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), this.magmaDolphin);
                this.closestPlayer.addStatusEffect(new StatusEffectInstance(ModEffects.BLAZING_HEART, 100), this.magmaDolphin);
            }

            @Override
            public void stop() {
                this.closestPlayer = null;
                this.magmaDolphin.getNavigation().stop();
            }

            @Override
            public void tick() {
                this.magmaDolphin.getLookControl().lookAt(this.closestPlayer,
                        this.magmaDolphin.getMaxHeadRotation() + 20,
                        this.magmaDolphin.getMaxLookPitchChange());

                if (this.magmaDolphin.squaredDistanceTo(this.closestPlayer) < 6.25F) {
                    this.magmaDolphin.getNavigation().stop();
                } else {
                    this.magmaDolphin.getNavigation().startMovingTo(this.closestPlayer, this.speed);
                }

                if (this.closestPlayer.isSwimming() && this.closestPlayer.getWorld().random.nextInt(6) == 0) {
                    this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), this.magmaDolphin);
                }
            }
        }
    }
}
