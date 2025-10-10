package net.ronm19.infernummod.api.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface ScheduledTick {
    // =============== Delayed Ritual Completion ===============
    void scheduledTick( BlockState state, net.minecraft.server.world.ServerWorld world,
                        BlockPos pos, java.util.Random random );
}
