package net.ronm19.infernummod.entity.ai.goals.infernum_herobrine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.ronm19.infernummod.entity.custom.InfernumHerobrineEntity;

import java.util.EnumSet;

public class HerobrineKnockbackSwipeGoal extends Goal {

    private final InfernumHerobrineEntity herobrine;
    private int cooldown = 0;

    public HerobrineKnockbackSwipeGoal(InfernumHerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = herobrine.getTarget();
        return target != null && herobrine.squaredDistanceTo(target) < 9.0; // within 3 blocks
    }

    @Override
    public void tick() {
        if (cooldown > 0) { cooldown--; return; }

        LivingEntity target = herobrine.getTarget();
        if (target != null && herobrine.squaredDistanceTo(target) < 9.0) {
            // deal damage
            target.damage(herobrine.getDamageSources().mobAttack(herobrine), 6.0F); // ~3 hearts
            // knockback
            double dx = target.getX() - herobrine.getX();
            double dz = target.getZ() - herobrine.getZ();
            target.takeKnockback(1.2F, dx, dz);

            herobrine.getWorld().playSound(
                    null, herobrine.getBlockPos(),
                    SoundEvents.ENTITY_IRON_GOLEM_ATTACK,
                    SoundCategory.HOSTILE, 1.2F, 0.9F
            );

            cooldown = 60; // 3s cooldown
        }
    }
}
