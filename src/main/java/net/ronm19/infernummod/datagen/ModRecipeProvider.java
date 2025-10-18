package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.api.interfaces.ModRecipeProviderGenerator;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.server.recipe.RecipeProvider.*;

public class ModRecipeProvider extends FabricRecipeProvider implements ModRecipeProviderGenerator {

    private final static List<ItemConvertible> NETHER_RUBY_SMELTABLES = List.of(ModItems.RAW_NETHER_RUBY,
            ModBlocks.NETHER_RUBY_ORE, ModBlocks.DEEPSLATE_NETHER_RUBY_ORE);

    private final static List<ItemConvertible> FIRERITE_SMELTABLES = List.of(ModItems.RAW_FIRERITE,
            ModBlocks.FIRERITE_ORE, ModBlocks.DEEPSLATE_FIRERITE_ORE);

    private final static List<ItemConvertible> EMBERSTONE_SMELTABLES = List.of(ModItems.RAW_EMBERSTONE,
            ModBlocks.EMBERSTONE_ORE);

    private final static List<ItemConvertible> PYROCLAST_SMELTABLES = List.of(ModItems.RAW_PYROCLAST,
            ModBlocks.NETHER_PYROCLAST_ORE);

    private final static List<ItemConvertible> CINDERSTONE_SMELTABLES = List.of(ModItems.RAW_CINDESTONE,
            ModBlocks.DEEPSLATE_CINDERSTONE_ORE);

    private final static List<ItemConvertible> INFERNIUM_SMELTABLES = List.of(ModItems.RAW_INFERNIUM,
            ModBlocks.STONE_INFERNIUM_ORE);

    public ModRecipeProvider( FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture ) {
        super(output);
    }

    @Override
    public void generate( RecipeExporter exporter ) {
        offerSmelting(exporter, NETHER_RUBY_SMELTABLES, RecipeCategory.MISC, ModItems.NETHER_RUBY,
                0.7f, 200, "nether_ruby");
        offerBlasting(exporter, NETHER_RUBY_SMELTABLES, RecipeCategory.MISC, ModItems.NETHER_RUBY,
                0.7f, 100, "nether_ruby");

        offerSmelting(exporter, FIRERITE_SMELTABLES, RecipeCategory.MISC, ModItems.FIRERITE,
                0.7f, 200, "firerite");
        offerBlasting(exporter, FIRERITE_SMELTABLES, RecipeCategory.MISC, ModItems.FIRERITE,
                0.7f, 100, "firerite");

        offerSmelting(exporter, EMBERSTONE_SMELTABLES, RecipeCategory.MISC, ModItems.EMBERSTONE,
                0.7f, 200, "emberstone");
        offerBlasting(exporter, EMBERSTONE_SMELTABLES, RecipeCategory.MISC, ModItems.EMBERSTONE,
                0.7f, 100, "emberstone");

        offerSmelting(exporter, PYROCLAST_SMELTABLES, RecipeCategory.MISC, ModItems.PYROCLAST,
                0.7f, 200, "pyroclast");
        offerBlasting(exporter, PYROCLAST_SMELTABLES, RecipeCategory.MISC, ModItems.PYROCLAST,
                0.7f, 100, "pyroclast");

        offerSmelting(exporter, CINDERSTONE_SMELTABLES, RecipeCategory.MISC, ModItems.CINDERTSONE,
                0.7f, 200, "cinderstone");
        offerBlasting(exporter, CINDERSTONE_SMELTABLES, RecipeCategory.MISC, ModItems.CINDERTSONE,
                0.7f, 100, "cinderstone");

        offerSmelting(exporter, INFERNIUM_SMELTABLES, RecipeCategory.MISC, ModItems.INFERNIUM,
                0.7f, 200, "infernium");
        offerBlasting(exporter, INFERNIUM_SMELTABLES, RecipeCategory.MISC, ModItems.INFERNIUM,
                0.7f, 100, "infernium");

        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.NETHER_RUBY, RecipeCategory.DECORATIONS,
                ModBlocks.NETHER_RUBY_BLOCK);

        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.FIRERITE, RecipeCategory.DECORATIONS,
                ModBlocks.FIRERITE_BLOCK);

        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.EMBERSTONE, RecipeCategory.DECORATIONS,
                ModBlocks.EMBERSTONE_BLOCK);

        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Items.BLAZE_ROD, RecipeCategory.DECORATIONS,
                ModBlocks.BLAZE_BLOCK);

        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.ASH_DUST, RecipeCategory.DECORATIONS,
                ModBlocks.ASH_BLOCK);

        // Raw Nether Ruby (surrounded by stone)
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RAW_NETHER_RUBY, 1)
                .pattern("SSS")
                .pattern("SNS")
                .pattern("SSS")
                .input('S', Items.STONE)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(Items.STONE), conditionsFromItem(Items.STONE))
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RAW_NETHER_RUBY)));

// Nether Ruby Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.NETHER_RUBY_STAIRS, 4)
                .pattern("N  ")
                .pattern("NN ")
                .pattern("NNN")
                .input('N', ModBlocks.NETHER_RUBY_BLOCK)
                .criterion(hasItem(ModBlocks.NETHER_RUBY_BLOCK), conditionsFromItem(ModBlocks.NETHER_RUBY_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.NETHER_RUBY_STAIRS)));

// Nether Ruby Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.NETHER_RUBY_TRAPDOOR, 2)
                .pattern("NNN")
                .pattern("NNN")
                .input('N', ModBlocks.NETHER_RUBY_BLOCK)
                .criterion(hasItem(ModBlocks.NETHER_RUBY_BLOCK), conditionsFromItem(ModBlocks.NETHER_RUBY_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.NETHER_RUBY_TRAPDOOR)));

// Nether Ruby Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.NETHER_RUBY_WALL, 6)
                .pattern("NNN")
                .input('N', ModBlocks.NETHER_RUBY_BLOCK)
                .criterion(hasItem(ModBlocks.NETHER_RUBY_BLOCK), conditionsFromItem(ModBlocks.NETHER_RUBY_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.NETHER_RUBY_WALL)));

// Nether Ruby Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.NETHER_RUBY_FENCE, 3)
                .pattern("NSN")
                .pattern("NSN")
                .input('N', ModBlocks.NETHER_RUBY_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.NETHER_RUBY_BLOCK), conditionsFromItem(ModBlocks.NETHER_RUBY_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.NETHER_RUBY_FENCE)));

// Nether Ruby Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.NETHER_RUBY_FENCE_GATE, 1)
                .pattern("SNS")
                .pattern("SNS")
                .input('N', ModBlocks.NETHER_RUBY_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.NETHER_RUBY_BLOCK), conditionsFromItem(ModBlocks.NETHER_RUBY_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.NETHER_RUBY_FENCE_GATE)));

// Nether Ruby Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.NETHER_RUBY_BUTTON, 1)
                .pattern("   ")
                .pattern(" N ")
                .pattern("   ")
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.NETHER_RUBY_BUTTON)));

// Nether Ruby Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.NETHER_RUBY_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("NN ")
                .pattern("   ")
                .input('N', ModBlocks.NETHER_RUBY_BLOCK)
                .criterion(hasItem(ModBlocks.NETHER_RUBY_BLOCK), conditionsFromItem(ModBlocks.NETHER_RUBY_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.NETHER_RUBY_PRESSURE_PLATE)));

// Nether Ruby Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.NETHER_RUBY_DOOR, 3)
                .pattern("NN ")
                .pattern("NN ")
                .pattern("NN ")
                .input('N', ModBlocks.NETHER_RUBY_BLOCK)
                .criterion(hasItem(ModBlocks.NETHER_RUBY_BLOCK), conditionsFromItem(ModBlocks.NETHER_RUBY_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.NETHER_RUBY_DOOR)));


        // Firerite Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FIRERITE_STAIRS, 4)
                .pattern("F  ")
                .pattern("FF ")
                .pattern("FFF")
                .input('F', ModBlocks.FIRERITE_BLOCK)
                .criterion(hasItem(ModBlocks.FIRERITE_BLOCK), conditionsFromItem(ModBlocks.FIRERITE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.FIRERITE_STAIRS)));

// Firerite Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.FIRERITE_TRAPDOOR, 2)
                .pattern("FFF")
                .input('F', ModBlocks.FIRERITE_BLOCK)
                .criterion(hasItem(ModBlocks.FIRERITE_BLOCK), conditionsFromItem(ModBlocks.FIRERITE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.FIRERITE_TRAPDOOR)));

// Firerite Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FIRERITE_WALL, 6)
                .pattern("FFF")
                .pattern("FFF")
                .input('F', ModBlocks.FIRERITE_BLOCK)
                .criterion(hasItem(ModBlocks.FIRERITE_BLOCK), conditionsFromItem(ModBlocks.FIRERITE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.FIRERITE_WALL)));

// Firerite Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FIRERITE_FENCE, 3)
                .pattern("FSF")
                .pattern("FSF")
                .input('F', ModBlocks.FIRERITE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.FIRERITE_BLOCK), conditionsFromItem(ModBlocks.FIRERITE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.FIRERITE_FENCE)));

// Firerite Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.FIRERITE_FENCE_GATE, 1)
                .pattern("SFS")
                .pattern("SFS")
                .input('F', ModBlocks.FIRERITE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.FIRERITE_BLOCK), conditionsFromItem(ModBlocks.FIRERITE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.FIRERITE_FENCE_GATE)));

// Firerite Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.FIRERITE_BUTTON, 1)
                .pattern("   ")
                .pattern(" F ")
                .pattern("   ")
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.FIRERITE_BUTTON)));

// Firerite Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.FIRERITE_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("FF ")
                .pattern("   ")
                .input('F', ModBlocks.FIRERITE_BLOCK)
                .criterion(hasItem(ModBlocks.FIRERITE_BLOCK), conditionsFromItem(ModBlocks.FIRERITE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.FIRERITE_PRESSURE_PLATE)));

// Firerite Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FIRERITE_DOOR, 3)
                .pattern("FF ")
                .pattern("FF ")
                .pattern("FF ")
                .input('F', ModBlocks.FIRERITE_BLOCK)
                .criterion(hasItem(ModBlocks.FIRERITE_BLOCK), conditionsFromItem(ModBlocks.FIRERITE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.FIRERITE_DOOR)));


        // Blaze Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLAZE_STAIRS, 4)
                .pattern("B  ")
                .pattern("BB ")
                .pattern("BBB")
                .input('B', ModBlocks.BLAZE_BLOCK)
                .criterion(hasItem(ModBlocks.BLAZE_BLOCK), conditionsFromItem(ModBlocks.BLAZE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLAZE_STAIRS)));

// Blaze Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.BLAZE_TRAPDOOR, 2)
                .pattern("BBB")
                .input('B', ModBlocks.BLAZE_BLOCK)
                .criterion(hasItem(ModBlocks.BLAZE_BLOCK), conditionsFromItem(ModBlocks.BLAZE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLAZE_TRAPDOOR)));

// Blaze Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLAZE_WALL, 6)
                .pattern("BBB")
                .pattern("BBB")
                .input('B', ModBlocks.BLAZE_BLOCK)
                .criterion(hasItem(ModBlocks.BLAZE_BLOCK), conditionsFromItem(ModBlocks.BLAZE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLAZE_WALL)));

// Blaze Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLAZE_FENCE, 3)
                .pattern("BSB")
                .pattern("BSB")
                .input('B', ModBlocks.BLAZE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.BLAZE_BLOCK), conditionsFromItem(ModBlocks.BLAZE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLAZE_FENCE)));

// Blaze Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.BLAZE_FENCE_GATE, 1)
                .pattern("SBS")
                .pattern("SBS")
                .input('B', ModBlocks.BLAZE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.BLAZE_BLOCK), conditionsFromItem(ModBlocks.BLAZE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLAZE_FENCE_GATE)));

// Blaze Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.BLAZE_BUTTON, 1)
                .pattern("   ")
                .pattern(" BS")
                .pattern("   ")
                .input('B', Items.BLAZE_ROD)
                .input('S', Blocks.STONE_BUTTON)
                .criterion(hasItem(Items.BLAZE_ROD), conditionsFromItem(Items.BLAZE_ROD))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLAZE_BUTTON)));

// Blaze Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.BLAZE_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("BB ")
                .pattern("   ")
                .input('B', ModBlocks.BLAZE_BLOCK)
                .criterion(hasItem(ModBlocks.BLAZE_BLOCK), conditionsFromItem(ModBlocks.BLAZE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLAZE_PRESSURE_PLATE)));

// Blaze Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLAZE_DOOR, 3)
                .pattern("BB ")
                .pattern("BB ")
                .pattern("BB ")
                .input('B', ModBlocks.BLAZE_BLOCK)
                .criterion(hasItem(ModBlocks.BLAZE_BLOCK), conditionsFromItem(ModBlocks.BLAZE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLAZE_DOOR)));

        // Emberstone Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.EMBERSTONE_STAIRS, 4)
                .pattern("E  ")
                .pattern("EE ")
                .pattern("EEE")
                .input('E', ModBlocks.EMBERSTONE_BLOCK)
                .criterion(hasItem(ModBlocks.EMBERSTONE_BLOCK), conditionsFromItem(ModBlocks.EMBERSTONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.EMBERSTONE_STAIRS)));

// Emberstone Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.EMBERSTONE_TRAPDOOR, 2)
                .pattern("EEE")
                .input('E', ModBlocks.EMBERSTONE_BLOCK)
                .criterion(hasItem(ModBlocks.EMBERSTONE_BLOCK), conditionsFromItem(ModBlocks.EMBERSTONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.EMBERSTONE_TRAPDOOR)));

// Emberstone Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.EMBERSTONE_WALL, 6)
                .pattern("EEE")
                .pattern("EEE")
                .input('E', ModBlocks.EMBERSTONE_BLOCK)
                .criterion(hasItem(ModBlocks.EMBERSTONE_BLOCK), conditionsFromItem(ModBlocks.EMBERSTONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.EMBERSTONE_WALL)));

// Emberstone Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.EMBERSTONE_FENCE, 3)
                .pattern("ESE")
                .pattern("ESE")
                .input('E', ModBlocks.EMBERSTONE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.EMBERSTONE_BLOCK), conditionsFromItem(ModBlocks.EMBERSTONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.EMBERSTONE_FENCE)));

// Emberstone Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.EMBERSTONE_FENCE_GATE, 1)
                .pattern("SES")
                .pattern("SES")
                .input('E', ModBlocks.EMBERSTONE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.EMBERSTONE_BLOCK), conditionsFromItem(ModBlocks.EMBERSTONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.EMBERSTONE_FENCE_GATE)));

// Emberstone Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.EMBERSTONE_BUTTON, 1)
                .pattern("   ")
                .pattern(" ES")
                .pattern("   ")
                .input('E', ModItems.EMBERSTONE)
                .input('S', Blocks.STONE_BUTTON)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.EMBERSTONE_BUTTON)));

// Emberstone Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.EMBERSTONE_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("EE ")
                .pattern("   ")
                .input('E', ModBlocks.EMBERSTONE_BLOCK)
                .criterion(hasItem(ModBlocks.EMBERSTONE_BLOCK), conditionsFromItem(ModBlocks.EMBERSTONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.EMBERSTONE_PRESSURE_PLATE)));

// Emberstone Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.EMBERSTONE_DOOR, 3)
                .pattern("EE ")
                .pattern("EE ")
                .pattern("EE ")
                .input('E', ModBlocks.EMBERSTONE_BLOCK)
                .criterion(hasItem(ModBlocks.EMBERSTONE_BLOCK), conditionsFromItem(ModBlocks.EMBERSTONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.EMBERSTONE_DOOR)));

        // Infernal Bricks Stone Block (4 stones = 1 brick, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_BRICKS_STONE_BLOCK, 1)
                .pattern("II ")
                .pattern("II ")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)));

        // Infernal Bricks Stone Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_BRICKS_STONE_STAIRS, 4)
                .pattern("I  ")
                .pattern("II ")
                .pattern("III")
                .input('I', ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_STAIRS)));

// Infernal Bricks Stone Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNAL_BRICKS_STONE_TRAPDOOR, 2)
                .pattern("III")
                .input('I', ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_TRAPDOOR)));

// Infernal Bricks Stone Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_BRICKS_STONE_WALL, 6)
                .pattern("III")
                .pattern("III")
                .input('I', ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_WALL)));

// Infernal Bricks Stone Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_BRICKS_STONE_FENCE, 3)
                .pattern("ISI")
                .pattern("ISI")
                .input('I', ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_FENCE)));

// Infernal Bricks Stone Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNAL_BRICKS_STONE_FENCE_GATE, 1)
                .pattern("SIS")
                .pattern("SIS")
                .input('I', ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_FENCE_GATE)));

// Infernal Bricks Stone Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNAL_BRICKS_STONE_BUTTON, 1)
                .pattern("   ")
                .pattern(" I ")
                .pattern("   ")
                .input('I', ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_BUTTON)));

// Infernal Bricks Stone Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNAL_BRICKS_STONE_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("II ")
                .pattern("   ")
                .input('I', ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_PRESSURE_PLATE)));

// Infernal Bricks Stone Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_BRICKS_STONE_DOOR, 3)
                .pattern("II ")
                .pattern("II ")
                .pattern("II ")
                .input('I', ModBlocks.INFERNAL_BRICKS_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_BRICKS_STONE_DOOR)));

        // Molten Bricks Stone Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_BRICKS_STAIRS, 4)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .input('M', ModBlocks.MOLTEN_BRICKS_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_BRICKS_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_BRICKS_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_BRICKS_STAIRS)));

// Molten Bricks Stone Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MOLTEN_BRICKS_TRAPDOOR, 2)
                .pattern("MMM")
                .input('M', ModBlocks.MOLTEN_BRICKS_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_BRICKS_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_BRICKS_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_BRICKS_TRAPDOOR)));

// Molten Bricks Stone Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_BRICKS_WALL, 6)
                .pattern("MMM")
                .pattern("MMM")
                .input('M', ModBlocks.MOLTEN_BRICKS_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_BRICKS_WALL), conditionsFromItem(ModBlocks.MOLTEN_BRICKS_WALL))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_BRICKS_WALL)));

// Molten Bricks Stone Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_BRICKS_FENCE, 3)
                .pattern("MSM")
                .pattern("MSM")
                .input('M', ModBlocks.MOLTEN_BRICKS_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.MOLTEN_BRICKS_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_BRICKS_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_BRICKS_FENCE)));

// Molten Bricks Stone Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MOLTEN_BRICKS_FENCE_GATE, 1)
                .pattern("SMS")
                .pattern("SMS")
                .input('M', ModBlocks.MOLTEN_BRICKS_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.MOLTEN_BRICKS_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_BRICKS_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_BRICKS_FENCE_GATE)));

// Molten Bricks Stone Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MOLTEN_BRICKS_BUTTON, 1)
                .pattern("   ")
                .pattern(" M ")
                .pattern("   ")
                .input('M', ModBlocks.MOLTEN_BRICKS_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_BRICKS_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_BRICKS_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_BRICKS_BUTTON)));

// Molten Bricks Stone Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MOLTEN_BRICKS_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("MM ")
                .pattern("   ")
                .input('M', ModBlocks.MOLTEN_BRICKS_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_BRICKS_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_BRICKS_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_BRICKS_PRESSURE_PLATE)));

// Molten Bricks Stone Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_BRICKS_DOOR, 3)
                .pattern("MM ")
                .pattern("MM ")
                .pattern("MM ")
                .input('M', ModBlocks.MOLTEN_BRICKS_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_BRICKS_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_BRICKS_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_BRICKS_DOOR)));

        // Molten Stone Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_STONE_STAIRS, 4)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_STONE_STAIRS)));

// Molten Stone Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MOLTEN_STONE_TRAPDOOR, 2)
                .pattern("MMM")
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_STONE_TRAPDOOR)));

// Molten Stone Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_STONE_WALL, 6)
                .pattern("MMM")
                .pattern("MMM")
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_STONE_WALL)));

// Molten Stone Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_STONE_FENCE, 3)
                .pattern("MSM")
                .pattern("MSM")
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_STONE_FENCE)));

// Molten Stone Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MOLTEN_STONE_FENCE_GATE, 1)
                .pattern("SMS")
                .pattern("SMS")
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_STONE_FENCE_GATE)));

// Molten Stone Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MOLTEN_STONE_BUTTON, 1)
                .pattern("   ")
                .pattern(" M ")
                .pattern("   ")
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_STONE_BUTTON)));

// Molten Stone Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MOLTEN_STONE_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("MM ")
                .pattern("   ")
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_STONE_PRESSURE_PLATE)));

// Molten Stone Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_STONE_DOOR, 3)
                .pattern("MM ")
                .pattern("MM ")
                .pattern("MM ")
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_STONE_DOOR)));

        // Infernal Stone Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_STONE_STAIRS, 4)
                .pattern("I  ")
                .pattern("II ")
                .pattern("III")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_STONE_STAIRS)));

// Infernal Stone Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNAL_STONE_TRAPDOOR, 2)
                .pattern("III")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_STONE_TRAPDOOR)));

// Infernal Stone Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_STONE_WALL, 6)
                .pattern("III")
                .pattern("III")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_WALL), conditionsFromItem(ModBlocks.INFERNAL_STONE_WALL))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_STONE_WALL)));

// Infernal Stone Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_STONE_FENCE, 3)
                .pattern("ISI")
                .pattern("ISI")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_STONE_FENCE)));

// Infernal Stone Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNAL_STONE_FENCE_GATE, 1)
                .pattern("SIS")
                .pattern("SIS")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_STONE_FENCE_GATE)));

// Infernal Stone Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNAL_STONE_BUTTON, 1)
                .pattern("   ")
                .pattern(" I ")
                .pattern("   ")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_STONE_BUTTON)));

// Infernal Stone Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNAL_STONE_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("II ")
                .pattern("   ")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_STONE_PRESSURE_PLATE)));

// Infernal Stone Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_STONE_DOOR, 3)
                .pattern("II ")
                .pattern("II ")
                .pattern("II ")
                .input('I', ModBlocks.INFERNAL_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.INFERNAL_STONE_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_STONE_DOOR)));


        // Nether Ruby Pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_PICKAXE, 1)
                .pattern("NNN")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_PICKAXE)));

        // Nether Ruby Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_AXE, 1)
                .pattern("NN ")
                .pattern("NS ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_AXE)));

        // Nether Ruby Shovel
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_SHOVEL, 1)
                .pattern(" N ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_SHOVEL)));

        // Nether Ruby Sword
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_SWORD, 1)
                .pattern(" N ")
                .pattern(" N ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_SWORD)));

        // Nether Ruby Hoe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_HOE, 1)
                .pattern("NN ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_HOE)));

        // Emberstone Pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_PICKAXE, 1)
                .pattern("EEE")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_PICKAXE)));

        // Emberstone Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_AXE, 1)
                .pattern("EE ")
                .pattern("ES ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_AXE)));

        // Emberstone Shovel
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_SHOVEL, 1)
                .pattern(" E ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_SHOVEL)));

        // Emberstone Sword
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_SWORD, 1)
                .pattern(" E ")
                .pattern(" E ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_SWORD)));

        // Emberstone Hoe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_HOE, 1)
                .pattern("EE ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_HOE)));


        // Cinderstone Pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_PICKAXE, 1)
                .pattern("CCC")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_PICKAXE)));

        // Cinderstone Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_AXE, 1)
                .pattern("CC ")
                .pattern("CS ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_AXE)));

        // Cinderstone Shovel
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_SHOVEL, 1)
                .pattern(" C ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_SHOVEL)));

        // Cinderstone Sword
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_SWORD, 1)
                .pattern(" C ")
                .pattern(" C ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_SWORD)));

        // Cinderstone Hoe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_HOE, 1)
                .pattern("CC ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_HOE)));


        // Infernium Pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_PICKAXE, 1)
                .pattern("III")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_PICKAXE)));

        // Infernium Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_AXE, 1)
                .pattern("II ")
                .pattern("IS ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_AXE)));

        // Infernium Shovel
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_SHOVEL, 1)
                .pattern(" I ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_SHOVEL)));

        // Infernium Sword
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_SWORD, 1)
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_SWORD)));

        // Infernium Hoe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_HOE, 1)
                .pattern("II ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_HOE)));


        // Pyroclast Pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_PICKAXE, 1)
                .pattern("PPP")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_PICKAXE)));

        // Pyroclast Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_AXE, 1)
                .pattern("PP ")
                .pattern("PS ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_AXE)));

        // Pyroclast Shovel
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_SHOVEL, 1)
                .pattern(" P ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_SHOVEL)));

        // Pyroclast Sword
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_SWORD, 1)
                .pattern(" P ")
                .pattern(" P ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_SWORD)));

        // Pyroclast Hoe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_HOE, 1)
                .pattern("PP ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_HOE)));


        // Firerite Pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_PICKAXE, 1)
                .pattern("FFF")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_PICKAXE)));

        // Firerite  Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_AXE, 1)
                .pattern("FF ")
                .pattern("FS ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_AXE)));

        // Firerite  Shovel
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_SHOVEL, 1)
                .pattern(" F ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_SHOVEL)));

        // Firerite  Sword
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_SWORD, 1)
                .pattern(" F ")
                .pattern(" F ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_SWORD)));

        // Firerite  Hoe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_HOE, 1)
                .pattern("FF ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_HOE)));


        // Nether Ruby Helmet
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_HELMET, 1)
                .pattern("NNN")
                .pattern("N N")
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_HELMET)));

        // Nether Ruby Chestplate
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_CHESTPLATE, 1)
                .pattern("N N")
                .pattern("NNN")
                .pattern("NNN")
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_CHESTPLATE)));

        // Nether Ruby Leggings
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_LEGGINGS, 1)
                .pattern("NNN")
                .pattern("N N")
                .pattern("N N")
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_LEGGINGS)));

        // Nether Ruby Boots
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NETHER_RUBY_BOOTS, 1)
                .pattern("N N")
                .pattern("N N")
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NETHER_RUBY_BOOTS)));


        // Cinderstone Helmet
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_HELMET, 1)
                .pattern("CCC")
                .pattern("C C")
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_HELMET)));

        // Cinderstone  Chestplate
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_CHESTPLATE, 1)
                .pattern("C C")
                .pattern("CCC")
                .pattern("CCC")
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_CHESTPLATE)));

        // Cinderstone Leggings
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_LEGGINGS, 1)
                .pattern("CCC")
                .pattern("C C")
                .pattern("C C")
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_LEGGINGS)));

        // Cinderstone  Boots
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CINDERSTONE_BOOTS, 1)
                .pattern("C C")
                .pattern("C C")
                .input('C', ModItems.CINDERTSONE)
                .criterion(hasItem(ModItems.CINDERTSONE), conditionsFromItem(ModItems.CINDERTSONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CINDERSTONE_BOOTS)));


        // Emberstone Helmet
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_HELMET, 1)
                .pattern("EEE")
                .pattern("E E")
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_HELMET)));

        // Emberstone Chestplate
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_CHESTPLATE, 1)
                .pattern("E E")
                .pattern("EEE")
                .pattern("EEE")
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_CHESTPLATE)));

        // Emberstone Leggings
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_LEGGINGS, 1)
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_LEGGINGS)));

        // Emberstone Boots
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBERSTONE_BOOTS, 1)
                .pattern("E E")
                .pattern("E E")
                .input('E', ModItems.EMBERSTONE)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBERSTONE_BOOTS)));


        // Infernium Helmet
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_HELMET, 1)
                .pattern("III")
                .pattern("I I")
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_HELMET)));

        // Infernium Chestplate
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_CHESTPLATE, 1)
                .pattern("I I")
                .pattern("III")
                .pattern("III")
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_CHESTPLATE)));

        // Infernium Leggings
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_LEGGINGS, 1)
                .pattern("III")
                .pattern("I I")
                .pattern("I I")
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_LEGGINGS)));

        // Infernium Boots
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNIUM_BOOTS, 1)
                .pattern("I I")
                .pattern("I I")
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNIUM_BOOTS)));


        // Pyroclast Helmet
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_HELMET, 1)
                .pattern("PPP")
                .pattern("P P")
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_HELMET)));

        // Pyroclast Chestplate
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_CHESTPLATE, 1)
                .pattern("P P")
                .pattern("PPP")
                .pattern("PPP")
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_CHESTPLATE)));

        // Pyroclast Leggings
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_LEGGINGS, 1)
                .pattern("PPP")
                .pattern("P P")
                .pattern("P P")
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_LEGGINGS)));

        // Pyroclast Boots
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PYROCLAST_BOOTS, 1)
                .pattern("P P")
                .pattern("P P")
                .input('P', ModItems.PYROCLAST)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYROCLAST_BOOTS)));


        // Firerite Helmet
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_HELMET, 1)
                .pattern("FFF")
                .pattern("F F")
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_HELMET)));

        // Firerite Chestplate
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_CHESTPLATE, 1)
                .pattern("F F")
                .pattern("FFF")
                .pattern("FFF")
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_CHESTPLATE)));

        // Firerite Leggings
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_LEGGINGS, 1)
                .pattern("FFF")
                .pattern("F F")
                .pattern("F F")
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_LEGGINGS)));

        // Firerite Boots
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRERITE_BOOTS, 1)
                .pattern("F F")
                .pattern("F F")
                .input('F', ModItems.FIRERITE)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FIRERITE_BOOTS)));


        // Inferno Essence Planks
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNO_ESSENCE_PLANKS, 4)
                .pattern("  ")
                .pattern("I ")
                .input('I', ModBlocks.INFERNO_ESSENCE_LOG)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_LOG), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_LOG))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_PLANKS)));

        // Inferno Boat
        ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, ModItems.INFERNO_BOAT, 1)
                .pattern("I I")
                .pattern("III")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNO_BOAT)));

        // Inferno Chest Boat
        ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, ModItems.INFERNO_CHEST_BOAT, 1)
                .pattern("ICI")
                .pattern("III")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .input('C', Blocks.CHEST)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNO_CHEST_BOAT)));


        // Inferno Essence Stairs (4 stairs, vanilla pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNO_ESSENCE_STAIRS, 4)
                .pattern("I  ")
                .pattern("II ")
                .pattern("III")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_STAIRS)));

// Inferno Essence Trapdoor (6 rubies = 2 trapdoors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNO_ESSENCE_TRAPDOOR, 2)
                .pattern("III")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_TRAPDOOR)));

// Inferno Essence Wall (6 rubies = 6 walls, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNO_ESSENCE_WALL, 6)
                .pattern("III")
                .pattern("III")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_WALL)));

// Inferno Essence Fence (4 rubies + 2 sticks = 3 fences, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNO_ESSENCE_FENCE, 3)
                .pattern("ISI")
                .pattern("ISI")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_FENCE)));

// Inferno Essence Fence Gate (4 rubies + 2 sticks = 1 gate, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNO_ESSENCE_FENCE_GATE, 1)
                .pattern("SIS")
                .pattern("SIS")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .input('S', Items.STICK)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_FENCE_GATE)));

// Inferno Essence Button (1 ruby = 1 button, shapeless style → 3x3 with ruby in middle)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNO_ESSENCE_BUTTON, 1)
                .pattern("   ")
                .pattern(" I ")
                .pattern("   ")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_BUTTON)));

// Inferno Essence Pressure Plate (2 rubies side by side, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.INFERNO_ESSENCE_PRESSURE_PLATE, 1)
                .pattern("   ")
                .pattern("II ")
                .pattern("   ")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_PRESSURE_PLATE)));

// Inferno Essence Door (6 rubies = 3 doors, vanilla)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNO_ESSENCE_DOOR, 3)
                .pattern("II ")
                .pattern("II ")
                .pattern("II ")
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .criterion(hasItem(ModBlocks.INFERNO_ESSENCE_PLANKS), conditionsFromItem(ModBlocks.INFERNO_ESSENCE_PLANKS))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNO_ESSENCE_DOOR)));

        // Abyssium Stone (7 Obsidians, 1 Ash Block)
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ABYSSIUM_STONE_BLOCK, 4)
                .pattern("OOO")
                .pattern("OGO")
                .pattern("OOO")
                .input('G', Blocks.GRAVEL)
                .input('O', Blocks.OBSIDIAN)
                .criterion(hasItem(Blocks.OBSIDIAN), conditionsFromItem(Blocks.OBSIDIAN))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.ABYSSIUM_STONE_BLOCK)));

        // Flame Staff
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FLAME_STAFF, 1)
                .pattern(" A ")
                .pattern(" A ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('A', ModItems.ASH_DUST)
                .criterion(hasItem(ModItems.ASH_DUST), conditionsFromItem(ModItems.ASH_DUST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.FLAME_STAFF)));

        // Infernum Paxel
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.INFERNUM_PAXEL, 1)
                .pattern("NNN")
                .pattern("NS ")
                .pattern(" S ")
                .input('S', Items.STICK)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNUM_PAXEL)));

        // Infernum Dagger
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.INFERNUM_DAGGER, 1)
                .pattern(" N ")
                .pattern(" S ")
                .pattern(" N ")
                .input('S', Items.STICK)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNUM_DAGGER)));

        // Inferno Fang
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.INFERNO_FANG, 1)
                .pattern(" I ")
                .pattern(" F ")
                .pattern(" I ")
                .input('F', ModItems.FIRERITE)
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNO_FANG)));

        // Inferno Fang
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNUM_STAFF, 1)
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" I ")
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNUM_STAFF)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.INFERNO_SHIELD, 1)
                .pattern("IFI")
                .pattern("III")
                .pattern(" I ")
                .input('F', ModItems.FIRERITE)
                .input('I', ModBlocks.INFERNO_ESSENCE_PLANKS)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNO_SHIELD)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_RUNE_BLOCK, 1)
                .pattern("III")
                .pattern("IPI")
                .pattern("III")
                .input('P', ModItems.PYROCLAST)
                .input('I', ModItems.INFERNIUM)
                .criterion(hasItem(ModItems.PYROCLAST), conditionsFromItem(ModItems.PYROCLAST))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_RUNE_BLOCK)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOLTEN_GRANITE_BLOCK, 1)
                .pattern("MMM")
                .pattern("MGM")
                .pattern("MMM")
                .input('G', Blocks.GRANITE)
                .input('M', ModBlocks.MOLTEN_STONE_BLOCK)
                .criterion(hasItem(ModBlocks.MOLTEN_STONE_BLOCK), conditionsFromItem(ModBlocks.MOLTEN_STONE_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MOLTEN_GRANITE_BLOCK)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_OBSIDIAN_BLOCK, 1)
                .pattern("MMM")
                .pattern("MOM")
                .pattern("MMM")
                .input('M', Blocks.MAGMA_BLOCK)
                .input('O', Blocks.OBSIDIAN)
                .criterion(hasItem(Blocks.MAGMA_BLOCK), conditionsFromItem(Blocks.MAGMA_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_OBSIDIAN_BLOCK)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_EYE_STATUE_BLOCK, 1)
                .pattern("AAA")
                .pattern("AEA")
                .pattern("AAA")
                .input('A', ModBlocks.ASH_BLOCK)
                .input('E', Items.ENDER_EYE)
                .criterion(hasItem(ModBlocks.ASH_BLOCK), conditionsFromItem(ModBlocks.ASH_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_EYE_STATUE_BLOCK)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CURSED_FLINT, 1)
                .pattern(" S ")
                .pattern(" F ")
                .pattern(" S ")
                .input('F', Items.FLINT)
                .input('S', Blocks.SOUL_SAND)
                .criterion(hasItem(Blocks.SOUL_SAND), conditionsFromItem(Blocks.SOUL_SAND))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CURSED_FLINT)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ECHO_OF_DAMNATION, 1)
                .pattern("III")
                .pattern("IBI")
                .pattern("III")
                .input('I', ModItems.INFERNAL_BEAST_HORN)
                .input('B', ModItems.BLAZE_HEART)
                .criterion(hasItem(ModItems.BLAZE_HEART), conditionsFromItem(ModItems.BLAZE_HEART))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.ECHO_OF_DAMNATION)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARRED_WOOL_BLOCK, 1)
                .pattern("WWW")
                .pattern("WBW")
                .pattern("WWW")
                .input('W', Items.WHITE_WOOL)
                .input('B', Items.BLAZE_POWDER)
                .criterion(hasItem(Items.BLAZE_POWDER), conditionsFromItem(Items.BLAZE_POWDER))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.CHARRED_WOOL_BLOCK)));


        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_FORGE_BLOCK, 1)
                .pattern("III")
                .pattern("IBI")
                .pattern("III")
                .input('I', ModBlocks.INFERNAL_OBSIDIAN_BLOCK)
                .input('B', Items.BLAZE_POWDER)
                .criterion(hasItem(ModBlocks.INFERNAL_OBSIDIAN_BLOCK), conditionsFromItem(ModBlocks.INFERNAL_OBSIDIAN_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_FORGE_BLOCK)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFERNAL_GRASS_BLOCK, 1)
                .pattern("MMM")
                .pattern("MGM")
                .pattern("MMM")
                .input('M', Blocks.MAGMA_BLOCK)
                .input('G', Items.GRASS)
                .criterion(hasItem(Blocks.MAGMA_BLOCK), conditionsFromItem(Blocks.MAGMA_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFERNAL_GRASS_BLOCK)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNUM_HEROBRINE_RELIC, 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .input('I', ModItems.INFERNAL_BEAST_HORN)
                .input('C', ModItems.CURSED_FLINT)
                .criterion(hasItem(ModItems.CURSED_FLINT), conditionsFromItem(ModItems.CURSED_FLINT))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNUM_HEROBRINE_RELIC)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.INFERNAL_APPLE, 1)
                .pattern("MMM")
                .pattern("MAM")
                .pattern("MMM")
                .input('M', Blocks.MAGMA_BLOCK)
                .input('A', Items.APPLE)
                .criterion(hasItem(Blocks.MAGMA_BLOCK), conditionsFromItem(Blocks.MAGMA_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName( ModItems.INFERNAL_APPLE)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMBER_ASH, 1)
                .pattern("   ")
                .pattern(" A ")
                .pattern(" E ")
                .input('E', ModItems.EMBERSTONE)
                .input('A', ModItems.ASH_DUST)
                .criterion(hasItem(ModItems.EMBERSTONE), conditionsFromItem(ModItems.EMBERSTONE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.EMBER_ASH)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.LAVAGER_ARROW, 1)
                .pattern("  F")
                .pattern(" S ")
                .pattern("S  ")
                .input('F', ModItems.FIRERITE)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.LAVAGER_ARROW)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNAL_GEM, 1)
                .pattern(" F ")
                .pattern(" D ")
                .pattern("   ")
                .input('F', ModItems.FIRERITE)
                .input('D', Items.DIAMOND)
                .criterion(hasItem(ModItems.FIRERITE), conditionsFromItem(ModItems.FIRERITE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNAL_GEM)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNAL_ROYAL_STAFF, 1)
                .pattern(" I ")
                .pattern(" N ")
                .pattern(" S ")
                .input('I', ModItems.INFERNIUM)
                .input('N', ModItems.NETHER_RUBY)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.INFERNIUM), conditionsFromItem(ModItems.INFERNIUM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNAL_ROYAL_STAFF)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.INFERNUM_SWORD, 1)
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" S ")
                .input('I', ModItems.INFERNAL_GEM)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.INFERNAL_GEM), conditionsFromItem(ModItems.INFERNAL_GEM))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.INFERNUM_SWORD)));
    }
}


