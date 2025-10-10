package net.ronm19.infernummod.world.dimension;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.world.biome.ModBiomes;

import java.util.OptionalLong;

public class ModDimensions {
    public static final RegistryKey<DimensionOptions> ABYSSIUM_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            new Identifier(InfernumMod.MOD_ID, "abyssium"));
    public static final RegistryKey<World> ABYSSIUM_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(InfernumMod.MOD_ID, "abyssium"));
    public static final RegistryKey<DimensionType> ABYSSIUM_DIM_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            new Identifier(InfernumMod.MOD_ID, "abyssium_type"));


    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(ABYSSIUM_DIM_KEY, new DimensionType(
                OptionalLong.of(12000), // fixedTime
                false, // hasSkylight
                true, // hasCeiling
                true, // ultraWarm
                true, // natural
                1.0, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                DimensionTypes.OVERWORLD_ID,
                2.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)));

    }
}

