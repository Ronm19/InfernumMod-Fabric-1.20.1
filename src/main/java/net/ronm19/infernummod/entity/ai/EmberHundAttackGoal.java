package net.ronm19.infernummod.entity.ai;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Hand;
import net.ronm19.infernummod.entity.custom.EmberHundEntity;

public class EmberHundAttackGoal extends MeleeAttackGoal {
    private final EmberHundEntity entity;
    private int attackDelay = 10;
    private int ticksUntilNextAttack = 10;
    private boolean shouldCountTillNextAttack = false;

    public EmberHundAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenIdle) {
        super(mob, speed, pauseWhenIdle);
        this.entity = (EmberHundEntity) mob;
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 10;
        ticksUntilNextAttack = 10;
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity target) {
        return this.entity.distanceTo(target) <= 2.0F; // bite range
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getTickCount(attackDelay);
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected void performAttack(LivingEntity target) {
        this.resetAttackCooldown();
        this.mob.swingHand(Hand.MAIN_HAND);

        if (this.mob.tryAttack(target)) {
            // 🔥 Ignite the target when bite hits
            target.setOnFireFor(4); // 4 seconds of fire, tweak as needed
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }

    @Override
    public void attack(LivingEntity target) {
        if (isEnemyWithinAttackDistance(target)) {
            shouldCountTillNextAttack = true;

            if (isTimeToStartAttackAnimation()) {
                entity.setAttacking(true);
            }

            if (isTimeToAttack()) {
                this.mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());
                performAttack(target);
            }
        } else {
            resetAttackCooldown();
            shouldCountTillNextAttack = false;
            entity.setAttacking(false);
            entity.attackAnimationTimeout = 0;
        }
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }
}
