package net.ronm19.infernummod.entity.ai;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.math.random.Random;

import java.util.EnumSet;

public class FlyRandomlyGoal extends Goal {
    private final FlyingEntity flyingEntity;

    public FlyRandomlyGoal(FlyingEntity flyingEntity) {
        this.flyingEntity = flyingEntity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public boolean canStart() {
        MoveControl moveControl = this.flyingEntity.getMoveControl();
        if (!moveControl.isMoving()) {
            return true;
        } else {
            double d = moveControl.getTargetX() - this.flyingEntity.getX();
            double e = moveControl.getTargetY() - this.flyingEntity.getY();
            double f = moveControl.getTargetZ() - this.flyingEntity.getZ();
            double g = d * d + e * e + f * f;
            return g < (double)1.0F || g > (double)3600.0F;
        }
    }

    public boolean shouldContinue() {
        return false;
    }

    public void start() {
        Random random = this.flyingEntity.getRandom();
        double d = this.flyingEntity.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double e = this.flyingEntity.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double f = this.flyingEntity.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        this.flyingEntity.getMoveControl().moveTo(d, e, f, (double)1.0F);
    }
}