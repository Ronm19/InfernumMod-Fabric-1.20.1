package net.ronm19.infernummod.entity.ai.infernum_herobrine;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.ronm19.infernummod.entity.custom.InfernumHerobrineEntity;

import java.util.EnumSet;
import java.util.List;

public class HerobrineFlameWaveGoal extends Goal {

    private final InfernumHerobrineEntity herobrine;
    private int cooldown = 0;

    // Configurable
    private static final int MAX_COOLDOWN = 120;       // ~6 seconds between uses
    private static final double RANGE = 6.0D;          // distance of the flame wave
    private static final double CONE_ANGLE = 70.0;     // degrees for cone width
    private static final float DAMAGE = 3.5F;          // 1.75 hearts damage
    private static final float KNOCKBACK = 0.5F;

    public HerobrineFlameWaveGoal( InfernumHerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // Early phase only
        if (herobrine.isEnraged()) return false;

        var target = herobrine.getTarget();
        if (target == null || !target.isAlive()) return false;

        return herobrine.squaredDistanceTo(target) <= RANGE * RANGE;
    }

    @Override
    public void start() {
        // Face target
        var target = herobrine.getTarget();
        if (target != null) {
            herobrine.getLookControl().lookAt(target, 30.0F, 30.0F);
        }

        // Play charging sound
        herobrine.getWorld().playSound(
                null,
                herobrine.getX(), herobrine.getY(), herobrine.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE,
                SoundCategory.HOSTILE,
                1.3F, 0.7F
        );

        // Launch flame wave effect
        if (herobrine.getWorld() instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 40; i++) {
                double distance = (i / 6.0);  // spread wave forward
                Vec3d forward = herobrine.getRotationVec(1.0F).normalize();
                double spread = (herobrine.getRandom().nextDouble() - 0.5) * 1.2;
                serverWorld.spawnParticles(
                        ParticleTypes.FLAME,
                        herobrine.getX() + forward.x * distance + spread,
                        herobrine.getY() + 0.3,
                        herobrine.getZ() + forward.z * distance + spread,
                        1, 0, 0, 0, 0.02
                );
            }

            // Burn sound
            herobrine.getWorld().playSound(
                    null,
                    herobrine.getX(), herobrine.getY(), herobrine.getZ(),
                    SoundEvents.BLOCK_LAVA_EXTINGUISH,
                    SoundCategory.HOSTILE,
                    1.0F, 0.6F
            );

            // Damage players in cone
            List<PlayerEntity> players = serverWorld.getEntitiesByClass(
                    PlayerEntity.class,
                    herobrine.getBoundingBox().expand(RANGE),
                    p -> p.isAlive()
            );

            Vec3d look = herobrine.getRotationVec(1.0F).normalize();

            for (PlayerEntity player : players) {
                Vec3d dir = player.getPos().subtract(herobrine.getPos()).normalize();
                double angle = Math.toDegrees(Math.acos(look.dotProduct(dir)));

                if (angle <= CONE_ANGLE / 2) {
                    player.damage(herobrine.getDamageSources().mobAttack(herobrine), DAMAGE);
                    player.setOnFireFor(3);
                    Vec3d knock = dir.multiply(KNOCKBACK);
                    player.addVelocity(knock.x, 0.3, knock.z);
                }
            }
        }

        cooldown = MAX_COOLDOWN;
    }

    @Override
    public boolean shouldContinue() {
        return false; // one-shot attack
    }
}