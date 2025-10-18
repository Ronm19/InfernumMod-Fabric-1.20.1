package net.ronm19.infernummod.event;

import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.InfernalLightningEntity;

import java.util.Random;

public class InfernalWeatherHandler {
    private static final Random random = new Random();

    public static void tickServerWorld(ServerWorld world) {
        if (world.isRaining() && world.isThundering()) {
            if (random.nextInt(2500) == 0) { // 1/2500 chance per tick (~every 2 min)
                BlockPos pos = world.getRandomPosInChunk(world.getRandom().nextInt(16), 0, world.getRandom().nextInt(16), 15);
                InfernalLightningEntity lightning = new InfernalLightningEntity(
                        ModEntities.INFERNAL_LIGHTNING, world
                );

                lightning.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
                world.spawnEntity(lightning);
            }
        }
    }
}
