package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.item.ModItems;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
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

    public ModRecipeProvider( FabricDataOutput output ) {
        super(output);
    }

    @Override
    public void generate( Consumer<RecipeJsonProvider> exporter ) {
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

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RAW_NETHER_RUBY, 1)
                .pattern("SSS")
                .pattern("SNS")
                .pattern("SSS")
                .input('S', Items.STONE)
                .input('N', ModItems.NETHER_RUBY)
                .criterion(hasItem(Items.STONE), conditionsFromItem(Items.STONE))
                .criterion(hasItem(ModItems.NETHER_RUBY), conditionsFromItem(ModItems.NETHER_RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RAW_NETHER_RUBY)));
    }
}
