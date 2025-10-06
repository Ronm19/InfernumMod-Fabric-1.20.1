package net.ronm19.infernummod.world.biome.surface;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.ronm19.infernummod.block.ModBlocks;

public class ModMaterialRules {

    private static final MaterialRules.MaterialRule INFERNAL_GRASS_BLOCK = makeStateRule(ModBlocks.INFERNAL_GRASS_BLOCK);
    private static final MaterialRules.MaterialRule INFERNAL_DIRT_BLOCK = makeStateRule(ModBlocks.INFERNAL_DIRT_BLOCK);
    private static final MaterialRules.MaterialRule RAW_FIRERITE_BLOCK = makeStateRule(ModBlocks.RAW_FIRERITE_BLOCK);


    public static MaterialRules.MaterialRule makeInfernalRules() {
        // Surface condition: at or above water level
        MaterialRules.MaterialCondition isAtOrAboveWaterLevel = MaterialRules.water(-1, 0);
        MaterialRules.MaterialRule grassSurface = MaterialRules.sequence(
                MaterialRules.condition(isAtOrAboveWaterLevel, INFERNAL_GRASS_BLOCK),
                INFERNAL_DIRT_BLOCK
        );

        // Firerite / Molten Bricks placement rules (no biome check to avoid cycles)
        MaterialRules.MaterialRule fireriteAndBricks = MaterialRules.sequence(
                // RAW_FIRERITE underground
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, INFERNAL_GRASS_BLOCK),
                // MOLTEN_BRICKS closer to surface
                MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, INFERNAL_DIRT_BLOCK),
                MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, RAW_FIRERITE_BLOCK)
        );

        // Combine everything with default grass/dirt for other biomes
        return MaterialRules.sequence(
                fireriteAndBricks,
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, grassSurface)
        );
    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}
