package net.ronm19.infernummod.entity.ai.goals.obsidian_ghast;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.random.Random;
import net.ronm19.infernummod.entity.custom.ObsidianGhastEntity;

import java.util.EnumSet;

public class FlyRandomlyGoal extends Goal {
    private final ObsidianGhastEntity obsidianGhastEntity;

    public FlyRandomlyGoal(ObsidianGhastEntity obsidianGhastEntity) {
        this.obsidianGhastEntity = obsidianGhastEntity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public boolean canStart() {
        MoveControl moveControl = this.obsidianGhastEntity.getMoveControl();
        if (!moveControl.isMoving()) {
            return true;
        } else {
            double d = moveControl.getTargetX() - this.obsidianGhastEntity.getX();
            double e = moveControl.getTargetY() - this.obsidianGhastEntity.getY();
            double f = moveControl.getTargetZ() - this.obsidianGhastEntity.getZ();
            double g = d * d + e * e + f * f;
            return g < (double)1.0F || g > (double)3600.0F;
        }
    }

    public boolean shouldContinue() {
        return false;
    }

    public void start() {
        Random random = this.obsidianGhastEntity.getRandom();
        double d = this.obsidianGhastEntity.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double e = this.obsidianGhastEntity.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double f = this.obsidianGhastEntity.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        this.obsidianGhastEntity.getMoveControl().moveTo(d, e, f, (double)1.0F);
    }
}