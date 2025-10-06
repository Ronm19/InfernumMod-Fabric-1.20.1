package net.ronm19.infernummod.entity.ai.infernal_hoarde;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Hand;
import net.ronm19.infernummod.api.interfaces.AttackingEntity;
import net.ronm19.infernummod.entity.custom.InfernalHoardeEntity;

public class InfernalHoardeAttackGoal extends MeleeAttackGoal implements AttackingEntity {
    private final InfernalHoardeEntity entity;
    private int attackDelay = 11;
    private int ticksUntilNextAttack = 11;
    private boolean shouldCountTillNextAttack = false;


    public InfernalHoardeAttackGoal( PathAwareEntity mob, double speed, boolean pauseWhenMobIdle ) {
        super(mob, speed, pauseWhenMobIdle);
        entity = ((InfernalHoardeEntity) mob);
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 11;
        ticksUntilNextAttack = 11;
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy) {
        return this.entity.distanceTo(pEnemy) <= 2f; // TODO
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getTickCount(attackDelay * 1);
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected void performAttack( LivingEntity pEnemy) {
        this.resetAttackCooldown();
        this.mob.swingHand(Hand.MAIN_HAND);
        this.mob.tryAttack(pEnemy);
    }

    @Override
    public void attack( LivingEntity pEnemy ) {
        if (isEnemyWithinAttackDistance(pEnemy)) {
            shouldCountTillNextAttack = true;

            if(isTimeToStartAttackAnimation()) {
                entity.setAttacking(true);
            }

            if(isTimeToAttack()) {
                this.mob.getLookControl().lookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                performAttack(pEnemy);
            }
        } else {
            resetAttackCooldown();
            shouldCountTillNextAttack = false;
            entity.setAttacking(false);
            entity.attackAnimationTimeout = 0;
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
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }
}
