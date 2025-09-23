package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.tag.ItemTags;
import net.ronm19.infernummod.block.ModBlocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {

        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(ModBlocks.ASH_BLOCK);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.NETHER_RUBY_DOOR)
                .add(ModBlocks.EMBERSTONE_DOOR)
                .add(ModBlocks.FIRERITE_DOOR)
                .add(ModBlocks.BLAZE_DOOR)
                .add(ModBlocks.INFERNAL_STONE_DOOR)
                .add(ModBlocks.MOLTEN_BRICKS_DOOR)
                .add(ModBlocks.MOLTEN_STONE_DOOR)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_DOOR)

                .add(ModBlocks.NETHER_RUBY_TRAPDOOR)
                .add(ModBlocks.EMBERSTONE_TRAPDOOR)
                .add(ModBlocks.FIRERITE_TRAPDOOR)
                .add(ModBlocks.BLAZE_TRAPDOOR)
                .add(ModBlocks.INFERNAL_STONE_TRAPDOOR)
                .add(ModBlocks.MOLTEN_BRICKS_TRAPDOOR)
                .add(ModBlocks.MOLTEN_STONE_TRAPDOOR)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_TRAPDOOR)

                .add(ModBlocks.NETHER_RUBY_PRESSURE_PLATE)
                .add(ModBlocks.EMBERSTONE_PRESSURE_PLATE)
                .add(ModBlocks.FIRERITE_PRESSURE_PLATE)
                .add(ModBlocks.BLAZE_PRESSURE_PLATE)
                .add(ModBlocks.INFERNAL_STONE_PRESSURE_PLATE)
                .add(ModBlocks.MOLTEN_BRICKS_PRESSURE_PLATE)
                .add(ModBlocks.MOLTEN_STONE_PRESSURE_PLATE)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_PRESSURE_PLATE)

                .add(ModBlocks.NETHER_RUBY_FENCE)
                .add(ModBlocks.EMBERSTONE_FENCE)
                .add(ModBlocks.FIRERITE_FENCE)
                .add(ModBlocks.BLAZE_FENCE)
                .add(ModBlocks.INFERNAL_STONE_FENCE)
                .add(ModBlocks.MOLTEN_BRICKS_FENCE)
                .add(ModBlocks.MOLTEN_STONE_FENCE)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_FENCE)

                .add(ModBlocks.NETHER_RUBY_FENCE_GATE)
                .add(ModBlocks.EMBERSTONE_FENCE_GATE)
                .add(ModBlocks.FIRERITE_FENCE_GATE)
                .add(ModBlocks.BLAZE_FENCE_GATE)
                .add(ModBlocks.INFERNAL_STONE_FENCE_GATE)
                .add(ModBlocks.MOLTEN_BRICKS_FENCE_GATE)
                .add(ModBlocks.MOLTEN_STONE_FENCE_GATE)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_FENCE_GATE)

                .add(ModBlocks.INFERNO_ESSENCE_PLANKS)
                .add(ModBlocks.INFERNO_ESSENCE_BUTTON)
                .add(ModBlocks.INFERNO_ESSENCE_DOOR)
                .add(ModBlocks.INFERNO_ESSENCE_FENCE)
                .add(ModBlocks.INFERNO_ESSENCE_FENCE_GATE)
                .add(ModBlocks.INFERNO_ESSENCE_LOG)
                .add(ModBlocks.INFERNO_ESSENCE_PRESSURE_PLATE)
                .add(ModBlocks.INFERNO_ESSENCE_SLAB)
                .add(ModBlocks.INFERNO_ESSENCE_STAIRS)
                .add(ModBlocks.INFERNO_ESSENCE_TRAPDOOR)
                .add(ModBlocks.INFERNO_ESSENCE_WOOD)
                .add(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG)
                .add(ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD);


        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.NETHER_RUBY_BLOCK)
                .add(ModBlocks.RAW_NETHER_RUBY_BLOCK)
                .add(ModBlocks.NETHER_RUBY_ORE)
                .add(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE)
                .add(ModBlocks.NETHER_RUBY_WALL)
                .add(ModBlocks.NETHER_RUBY_SLAB)


                .add(ModBlocks.FIRERITE_BLOCK)
                .add(ModBlocks.RAW_FIRERITE_BLOCK)
                .add(ModBlocks.FIRERITE_ORE)
                .add(ModBlocks.DEEPSLATE_FIRERITE_ORE)
                .add(ModBlocks.FIRERITE_WALL)
                .add(ModBlocks.FIRERITE_SLAB)


                .add(ModBlocks.BLAZE_BLOCK)
                .add(ModBlocks.BLAZE_STONE_BLOCK)
                .add(ModBlocks.BLAZE_WALL)
                .add(ModBlocks.BLAZE_SLAB)

                .add(ModBlocks.EMBERSTONE_BLOCK)
                .add(ModBlocks.RAW_EMBERSTONE_BLOCK)
                .add(ModBlocks.EMBERSTONE_ORE)
                .add(ModBlocks.EMBERSTONE_WALL)
                .add(ModBlocks.EMBERSTONE_SLAB)


                .add(ModBlocks.MOLTEN_STONE_BLOCK)
                .add(ModBlocks.MOLTEN_STONE_WALL)
                .add(ModBlocks.MOLTEN_STONE_SLAB)

                .add(ModBlocks.MOLTEN_BRICKS_BLOCK)
                .add(ModBlocks.MOLTEN_BRICKS_WALL)
                .add(ModBlocks.MOLTEN_BRICKS_SLAB)

                .add(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_WALL)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_SLAB)

                .add(ModBlocks.INFERNAL_STONE_BLOCK)
                .add(ModBlocks.INFERNAL_STONE_WALL)
                .add(ModBlocks.INFERNAL_STONE_SLAB)

                .add(ModBlocks.INFERNAL_STONE_SLAB)

                .add(ModBlocks.DEEPSLATE_CINDERSTONE_ORE)
                .add(ModBlocks.STONE_INFERNIUM_ORE)
                .add(ModBlocks.INFERNAL_RUNE_BLOCK);


        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.NETHER_RUBY_BLOCK)
                .add(ModBlocks.RAW_NETHER_RUBY_BLOCK)
                .add(ModBlocks.NETHER_RUBY_ORE)
                .add(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE)
                .add(ModBlocks.NETHER_RUBY_WALL)
                .add(ModBlocks.NETHER_RUBY_SLAB)


                .add(ModBlocks.FIRERITE_BLOCK)
                .add(ModBlocks.RAW_FIRERITE_BLOCK)
                .add(ModBlocks.FIRERITE_ORE)
                .add(ModBlocks.DEEPSLATE_FIRERITE_ORE)
                .add(ModBlocks.FIRERITE_WALL)
                .add(ModBlocks.FIRERITE_SLAB)


                .add(ModBlocks.BLAZE_BLOCK)
                .add(ModBlocks.BLAZE_STONE_BLOCK)
                .add(ModBlocks.BLAZE_WALL)
                .add(ModBlocks.BLAZE_SLAB)

                .add(ModBlocks.EMBERSTONE_BLOCK)
                .add(ModBlocks.RAW_EMBERSTONE_BLOCK)
                .add(ModBlocks.EMBERSTONE_ORE)
                .add(ModBlocks.EMBERSTONE_WALL)
                .add(ModBlocks.EMBERSTONE_SLAB)


                .add(ModBlocks.MOLTEN_STONE_BLOCK)
                .add(ModBlocks.MOLTEN_STONE_WALL)
                .add(ModBlocks.MOLTEN_STONE_SLAB)

                .add(ModBlocks.MOLTEN_BRICKS_BLOCK)
                .add(ModBlocks.MOLTEN_BRICKS_WALL)
                .add(ModBlocks.MOLTEN_BRICKS_SLAB)

                .add(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_WALL)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_SLAB)

                .add(ModBlocks.INFERNAL_STONE_BLOCK)
                .add(ModBlocks.INFERNAL_STONE_WALL)
                .add(ModBlocks.INFERNAL_STONE_SLAB)

                .add(ModBlocks.INFERNAL_STONE_SLAB)

                .add(ModBlocks.DEEPSLATE_CINDERSTONE_ORE)
                .add(ModBlocks.STONE_INFERNIUM_ORE);

        getOrCreateTagBuilder(BlockTags.FENCES)
                .add(ModBlocks.NETHER_RUBY_FENCE)
                .add(ModBlocks.EMBERSTONE_FENCE)
                .add(ModBlocks.FIRERITE_FENCE)
                .add(ModBlocks.INFERNO_ESSENCE_FENCE)
                .add(ModBlocks.MOLTEN_STONE_FENCE)
                .add(ModBlocks.MOLTEN_BRICKS_FENCE)
                .add(ModBlocks.INFERNAL_STONE_FENCE)
                .add(ModBlocks.BLAZE_FENCE)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_FENCE);


        getOrCreateTagBuilder(BlockTags.FENCE_GATES)

                .add(ModBlocks.NETHER_RUBY_FENCE_GATE)
                .add(ModBlocks.EMBERSTONE_FENCE_GATE)
                .add(ModBlocks.FIRERITE_FENCE_GATE)
                .add(ModBlocks.BLAZE_FENCE_GATE)
                .add(ModBlocks.INFERNAL_STONE_FENCE_GATE)
                .add(ModBlocks.MOLTEN_BRICKS_FENCE_GATE)
                .add(ModBlocks.MOLTEN_STONE_FENCE_GATE)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_FENCE_GATE)
                .add(ModBlocks.INFERNO_ESSENCE_FENCE_GATE);

        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(ModBlocks.INFERNAL_STONE_WALL)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_WALL)
                .add(ModBlocks.MOLTEN_BRICKS_WALL)
                .add(ModBlocks.MOLTEN_STONE_WALL)
                .add(ModBlocks.EMBERSTONE_WALL)
                .add(ModBlocks.BLAZE_WALL)
                .add(ModBlocks.FIRERITE_WALL)
                .add(ModBlocks.NETHER_RUBY_WALL)
                .add(ModBlocks.INFERNO_ESSENCE_WALL);

        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.INFERNO_ESSENCE_LOG)
                .add(ModBlocks.INFERNO_ESSENCE_WOOD)
                .add(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG)
                .add(ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD);

        getOrCreateTagBuilder(ModTags.Blocks.INFERNUM_PAXEL_MINEABLE)
                .forceAddTag(BlockTags.PICKAXE_MINEABLE)
                .forceAddTag(BlockTags.AXE_MINEABLE)
                .forceAddTag(BlockTags.SHOVEL_MINEABLE);
    }
}