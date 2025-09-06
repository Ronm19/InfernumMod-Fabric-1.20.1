package net.ronm19.infernummod.item.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.FireProofItem;

public class EmberstoneSwordItem extends SwordItem implements FireProofItem {

    public EmberstoneSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean isFireProof() {
        return true; // Fireproof if dropped in lava
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Always glows
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setOnFireFor(5); // Ignite mobs on hit
        if (attacker.getWorld().getRegistryKey() == World.NETHER && attacker instanceof PlayerEntity player) {
            target.damage(attacker.getDamageSources().playerAttack(player), 2.0F); // Bonus damage in Nether
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && selected) {
            BlockPos pos = player.getBlockPos();
            boolean nearLava = BlockPos.streamOutwards(pos, 5, 2, 5)
                    .anyMatch(p -> world.getBlockState(p).isOf(Blocks.LAVA));

            if (nearLava) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 60, 0)); // +1 damage
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 60, 0)); // Faster attack
            }
        }
    }
}