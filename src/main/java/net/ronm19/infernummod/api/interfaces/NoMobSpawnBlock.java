package net.ronm19.infernummod.api.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public interface NoMobSpawnBlock {
    boolean canMobSpawn( BlockState state, WorldAccess world, BlockPos pos, SpawnReason reason );

    boolean canEntitySpawn( BlockState state, WorldView world, BlockPos pos, MobEntity entity );
}
