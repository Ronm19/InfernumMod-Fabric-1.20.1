package net.ronm19.infernummod.entity.ai.goals.infernum_herobrine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.EnumSet;

public class InfernumHerobrineAttackGoal extends Goal {
    private final MobEntity mob;
    private LivingEntity target;
    private final World world;

    private int cooldown = 0; // ticks until next attack
    private final int fireballCooldown = 40; // 2s
    private final int skullCooldown = 60;    // 3s (stronger, slower)

    public InfernumHerobrineAttackGoal(MobEntity mob) {
        this.mob = mob;
        this.world = mob.getWorld();
        this.setControls(EnumSet.of(Control.TARGET, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.target = mob.getTarget();
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        mob.getLookControl().lookAt(target, 30.0F, 30.0F);

        if (--cooldown <= 0) {
            if (isEnraged()) {
                shootWitherSkull(target);
                cooldown = skullCooldown;
            } else {
                shootFireball(target);
                cooldown = fireballCooldown;
            }
        }
    }

    private boolean isEnraged() {
        return mob.getHealth() <= mob.getMaxHealth() * 0.5F;
    }

    private void shootFireball(LivingEntity target) {
        double dX = target.getX() - this.mob.getX();
        double dY = target.getBodyY(0.5) - this.mob.getEyeY();
        double dZ = target.getZ() - this.mob.getZ();

        SmallFireballEntity fireball = new SmallFireballEntity(
                world,
                mob,
                dX + mob.getRandom().nextGaussian() * 0.05,
                dY,
                dZ + mob.getRandom().nextGaussian() * 0.05
        );

        fireball.setPos(mob.getX(), mob.getEyeY() + 0.5, mob.getZ());
        world.spawnEntity(fireball);

        mob.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F,
                0.8F + mob.getRandom().nextFloat() * 0.4F);
    }

    private void shootWitherSkull(LivingEntity target) {
        double dX = target.getX() - this.mob.getX();
        double dY = target.getBodyY(0.5) - this.mob.getEyeY();
        double dZ = target.getZ() - this.mob.getZ();

        WitherSkullEntity skull = new WitherSkullEntity(world, mob, dX, dY, dZ);

        // Spawn slightly forward to avoid self-collisions
        double forward = 1.5;
        skull.setPos(
                mob.getX() + mob.getRotationVector().x * forward,
                mob.getEyeY(),
                mob.getZ() + mob.getRotationVector().z * forward
        );

        skull.setCharged(true);
        world.spawnEntity(skull);

        mob.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 1.0F,
                0.9F + mob.getRandom().nextFloat() * 0.2F);
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }
}