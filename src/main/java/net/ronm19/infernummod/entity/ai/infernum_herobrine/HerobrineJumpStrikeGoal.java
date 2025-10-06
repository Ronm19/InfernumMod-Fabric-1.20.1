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

public class HerobrineJumpStrikeGoal extends Goal {

    private final InfernumHerobrineEntity herobrine;
    private int cooldown = 0;
    private boolean isJumping = false;

    // Configurable
    private static final int MAX_COOLDOWN = 100;      // ~5 seconds between jumps
    private static final double JUMP_RANGE = 6.0D;    // max distance to trigger leap
    private static final double LAND_RADIUS = 3.0D;   // AoE damage radius
    private static final float LAND_DAMAGE = 5.0F;    // 2.5 hearts damage
    private static final float KNOCKBACK_STRENGTH = 0.7F;

    public HerobrineJumpStrikeGoal(InfernumHerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // Only during early phase (above 50% HP)
        if (herobrine.isEnraged()) return false;

        // Need a valid nearby target
        var target = herobrine.getTarget();
        if (target == null || !target.isAlive()) return false;

        return herobrine.squaredDistanceTo(target) <= JUMP_RANGE * JUMP_RANGE;
    }

    @Override
    public void start() {
        var target = herobrine.getTarget();
        if (target != null) {
            // Face target
            herobrine.getLookControl().lookAt(target, 30.0F, 30.0F);

            // Play leap sound
            herobrine.getWorld().playSound(
                    null,
                    herobrine.getX(), herobrine.getY(), herobrine.getZ(),
                    SoundEvents.ENTITY_IRON_GOLEM_ATTACK,
                    SoundCategory.HOSTILE,
                    1.2F, 0.8F
            );

            // Jump motion toward target
            Vec3d dir = new Vec3d(
                    target.getX() - herobrine.getX(),
                    0,
                    target.getZ() - herobrine.getZ()
            ).normalize();

            herobrine.addVelocity(dir.x * 0.5, 0.6, dir.z * 0.5);
            isJumping = true;
        }
    }

    @Override
    public void tick() {
        if (isJumping && herobrine.isOnGround()) {
            // Landing impact
            if (herobrine.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        herobrine.getX(), herobrine.getY(), herobrine.getZ(),
                        25, 1.0, 0.3, 1.0, 0.05
                );
                herobrine.getWorld().playSound(
                        null,
                        herobrine.getX(), herobrine.getY(), herobrine.getZ(),
                        SoundEvents.ENTITY_GENERIC_EXPLODE,
                        SoundCategory.HOSTILE,
                        1.4F, 0.7F
                );

                // AoE damage + knockback
                List<PlayerEntity> players = serverWorld.getEntitiesByClass(
                        PlayerEntity.class,
                        herobrine.getBoundingBox().expand(LAND_RADIUS),
                        p -> p.isAlive()
                );

                for (PlayerEntity player : players) {
                    player.damage(herobrine.getDamageSources().mobAttack(herobrine), LAND_DAMAGE);
                    Vec3d knockDir = player.getPos().subtract(herobrine.getPos()).normalize().multiply(KNOCKBACK_STRENGTH);
                    player.addVelocity(knockDir.x, 0.3, knockDir.z);
                    player.setOnFireFor(2);
                }
            }

            // Reset after landing
            isJumping = false;
            cooldown = MAX_COOLDOWN;
        }
    }

    @Override
    public boolean shouldContinue() {
        return false; // single action per trigger
    }
}
