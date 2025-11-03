package net.ronm19.infernummod.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.world.Heightmap;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.*;
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
                SpawnGroup.MONSTER, ModEntities.OBSIDIAN_GHAST, 20, 3, 4);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.INFERNO_ZOMBIE, 40, 5, 9);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.INFERNAL_ZOMBILAGER, 40, 5, 9);


        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.FLAME_SKELETON, 30, 2, 6);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.INFERNAL_VEX, 30, 2, 5);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.INFERNAL_WRAITH, 30, 2, 5);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.MAGMA_SPIDER, 30, 3, 8);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.LAVACATOR, 30, 3, 6);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.LAVAGER, 30, 3, 6);


        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.LAVA_WITCH, 40, 5, 9);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.LAVA_SLIME, 40, 3, 8);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.MAGMA_CREEPER, 30, 2, 8);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.MAGMA_STRIDER, 30, 3, 6);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.EMBER_SERPENT, 30, 3, 6);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.INFERNO_ENDERMAN, 20, 2, 6);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.MONSTER, ModEntities.INFERNAL_BEAST, 10, 2, 3);



        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.PYERLING_WYRN, 50, 2, 6);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.INFERNAL_EYE, 50, 2, 7);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.INFERNAL_HORSE, 50, 3, 8);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.ASHBONE_HORSE, 50, 2, 7);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.FLAME_HORSE, 20, 2, 4);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.EMBER_HUND, 50, 3, 5);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.VOLCARNIS, 50, 2, 6);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.SCORCHED_WOOLIE, 50, 3, 6);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.ASH_CHICKEN, 50, 3, 8);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.EMBER_BOAR, 70, 3, 9);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.INFERNAL_PHANTOM, 50, 2, 5);

        BiomeModifications.addSpawn( context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.CREATURE, ModEntities.MAGMA_BEAR, 20, 2, 6);


        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.WATER_AMBIENT, ModEntities.LAVA_FISH, 30, 3, 6);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.WATER_AMBIENT, ModEntities.MAGMA_FISH, 28, 3, 6);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.WATER_AMBIENT, ModEntities.FIRE_FISH, 25, 3, 8);

        BiomeModifications.addSpawn(context -> context.getBiomeKey().getValue().getNamespace().equals(InfernumMod.MOD_ID),
                SpawnGroup.WATER_CREATURE, ModEntities.MAGMA_DOLPHIN, 25, 4, 8);




        SpawnRestriction.register(ModEntities.DEMON, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.MALFURYX, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.INFERNAL_HOARDE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.INFERNAL_WRAITH, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.INFERNAL_BEAST, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.OBSIDIAN_GHAST, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.WORLD_SURFACE_WG, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.INFERNO_ZOMBIE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.INFERNAL_ZOMBILAGER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.INFERNAL_VEX, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.LAVACATOR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.LAVAGER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.LAVA_WITCH, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.LAVA_SLIME, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, LavaSlimeEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.MAGMA_SPIDER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.MAGMA_STRIDER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.MAGMA_CREEPER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.EMBER_SERPENT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.FLAME_SKELETON, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.INFERNO_ENDERMAN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);

        SpawnRestriction.register(ModEntities.PYERLING_WYRN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.INFERNAL_EYE, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.INFERNAL_HORSE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.FLAME_HORSE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.ASHBONE_HORSE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.INFERNAL_PHANTOM, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.EMBER_HUND, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.VOLCARNIS, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.SCORCHED_WOOLIE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.ASH_CHICKEN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.EMBER_BOAR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.MAGMA_BEAR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canMobSpawn);


        SpawnRestriction.register(ModEntities.LAVA_FISH, SpawnRestriction.Location.IN_WATER, Heightmap.Type.OCEAN_FLOOR, SchoolingFishEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.MAGMA_FISH, SpawnRestriction.Location.IN_WATER, Heightmap.Type.OCEAN_FLOOR, SchoolingFishEntity::canMobSpawn);
        SpawnRestriction.register(ModEntities.FIRE_FISH, SpawnRestriction.Location.IN_WATER, Heightmap.Type.OCEAN_FLOOR, SchoolingFishEntity::canMobSpawn);

        SpawnRestriction.register(ModEntities.MAGMA_DOLPHIN, SpawnRestriction.Location.IN_WATER, Heightmap.Type.OCEAN_FLOOR, MagmaDolphinEntity::canMobSpawn);



    }
}
