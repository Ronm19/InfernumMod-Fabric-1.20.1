package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.item.ModItems;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider( FabricDataOutput dataOutput ) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.FIRERITE_BLOCK);
        addDrop(ModBlocks.NETHER_RUBY_BLOCK);
        addDrop(ModBlocks.RAW_FIRERITE_BLOCK);
        addDrop(ModBlocks.RAW_NETHER_RUBY_BLOCK);

        addDrop(ModBlocks.FIRERITE_ORE, copperLikeOreDrops(ModBlocks.FIRERITE_ORE, ModItems.RAW_FIRERITE, UniformLootNumberProvider.create(2.0F, 6.0F)));
        addDrop(ModBlocks.DEEPSLATE_FIRERITE_ORE, copperLikeOreDrops(ModBlocks.DEEPSLATE_FIRERITE_ORE, ModItems.RAW_FIRERITE, UniformLootNumberProvider.create(3.0F, 7.0F)));
        addDrop(ModBlocks.NETHER_RUBY_ORE, copperLikeOreDrops(ModBlocks.NETHER_RUBY_ORE, ModItems.RAW_NETHER_RUBY, UniformLootNumberProvider.create(2.0F, 5.0F)));
        addDrop(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE, copperLikeOreDrops(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE, ModItems.RAW_NETHER_RUBY, UniformLootNumberProvider.create(3.0F, 6.0F)));

        addDrop(ModBlocks.BLAZE_BLOCK);
        addDrop(ModBlocks.BLAZE_STONE_BLOCK);
        addDrop(ModBlocks.EMBERSTONE_BLOCK);
        addDrop(ModBlocks.RAW_EMBERSTONE_BLOCK);
        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK);
        addDrop(ModBlocks.INFERNAL_STONE_BLOCK);
        addDrop(ModBlocks.MOLTEN_BRICKS_BLOCK);
        addDrop(ModBlocks.MOLTEN_GRANITE_BLOCK);
        addDrop(ModBlocks.MOLTEN_STONE_BLOCK);
        addDrop(ModBlocks.ASH_BLOCK, copperLikeOreDrops(ModBlocks.ASH_BLOCK, ModItems.ASH_DUST, UniformLootNumberProvider.create(1.0F, 2.0F)));

        addDrop(ModBlocks.EMBERSTONE_ORE, copperLikeOreDrops(ModBlocks.EMBERSTONE_ORE, ModItems.RAW_EMBERSTONE, UniformLootNumberProvider.create(2.0F, 6.0F)));
        addDrop(ModBlocks.DEEPSLATE_CINDERSTONE_ORE, copperLikeOreDrops(ModBlocks.DEEPSLATE_CINDERSTONE_ORE, ModItems.RAW_CINDESTONE, UniformLootNumberProvider.create(3.0F, 7.0F)));
        addDrop(ModBlocks.NETHER_PYROCLAST_ORE, copperLikeOreDrops(ModBlocks.NETHER_PYROCLAST_ORE, ModItems.RAW_PYROCLAST, UniformLootNumberProvider.create(2.0F, 6.0F)));
        addDrop(ModBlocks.STONE_INFERNIUM_ORE, copperLikeOreDrops(ModBlocks.STONE_INFERNIUM_ORE, ModItems.RAW_INFERNIUM, UniformLootNumberProvider.create(2.0F, 6.0F)));

        addDrop(ModBlocks.NETHER_RUBY_STAIRS);
        addDrop(ModBlocks.NETHER_RUBY_TRAPDOOR);
        addDrop(ModBlocks.NETHER_RUBY_WALL);
        addDrop(ModBlocks.NETHER_RUBY_FENCE);
        addDrop(ModBlocks.NETHER_RUBY_FENCE_GATE);
        addDrop(ModBlocks.NETHER_RUBY_BUTTON);
        addDrop(ModBlocks.NETHER_RUBY_PRESSURE_PLATE);

        addDrop(ModBlocks.FIRERITE_STAIRS);
        addDrop(ModBlocks.FIRERITE_TRAPDOOR);
        addDrop(ModBlocks.FIRERITE_WALL);
        addDrop(ModBlocks.FIRERITE_FENCE);
        addDrop(ModBlocks.FIRERITE_FENCE_GATE);
        addDrop(ModBlocks.FIRERITE_BUTTON);
        addDrop(ModBlocks.FIRERITE_PRESSURE_PLATE);

        addDrop(ModBlocks.BLAZE_STAIRS);
        addDrop(ModBlocks.BLAZE_TRAPDOOR);
        addDrop(ModBlocks.BLAZE_WALL);
        addDrop(ModBlocks.BLAZE_FENCE);
        addDrop(ModBlocks.BLAZE_FENCE_GATE);
        addDrop(ModBlocks.BLAZE_BUTTON);
        addDrop(ModBlocks.BLAZE_PRESSURE_PLATE);

        addDrop(ModBlocks.EMBERSTONE_STAIRS);
        addDrop(ModBlocks.EMBERSTONE_TRAPDOOR);
        addDrop(ModBlocks.EMBERSTONE_WALL);
        addDrop(ModBlocks.EMBERSTONE_FENCE);
        addDrop(ModBlocks.EMBERSTONE_FENCE_GATE);
        addDrop(ModBlocks.EMBERSTONE_BUTTON);
        addDrop(ModBlocks.EMBERSTONE_PRESSURE_PLATE);

        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_STAIRS);
        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_TRAPDOOR);
        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_WALL);
        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_FENCE);
        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_FENCE_GATE);
        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_BUTTON);
        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_PRESSURE_PLATE);

        addDrop(ModBlocks.MOLTEN_BRICKS_STAIRS);
        addDrop(ModBlocks.MOLTEN_BRICKS_TRAPDOOR);
        addDrop(ModBlocks.MOLTEN_BRICKS_WALL);
        addDrop(ModBlocks.MOLTEN_BRICKS_FENCE);
        addDrop(ModBlocks.MOLTEN_BRICKS_FENCE_GATE);
        addDrop(ModBlocks.MOLTEN_BRICKS_BUTTON);
        addDrop(ModBlocks.MOLTEN_BRICKS_PRESSURE_PLATE);

        addDrop(ModBlocks.MOLTEN_STONE_STAIRS);
        addDrop(ModBlocks.MOLTEN_STONE_TRAPDOOR);
        addDrop(ModBlocks.MOLTEN_STONE_WALL);
        addDrop(ModBlocks.MOLTEN_STONE_FENCE);
        addDrop(ModBlocks.MOLTEN_STONE_FENCE_GATE);
        addDrop(ModBlocks.MOLTEN_STONE_BUTTON);
        addDrop(ModBlocks.MOLTEN_STONE_PRESSURE_PLATE);

        addDrop(ModBlocks.INFERNAL_STONE_STAIRS);
        addDrop(ModBlocks.INFERNAL_STONE_TRAPDOOR);
        addDrop(ModBlocks.INFERNAL_STONE_WALL);
        addDrop(ModBlocks.INFERNAL_STONE_FENCE);
        addDrop(ModBlocks.INFERNAL_STONE_FENCE_GATE);
        addDrop(ModBlocks.INFERNAL_STONE_BUTTON);
        addDrop(ModBlocks.INFERNAL_STONE_PRESSURE_PLATE);

        addDrop(ModBlocks.NETHER_RUBY_DOOR, doorDrops(ModBlocks.NETHER_RUBY_DOOR));
        addDrop(ModBlocks.NETHER_RUBY_SLAB, slabDrops(ModBlocks.NETHER_RUBY_SLAB));

        addDrop(ModBlocks.FIRERITE_DOOR, doorDrops(ModBlocks.FIRERITE_DOOR));
        addDrop(ModBlocks.FIRERITE_SLAB, slabDrops(ModBlocks.FIRERITE_SLAB));

        addDrop(ModBlocks.BLAZE_DOOR, doorDrops(ModBlocks.BLAZE_DOOR));
        addDrop(ModBlocks.BLAZE_SLAB, slabDrops(ModBlocks.BLAZE_SLAB));

        addDrop(ModBlocks.EMBERSTONE_DOOR, doorDrops(ModBlocks.BLAZE_DOOR));
        addDrop(ModBlocks.EMBERSTONE_SLAB, slabDrops(ModBlocks.BLAZE_SLAB));

        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_DOOR, doorDrops(ModBlocks.INFERNAL_BRICKS_STONE_DOOR));
        addDrop(ModBlocks.INFERNAL_BRICKS_STONE_SLAB, slabDrops(ModBlocks.INFERNAL_BRICKS_STONE_SLAB));

        addDrop(ModBlocks.MOLTEN_BRICKS_DOOR, doorDrops(ModBlocks.MOLTEN_BRICKS_DOOR));
        addDrop(ModBlocks.MOLTEN_BRICKS_SLAB, slabDrops(ModBlocks.MOLTEN_BRICKS_SLAB));

        addDrop(ModBlocks.MOLTEN_STONE_DOOR, doorDrops(ModBlocks.MOLTEN_STONE_DOOR));
        addDrop(ModBlocks.MOLTEN_STONE_SLAB, slabDrops(ModBlocks.MOLTEN_STONE_SLAB));

        addDrop(ModBlocks.MOLTEN_STONE_DOOR, doorDrops(ModBlocks.INFERNAL_STONE_DOOR));
        addDrop(ModBlocks.MOLTEN_STONE_SLAB, slabDrops(ModBlocks.INFERNAL_STONE_SLAB));

        addDrop(ModBlocks.BLAZEBLOOM);
        addPottedPlantDrops(ModBlocks.POTTED_BLAZEBLOOM);

        addDrop(ModBlocks.INFERNO_ESSENCE_LOG);
        addDrop(ModBlocks.INFERNO_ESSENCE_WOOD);
        addDrop(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG);
        addDrop(ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD);
        addDrop(ModBlocks.INFERNO_ESSENCE_PLANKS);
        addDrop(ModBlocks.INFERNO_ESSENCE_SAPLING);

        addDrop(ModBlocks.INFERNO_ESSENCE_LEAVES, leavesDrops(ModBlocks.INFERNO_ESSENCE_LEAVES, ModBlocks.INFERNO_ESSENCE_SAPLING, 0.0035f));
    }

    public LootTable.Builder copperLikeOreDrops( Block drop, Item item, UniformLootNumberProvider uniformLootNumberProvider ) {
        return dropsWithSilkTouch(drop, (LootPoolEntry.Builder)this.applyExplosionDecay(drop, ItemEntry.builder(item).apply
                 (SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }

}
