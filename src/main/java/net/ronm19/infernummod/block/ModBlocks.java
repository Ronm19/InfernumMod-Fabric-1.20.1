package net.ronm19.infernummod.block;

import com.terraformersmc.terraform.sign.block.TerraformHangingSignBlock;
import com.terraformersmc.terraform.sign.block.TerraformSignBlock;
import com.terraformersmc.terraform.sign.block.TerraformWallHangingSignBlock;
import com.terraformersmc.terraform.sign.block.TerraformWallSignBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.custom.AshBlock;
import net.ronm19.infernummod.block.custom.BlazebloomBlock;
import net.ronm19.infernummod.block.custom.RawFireriteBlock;
import net.ronm19.infernummod.world.tree.InfernoEssenceSaplingGenerator;

import static net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.copyOf;

public class ModBlocks {
    public static final Block NETHER_RUBY_BLOCK = registerBlock("nether_ruby_block",
            new Block(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK)));
    public static final Block RAW_NETHER_RUBY_BLOCK = registerBlock("raw_nether_ruby_block",
            new Block(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK)));
    public static final Block NETHER_RUBY_ORE = registerBlock("nether_ruby_ore",
            new ExperienceDroppingBlock(copyOf(Blocks.NETHERRACK).strength(3f), UniformIntProvider.create(2, 4)));
    public static final Block DEEPSLATE_NETHER_RUBY_ORE = registerBlock("deepslate_nether_ruby_ore",
            new ExperienceDroppingBlock(copyOf(Blocks.NETHERRACK).strength(4f), UniformIntProvider.create(2, 4)));

    public static final Block FIRERITE_BLOCK = registerBlock("firerite_block",
            new Block(copyOf(Blocks.MAGMA_BLOCK).sounds(BlockSoundGroup.GRAVEL)));
    public static final Block RAW_FIRERITE_BLOCK = registerBlock("raw_firerite_block",
            new RawFireriteBlock(copyOf(Blocks.MAGMA_BLOCK).sounds(BlockSoundGroup.NETHERRACK).luminance(7)));
    public static final Block FIRERITE_ORE = registerBlock("firerite_ore",
            new ExperienceDroppingBlock(copyOf(Blocks.NETHERRACK).strength(3f), UniformIntProvider.create(3, 5)));
    public static final Block DEEPSLATE_FIRERITE_ORE = registerBlock("deepslate_firerite_ore",
            new ExperienceDroppingBlock(copyOf(Blocks.NETHERRACK).strength(4f), UniformIntProvider.create(3, 5)));

    public static final Block BLAZE_BLOCK = registerBlock("blaze_block",
            new Block(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.METAL).luminance(8)));
    public static final Block BLAZE_STONE_BLOCK = registerBlock("blaze_stone_block",
            new Block(copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));

    public static final Block EMBERSTONE_BLOCK = registerBlock("emberstone_block",
            new Block(copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));
    public static final Block RAW_EMBERSTONE_BLOCK = registerBlock("raw_emberstone_block",
            new Block(copyOf(Blocks.STONE).sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block INFERNAL_BRICKS_STONE_BLOCK = registerBlock("infernal_bricks_stone_block",
            new Block(copyOf(Blocks.BRICKS).sounds(BlockSoundGroup.DEEPSLATE_BRICKS)));
    public static final Block INFERNAL_STONE_BLOCK = registerBlock("infernal_stone_block",
            new Block(copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));

    public static final Block MOLTEN_BRICKS_BLOCK = registerBlock("molten_bricks_block",
            new Block(copyOf(Blocks.MAGMA_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));
    public static final Block MOLTEN_GRANITE_BLOCK = registerBlock("molten_granite_block",
            new Block(copyOf(Blocks.GRANITE).sounds(BlockSoundGroup.GRAVEL)));
    public static final Block MOLTEN_STONE_BLOCK = registerBlock("molten_stone_block",
            new Block(copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));

    public static final Block ASH_BLOCK = registerBlock("ash_block",
            new AshBlock(copyOf(Blocks.GRAVEL).sounds(BlockSoundGroup.GRAVEL).strength(1.0F, 3.0F).ticksRandomly().requiresTool()));

    public static final Block EMBERSTONE_ORE = registerBlock("emberstone_ore",
            new ExperienceDroppingBlock(copyOf(Blocks.GOLD_ORE).strength(3f), UniformIntProvider.create(3, 5)));
    public static final Block NETHER_PYROCLAST_ORE = registerBlock("nether_pyroclast_ore",
            new ExperienceDroppingBlock(copyOf(Blocks.NETHER_GOLD_ORE).strength(4f), UniformIntProvider.create(3, 6)));
    public static final Block STONE_INFERNIUM_ORE = registerBlock("stone_infernium_ore",
            new ExperienceDroppingBlock(copyOf(Blocks.GOLD_ORE).strength(2f), UniformIntProvider.create(3, 4)));
    public static final Block DEEPSLATE_CINDERSTONE_ORE = registerBlock("deepslate_cinderstone_ore",
            new ExperienceDroppingBlock(copyOf(Blocks.DEEPSLATE).strength(3f), UniformIntProvider.create(3, 7)));

    public static final Block NETHER_RUBY_STAIRS = registerBlock("nether_ruby_stairs",
            new StairsBlock(ModBlocks.NETHER_RUBY_BLOCK.getDefaultState(), copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK)));
    public static final Block NETHER_RUBY_SLAB = registerBlock("nether_ruby_slab",
            new SlabBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK)));

    public static final Block NETHER_RUBY_BUTTON = registerBlock("nether_ruby_button",
            new ButtonBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK), BlockSetType.OAK, 10, true));
    public static final Block NETHER_RUBY_PRESSURE_PLATE = registerBlock("nether_ruby_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK), BlockSetType.OAK));

    public static final Block NETHER_RUBY_FENCE = registerBlock("nether_ruby_fence",
            new FenceBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK)));
    public static final Block NETHER_RUBY_FENCE_GATE = registerBlock("nether_ruby_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK), WoodType.ACACIA));
    public static final Block NETHER_RUBY_WALL = registerBlock("nether_ruby_wall",
            new WallBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK)));

    public static final Block NETHER_RUBY_DOOR = registerBlock("nether_ruby_door",
            new DoorBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK).nonOpaque(), BlockSetType.OAK));
    public static final Block NETHER_RUBY_TRAPDOOR = registerBlock("nether_ruby_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHERRACK).nonOpaque(), BlockSetType.OAK));

    public static final Block FIRERITE_STAIRS = registerBlock("firerite_stairs",
            new StairsBlock(ModBlocks.FIRERITE_BLOCK.getDefaultState(), copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL)));
    public static final Block FIRERITE_SLAB = registerBlock("firerite_slab",
            new SlabBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL)));

    public static final Block FIRERITE_BUTTON = registerBlock("firerite_button",
            new ButtonBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL), BlockSetType.OAK, 10, true));
    public static final Block FIRERITE_PRESSURE_PLATE = registerBlock("firerite_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL), BlockSetType.OAK));

    public static final Block FIRERITE_FENCE = registerBlock("firerite_fence",
            new FenceBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL)));
    public static final Block FIRERITE_FENCE_GATE = registerBlock("firerite_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL), WoodType.ACACIA));
    public static final Block FIRERITE_WALL = registerBlock("firerite_wall",
            new WallBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL)));

    public static final Block FIRERITE_DOOR = registerBlock("firerite_door",
            new DoorBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL).nonOpaque(), BlockSetType.OAK));
    public static final Block FIRERITE_TRAPDOOR = registerBlock("firerite_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.GRAVEL).nonOpaque(), BlockSetType.OAK));

    public static final Block BLAZE_STAIRS = registerBlock("blaze_stairs",
            new StairsBlock(ModBlocks.BLAZE_BLOCK.getDefaultState(), copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));
    public static final Block BLAZE_SLAB = registerBlock("blaze_slab",
            new SlabBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));

    public static final Block BLAZE_BUTTON = registerBlock("blaze_button",
            new ButtonBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), BlockSetType.OAK, 10, true));
    public static final Block BLAZE_PRESSURE_PLATE = registerBlock("blaze_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), BlockSetType.OAK));

    public static final Block BLAZE_FENCE = registerBlock("blaze_fence",
            new FenceBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));
    public static final Block BLAZE_FENCE_GATE = registerBlock("blaze_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), WoodType.OAK));
    public static final Block BLAZE_WALL = registerBlock("blaze_wall",
            new WallBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));

    public static final Block BLAZE_DOOR = registerBlock("blaze_door",
            new DoorBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE).nonOpaque(), BlockSetType.OAK));
    public static final Block BLAZE_TRAPDOOR = registerBlock("blaze_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE).nonOpaque(), BlockSetType.OAK));

    public static final Block EMBERSTONE_STAIRS = registerBlock("emberstone_stairs",
            new StairsBlock(ModBlocks.EMBERSTONE_BLOCK.getDefaultState(), copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));
    public static final Block EMBERSTONE_SLAB = registerBlock("emberstone_slab",
            new SlabBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));

    public static final Block EMBERSTONE_BUTTON = registerBlock("emberstone_button",
            new ButtonBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), BlockSetType.OAK, 10, true));
    public static final Block EMBERSTONE_PRESSURE_PLATE = registerBlock("emberstone_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), BlockSetType.OAK));

    public static final Block EMBERSTONE_FENCE = registerBlock("emberstone_fence",
            new FenceBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));
    public static final Block EMBERSTONE_FENCE_GATE = registerBlock("emberstone_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), WoodType.OAK));
    public static final Block EMBERSTONE_WALL = registerBlock("emberstone_wall",
            new WallBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));

    public static final Block EMBERSTONE_DOOR = registerBlock("emberstone_door",
            new DoorBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE).nonOpaque(), BlockSetType.OAK));
    public static final Block EMBERSTONE_TRAPDOOR = registerBlock("emberstone_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE).nonOpaque(), BlockSetType.OAK));

    public static final Block INFERNAL_BRICKS_STONE_STAIRS = registerBlock("infernal_bricks_stone_stairs",
            new StairsBlock(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK.getDefaultState(), copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));
    public static final Block INFERNAL_BRICKS_STONE_SLAB = registerBlock("infernal_bricks_stone_slab",
            new SlabBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));

    public static final Block INFERNAL_BRICKS_STONE_BUTTON = registerBlock("infernal_bricks_stone_button",
            new ButtonBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS), BlockSetType.OAK, 10, true));
    public static final Block INFERNAL_BRICKS_STONE_PRESSURE_PLATE = registerBlock("infernal_bricks_stone_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS), BlockSetType.OAK));

    public static final Block INFERNAL_BRICKS_STONE_FENCE = registerBlock("infernal_bricks_stone_fence",
            new FenceBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));
    public static final Block INFERNAL_BRICKS_STONE_FENCE_GATE = registerBlock("infernal_bricks_stone_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS), WoodType.OAK));
    public static final Block INFERNAL_BRICKS_STONE_WALL = registerBlock("infernal_bricks_stone_wall",
            new WallBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));

    public static final Block INFERNAL_BRICKS_STONE_DOOR = registerBlock("infernal_bricks_stone_door",
            new DoorBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS).nonOpaque(), BlockSetType.OAK));
    public static final Block INFERNAL_BRICKS_STONE_TRAPDOOR = registerBlock("infernal_bricks_stone_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS).nonOpaque(), BlockSetType.OAK));


    public static final Block MOLTEN_BRICKS_STAIRS = registerBlock("molten_bricks_stairs",
            new StairsBlock(ModBlocks.MOLTEN_BRICKS_BLOCK.getDefaultState(), copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));
    public static final Block MOLTEN_BRICKS_SLAB = registerBlock("molten_bricks_slab",
            new SlabBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));

    public static final Block MOLTEN_BRICKS_BUTTON = registerBlock("molten_bricks_button",
            new ButtonBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS), BlockSetType.OAK, 10, true));
    public static final Block MOLTEN_BRICKS_PRESSURE_PLATE = registerBlock("molten_bricks_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS), BlockSetType.OAK));

    public static final Block MOLTEN_BRICKS_FENCE = registerBlock("molten_bricks_fence",
            new FenceBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));
    public static final Block MOLTEN_BRICKS_FENCE_GATE = registerBlock("molten_bricks_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS), WoodType.OAK));
    public static final Block MOLTEN_BRICKS_WALL = registerBlock("molten_bricks_wall",
            new WallBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS)));

    public static final Block MOLTEN_BRICKS_DOOR = registerBlock("molten_bricks_door",
            new DoorBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS).nonOpaque(), BlockSetType.OAK));
    public static final Block MOLTEN_BRICKS_TRAPDOOR = registerBlock("molten_bricks_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.NETHER_BRICKS).nonOpaque(), BlockSetType.OAK));

    public static final Block MOLTEN_STONE_STAIRS = registerBlock("molten_stone_stairs",
            new StairsBlock(ModBlocks.MOLTEN_STONE_BLOCK.getDefaultState(), copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));
    public static final Block MOLTEN_STONE_SLAB = registerBlock("molten_stone_slab",
            new SlabBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));

    public static final Block MOLTEN_STONE_BUTTON = registerBlock("molten_stone_button",
            new ButtonBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), BlockSetType.OAK, 10, true));
    public static final Block MOLTEN_STONE_PRESSURE_PLATE = registerBlock("molten_stone_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), BlockSetType.OAK));

    public static final Block MOLTEN_STONE_FENCE = registerBlock("molten_stone_fence",
            new FenceBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));
    public static final Block MOLTEN_STONE_FENCE_GATE = registerBlock("molten_stone_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), WoodType.OAK));
    public static final Block MOLTEN_STONE_WALL = registerBlock("molten_stone_wall",
            new WallBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));

    public static final Block MOLTEN_STONE_DOOR = registerBlock("molten_stone_door",
            new DoorBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE).nonOpaque(), BlockSetType.OAK));
    public static final Block MOLTEN_STONE_TRAPDOOR = registerBlock("molten_stone_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE).nonOpaque(), BlockSetType.OAK));

    public static final Block INFERNAL_STONE_STAIRS = registerBlock("infernal_stone_stairs",
            new StairsBlock(ModBlocks.MOLTEN_STONE_BLOCK.getDefaultState(), copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));
    public static final Block INFERNAL_STONE_SLAB = registerBlock("infernal_stone_slab",
            new SlabBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));

    public static final Block INFERNAL_STONE_BUTTON = registerBlock("infernal_stone_button",
            new ButtonBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), BlockSetType.OAK, 10, true));
    public static final Block INFERNAL_STONE_PRESSURE_PLATE = registerBlock("infernal_stone_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), BlockSetType.OAK));

    public static final Block INFERNAL_STONE_FENCE = registerBlock("infernal_stone_fence",
            new FenceBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));
    public static final Block INFERNAL_STONE_FENCE_GATE = registerBlock("infernal_stone_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE), WoodType.OAK));
    public static final Block INFERNAL_STONE_WALL = registerBlock("infernal_stone_wall",
            new WallBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE)));

    public static final Block INFERNAL_STONE_DOOR = registerBlock("infernal_stone_door",
            new DoorBlock(copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE).nonOpaque(), BlockSetType.OAK));
    public static final Block INFERNAL_STONE_TRAPDOOR = registerBlock("infernal_stone_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE).nonOpaque(), BlockSetType.OAK));

    public static final Block BLAZEBLOOM = registerBlock("blazebloom",
            new BlazebloomBlock(StatusEffects.FIRE_RESISTANCE, 5, copyOf(Blocks.WITHER_ROSE).nonOpaque().noCollision()));
    public static final Block POTTED_BLAZEBLOOM = Registry.register(Registries.BLOCK, new Identifier(InfernumMod.MOD_ID, "potted_blazebloom"),
        new FlowerPotBlock(BLAZEBLOOM, FabricBlockSettings.copyOf(Blocks.POTTED_WITHER_ROSE).nonOpaque()));

    public static final Block INFERNO_ESSENCE_LOG = registerBlock("inferno_essence_log",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).strength(3f)));
    public static final Block INFERNO_ESSENCE_WOOD = registerBlock("inferno_essence_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).strength(3f)));
    public static final Block STRIPPED_INFERNO_ESSENCE_LOG = registerBlock("stripped_inferno_essence_log",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final Block STRIPPED_INFERNO_ESSENCE_WOOD = registerBlock("stripped_inferno_essence_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_WOOD).strength(3f)));

    public static final Block INFERNO_ESSENCE_PLANKS = registerBlock("inferno_essence_planks",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(3f)));
    public static final Block INFERNO_ESSENCE_LEAVES = registerBlock("inferno_essence_leaves",
            new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).strength(3f).nonOpaque()));

    public static final Block INFERNO_ESSENCE_SAPLING = registerBlock("inferno_essence_sapling",
            new SaplingBlock(new InfernoEssenceSaplingGenerator(),FabricBlockSettings.copyOf(Blocks.OAK_SAPLING)));

    public static final Block POTTED_INFERNO_ESSENCE_SAPLING = Registry.register(Registries.BLOCK, new Identifier(InfernumMod.MOD_ID, "potted_inferno_essence_sapling"),
            new FlowerPotBlock(INFERNO_ESSENCE_SAPLING, FabricBlockSettings.copyOf(Blocks.OAK_SAPLING).nonOpaque()));


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
