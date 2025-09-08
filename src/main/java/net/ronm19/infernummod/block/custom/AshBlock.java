package net.ronm19.infernummod.block.custom;

import net.minecraft.block.Block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.server.world.ServerWorld;
import net.ronm19.infernummod.api.interfaces.NoMobSpawnBlock;
import org.jetbrains.annotations.Nullable;

public class AshBlock extends Block implements NoMobSpawnBlock {

    public AshBlock(Settings settings) {
        super(settings);
    }
    public static DirectionProperty FACING = DirectionProperty.of("direction",
            Direction.UP,
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.DOWN);

    @Nullable
    @Override
    public BlockState getPlacementState( ItemPlacementContext ctx) {
        return getRandomBlockState();
    }

    public BlockState getRandomBlockState() {
        return this.getDefaultState().with(FACING, getRandomDirection());
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    private Direction getRandomDirection() {
        Direction[] dirs = new Direction[] {
                Direction.UP,
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST,
                Direction.DOWN
        };

        return dirs[Random.create().nextBetween(0, dirs.length-1)];
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