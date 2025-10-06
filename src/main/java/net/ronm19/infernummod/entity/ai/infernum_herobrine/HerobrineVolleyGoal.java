package net.ronm19.infernummod.entity.ai.infernum_herobrine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.ronm19.infernummod.entity.custom.InfernumHerobrineEntity;

import java.util.EnumSet;

public class HerobrineVolleyGoal extends Goal {

    private final InfernumHerobrineEntity herobrine;
    private int cooldown = 0;

    public HerobrineVolleyGoal(InfernumHerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = herobrine.getTarget();
        return target != null && herobrine.isEnraged() && herobrine.squaredDistanceTo(target) > 144.0; // >12 blocks
    }

    @Override
    public void tick() {
        if (cooldown > 0) { cooldown--; return; }

        LivingEntity target = herobrine.getTarget();
        if (target == null) return;

        herobrine.getWorld().playSound(
                null, herobrine.getBlockPos(),
                SoundEvents.ENTITY_WITHER_SHOOT,
                SoundCategory.HOSTILE, 1.5F, 0.8F + herobrine.getRandom().nextFloat() * 0.2F);

        for (int i = 0; i < 3; i++) {
            double dx = target.getX() - herobrine.getX();
            double dy = target.getEyeY() - herobrine.getEyeY();
            double dz = target.getZ() - herobrine.getZ();

            WitherSkullEntity skull = new WitherSkullEntity(herobrine.getWorld(), herobrine, dx, dy, dz);
            skull.setPos(herobrine.getX(), herobrine.getEyeY() - 0.2, herobrine.getZ());
            skull.setVelocity(dx, dy, dz, 1.4F, 0.02F);
            skull.setCharged(true);
            herobrine.getWorld().spawnEntity(skull);
        }

        cooldown = 100; // 5s between volleys
    }
}
