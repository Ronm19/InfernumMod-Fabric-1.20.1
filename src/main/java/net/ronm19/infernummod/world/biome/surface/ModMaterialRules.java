package net.ronm19.infernummod.world.biome.surface;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.ronm19.infernummod.block.ModBlocks;

public class ModMaterialRules {

    private static final MaterialRules.MaterialRule ASH_BLOCK = makeStateRule(ModBlocks.ASH_BLOCK);
    private static final MaterialRules.MaterialRule RAW_FIRERITE = makeStateRule(ModBlocks.RAW_FIRERITE_BLOCK);


    public static MaterialRules.MaterialRule makeAshlandRules() {
        // Surface condition: at or above water level
        MaterialRules.MaterialCondition isAtOrAboveWaterLevel = MaterialRules.water(-1, 0);
        MaterialRules.MaterialRule grassSurface = MaterialRules.sequence(
                MaterialRules.condition(isAtOrAboveWaterLevel, ASH_BLOCK),
                RAW_FIRERITE
        );

        // Firerite / Molten Bricks placement rules (no biome check to avoid cycles)
        MaterialRules.MaterialRule fireriteAndBricks = MaterialRules.sequence(
                // RAW_FIRERITE underground
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, ASH_BLOCK),
                // MOLTEN_BRICKS closer to surface
                MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, RAW_FIRERITE)
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
