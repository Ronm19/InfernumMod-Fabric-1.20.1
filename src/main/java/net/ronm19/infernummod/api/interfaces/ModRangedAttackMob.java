package net.ronm19.infernummod.api.interfaces;

import net.minecraft.entity.LivingEntity;

public interface ModRangedAttackMob {
    void performRangedAttack( LivingEntity target, float pullProgress );
}
