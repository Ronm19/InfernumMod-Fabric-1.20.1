package net.ronm19.infernummod.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.Heightmap;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.world.biome.ModBiomes;

public class ModEntitySpawns {
    public static void addSpawns() {

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.DEMON, 50, 1, 3);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.MALFURYX, 50, 1, 3);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.INFERNAL_HOARDE, 50, 1, 4);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.OBSIDIAN_GHAST, 10, 1, 1
        );

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.PYERLING_WYRN, 50, 2, 6);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.INFERNAL_EYE, 50, 2, 7);


        SpawnRestriction.register(ModEntities.DEMON, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.MALFURYX, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.INFERNAL_HOARDE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.OBSIDIAN_GHAST, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);

        SpawnRestriction.register(ModEntities.PYERLING_WYRN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.INFERNAL_EYE, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);

    }
}
