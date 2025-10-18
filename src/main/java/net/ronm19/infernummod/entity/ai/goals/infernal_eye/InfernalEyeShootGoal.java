package net.ronm19.infernummod.entity.ai.goals.infernal_eye;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvents;
import net.ronm19.infernummod.entity.custom.InfernalEyeEntity;

import java.util.EnumSet;

public class InfernalEyeShootGoal extends Goal {
    private final InfernalEyeEntity eye;
    private int cooldown;

    public InfernalEyeShootGoal(InfernalEyeEntity eye) {
        this.eye = eye;
        this.setControls(EnumSet.of(Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.eye.getTarget() != null;
    }

    @Override
    public void stop() {
        this.cooldown = 0;
        this.eye.setShooting(false);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.eye.getTarget();
        if (target == null) return;

        this.eye.getLookControl().lookAt(target, 30.0F, 30.0F);

        double maxDistSq = 64.0 * 64.0; // 64 block attack range
        if (this.eye.squaredDistanceTo(target) < maxDistSq && this.eye.canSee(target)) {
            ++this.cooldown;

            if (this.cooldown == 10) {
                // charging sound
                eye.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F);
                eye.setShooting(true);
            }

            if (this.cooldown == 30) {
                eye.shootAt(target, 1.0F); // <- calls your shootAt method
                this.cooldown = -40; // wait before next shot
                eye.setShooting(false);
            }
        } else if (this.cooldown > 0) {
            --this.cooldown;
        }
    }
}
