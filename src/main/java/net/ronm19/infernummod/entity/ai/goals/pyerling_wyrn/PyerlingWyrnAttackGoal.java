package net.ronm19.infernummod.entity.ai.goals.pyerling_wyrn;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Custom attack goal for the Pyerling Wyrm
 * Uses ProjectileAttackGoal logic, with optional rapid-fire bursts.
 */
public class PyerlingWyrnAttackGoal extends ProjectileAttackGoal {

    private final RangedAttackMob wyrm; // must implement RangedAttackMob
    private final MobEntity mob;        // convenience cast
    private int attackDelay = 10;
    private int ticksUntilNextAttack = 10;
    private boolean shouldCountTillNextAttack = false;

    public PyerlingWyrnAttackGoal(RangedAttackMob wyrm, double speed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange) {
        super(wyrm, speed, minIntervalTicks, maxIntervalTicks, maxShootRange);
        this.wyrm = wyrm;
        this.mob = (MobEntity) wyrm;
    }

    @Override
    public void tick() {
        super.tick();

        if (shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }

        // Rapid-fire machine gun logic
        LivingEntity target = mob.getTarget();
        if (target != null && target.isAlive()) {
            double distSq = mob.squaredDistanceTo(target);
            if (distSq < 16.0D) { // within 4 blocks
                float power = MathHelper.clamp((float) Math.sqrt(distSq) / 10.0F, 0.1F, 1.0F);
                wyrm.shootAt(target, power); // valid RangedAttackMob call
            }
        }
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 10;
        ticksUntilNextAttack = 10;
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity enemy) {
        return this.mob.distanceTo(enemy) <= 2f; // adjust as needed
    }

    private void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getTickCount(attackDelay);
    }

    private boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    private boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    private void performAttack(LivingEntity enemy) {
        this.resetAttackCooldown();
        wyrm.shootAt(enemy, 1.0f); // use standard ranged attack with full power
    }

    @Override
    public void stop() {
        this.mob.setAttacking(false);
        super.stop();
    }
}
