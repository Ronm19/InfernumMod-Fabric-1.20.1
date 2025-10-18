package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.item.ModItems;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider( FabricDataOutput output ) {
        super(output);
    }

    @Override
    public void generateBlockStateModels( BlockStateModelGenerator blockStateModelGenerator ) {

        BlockStateModelGenerator.BlockTexturePool netherRubyPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.NETHER_RUBY_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_NETHER_RUBY_BLOCK);
        BlockStateModelGenerator.BlockTexturePool fireritePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.FIRERITE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_FIRERITE_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_RUBY_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.FIRERITE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_FIRERITE_ORE);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ASH_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ABYSSIUM_STONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.INFERNAL_RUNE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CHARRED_WOOL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.INFERNAL_FORGE_BLOCK);

        BlockStateModelGenerator.BlockTexturePool blazePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.BLAZE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BLAZE_STONE_BLOCK);
        BlockStateModelGenerator.BlockTexturePool infernalStonePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.INFERNAL_STONE_BLOCK);
        BlockStateModelGenerator.BlockTexturePool moltenStonePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.MOLTEN_STONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOLTEN_GRANITE_BLOCK);
        BlockStateModelGenerator.BlockTexturePool moltenBricksPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.MOLTEN_BRICKS_BLOCK);
        BlockStateModelGenerator.BlockTexturePool emberstonePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.EMBERSTONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_EMBERSTONE_BLOCK);
        BlockStateModelGenerator.BlockTexturePool infernalBricksStonePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_CINDERSTONE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.EMBERSTONE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.STONE_INFERNIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_PYROCLAST_ORE);

        BlockStateModelGenerator.BlockTexturePool infernoEssencePlanksPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.INFERNO_ESSENCE_PLANKS);

        netherRubyPool.stairs(ModBlocks.NETHER_RUBY_STAIRS);
        netherRubyPool.slab(ModBlocks.NETHER_RUBY_SLAB);
        netherRubyPool.button(ModBlocks.NETHER_RUBY_BUTTON);
        netherRubyPool.pressurePlate(ModBlocks.NETHER_RUBY_PRESSURE_PLATE);
        netherRubyPool.fence(ModBlocks.NETHER_RUBY_FENCE);
        netherRubyPool.fenceGate(ModBlocks.NETHER_RUBY_FENCE_GATE);
        netherRubyPool.wall(ModBlocks.NETHER_RUBY_WALL);

        fireritePool.stairs(ModBlocks.FIRERITE_STAIRS);
        fireritePool.slab(ModBlocks.FIRERITE_SLAB);
        fireritePool.button(ModBlocks.FIRERITE_BUTTON);
        fireritePool.pressurePlate(ModBlocks.FIRERITE_PRESSURE_PLATE);
        fireritePool.fence(ModBlocks.FIRERITE_FENCE);
        fireritePool.fenceGate(ModBlocks.FIRERITE_FENCE_GATE);
        fireritePool.wall(ModBlocks.FIRERITE_WALL);

        blazePool.stairs(ModBlocks.BLAZE_STAIRS);
        blazePool.slab(ModBlocks.BLAZE_SLAB);
        blazePool.button(ModBlocks.BLAZE_BUTTON);
        blazePool.pressurePlate(ModBlocks.BLAZE_PRESSURE_PLATE);
        blazePool.fence(ModBlocks.BLAZE_FENCE);
        blazePool.fenceGate(ModBlocks.BLAZE_FENCE_GATE);
        blazePool.wall(ModBlocks.BLAZE_WALL);

        emberstonePool.stairs(ModBlocks.EMBERSTONE_STAIRS);
        emberstonePool.slab(ModBlocks.EMBERSTONE_SLAB);
        emberstonePool.button(ModBlocks.EMBERSTONE_BUTTON);
        emberstonePool.pressurePlate(ModBlocks.EMBERSTONE_PRESSURE_PLATE);
        emberstonePool.fence(ModBlocks.EMBERSTONE_FENCE);
        emberstonePool.fenceGate(ModBlocks.EMBERSTONE_FENCE_GATE);
        emberstonePool.wall(ModBlocks.EMBERSTONE_WALL);

        infernalBricksStonePool.stairs(ModBlocks.INFERNAL_BRICKS_STONE_STAIRS);
        infernalBricksStonePool.slab(ModBlocks.INFERNAL_BRICKS_STONE_SLAB);
        infernalBricksStonePool.button(ModBlocks.INFERNAL_BRICKS_STONE_BUTTON);
        infernalBricksStonePool.pressurePlate(ModBlocks.INFERNAL_BRICKS_STONE_PRESSURE_PLATE);
        infernalBricksStonePool.fence(ModBlocks.INFERNAL_BRICKS_STONE_FENCE);
        infernalBricksStonePool.fenceGate(ModBlocks.INFERNAL_BRICKS_STONE_FENCE_GATE);
        infernalBricksStonePool.wall(ModBlocks.INFERNAL_BRICKS_STONE_WALL);

        moltenBricksPool.stairs(ModBlocks.MOLTEN_BRICKS_STAIRS);
        moltenBricksPool.slab(ModBlocks.MOLTEN_BRICKS_SLAB);
        moltenBricksPool.button(ModBlocks.MOLTEN_BRICKS_BUTTON);
        moltenBricksPool.pressurePlate(ModBlocks.MOLTEN_BRICKS_PRESSURE_PLATE);
        moltenBricksPool.fence(ModBlocks.MOLTEN_BRICKS_FENCE);
        moltenBricksPool.fenceGate(ModBlocks.MOLTEN_BRICKS_FENCE_GATE);
        moltenBricksPool.wall(ModBlocks.MOLTEN_BRICKS_WALL);

        moltenStonePool.stairs(ModBlocks.MOLTEN_STONE_STAIRS);
        moltenStonePool.slab(ModBlocks.MOLTEN_STONE_SLAB);
        moltenStonePool.button(ModBlocks.MOLTEN_STONE_BUTTON);
        moltenStonePool.pressurePlate(ModBlocks.MOLTEN_STONE_PRESSURE_PLATE);
        moltenStonePool.fence(ModBlocks.MOLTEN_STONE_FENCE);
        moltenStonePool.fenceGate(ModBlocks.MOLTEN_STONE_FENCE_GATE);
        moltenStonePool.wall(ModBlocks.MOLTEN_STONE_WALL);

        infernalStonePool.stairs(ModBlocks.INFERNAL_STONE_STAIRS);
        infernalStonePool.slab(ModBlocks.INFERNAL_STONE_SLAB);
        infernalStonePool.button(ModBlocks.INFERNAL_STONE_BUTTON);
        infernalStonePool.pressurePlate(ModBlocks.INFERNAL_STONE_PRESSURE_PLATE);
        infernalStonePool.fence(ModBlocks.INFERNAL_STONE_FENCE);
        infernalStonePool.fenceGate(ModBlocks.INFERNAL_STONE_FENCE_GATE);
        infernalStonePool.wall(ModBlocks.INFERNAL_STONE_WALL);

        infernoEssencePlanksPool.stairs(ModBlocks.INFERNO_ESSENCE_STAIRS);
        infernoEssencePlanksPool.slab(ModBlocks.INFERNO_ESSENCE_SLAB);
        infernoEssencePlanksPool.button(ModBlocks.INFERNO_ESSENCE_BUTTON);
        infernoEssencePlanksPool.pressurePlate(ModBlocks.INFERNO_ESSENCE_PRESSURE_PLATE);
        infernoEssencePlanksPool.fence(ModBlocks.INFERNO_ESSENCE_FENCE);
        infernoEssencePlanksPool.fenceGate(ModBlocks.INFERNO_ESSENCE_FENCE_GATE);
        infernoEssencePlanksPool.wall(ModBlocks.INFERNO_ESSENCE_WALL);


        blockStateModelGenerator.registerDoor(ModBlocks.NETHER_RUBY_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.NETHER_RUBY_TRAPDOOR);

        blockStateModelGenerator.registerDoor(ModBlocks.FIRERITE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.FIRERITE_TRAPDOOR);

        blockStateModelGenerator.registerDoor(ModBlocks.BLAZE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.BLAZE_TRAPDOOR);

        blockStateModelGenerator.registerDoor(ModBlocks.EMBERSTONE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.EMBERSTONE_TRAPDOOR);

        blockStateModelGenerator.registerDoor(ModBlocks.INFERNAL_BRICKS_STONE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.INFERNAL_BRICKS_STONE_TRAPDOOR);

        blockStateModelGenerator.registerDoor(ModBlocks.MOLTEN_BRICKS_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.MOLTEN_BRICKS_TRAPDOOR);

        blockStateModelGenerator.registerDoor(ModBlocks.MOLTEN_STONE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.MOLTEN_STONE_TRAPDOOR);

        blockStateModelGenerator.registerDoor(ModBlocks.INFERNAL_STONE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.INFERNAL_STONE_TRAPDOOR);

        blockStateModelGenerator.registerDoor(ModBlocks.INFERNO_ESSENCE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.INFERNO_ESSENCE_TRAPDOOR);

        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.BLAZEBLOOM, ModBlocks.POTTED_BLAZEBLOOM, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.INFERNO_ESSENCE_SAPLING, ModBlocks.POTTED_INFERNO_ESSENCE_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);

        blockStateModelGenerator.registerLog(ModBlocks.INFERNO_ESSENCE_LOG).log(ModBlocks.INFERNO_ESSENCE_LOG).wood(ModBlocks.INFERNO_ESSENCE_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG).log(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG).wood(ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.INFERNO_ESSENCE_LEAVES);

        registerGrassBlock(blockStateModelGenerator, ModBlocks.INFERNAL_GRASS_BLOCK, "infernal_grass_top", "infernal_grass_side", "infernal_grass_bottom");
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.INFERNAL_DIRT_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.INFERNAL_OBSIDIAN_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.INFERNAL_EYE_STATUE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ROYAL_FIRE_BLOCK);
    }


    @Override
    public void generateItemModels( ItemModelGenerator itemModelGenerator ) {
        itemModelGenerator.register(ModItems.NETHER_RUBY, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_NETHER_RUBY, Models.GENERATED);

        itemModelGenerator.register(ModItems.FIRERITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_FIRERITE, Models.GENERATED);

        itemModelGenerator.register(ModItems.LAVA_FISH, Models.GENERATED);
        itemModelGenerator.register(ModItems.MAGMA_FISH, Models.GENERATED);
        itemModelGenerator.register(ModItems.FIRE_FISH, Models.GENERATED);
        itemModelGenerator.register(ModItems.INFERNAL_APPLE, Models.GENERATED);

        itemModelGenerator.register(ModItems.EMBERSTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_EMBERSTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.INFERNIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_INFERNIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.CINDERTSONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_CINDESTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYROCLAST, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_PYROCLAST, Models.GENERATED);

        itemModelGenerator.register(ModItems.NETHER_RUBY_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.NETHER_RUBY_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.NETHER_RUBY_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.NETHER_RUBY_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.NETHER_RUBY_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.EMBERSTONE_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.EMBERSTONE_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.EMBERSTONE_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.EMBERSTONE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.EMBERSTONE_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.CINDERSTONE_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.CINDERSTONE_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.CINDERSTONE_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.CINDERSTONE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.CINDERSTONE_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.INFERNIUM_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNIUM_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNIUM_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNIUM_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNIUM_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.PYROCLAST_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYROCLAST_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYROCLAST_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYROCLAST_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYROCLAST_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.FIRERITE_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.FIRERITE_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.FIRERITE_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.FIRERITE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.FIRERITE_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.INFERNUM_PAXEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNUM_DAGGER, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNUM_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNO_FANG, Models.HANDHELD);

        itemModelGenerator.registerArmor((ArmorItem) ModItems.NETHER_RUBY_HELMET);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.NETHER_RUBY_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.NETHER_RUBY_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.NETHER_RUBY_BOOTS);

        itemModelGenerator.registerArmor((ArmorItem) ModItems.CINDERSTONE_HELMET);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.CINDERSTONE_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.CINDERSTONE_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.CINDERSTONE_BOOTS);

        itemModelGenerator.registerArmor((ArmorItem) ModItems.EMBERSTONE_HELMET);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.EMBERSTONE_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.EMBERSTONE_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.EMBERSTONE_BOOTS);

        itemModelGenerator.registerArmor((ArmorItem) ModItems.INFERNIUM_HELMET);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.INFERNIUM_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.INFERNIUM_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.INFERNIUM_BOOTS);

        itemModelGenerator.registerArmor((ArmorItem) ModItems.PYROCLAST_HELMET);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.PYROCLAST_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.PYROCLAST_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.PYROCLAST_BOOTS);

        itemModelGenerator.registerArmor((ArmorItem) ModItems.FIRERITE_HELMET);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.FIRERITE_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.FIRERITE_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.FIRERITE_BOOTS);

        itemModelGenerator.register(ModItems.ASH_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASH_EGG, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMBER_ASH, Models.GENERATED);

        itemModelGenerator.register(ModItems.FLAME_STAFF, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNAL_GEM, Models.GENERATED);

        itemModelGenerator.register(ModItems.INFERNUM_STAFF, Models.HANDHELD);
        itemModelGenerator.register(ModItems.INFERNUM_BONE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.INFERNUM_HEROBRINE_RELIC, Models.GENERATED);
        itemModelGenerator.register(ModItems.INFERNAL_ROYAL_STAFF, Models.HANDHELD);

        itemModelGenerator.register(ModItems.ECHO_OF_DAMNATION, Models.GENERATED);
        itemModelGenerator.register(ModItems.INFERNAL_BEAST_HORN, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLAZE_HEART, Models.GENERATED);

        itemModelGenerator.register(ModItems.LAVAGER_ARROW, Models.GENERATED);

        itemModelGenerator.register(ModItems.CURSED_FLINT, Models.GENERATED);

        itemModelGenerator.register(ModItems.INFERNO_BOAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.INFERNO_CHEST_BOAT, Models.GENERATED);

        itemModelGenerator.register(ModItems.DEMON_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MALFURYX_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.OBSIDIAN_GHAST_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_HOARDE_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNO_ZOMBIE_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_ZOMBILAGER_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.FLAME_SKELETON_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_VOKER_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.LAVAGER_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.LAVACATOR_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.LAVA_WITCH_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.LAVA_SLIME_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_VEX_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_WRAITH_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MAGMA_STRIDER_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MAGMA_CREEPER_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MAGMA_SPIDER_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.EMBER_SERPENT_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNO_ENDERMAN_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNUM_HEROBRINE_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_BEAST_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNUM_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));


        itemModelGenerator.register(ModItems.INFERNAL_KNIGHT_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_EYE_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_HORSE_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.ASHBONE_HORSE_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.FLAME_HORSE_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.PYERLING_WYRN_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.INFERNAL_PHANTOM_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.EMBER_HUND_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.VOLCARNIS_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.SCORCHED_WOOLIE_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.ASH_CHICKEN_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.EMBER_BOAR_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.LAVA_FISH_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MAGMA_FISH_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.FIRE_FISH_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MAGMA_DOLPHIN_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));



    }

    // ** Custom Methods ** //

    /**
     * Registers a cube_bottom_top style block (e.g., grass-like blocks).
     *
     * @param blockStateModelGenerator The Fabric block state model generator.
     * @param block                    The block being registered.
     * @param topTexture               The name of the top texture (without "block/").
     * @param sideTexture              The name of the side texture (without "block/").
     */
    public void registerGrassBlock( BlockStateModelGenerator blockStateModelGenerator, Block block,
                                    String topTexture, String sideTexture, String bottomTexture ) {
        // Custom textures
        Identifier top = new Identifier(InfernumMod.MOD_ID, "block/" + topTexture);
        Identifier side = new Identifier(InfernumMod.MOD_ID, "block/" + sideTexture);
        Identifier bottom = new Identifier(InfernumMod.MOD_ID, "block/" + bottomTexture);

        // Build texture map
        TextureMap textures = new TextureMap()
                .put(TextureKey.BOTTOM, bottom)
                .put(TextureKey.TOP, top)
                .put(TextureKey.SIDE, side)
                .inherit(TextureKey.BOTTOM, TextureKey.PARTICLE);

        // Upload the cube_bottom_top model
        Identifier modelId = Models.CUBE_BOTTOM_TOP.upload(block, textures, blockStateModelGenerator.modelCollector);

        // Register blockstate using that model
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId))
        );

        // Also register item model so the block looks right in inventory
        blockStateModelGenerator.registerParentedItemModel(block, modelId);
    }
}

