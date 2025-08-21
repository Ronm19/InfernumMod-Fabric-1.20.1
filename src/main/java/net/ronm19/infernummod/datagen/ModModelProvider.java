package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider( FabricDataOutput output ) {
        super(output);
    }

    @Override
    public void generateBlockStateModels( BlockStateModelGenerator blockStateModelGenerator ) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_RUBY_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_NETHER_RUBY_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.FIRERITE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_FIRERITE_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_RUBY_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.FIRERITE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_FIRERITE_ORE);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BLAZE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BLAZE_STONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.INFERNAL_STONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOLTEN_STONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOLTEN_GRANITE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOLTEN_BRICKS_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.EMBERSTONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_EMBERSTONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_CINDERSTONE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.EMBERSTONE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.STONE_INFERNIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_PYROCLAST_ORE);

    }

    @Override
    public void generateItemModels( ItemModelGenerator itemModelGenerator ) {
        itemModelGenerator.register(ModItems.NETHER_RUBY, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_NETHER_RUBY, Models.GENERATED);

        itemModelGenerator.register(ModItems.FIRERITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_FIRERITE, Models.GENERATED);

        itemModelGenerator.register(ModItems.EMBERSTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_EMBERSTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.INFERNIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_INFERNIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.CINDERTSONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_CINDESTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYROCLAST, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_PYROCLAST, Models.GENERATED);
    }
}
