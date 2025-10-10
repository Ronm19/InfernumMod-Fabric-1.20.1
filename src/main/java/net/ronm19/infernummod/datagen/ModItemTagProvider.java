package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.item.ModItems;

import java.util.concurrent.CompletableFuture;

import static net.ronm19.infernummod.item.ModItems.*;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider( FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture ) {
        super(output, completableFuture);
    }

    @Override
    protected void configure( RegistryWrapper.WrapperLookup arg ) {
        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.NETHER_RUBY_HELMET, ModItems.NETHER_RUBY_CHESTPLATE, ModItems.NETHER_RUBY_LEGGINGS, ModItems.NETHER_RUBY_BOOTS)
                .add(ModItems.CINDERSTONE_HELMET, ModItems.CINDERSTONE_CHESTPLATE, ModItems.CINDERSTONE_LEGGINGS, ModItems.CINDERSTONE_BOOTS)
                .add(ModItems.EMBERSTONE_HELMET, ModItems.EMBERSTONE_CHESTPLATE, ModItems.EMBERSTONE_LEGGINGS, ModItems.EMBERSTONE_BOOTS)
                .add(ModItems.INFERNIUM_HELMET, ModItems.INFERNIUM_CHESTPLATE, ModItems.INFERNIUM_LEGGINGS, ModItems.INFERNIUM_BOOTS)
                .add(ModItems.PYROCLAST_HELMET, ModItems.PYROCLAST_CHESTPLATE, ModItems.PYROCLAST_LEGGINGS, ModItems.PYROCLAST_BOOTS)
                .add(ModItems.FIRERITE_HELMET, ModItems.FIRERITE_CHESTPLATE, ModItems.FIRERITE_LEGGINGS, ModItems.FIRERITE_BOOTS);

        getOrCreateTagBuilder(ItemTags.PLANKS)
                .add(ModBlocks.INFERNO_ESSENCE_PLANKS.asItem());

        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.INFERNO_ESSENCE_LOG.asItem())
                .add(ModBlocks.INFERNO_ESSENCE_WOOD.asItem())
                .add(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG.asItem())
                .add(ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD.asItem());

        getOrCreateTagBuilder(ItemTags.FISHES)
                .add(ModItems.LAVA_FISH)
                .add(ModItems.MAGMA_FISH)
                .add(ModItems.FIRE_FISH);




    }
}
