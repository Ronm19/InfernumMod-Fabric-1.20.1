package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;

public class AshEggEntity extends ThrownItemEntity {
    public AshEggEntity( EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public AshEggEntity(World world, LivingEntity owner) {
        super(ModEntities.ASH_EGG, owner, world);
    }

    public AshEggEntity(World world, double x, double y, double z) {
        super(ModEntities.ASH_EGG, x, y, z, world);
    }

    public void handleStatus(byte status) {
        if (status == 3) {
            double d = 0.08;

            for(int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(),
                        this.getY(), this.getZ(), ((double)this.random.nextFloat() - (double)0.5F) * 0.08, ((double)this.random.nextFloat()
                                - (double)0.5F) * 0.08, ((double)this.random.nextFloat() - (double)0.5F) * 0.08);
            }
        }

    }

    protected void onEntityHit( EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
    }

    protected void onCollision( HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }

                for(int j = 0; j < i; ++j) {
                    AshChickenEntity ashChickenEntity = (AshChickenEntity) ModEntities.ASH_CHICKEN.create(this.getWorld());
                    if (ashChickenEntity != null) {
                        ashChickenEntity.setBreedingAge(-24000);
                        ashChickenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
                        this.getWorld().spawnEntity(ashChickenEntity);
                    }
                }
            }

            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }

    }

    protected Item getDefaultItem() {
        return ModItems.ASH_EGG;
    }
}