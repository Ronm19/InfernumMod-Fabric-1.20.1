package net.ronm19.infernummod.world.tree;

import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.ronm19.infernummod.world.ModConfiguredFeatures;
import org.jetbrains.annotations.Nullable;

public class InfernoEssenceSaplingGenerator extends SaplingGenerator {
    @Override
    protected @Nullable RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature( Random random, boolean bees ) {
        return ModConfiguredFeatures.INFERNO_ESSENCE_KEY;
    }
}
