package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.ronm19.infernummod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

public class InfernalSkullEntity extends WitherSkullEntity {
    private int explosionPower = 2;
    private LivingEntity owner;

    private static final TrackedData<Boolean> HIT =
            DataTracker.registerData(InfernalSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static TrackedData<Boolean> CHARGED =
            DataTracker.registerData(InfernalSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int counter = 0;


    // === Registry constructor (for FabricEntityTypeBuilder) ===
    public InfernalSkullEntity( EntityType<? extends WitherSkullEntity> type, World world ) {
        super(type, world);
        this.setNoGravity(true);
    }

    public float getEffectiveExplosionResistance( Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max ) {
        return this.isCharged() && InfernalEyeEntity.canDestroy(blockState) ? Math.min(0.8F, max) : max;
    }

    protected float getDrag() {
        return this.isCharged() ? 0.73F : super.getDrag();
    }

    public InfernalSkullEntity( World world, LivingEntity owner, double dx, double dy, double dz ) {
        this(ModEntities.INFERNAL_SKULL, world);

        this.setOwner(owner);
        this.explosionPower = explosionPower;
        this.setNoGravity(true);

        // Spawn from owner’s eyes

        this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.setInfernalVelocity(dx, dy, dz, 6.0F, 0.02F); // fast, bullet-like

    }

    // === Velocity helper ===
    public void setInfernalVelocity( double dx, double dy, double dz, float velocity, float inaccuracy ) {
        Vec3d dir = new Vec3d(dx, dy, dz).normalize()
                .add(this.random.nextGaussian() * 0.0075 * inaccuracy,
                        this.random.nextGaussian() * 0.0075 * inaccuracy,
                        this.random.nextGaussian() * 0.0075 * inaccuracy)
                .multiply(velocity);
        this.setVelocity(dir);
        this.velocityModified = true;
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }


    public void setExplosionPower( float power) {
        this.explosionPower = (int) Math.max(0, power);

    }


    // === Hit Handling ===
    @Override
    protected void onEntityHit( EntityHitResult hit ) {
        super.onEntityHit(hit);
        if (!this.getWorld().isClient) {
            Entity target = hit.getEntity();
            if (target instanceof LivingEntity living && living != this.getOwner()) {
                living.damage(this.getDamageSources().magic(), 8.0F + explosionPower * 2.0F);
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100 + explosionPower * 20, 1),
                        this.getOwner());
            }
            explodeAndDiscard();
        }
    }

    @Override
    protected void onBlockHit( BlockHitResult blockHitResult ) {
        if (!this.getWorld().isClient) {
            explodeAndDiscard();
        } else {
            // Pretty explosion effect
            for (int i = 0; i < 16; i++) {
                this.getWorld().addParticle(ParticleTypes.EXPLOSION,
                        this.getX(), this.getY(), this.getZ(),
                        (this.random.nextDouble() - 0.5) * 0.5,
                        (this.random.nextDouble() - 0.5) * 0.5,
                        (this.random.nextDouble() - 0.5) * 0.5);
            }
        }
    }

    protected void onCollision( HitResult hitResult ) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            explodeAndDiscard();
        }
        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHitResult) {
            Entity hit = entityHitResult.getEntity();
            Entity owner = this.getOwner();

            if (owner != hit) {
                this.dataTracker.set(HIT, true);
                counter = this.age + 5;
            }
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.dataTracker.set(HIT, true);
            counter = this.age + 5;
        }
    }

    private void explodeAndDiscard() {
        this.getWorld().createExplosion(
                this,
                this.getX(), this.getY(), this.getZ(),
                this.explosionPower,
                false, // disables terrain damage, set true if you want griefing
                World.ExplosionSourceType.MOB

        );

        // Knockback & debuffs
        this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(4.0),
                e -> e instanceof LivingEntity && e != this.getOwner()
        ).forEach(e -> {
            LivingEntity living = (LivingEntity) e;
            living.damage(this.getDamageSources().magic(), 6.0F);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 1), this.getOwner());
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0), this.getOwner());
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0), this.getOwner());

            Vec3d knockback = e.getPos().subtract(this.getPos()).normalize().multiply(1.2);
            e.addVelocity(knockback.x, 0.4, knockback.z);
        });

        this.discard();
    }

    // === Ticking (particles + aura) ===
    private static final int MAX_LIFE_TICKS = 200; // 10 seconds


    @Override
    public void tick() {
        super.tick();

        // Auto-remove if grounded or too old
        if (!this.getWorld().isClient && (this.groundCollision || this.age > MAX_LIFE_TICKS)) {
            this.discard();
            return;
        }

        if (this.dataTracker.get(HIT) && this.age >= counter) {
            this.discard();
        }

        if (!this.getWorld().isClient) {
            // Perform raytrace for collisions
            Vec3d currentPos = this.getPos();
            Vec3d nextPos = currentPos.add(this.getVelocity());

            HitResult hitResult = this.getWorld().raycast(new RaycastContext(
                    currentPos,
                    nextPos,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    this
            ));

            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }

            // Wither aura
            this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(1.25),
                            e -> e instanceof LivingEntity && e != this.getOwner())
                    .forEach(e -> {
                        LivingEntity living = (LivingEntity) e;
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40, 0), this.getOwner());
                        setOnFireFor(3);
                    });
        } else {
            // Client-side smoke trail only
            for (int i = 0; i < 2; i++) {
                this.getWorld().addParticle(ParticleTypes.SMOKE,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.2,
                        0, 0, 0);
            }
        }
    }


    @Override
    public boolean hasNoGravity() {
        return true;
    }

    // === Data tracker ===
    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(HIT, false);
        this.dataTracker.startTracking(CHARGED, false);
    }

    public boolean isCharged() {
        return (Boolean) this.dataTracker.get(CHARGED);
    }

    public void setCharged( boolean charged ) {
        this.dataTracker.set(CHARGED, charged);
    }

    static {
        CHARGED = DataTracker.registerData(InfernalSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    // === NBT Save/Load ===
    @Override
    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("ExplosionPower", this.explosionPower);
    }

    @Override
    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        this.explosionPower = nbt.getInt("ExplosionPower");
    }
}

