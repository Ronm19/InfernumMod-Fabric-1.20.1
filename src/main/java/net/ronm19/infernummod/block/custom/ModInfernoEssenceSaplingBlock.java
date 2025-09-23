package net.ronm19.infernummod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.ronm19.infernummod.block.ModBlocks;

public class ModInfernoEssenceSaplingBlock extends SaplingBlock {
    public ModInfernoEssenceSaplingBlock( SaplingGenerator generator, Settings settings ) {
        super(generator, settings);
    }

    @Override
    protected boolean canPlantOnTop( BlockState floor, BlockView world, BlockPos pos ) {
        return floor.isOf(ModBlocks.ASH_BLOCK);
    }
}
