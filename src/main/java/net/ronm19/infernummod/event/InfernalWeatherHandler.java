package net.ronm19.infernummod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.particle.ParticleTypes;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.InfernalLightningEntity;
import net.ronm19.infernummod.world.biome.ModBiomes;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.InfernalLightningEntity;
import net.ronm19.infernummod.world.biome.ModBiomes;

import java.util.List;
import java.util.Random;

public class InfernalWeatherHandler {

    private static final Random random = new Random();

    public static void tickServerWorld(ServerWorld world) {

        // 🌋 check if in Infernal biome
        boolean isInfernalWorld = world.getRegistryKey().getValue().equals(ModBiomes.INFERNAL);

        // only during thunder OR random strikes in Infernal Dimension
        boolean shouldTrigger = (world.isRaining() && world.isThundering())
                || (isInfernalWorld && random.nextInt(600) == 0); // about every 30s

        if (!shouldTrigger) return;

        List<? extends Entity> players = world.getPlayers();
        if (players.isEmpty()) return;

        LivingEntity target = null;

        // 🎯 pick random player + nearby mobs
        Entity randomPlayer = players.get(random.nextInt(players.size()));
        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(
                LivingEntity.class,
                randomPlayer.getBoundingBox().expand(48.0D),
                e -> e.isAlive() && !e.isSpectator()
        );

        if (!nearbyEntities.isEmpty()) {
            target = nearbyEntities.get(random.nextInt(nearbyEntities.size()));
        } else if (randomPlayer instanceof LivingEntity livingPlayer) {
            target = livingPlayer;
        }

        if (target == null) return;

        Vec3d pos = target.getPos();
        double offsetX = (random.nextDouble() - 0.5) * 12.0;
        double offsetZ = (random.nextDouble() - 0.5) * 12.0;
        BlockPos strikePos = new BlockPos((int) (pos.x + offsetX), (int) pos.y, (int) (pos.z + offsetZ));
        int topY = world.getTopY(Heightmap.Type.WORLD_SURFACE, strikePos.getX(), strikePos.getZ());

        // ⚡ pre-strike rumble and particles
        world.playSound(null, strikePos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 2.0f, 0.6f);
        world.spawnParticles(ParticleTypes.SMOKE, strikePos.getX() + 0.5, topY + 2.0, strikePos.getZ() + 0.5,
                25, 0.8, 0.4, 0.8, 0.02);
        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, strikePos.getX() + 0.5, topY + 2.0, strikePos.getZ() + 0.5,
                12, 0.4, 0.3, 0.4, 0.01);

        // 🕒 slight delay for realism
        world.getServer().execute(() -> {
            InfernalLightningEntity lightning = new InfernalLightningEntity(ModEntities.INFERNAL_LIGHTNING, world);
            lightning.refreshPositionAndAngles(strikePos.getX(), topY, strikePos.getZ(), 0.0F, 0.0F);
            world.spawnEntity(lightning);
        });
    }
}

