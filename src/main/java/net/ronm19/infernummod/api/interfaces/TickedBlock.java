package net.ronm19.infernummod.api.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface TickedBlock {
    // === Potted version aura effect ===
    void randomDisplayTick( BlockState state, World world, BlockPos pos, java.util.Random random );
}
