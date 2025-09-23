package net.ronm19.infernummod.world;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.world.tree.custom.InfernoEssenceTrunkPlacer;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_RUBY_ORE_KEY = registerKey("nether_ruby_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DEEPSLATE_NETHER_RUBY_ORE_KEY = registerKey("deepslate_nether_ruby_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_PYROCLAST_ORE_KEY = registerKey("nether_pyroclast_ore");

    public static final RegistryKey<ConfiguredFeature<?, ?>> EMBERSTONE_ORE_KEY = registerKey("emberstone_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> STONE_INFERNIUM_ORE_KEY = registerKey("stone_infernium_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DEEPSLATE_CINDERSTONE_ORE_KEY = registerKey("deepslate_cinderstone_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FIRERITE_ORE_KEY = registerKey("firerite_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DEEPSLATE_FIRERITE_ORE_KEY = registerKey("deepslate_firerite_ore");

    public static final RegistryKey<ConfiguredFeature<?, ?>> INFERNO_ESSENCE_KEY = registerKey("inferno");

    public static final RegistryKey<ConfiguredFeature<?, ?>> BLAZEBLOOM_KEY = registerKey("blazebloom");

    public static void boostrap(Registerable<ConfiguredFeature<?,?>> context) {
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest netherReplaceables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> overworldOres = List.of(
                OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.EMBERSTONE_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.STONE_INFERNIUM_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.FIRERITE_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_CINDERSTONE_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_FIRERITE_ORE.getDefaultState())
        );

        List<OreFeatureConfig.Target> netherOres = List.of(
                OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.NETHER_RUBY_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.DEEPSLATE_NETHER_RUBY_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.NETHER_PYROCLAST_ORE.getDefaultState())
        );

// Configured features
        register(context, EMBERSTONE_ORE_KEY, Feature.ORE,
                new OreFeatureConfig(List.of(
                        OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.EMBERSTONE_ORE.getDefaultState())),
                        8)); // vein size

        register(context, STONE_INFERNIUM_ORE_KEY, Feature.ORE,
                new OreFeatureConfig(List.of(
                        OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.STONE_INFERNIUM_ORE.getDefaultState())),
                        6));

        register(context, DEEPSLATE_CINDERSTONE_ORE_KEY, Feature.ORE,
                new OreFeatureConfig(List.of(
                        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_CINDERSTONE_ORE.getDefaultState())),
                        5));

        register(context, FIRERITE_ORE_KEY, Feature.ORE,
                new OreFeatureConfig(List.of(
                        OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.FIRERITE_ORE.getDefaultState())),
                        6));

        register(context, DEEPSLATE_FIRERITE_ORE_KEY, Feature.ORE,
                new OreFeatureConfig(List.of(
                        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_FIRERITE_ORE.getDefaultState())),
                        4));

        register(context, NETHER_RUBY_ORE_KEY, Feature.ORE,
                new OreFeatureConfig(List.of(
                        OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.NETHER_RUBY_ORE.getDefaultState())),
                        10));

        register(context, DEEPSLATE_NETHER_RUBY_ORE_KEY, Feature.ORE,
                new OreFeatureConfig(List.of(
                        OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.DEEPSLATE_NETHER_RUBY_ORE.getDefaultState())),
                        4));

        register(context, NETHER_PYROCLAST_ORE_KEY, Feature.ORE,
                new OreFeatureConfig(List.of(
                        OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.NETHER_PYROCLAST_ORE.getDefaultState())),
                        7));

        register(context, INFERNO_ESSENCE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.INFERNO_ESSENCE_LOG),
                new StraightTrunkPlacer(7, 4, 5), // taller trunk with more variance

                BlockStateProvider.of(ModBlocks.INFERNO_ESSENCE_LEAVES),

                new BlobFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), 4), // bigger foliage, starts at trunk top
                new TwoLayersFeatureSize(2, 0, 3)) // slightly taller foliage layers
                .build());


        register(context, BLAZEBLOOM_KEY, Feature.FLOWER, new RandomPatchFeatureConfig(13, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.BLAZEBLOOM)))));
    }




    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey( String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(InfernumMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register( Registerable<ConfiguredFeature<?, ?>> context,
                                                                                    RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}

