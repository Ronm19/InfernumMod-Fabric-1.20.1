package net.ronm19.infernummod.world.biome;

import net.minecraft.client.sound.MusicType;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.world.ModPlacedFeatures;

import static com.ibm.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class ModBiomes {
    public static final RegistryKey<Biome> ASHLAND = RegistryKey.of(RegistryKeys.BIOME,
            new Identifier(InfernumMod.MOD_ID, "ashland"));

    public static void boostrap( Registerable<Biome> context ) {
        context.register(ASHLAND, ashlandBiome(context));
    }


    public static void globalOverworldGeneration( GenerationSettings.LookupBackedBuilder builder ) {
        DefaultBiomeFeatures.addLandCarvers(builder);
        DefaultBiomeFeatures.addAmethystGeodes(builder);
        DefaultBiomeFeatures.addDungeons(builder);
        DefaultBiomeFeatures.addMineables(builder);
        DefaultBiomeFeatures.addSprings(builder);
        DefaultBiomeFeatures.addFrozenLavaSpring(builder);
    }

    public static Biome ashlandBiome( Registerable<Biome> context ) {
        // Spawn settings
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.MALFURYX, 20, 2, 6)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntities.DEMON, 20, 2, 6)); // more frequent
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.BLAZE, 8, 1, 3)); // fiery mobs
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 5, 1, 2)); // creepy ambience
        // Optional: remove passive mobs for a desolate feeling
        // spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 5, 4, 4));

        DefaultBiomeFeatures.addBatsAndMonsters(spawnBuilder);

        // Terrain generation
        GenerationSettings.LookupBackedBuilder biomeBuilder =
                new GenerationSettings.LookupBackedBuilder(context.getRegistryLookup(RegistryKeys.PLACED_FEATURE),
                        context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER));

        globalOverworldGeneration(biomeBuilder);
        DefaultBiomeFeatures.addDefaultOres(biomeBuilder);
        DefaultBiomeFeatures.addExtraGoldOre(biomeBuilder);
        DefaultBiomeFeatures.addNetherMineables(biomeBuilder); // extra volcanic feel

        // Vegetation — corrupted, minimal
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.MUSHROOM_ISLAND_VEGETATION); // sparse weird plants
        DefaultBiomeFeatures.addDefaultMushrooms(biomeBuilder);
        DefaultBiomeFeatures.addDefaultVegetation(biomeBuilder);

        // Build biome
        return new Biome.Builder()
                .precipitation(false)
                .downfall(0.0f)
                .temperature(3.0f) // scorching heat
                .generationSettings(biomeBuilder.build())
                .spawnSettings(spawnBuilder.build())
                .effects((new BiomeEffects.Builder())
                        .waterColor(0x9e0b0f)      // blood-red water
                        .waterFogColor(0x4a0505)   // deep crimson fog
                        .skyColor(0x3d0c0c)        // dark hellish sky
                        .fogColor(0x1a0505)        // oppressive fog
                        .grassColor(0x5c1010)      // scorched grass
                        .foliageColor(0x3a0b0b)    // dead foliage
                        .moodSound(BiomeMoodSound.CAVE)
                        .music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_BASALT_DELTAS)) // darker music
                        .build()).build();
    }
}