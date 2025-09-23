package net.ronm19.infernummod.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ModInfernumWeaknessSwordItem extends SwordItem {
    public ModInfernumWeaknessSwordItem( ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings ) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit( ItemStack stack, LivingEntity target, LivingEntity attacker ) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1), attacker);
        target.setOnFireFor(5);
        return super.postHit(stack, target, attacker);
    }


}
