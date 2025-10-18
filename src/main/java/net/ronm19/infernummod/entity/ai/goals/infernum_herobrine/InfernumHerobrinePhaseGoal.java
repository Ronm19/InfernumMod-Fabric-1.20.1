// InfernumHerobrinePhaseGoal.java
package net.ronm19.infernummod.entity.ai.goals.infernum_herobrine;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.ronm19.infernummod.entity.custom.InfernumHerobrineEntity;

import java.util.EnumSet;

public class InfernumHerobrinePhaseGoal extends Goal {

    private final InfernumHerobrineEntity herobrine;
    private boolean enragedTriggered = false;
    private int teleportCooldown = 0;
    private int fireTrailTimer = 0;

    private static final double BASE_SPEED = 0.19D;
    private static final double ENRAGED_SPEED = 0.26D;

    public InfernumHerobrinePhaseGoal(InfernumHerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void tick() {
        if (!(herobrine.getWorld() instanceof ServerWorld serverWorld)) return;

        // === Trigger enraged phase ===
        if (herobrine.isEnraged() && !enragedTriggered) {
            enragedTriggered = true;

            EntityAttributeInstance speed = herobrine.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (speed != null) speed.setBaseValue(ENRAGED_SPEED);

            serverWorld.playSound(null,
                    herobrine.getX(), herobrine.getY(), herobrine.getZ(),
                    SoundEvents.ENTITY_WITHER_SPAWN,
                    SoundCategory.HOSTILE, 2.0F, 0.6F);

            // burst particles
            for (int i = 0; i < 80; i++) {
                double ox = (herobrine.getRandom().nextDouble() - 0.5) * herobrine.getWidth() * 2.5;
                double oy = herobrine.getRandom().nextDouble() * herobrine.getHeight() + 0.2;
                double oz = (herobrine.getRandom().nextDouble() - 0.5) * herobrine.getWidth() * 2.5;
                serverWorld.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        herobrine.getX() + ox,
                        herobrine.getY() + oy,
                        herobrine.getZ() + oz,
                        1, 0, 0, 0, 0.1
                );
            }
        }

        // === Fire trail every 5 ticks while enraged ===
        if (herobrine.isEnraged()) {
            fireTrailTimer++;
            if (fireTrailTimer >= 5) {
                fireTrailTimer = 0;
                BlockPos beneath = herobrine.getBlockPos().down();

                if (serverWorld.getBlockState(beneath).isAir()) {
                    serverWorld.setBlockState(beneath, Blocks.FIRE.getDefaultState());
                }

                // Damage players stepping in the fire
                serverWorld.getEntitiesByClass(PlayerEntity.class,
                        herobrine.getBoundingBox().expand(1.5),
                        p -> true).forEach(player -> {
                    BlockPos feet = player.getBlockPos().down();
                    if (serverWorld.getBlockState(feet).isOf(Blocks.FIRE)) {
                        player.damage(herobrine.getDamageSources().magic(), 2.0F);
                        player.setOnFireFor(2);
                    }
                });
            }

            // === Teleport flicker every 7 seconds ===
            if (teleportCooldown-- <= 0) {
                var target = herobrine.getTarget();
                if (target != null && herobrine.squaredDistanceTo(target) > 9.0) {
                    // pick a target offset near the player
                    double tx = target.getX() + (herobrine.getRandom().nextDouble() - 0.5) * 4;
                    double ty = target.getY();
                    double tz = target.getZ() + (herobrine.getRandom().nextDouble() - 0.5) * 4;

                    BlockPos safe = findSafeLanding(serverWorld, new BlockPos((int) tx, (int) ty, (int) tz));
                    if (safe != null) {
                        herobrine.requestTeleport(safe.getX() + 0.5, safe.getY() + 1, safe.getZ() + 0.5);
                        herobrine.getLookControl().lookAt(target, 30.0F, 30.0F);

                        serverWorld.playSound(null,
                                herobrine.getX(), herobrine.getY(), herobrine.getZ(),
                                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                                SoundCategory.HOSTILE, 1.5F, 0.8F);
                    }
                }
                teleportCooldown = 140; // 7s
            }
        } else if (enragedTriggered && !herobrine.isEnraged()) {
            // Reset if healed above 50%
            EntityAttributeInstance speed = herobrine.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (speed != null) speed.setBaseValue(BASE_SPEED);
            enragedTriggered = false;
        }
    }

    /**
     * Searches for a safe ground spot near the target position.
     * Safe = solid ground + at least 2 blocks of air above.
     */
    private BlockPos findSafeLanding(ServerWorld world, BlockPos center) {
        // Try up to 8 random offsets for a valid location
        for (int attempt = 0; attempt < 8; attempt++) {
            double ox = (world.getRandom().nextDouble() - 0.5) * 6;
            double oz = (world.getRandom().nextDouble() - 0.5) * 6;
            BlockPos.Mutable pos = new Mutable(center.getX() + (int) ox, center.getY() + 3, center.getZ() + (int) oz);

            // scan down to ground
            while (pos.getY() > world.getBottomY() + 2 && world.isAir(pos)) {
                pos.move(0, -1, 0);
            }

            BlockState ground = world.getBlockState(pos);
            if (!ground.isAir() && ground.getCollisionShape(world, pos).isEmpty() == false) {
                // check 2 blocks above are air
                if (world.getBlockState(pos.up()).isAir() && world.getBlockState(pos.up(2)).isAir()) {
                    return pos;
                }
            }
        }
        return null; // no valid landing spot found
    }
}
