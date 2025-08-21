package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider( FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure( RegistryWrapper.WrapperLookup arg ) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.FIRERITE_BLOCK)
                .add(ModBlocks.NETHER_RUBY_BLOCK)
                .add(ModBlocks.RAW_FIRERITE_BLOCK)
                .add(ModBlocks.RAW_NETHER_RUBY_BLOCK)
                .add(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE)
                .add(ModBlocks.NETHER_RUBY_ORE)
                .add(ModBlocks.FIRERITE_ORE)
                .add(ModBlocks.DEEPSLATE_FIRERITE_ORE)
                .add(ModBlocks.EMBERSTONE_BLOCK)
                .add(ModBlocks.RAW_EMBERSTONE_BLOCK)
                .add(ModBlocks.BLAZE_STONE_BLOCK)
                .add(ModBlocks.BLAZE_BLOCK)
                .add(ModBlocks.INFERNAL_STONE_BLOCK)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .add(ModBlocks.MOLTEN_BRICKS_BLOCK)
                .add(ModBlocks.MOLTEN_GRANITE_BLOCK)
                .add(ModBlocks.MOLTEN_STONE_BLOCK)
                .add(ModBlocks.EMBERSTONE_ORE)
                .add(ModBlocks.DEEPSLATE_CINDERSTONE_ORE)
                .add(ModBlocks.STONE_INFERNIUM_ORE)
                .add(ModBlocks.NETHER_PYROCLAST_ORE);


        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.FIRERITE_BLOCK)
                .add(ModBlocks.NETHER_RUBY_BLOCK)
                .add(ModBlocks.RAW_FIRERITE_BLOCK)
                .add(ModBlocks.RAW_NETHER_RUBY_BLOCK)
                .add(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE)
                .add(ModBlocks.NETHER_RUBY_ORE)
                .add(ModBlocks.FIRERITE_ORE)
                .add(ModBlocks.DEEPSLATE_FIRERITE_ORE)
                .add(ModBlocks.EMBERSTONE_BLOCK)
                .add(ModBlocks.RAW_EMBERSTONE_BLOCK)
                .add(ModBlocks.BLAZE_STONE_BLOCK)
                .add(ModBlocks.BLAZE_BLOCK)
                .add(ModBlocks.INFERNAL_STONE_BLOCK)
                .add(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .add(ModBlocks.MOLTEN_BRICKS_BLOCK)
                .add(ModBlocks.MOLTEN_GRANITE_BLOCK)
                .add(ModBlocks.MOLTEN_STONE_BLOCK)
                .add(ModBlocks.EMBERSTONE_ORE)
                .add(ModBlocks.DEEPSLATE_CINDERSTONE_ORE)
                .add(ModBlocks.STONE_INFERNIUM_ORE)
                .add(ModBlocks.NETHER_PYROCLAST_ORE);

        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric", "needs_tool_level_5")));



        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric", "needs_tool_level_6")));

    }
}
