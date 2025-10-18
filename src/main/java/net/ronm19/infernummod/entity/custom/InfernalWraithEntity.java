package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.particle.ParticleTypes;

import java.util.EnumSet;

public class InfernalWraithEntity extends BlazeEntity implements RangedAttackMob, Monster {

    private float eyeOffset = 0.5F;
    private int eyeOffsetCooldown;
    private static final TrackedData<Byte> BLAZE_FLAGS;
    private int firePillarCooldown = 0;
    private int phaseCooldown = 0;

    private static final TrackedData<Boolean> PHASING =
            DataTracker.registerData(InfernalWraithEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public InfernalWraithEntity(EntityType<? extends BlazeEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.moveControl = new FlightMoveControl(this, 10, true);
        this.experiencePoints = 25;
        this.noClip = false;
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createInfernalWraithAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 150.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.38D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 6.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D);
    }

    // ---------------- GOALS ----------------
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.0D, 40, 24.0F)); // Fireball attacks
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.8D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new GoToWalkTargetGoal(this, (double)1.0F));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, TameableEntity.class, true));
        this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge(InfernalWraithEntity.class));
    }

    // ---------------- DATA TRACKER ----------------
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PHASING, false);
        this.dataTracker.startTracking(BLAZE_FLAGS, (byte)0);
    }

    public boolean isPhasing() {
        return this.dataTracker.get(PHASING);
    }

    public void setPhasing(boolean value) {
        this.dataTracker.set(PHASING, value);
    }

    public boolean isOnFire() {
        return this.isFireActive();
    }

    private boolean isFireActive() {
        return ((Byte)this.dataTracker.get(BLAZE_FLAGS) & 1) != 0;
    }

    void setFireActive(boolean fireActive) {
        byte b = (Byte) this.dataTracker.get(BLAZE_FLAGS);
        if (fireActive) {
            b = (byte) (b | 1);
        } else {
            b = (byte) (b & -2);
        }
    }

    static {
        BLAZE_FLAGS = DataTracker.registerData(InfernalWraithEntity.class, TrackedDataHandlerRegistry.BYTE);
    }

        // ---------------- TICK LOGIC ----------------
    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            if (this.isPhasing()) {
                this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.0D, this.getZ(), 0.0D, 0.05D, 0.0D);
            } else {
                if (this.random.nextInt(4) == 0) {
                    this.getWorld().addParticle(ParticleTypes.FLAME, this.getParticleX(0.6D), this.getRandomBodyY(), this.getParticleZ(0.6D), 0.0D, 0.02D, 0.0D);
                }
            }
        }

        if (!this.getWorld().isClient()) {
            if (firePillarCooldown > 0) firePillarCooldown--;
            if (phaseCooldown > 0) phaseCooldown--;

            if (this.getTarget() != null) {
                // Summon fire pillars occasionally
                if (firePillarCooldown <= 0 && this.random.nextFloat() < 0.01F) {
                    summonFirePillars(this.getTarget());
                    firePillarCooldown = 200; // 10 seconds
                }

                // Phase shift occasionally when low HP
                if (this.getHealth() < this.getMaxHealth() / 2 && phaseCooldown <= 0) {
                    phaseShift(this.getTarget());
                    phaseCooldown = 300; // 15 seconds
                }
            }
        }
    }

    // ---------------- FIRE PILLARS ----------------
    private void summonFirePillars(LivingEntity target) {
        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        double radius = 4.0D;

        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians((i * 72) + this.random.nextInt(20));
            double x = target.getX() + Math.cos(angle) * radius;
            double z = target.getZ() + Math.sin(angle) * radius;
            BlockPos pos = BlockPos.ofFloored(x, target.getY(), z);

            // Add fire particle burst
            serverWorld.spawnParticles(ParticleTypes.LAVA, x, pos.getY(), z, 20, 0.2D, 0.8D, 0.2D, 0.05D);
            serverWorld.spawnParticles(ParticleTypes.FLAME, x, pos.getY(), z, 30, 0.2D, 1.2D, 0.2D, 0.02D);

            // Optional: temporary fire block
            if (serverWorld.getBlockState(pos).isAir()) {
                serverWorld.setBlockState(pos, net.minecraft.block.Blocks.FIRE.getDefaultState());
            }
        }
    }

    // ---------------- PHASING ABILITY ----------------
    private void phaseShift(LivingEntity target) {
        this.setPhasing(true);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 40, 0, false, false));
        this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.2F, 0.8F + random.nextFloat() * 0.3F);

        // Teleport near target
        Vec3d newPos = target.getPos().add((random.nextDouble() - 0.5D) * 4.0D, 0.0D, (random.nextDouble() - 0.5D) * 4.0D);
        this.requestTeleport(newPos.x, newPos.y, newPos.z);

        this.getWorld().sendEntityStatus(this, (byte) 65);
        this.setPhasing(false);
    }

    // ---------------- TICK MOVEMENT ----------------

    public void tickMovement() {
        if (!this.isOnGround() && this.getVelocity().y < (double)0.0F) {
            this.setVelocity(this.getVelocity().multiply((double)1.0F, 0.6, (double)1.0F));
        }

        if (this.getWorld().isClient) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.getWorld().playSound(this.getX() + (double)0.5F, this.getY() + (double)0.5F, this.getZ() + (double)0.5F, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for(int i = 0; i < 2; ++i) {
                this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE, this.getParticleX((double)0.5F), this.getRandomBodyY(), this.getParticleZ((double)0.5F), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

        super.tickMovement();
    }

    // ---------------- MOB TICK ----------------


    protected void mobTick() {
        --this.eyeOffsetCooldown;
        if (this.eyeOffsetCooldown <= 0) {
            this.eyeOffsetCooldown = 100;
            this.eyeOffset = (float)this.random.nextTriangular((double)0.5F, 6.891);
        }

        LivingEntity livingEntity = this.getTarget();
        if (livingEntity != null && livingEntity.getEyeY() > this.getEyeY() + (double)this.eyeOffset && this.canTarget(livingEntity)) {
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(this.getVelocity().add((double)0.0F, ((double)0.3F - vec3d.y) * (double)0.3F, (double)0.0F));
            this.velocityDirty = true;
        }

        super.mobTick();
    }

    // ---------------- RANGED ATTACK ----------------
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (!this.getWorld().isClient()) {
            Vec3d vec3d = this.getRotationVec(1.0F);
            double dx = target.getX() - (this.getX() + vec3d.x * 4.0D);
            double dy = target.getBodyY(0.5D) - (0.5D + this.getBodyY(0.5D));
            double dz = target.getZ() - (this.getZ() + vec3d.z * 4.0D);

            // Spawn your custom explosive projectile
            ExplosiveFireballEntity fireball = new ExplosiveFireballEntity(this.getWorld(), this, dx, dy, dz);
            fireball.setPos(
                    this.getX() + vec3d.x * 2.0D,
                    this.getBodyY(0.5D) + 0.5D,
                    this.getZ() + vec3d.z * 2.0D
            );
            this.getWorld().spawnEntity(fireball);

            this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }
    }


    // ---------------- SOUND ----------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    // ---------------- OTHER ----------------


    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false; // immune to fall
    }

    @Override
    protected void fall( double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        // no-op
    }

    public boolean hurtByWater() {
        return true;
    }
}