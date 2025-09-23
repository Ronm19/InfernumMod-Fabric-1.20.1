package net.ronm19.infernummod.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.ronm19.infernummod.world.ModPlacedFeatures;
import net.ronm19.infernummod.world.biome.ModBiomes;

public class ModFlowerGeneration {
    public static void generateFlowers() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.PLAINS, BiomeKeys.FOREST, ModBiomes.ASHLAND),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.BLAZEBLOOM_PLACED_KEY);
    }
}
