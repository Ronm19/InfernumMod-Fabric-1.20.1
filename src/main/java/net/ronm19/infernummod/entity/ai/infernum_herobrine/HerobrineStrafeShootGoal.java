package net.ronm19.infernummod.entity.ai.infernum_herobrine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import net.ronm19.infernummod.entity.custom.InfernumHerobrineEntity;

import java.util.EnumSet;

public class HerobrineStrafeShootGoal extends Goal {

    private final InfernumHerobrineEntity herobrine;
    private LivingEntity target;
    private int strafeDirTimer;      // Timer to change strafing direction
    private boolean clockwise = true;

    public HerobrineStrafeShootGoal(InfernumHerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.target = this.herobrine.getTarget();
        return this.target != null && this.target.isAlive()
                && this.herobrine.squaredDistanceTo(this.target) < 100; // starts when within ~10 blocks
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void start() {
        this.strafeDirTimer = 0;
    }

    @Override
    public void tick() {
        if (target == null) return;

        double distSq = herobrine.squaredDistanceTo(target);

        // Look at the target
        herobrine.getLookControl().lookAt(target, 30.0F, 30.0F);

        // Switch direction every ~2 seconds
        if (--strafeDirTimer <= 0) {
            strafeDirTimer = 40 + herobrine.getRandom().nextInt(40); // 2–4 seconds
            clockwise = !clockwise;
        }

        // Strafing logic
        Vec3d toTarget = new Vec3d(
                target.getX() - herobrine.getX(),
                0,
                target.getZ() - herobrine.getZ()
        ).normalize();

        Vec3d strafeDir = clockwise
                ? new Vec3d(-toTarget.z, 0, toTarget.x)  // perpendicular left/right
                : new Vec3d(toTarget.z, 0, -toTarget.x);

        double strafeSpeed = herobrine.isEnraged() ? 0.35D : 0.25D; // faster in rage
        Vec3d move = toTarget.multiply(0.15D).add(strafeDir.multiply(strafeSpeed));

        herobrine.getNavigation().startMovingTo(
                herobrine.getX() + move.x,
                herobrine.getY(),
                herobrine.getZ() + move.z,
                1.2D
        );

        // Occasionally fire while strafing
        if (herobrine.age % (herobrine.isEnraged() ? 25 : 45) == 0) {
            herobrine.shootAt(target, 1.0f);
        }
    }

    @Override
    public void stop() {
        this.target = null;
        this.herobrine.getNavigation().stop();
    }
}
