package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class ExplosiveFireballEntity extends SmallFireballEntity {
    public ExplosiveFireballEntity( World world, LivingEntity shooter, double x, double y, double z) {
        super(world, shooter, x, y, z);
    }

    @Override
    protected void onEntityHit( EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient) {
            // Make explosion at hit location
            this.getWorld().createExplosion(
                    this,
                    this.getX(), this.getY(), this.getZ(),
                    2.5F, // explosion power (like TNT = 4.0F)
                    World.ExplosionSourceType.MOB
            );

            // Optional: damage entity directly
            Entity target = entityHitResult.getEntity();
            if (target instanceof LivingEntity living) {
                living.damage(this.getDamageSources().fireball(this, this.getOwner()), 6.0F);
                living.setOnFireFor(4);
            }

            this.discard(); // remove projectile
        }
    }

    @Override
    protected void onBlockHit( BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        // ❌ Do NOT explode on blocks
        this.discard();
    }
}
