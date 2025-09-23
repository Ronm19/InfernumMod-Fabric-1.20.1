package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ai.FlyRandomlyGoal;
import net.ronm19.infernummod.entity.ai.LookAtTargetGoal;
import net.ronm19.infernummod.item.ModItems;
import org.joml.Vector3f;

public class ObsidianGhastEntity extends GhastEntity implements Monster {
    private static final TrackedData<Boolean> SHOOTING;
    private int fireballStrength = 3;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public ObsidianGhastEntity( EntityType<? extends GhastEntity> entityType, World world ) {
        super(entityType, world);

        this.experiencePoints = 5;
        this.moveControl = new ObsidianGhastMoveControl(this);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    protected void updateLimbs( float posDelta ) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);
    }

    private int attackCooldown = 0;

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }

        if (!this.getWorld().isClient) {
            LivingEntity target = this.getTarget();

            if (target != null && this.canSee(target) &&
                    this.squaredDistanceTo(target) < 64 * 64) {

                ++attackCooldown;

                // Charge-up sound at 10 ticks
                if (attackCooldown == 10 && !this.isSilent()) {
                    this.getWorld().syncWorldEvent(null, 1015, this.getBlockPos(), 0); // Ghast charge
                }

                // Shoot at 20 ticks
                if (attackCooldown == 20) {
                    shootSingle(target);
                    attackCooldown = -40; // Delay before next attack
                }
            } else if (attackCooldown > 0) {
                --attackCooldown;
            }

            // Animate “shooting” flag if you need it
            this.setShooting(attackCooldown > 10);
        }

        if (this.getWorld().isClient) {
            setupAnimationStates();
        }
    }

    private void shootSingle(LivingEntity target) {
        Vec3d look = this.getRotationVec(1.0F);

        double spawnX = this.getX() + look.x * 4.0;
        double spawnY = this.getBodyY(0.5) + 0.5;
        double spawnZ = this.getZ() + look.z * 4.0;

        // Direction to target
        double dx = target.getX() - spawnX;
        double dy = target.getBodyY(0.5) - spawnY;
        double dz = target.getZ() - spawnZ;

        if (!this.isSilent()) {
            this.getWorld().syncWorldEvent(null, 1016, this.getBlockPos(), 0); // Ghast shoot
        }

        FireballEntity fireball = new FireballEntity(
                this.getWorld(),
                this,
                dx,
                dy,
                dz,
                this.getFireballStrength() // much stronger than vanilla
        );

        fireball.setPos(spawnX, spawnY, spawnZ);
        this.getWorld().spawnEntity(fireball);

        this.playSound(SoundEvents.ENTITY_GHAST_SHOOT, 1.0F,
                0.8F + this.random.nextFloat() * 0.4F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (!this.getWorld().isClient) {
            this.dropStack(new ItemStack(ModItems.INFERNO_FANG, 1));
        }
    }


    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }

    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    @Override
    public boolean cannotDespawn() {
        // Prevents despawn in normal conditions, but monsters still despawn on peaceful
        return this.getType().getSpawnGroup() != SpawnGroup.MONSTER || this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
    }

    public int getFireballStrength() {
        return this.fireballStrength;
    }

    @Override
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && !this.isShooting();
    }

    private static boolean isFireballFromPlayer( DamageSource damageSource ) {
        return damageSource.getSource() instanceof FireballEntity && damageSource.getAttacker() instanceof PlayerEntity;
    }

    @Override
    public boolean isFireImmune() {
        return true; // is fire-resistant
    }

    public boolean isInvulnerableTo( DamageSource damageSource ) {
        return !isFireballFromPlayer(damageSource) && super.isInvulnerableTo(damageSource);
    }

    public boolean damage( DamageSource source, float amount ) {
        if (isFireballFromPlayer(source)) {
            super.damage(source, 1000.0F);
            return true;
        } else {
            return !this.isInvulnerableTo(source) && super.damage(source, amount);
        }
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
    }

    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("ExplosionPower", (byte) this.fireballStrength);
    }

    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("ExplosionPower", 99)) {
            this.fireballStrength = nbt.getByte("ExplosionPower");
        }

    }

    protected float getActiveEyeHeight( EntityPose pose, EntityDimensions dimensions ) {
        return 2.6F;
    }

    static {
        SHOOTING = DataTracker.registerData(ObsidianGhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    static class ObsidianGhastMoveControl extends MoveControl {
        private final ObsidianGhastEntity obsidianGhastEntity;
        private int collisionCheckCooldown;

        public ObsidianGhastMoveControl( ObsidianGhastEntity obsidianGhastEntity ) {
            super(obsidianGhastEntity);
            this.obsidianGhastEntity = obsidianGhastEntity;
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                if (this.collisionCheckCooldown-- <= 0) {
                    this.collisionCheckCooldown += this.obsidianGhastEntity.getRandom().nextInt(5) + 2;
                    Vec3d vec3d = new Vec3d(this.targetX - this.obsidianGhastEntity.getX(), this.targetY - this.obsidianGhastEntity.getY(), this.targetZ - this.obsidianGhastEntity.getZ());
                    double d = vec3d.length();
                    vec3d = vec3d.normalize();
                    if (this.willCollide(vec3d, MathHelper.ceil(d))) {
                        this.obsidianGhastEntity.setVelocity(this.obsidianGhastEntity.getVelocity().add(vec3d.multiply(0.1)));
                    } else {
                        this.state = State.WAIT;
                    }
                }

            }
        }

        private boolean willCollide( Vec3d direction, int steps ) {
            Box box = this.obsidianGhastEntity.getBoundingBox();

            for (int i = 1; i < steps; ++i) {
                box = box.offset(direction);
                if (!this.obsidianGhastEntity.getWorld().isSpaceEmpty(this.obsidianGhastEntity, box)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int getLimitPerChunk() {
        return 1;
    }

    protected Vector3f getPassengerAttachmentPos( Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, dimensions.height + 0.0625F * scaleFactor, 0.0F);
    }

    protected float getUnscaledRidingOffset(Entity vehicle) {
        return 0.5F;
    }

public static DefaultAttributeContainer.Builder createObsidianGhastAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 90.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.1)
                .add(EntityAttributes.GENERIC_ARMOR, 0.4)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.2)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
        this.goalSelector.add(2, new LookAroundGoal(this));
        this.goalSelector.add(3, new FlyRandomlyGoal(this));
        this.goalSelector.add(4, new LookAtTargetGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, ( entity) -> Math.abs(entity.getY() - this.getY()) <= (double)4.0F));
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_GHAST_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GHAST_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GHAST_DEATH;
    }

    protected float getSoundVolume() {
        return 5.0F;
    }
}
