package net.ronm19.infernummod.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.ronm19.infernummod.world.ModPlacedFeatures;
import net.ronm19.infernummod.world.biome.ModBiomes;

public class ModTreeGeneration {
    public static void generateTrees() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(ModBiomes.INFERNAL),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.INFERNO_ESSENCE_PLACED_KEY);
    }
}
