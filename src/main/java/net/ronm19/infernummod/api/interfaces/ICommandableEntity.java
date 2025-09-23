package net.ronm19.infernummod.api.interfaces;

import net.ronm19.infernummod.entity.ai.custom.CommandState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ai.custom.CommandState;

public interface ICommandableEntity {
    CommandState getCommandState();
    void setCommandState(CommandState state);

    /** Call when switching states to trigger effects */
    default void playModeEffect(World world, BlockPos pos, CommandState state) {
        switch (state) {
            case FOLLOW -> {
                world.playSound(null, pos, SoundEvents.ENTITY_WOLF_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                spawnParticles(world, pos, ParticleTypes.HEART, 6);
            }
            case HOLD -> {
                world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                spawnParticles(world, pos, ParticleTypes.SMOKE, 10);
            }
            case PATROL -> {
                world.playSound(null, pos, SoundEvents.ENTITY_WOLF_HOWL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                spawnParticles(world, pos, ParticleTypes.CLOUD, 12);
            }
        }
    }

    /** Spawns particles in the world */
    default void spawnParticles(World world, BlockPos pos, ParticleEffect type, int count) {
        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < count; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 0.5;
                double offsetY = world.random.nextDouble() * 0.5 + 0.5;
                double offsetZ = (world.random.nextDouble() - 0.5) * 0.5;
                serverWorld.spawnParticles(type,
                        pos.getX() + 0.5 + offsetX,
                        pos.getY() + offsetY,
                        pos.getZ() + 0.5 + offsetZ,
                        1, 0, 0, 0, 0);
            }
        }
    }
}
