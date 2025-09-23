package net.ronm19.infernummod.world;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.ModBlocks;

import java.util.List;

public class ModPlacedFeatures {

    // Nether
    public static final RegistryKey<PlacedFeature> NETHER_RUBY_ORE_PLACED_KEY = registerKey("nether_ruby_ore_placed");
    public static final RegistryKey<PlacedFeature> DEEPSLATE_NETHER_RUBY_ORE_PLACED_KEY = registerKey("deepslate_nether_ruby_ore_placed");
    public static final RegistryKey<PlacedFeature> NETHER_PYROCLAST_ORE_PLACED_KEY = registerKey("nether_pyroclast_ore_placed");


    // Overworld
    public static final RegistryKey<PlacedFeature> EMBERSTONE_ORE_PLACED_KEY = registerKey("emberstone_ore_placed");
    public static final RegistryKey<PlacedFeature> STONE_INFERNIUM_ORE_PLACED_KEY = registerKey("stone_infernium_ore_placed");
    public static final RegistryKey<PlacedFeature> DEEPSLATE_CINDERSTONE_ORE_PLACED_KEY = registerKey("deepslate_cinderstone_ore_placed");
    public static final RegistryKey<PlacedFeature> FIRERITE_ORE_PLACED_KEY = registerKey("firerite_ore_placed");
    public static final RegistryKey<PlacedFeature> DEEPSLATE_FIRERITE_ORE_PLACED_KEY = registerKey("deepslate_firerite_ore_placed");


    // Trees

    public static final RegistryKey<PlacedFeature> INFERNO_ESSENCE_PLACED_KEY = registerKey("inferno_essence_placed");


    // Flowers

    public static final RegistryKey<PlacedFeature> BLAZEBLOOM_PLACED_KEY = registerKey("blazebloom_placed");

    public static void boostrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);



        // Nether Ores
        register(context, NETHER_RUBY_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.NETHER_RUBY_ORE_KEY),
                ModOrePlacement.modifiersWithCount(12, // veins per chunk
                        HeightRangePlacementModifier.uniform(YOffset.fixed(10), YOffset.fixed(128))));

        register(context, DEEPSLATE_NETHER_RUBY_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DEEPSLATE_NETHER_RUBY_ORE_KEY),
                ModOrePlacement.modifiersWithCount(5, // rarer
                        HeightRangePlacementModifier.uniform(YOffset.fixed(10), YOffset.fixed(64)))); // from 10 to 64

        register(context, NETHER_PYROCLAST_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.NETHER_PYROCLAST_ORE_KEY),
                ModOrePlacement.modifiersWithCount(8, // mid-rarity
                        HeightRangePlacementModifier.uniform(YOffset.fixed(10), YOffset.fixed(96))));

// Overworld Ores
        register(context, EMBERSTONE_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.EMBERSTONE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(10,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(64))));

        register(context, STONE_INFERNIUM_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.STONE_INFERNIUM_ORE_KEY),
                ModOrePlacement.modifiersWithCount(7,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-32), YOffset.fixed(48))));

        register(context, DEEPSLATE_CINDERSTONE_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DEEPSLATE_CINDERSTONE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(5,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-16))));

        register(context, FIRERITE_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FIRERITE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(6,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(80))));

        register(context, DEEPSLATE_FIRERITE_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DEEPSLATE_FIRERITE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(4,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-32))));

// Trees

        register(context, INFERNO_ESSENCE_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.INFERNO_ESSENCE_KEY),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(3, 0.2f, 3),
                        ModBlocks.INFERNO_ESSENCE_SAPLING));

// Flowers

        register(context, BLAZEBLOOM_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.BLAZEBLOOM_KEY),
                RarityFilterPlacementModifier.of(4), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(InfernumMod.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register( Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                    RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                    PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}
