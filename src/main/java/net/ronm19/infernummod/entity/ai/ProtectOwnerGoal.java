package net.ronm19.infernummod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.ronm19.infernummod.entity.custom.EmberHundEntity;

public class ProtectOwnerGoal extends Goal {
    private final TameableEntity tameable;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public ProtectOwnerGoal( TameableEntity tameable) {
        super();
        this.tameable = tameable;
    }

    @Override
    public boolean canStart() {
        if (!tameable.isTamed() || tameable.isLiving()) {
            return false;
        }
        LivingEntity owner = tameable.getOwner();
        if (owner == null) {
            return false;
        }
        this.attacker = owner.getAttacker();
        int i = owner.getLastAttackedTime();
        return i != this.lastAttackedTime && this.canStart();
    }

    @Override
    public void start() {
        this.attacker.getTargetingMargin();
        LivingEntity owner = tameable.getOwner();
        if (owner != null) {
            this.lastAttackedTime = owner.getLastAttackedTime();
        }
        super.start();
    }
}
