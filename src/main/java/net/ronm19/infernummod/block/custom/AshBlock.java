package net.ronm19.infernummod.block.custom;

import net.minecraft.block.Block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.server.world.ServerWorld;
import net.ronm19.infernummod.api.interfaces.NoMobSpawnBlock;

public class AshBlock extends Block implements NoMobSpawnBlock {

    public AshBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMobSpawn( BlockState state, WorldAccess world, BlockPos pos, SpawnReason reason ) {
        // Prevent mobs from spawning directly on the block
        return false;
    }

    @Override
    public boolean canEntitySpawn( BlockState state, WorldView world, BlockPos pos, MobEntity entity ) {
        // Prevent mobs from spawning within 5 blocks of any Ash Block
        Box spawnZone = new Box(pos).expand(5);
        for (BlockPos nearby : BlockPos.stream(spawnZone).map(BlockPos::toImmutable).toList()) {
            if (world.getBlockState(nearby).getBlock() instanceof AshBlock) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextFloat() <= 0.05F) { // 5% chance
            BlockPos above = pos.up();
            if (world.isAir(above)) {
                // Placeholder for Ash Bloom spawn
                // world.setBlockState(above, ModBlocks.ASH_BLOOM.getDefaultState());
            }
        }
    }
}