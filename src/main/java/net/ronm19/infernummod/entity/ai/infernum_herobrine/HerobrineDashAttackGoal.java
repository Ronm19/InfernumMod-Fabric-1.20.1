package net.ronm19.infernummod.entity.ai.infernum_herobrine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import net.ronm19.infernummod.entity.custom.InfernumHerobrineEntity;

import java.util.EnumSet;

public class HerobrineDashAttackGoal extends Goal {

    private final InfernumHerobrineEntity herobrine;
    private LivingEntity target;
    private int dashCooldown = 0;
    private boolean dashing = false;
    private int dashTime = 0;

    public HerobrineDashAttackGoal(InfernumHerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.target = herobrine.getTarget();
        return this.target != null && this.target.isAlive()
                && herobrine.squaredDistanceTo(target) < 36.0; // within 6 blocks
    }

    @Override
    public boolean shouldContinue() {
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        if (target == null) return;

        // Cooldown handling
        if (dashCooldown > 0) dashCooldown--;

        // Face the target
        herobrine.getLookControl().lookAt(target, 40.0F, 40.0F);

        double distSq = herobrine.squaredDistanceTo(target);

        // Start dash if ready and close enough
        if (!dashing && dashCooldown <= 0 && distSq < 16.0) {  // <4 blocks
            startDash();
        }

        // During dash
        if (dashing) {
            dashTime--;
            Vec3d dir = new Vec3d(
                    target.getX() - herobrine.getX(),
                    0,
                    target.getZ() - herobrine.getZ()
            ).normalize();

            double speed = herobrine.isEnraged() ? 1.2D : 0.9D; // dash boost
            herobrine.setVelocity(dir.multiply(speed));
            herobrine.velocityModified = true;

            // End dash
            if (dashTime <= 0) {
                dashing = false;
                dashCooldown = herobrine.isEnraged() ? 60 : 100; // shorter CD when enraged
            }

            // Apply damage if colliding
            if (herobrine.getBoundingBox().intersects(target.getBoundingBox())) {
                herobrine.tryAttack(target);
            }
        }
    }

    private void startDash() {
        dashing = true;
        dashTime = 10; // dash lasts ~0.5s
        herobrine.playSound(
                net.minecraft.sound.SoundEvents.ENTITY_ENDER_DRAGON_FLAP,
                1.0F,
                0.7F + herobrine.getRandom().nextFloat() * 0.3F
        );
    }

    @Override
    public void stop() {
        dashing = false;
        target = null;
    }
}
