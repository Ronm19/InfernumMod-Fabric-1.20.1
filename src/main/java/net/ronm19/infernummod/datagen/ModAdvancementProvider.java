package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.UsingItemCriterion;
import net.minecraft.data.server.advancement.AdvancementProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.util.ModTags;
import net.ronm19.infernummod.world.biome.ModBiomes;
import net.ronm19.infernummod.world.dimension.ModDimensions;

import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider( FabricDataOutput output ) {
        super(output);
    }

    @Override
    public void generateAdvancement( Consumer<AdvancementEntry> consumer ) {
        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.FIRERITE),
                        Text.literal("Infernum"), Text.literal("The Power lies in the infernium region, glad that you decided to begin your journey!"),
                        new Identifier(InfernumMod.MOD_ID, "textures/block/nether_ruby_block.png" ), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_nether_ruby", InventoryChangedCriterion.Conditions.items(ModItems.NETHER_RUBY))
                .build(consumer, InfernumMod.MOD_ID + ":infernummod");

        AdvancementEntry FoundTheNetherRuby = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.RAW_NETHER_RUBY),
                        Text.literal("Found the Nether Ruby"), Text.literal("This raw gem seems interesting, I wonder what it does???"),
                        new Identifier(InfernumMod.MOD_ID, "textures/block/nether_ruby_block.png" ), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("get_raw_nether_ruby", InventoryChangedCriterion.Conditions.items(ModItems.RAW_NETHER_RUBY))
                .build(consumer, InfernumMod.MOD_ID + ":raw_nether_ruby");

        AdvancementEntry theNetherRubySword = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.NETHER_RUBY_SWORD),
                        Text.literal("The Sword of the Nether"), Text.literal("You have now discovered the Nether Ruby Sword, use it wisely"),
                        new Identifier(InfernumMod.MOD_ID, "textures/block/nether_ruby_block.png" ), AdvancementFrame.GOAL,
                        true, true, false))
                .criterion("has_nether_ruby_sword", InventoryChangedCriterion.Conditions.items(ModItems.NETHER_RUBY_SWORD))
                .parent(rootAdvancement)
                .build(consumer, InfernumMod.MOD_ID + ":nether_ruby_sword");

        AdvancementEntry theFlameStaff = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.FLAME_STAFF),
                        Text.literal("The Igniter of the Abyss"), Text.literal("You've found the key to summon the abyss, use it wisely."),
                        new Identifier(InfernumMod.MOD_ID, "textures/block/nether_ruby_block.png" ), AdvancementFrame.GOAL,
                        true, true, false))
                .criterion("has_flame_staff", InventoryChangedCriterion.Conditions.items(ModItems.FLAME_STAFF))
                .parent(rootAdvancement)
                .build(consumer, InfernumMod.MOD_ID + ":flame_staff");

        AdvancementEntry EnteredIntoAbyssium = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModBlocks.ABYSSIUM_STONE),
                        Text.literal("Entered Into the Abyss"), Text.literal("You have entered into the Abyss! Welcome to Abyssium."),
                        new Identifier(InfernumMod.MOD_ID, "textures/block/nether_ruby_block.png" ), AdvancementFrame.CHALLENGE,
                        true, true, false))
                .criterion("entered_abyssium", ChangedDimensionCriterion.Conditions.to(ModDimensions.ABYSSIUM_LEVEL_KEY))
                .parent(rootAdvancement)
                .rewards(AdvancementRewards.Builder.experience(20))
                .build(consumer, InfernumMod.MOD_ID + ":entered_abyssium");


    }
}
