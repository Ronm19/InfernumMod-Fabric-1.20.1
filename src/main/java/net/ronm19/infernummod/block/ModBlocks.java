package net.ronm19.infernummod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.custom.RawFireriteBlock;

public class ModBlocks {
    public static final Block NETHER_RUBY_BLOCK = registerBlock("nether_ruby_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK)));
    public static final Block RAW_NETHER_RUBY_BLOCK = registerBlock("raw_nether_ruby_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK)));
    public static final Block NETHER_RUBY_ORE = registerBlock("nether_ruby_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.NETHERRACK).strength(3f), UniformIntProvider.create(2, 4)));
    public static final Block DEEPSLATE_NETHER_RUBY_ORE = registerBlock("deepslate_nether_ruby_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.NETHERRACK).strength(4f), UniformIntProvider.create(2, 4)));

    public static final Block FIRERITE_BLOCK = registerBlock("firerite_block",
            new Block(FabricBlockSettings.copyOf(Blocks.MAGMA_BLOCK).sounds(BlockSoundGroup.GRAVEL)));
    public static final Block RAW_FIRERITE_BLOCK = registerBlock("raw_firerite_block",
            new RawFireriteBlock(FabricBlockSettings.copyOf(Blocks.MAGMA_BLOCK).sounds(BlockSoundGroup.NETHERRACK).luminance(7)));
    public static final Block FIRERITE_ORE = registerBlock("firerite_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.NETHERRACK).strength(3f), UniformIntProvider.create(3, 5)));
    public static final Block DEEPSLATE_FIRERITE_ORE = registerBlock("deepslate_firerite_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.NETHERRACK).strength(4f), UniformIntProvider.create(3, 5)));

    public static final Block BLAZE_BLOCK = registerBlock("blaze_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.METAL)));
    public static final Block BLAZE_STONE_BLOCK = registerBlock("blaze_stone_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));
    public static final Block EMBERSTONE_BLOCK = registerBlock("emberstone_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));
    public static final Block RAW_EMBERSTONE_BLOCK = registerBlock("raw_emberstone_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.DEEPSLATE)));
    public static final Block INFERNAL_BRICKS_STONE_BLOCK = registerBlock("infernal_bricks_stone_block",
            new Block(FabricBlockSettings.copyOf(Blocks.BRICKS).sounds(BlockSoundGroup.DEEPSLATE_BRICKS)));
    public static final Block INFERNAL_STONE_BLOCK = registerBlock("infernal_stone_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));
    public static final Block MOLTEN_BRICKS_BLOCK = registerBlock("molten_bricks_block",
            new Block(FabricBlockSettings.copyOf(Blocks.MAGMA_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));
    public static final Block MOLTEN_GRANITE_BLOCK = registerBlock("molten_granite_block",
            new Block(FabricBlockSettings.copyOf(Blocks.GRANITE).sounds(BlockSoundGroup.GRAVEL)));
    public static final Block MOLTEN_STONE_BLOCK = registerBlock("molten_stone_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));

    public static final Block EMBERSTONE_ORE = registerBlock("emberstone_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.GOLD_ORE).strength(3f), UniformIntProvider.create(3, 5)));
    public static final Block NETHER_PYROCLAST_ORE = registerBlock("nether_pyroclast_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.NETHER_GOLD_ORE).strength(4f), UniformIntProvider.create(3, 6)));
    public static final Block STONE_INFERNIUM_ORE = registerBlock("stone_infernium_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.GOLD_ORE).strength(2f), UniformIntProvider.create(3, 4)));
    public static final Block DEEPSLATE_CINDERSTONE_ORE = registerBlock("deepslate_cinderstone_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).strength(3f), UniformIntProvider.create(3, 7)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(InfernumMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem( String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(InfernumMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlock() {
        InfernumMod.LOGGER.info("Registering ModBlocks for " + InfernumMod.MOD_ID);
    }
}
