package net.ronm19.infernummod.world.biome;

import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.*;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.ModEntities;

public class ModBiomes {
    public static final RegistryKey<Biome> INFERNAL = RegistryKey.of(RegistryKeys.BIOME,
            new Identifier(InfernumMod.MOD_ID, "infernal"));

    public static void boostrap( Registerable<Biome> context ) {
        context.register(INFERNAL, infernalBiome(context));
    }


    public static void globalOverworldGeneration( GenerationSettings.LookupBackedBuilder builder ) {
        DefaultBiomeFeatures.addLandCarvers(builder);
        DefaultBiomeFeatures.addAmethystGeodes(builder);
        DefaultBiomeFeatures.addDungeons(builder);
        DefaultBiomeFeatures.addMineables(builder);
        DefaultBiomeFeatures.addSprings(builder);
        DefaultBiomeFeatures.addFrozenLavaSpring(builder);
    }

    public static Biome infernalBiome( Registerable<Biome> context ) {
        // Spawn settings
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.MALFURYX, 50, 2, 6)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.DEMON, 50, 2, 6)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.OBSIDIAN_GHAST, 50, 2, 6)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.INFERNAL_HOARDE, 50, 2, 4)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.INFERNO_ZOMBIE, 50, 2, 4)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.INFERNAL_ZOMBILAGER, 50, 2, 5)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.FLAME_SKELETON, 50, 2, 4)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.INFERNAL_WRAITH, 50, 2, 4)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.INFERNAL_VEX, 50, 2, 5)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.LAVACATOR, 50, 3, 8)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.LAVAGER, 50, 3, 8)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.LAVA_WITCH, 50, 3, 8)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.LAVA_SLIME, 50, 4, 8)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.MAGMA_STRIDER, 50, 2, 7)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.MAGMA_CREEPER, 50, 2, 7)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.EMBER_SERPENT, 50, 2, 7)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.INFERNO_ENDERMAN, 50, 2, 6)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.INFERNAL_BEAST, 50, 1, 4)); // more frequent

        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.PYERLING_WYRN, 50, 2, 6)); // more frequent
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.INFERNAL_EYE, 50, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.INFERNAL_HORSE, 50, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.FLAME_HORSE, 50, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.ASHBONE_HORSE, 50, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.EMBER_HUND, 50, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.VOLCARNIS, 40, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.SCORCHED_WOOLIE, 40, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.ASH_CHICKEN, 40, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.EMBER_BOAR, 70, 3, 9));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.INFERNAL_PHANTOM, 50, 2, 6));

        spawnBuilder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(ModEntities.LAVA_FISH, 50, 2, 6));
        spawnBuilder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(ModEntities.MAGMA_FISH, 50, 2, 6));
        spawnBuilder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(ModEntities.FIRE_FISH, 50, 2, 6));

        spawnBuilder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(ModEntities.MAGMA_DOLPHIN, 50, 2, 6));


        // Terrain generation
        GenerationSettings.LookupBackedBuilder biomeBuilder =
                new GenerationSettings.LookupBackedBuilder(context.getRegistryLookup(RegistryKeys.PLACED_FEATURE),
                        context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER));

        globalOverworldGeneration(biomeBuilder);
        DefaultBiomeFeatures.addDefaultOres(biomeBuilder);
        DefaultBiomeFeatures.addExtraGoldOre(biomeBuilder);
        DefaultBiomeFeatures.addNetherMineables(biomeBuilder);// extra volcanic feel
        // Vegetation — corrupted, minimal// sparse weird plants

        DefaultBiomeFeatures.addDefaultVegetation(biomeBuilder);

        // Build biome
        return new Biome.Builder()
                .precipitation(false)
                .downfall(0.0f)
                .temperature(3.0f) // scorching heat
                .generationSettings(biomeBuilder.build())
                .spawnSettings(spawnBuilder.build())
                .effects((new BiomeEffects.Builder())
                        .waterFogColor(0x5e0b0b)   // richer crimson fog, more visible depth underwater
                        .waterColor(0x8f1e1e)      // vivid molten red on surface water
                        .skyColor(0x2d0707)        // deeper maroon sky to give sunsets a fiery glow
                        .fogColor(0x220606)     // oppressive fog
                        .grassColor(0x5c1010)      // scorched grass
                        .foliageColor(0x3a0b0b)    // dead foliage
                        .moodSound(BiomeMoodSound.CAVE)
                        .music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_CRIMSON_FOREST)) // darker music
                        .build()).build();
    }
}





