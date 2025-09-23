package net.ronm19.infernummod.item.custom;

import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.ronm19.infernummod.util.ModTags;

public class InfernumPaxelItem extends MiningToolItem {
    public InfernumPaxelItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings ) {
        super(attackDamage, attackSpeed, material, ModTags.Blocks.INFERNUM_PAXEL_MINEABLE, settings);
    }
}
