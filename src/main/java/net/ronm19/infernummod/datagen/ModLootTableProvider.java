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

        addDrop(ModBlocks.EMBERSTONE_ORE, copperLikeOreDrops(ModBlocks.EMBERSTONE_ORE, ModItems.RAW_EMBERSTONE, UniformLootNumberProvider.create(2.0F, 6.0F)));
        addDrop(ModBlocks.DEEPSLATE_CINDERSTONE_ORE, copperLikeOreDrops(ModBlocks.DEEPSLATE_CINDERSTONE_ORE, ModItems.RAW_CINDESTONE, UniformLootNumberProvider.create(3.0F, 7.0F)));
        addDrop(ModBlocks.NETHER_PYROCLAST_ORE, copperLikeOreDrops(ModBlocks.NETHER_PYROCLAST_ORE, ModItems.RAW_PYROCLAST, UniformLootNumberProvider.create(2.0F, 6.0F)));
        addDrop(ModBlocks.STONE_INFERNIUM_ORE, copperLikeOreDrops(ModBlocks.STONE_INFERNIUM_ORE, ModItems.RAW_INFERNIUM, UniformLootNumberProvider.create(2.0F, 6.0F)));
    }

    public LootTable.Builder copperLikeOreDrops( Block drop, Item item, UniformLootNumberProvider uniformLootNumberProvider ) {
        return dropsWithSilkTouch(drop, (LootPoolEntry.Builder)this.applyExplosionDecay(drop, ItemEntry.builder(item).apply
                 (SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }

}
