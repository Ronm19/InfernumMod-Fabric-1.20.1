package net.ronm19.infernummod.entity.ai.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class LookAtTargetGoal extends Goal {
    private final MobEntity mobEntity;

    public LookAtTargetGoal( MobEntity mobEntity) {
        this.mobEntity = mobEntity;
        this.setControls(EnumSet.of(Control.LOOK));
    }

    public boolean canStart() {
        return true;
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        if (this.mobEntity.getTarget() == null) {
            Vec3d vec3d = this.mobEntity.getVelocity();
            this.mobEntity.setYaw(-((float) MathHelper.atan2(vec3d.x, vec3d.z)) * (180F / (float)Math.PI));
            this.mobEntity.bodyYaw = this.mobEntity.getYaw();
        } else {
            LivingEntity livingEntity = this.mobEntity.getTarget();
            double d = (double)64.0F;
            if (livingEntity.squaredDistanceTo(this.mobEntity) < (double)4096.0F) {
                double e = livingEntity.getX() - this.mobEntity.getX();
                double f = livingEntity.getZ() - this.mobEntity.getZ();
                this.mobEntity.setYaw(-((float)MathHelper.atan2(e, f)) * (180F / (float)Math.PI));
                this.mobEntity.bodyYaw = this.mobEntity.getYaw();
            }
        }

    }
}