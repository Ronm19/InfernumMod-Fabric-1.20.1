package net.ronm19.infernummod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;  // optional if you want clean helper
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.potion.ModPotions;
import net.ronm19.infernummod.world.biome.ModBiomes;
import net.minecraft.advancement.criterion.EnterBlockCriterion;

import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.enchantment.EnchantmentHelper.createNbt;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<AdvancementEntry> consumer) {

        // ROOT advancement
        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.FIRERITE),
                        Text.translatable("advancements.infernummod.root.title"),
                        Text.translatable("advancements.infernummod.root.desc"),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"), // vanilla-style background
                        AdvancementFrame.TASK,
                        false,  // show toast? false for root
                        false,  // announce to chat
                        false   // hidden
                ))
                .criterion("has_nether_ruby", InventoryChangedCriterion.Conditions.items(ModItems.NETHER_RUBY))
                .build(consumer, InfernumMod.MOD_ID + ":root");

        // === PROGRESSION TREE ===

        // Found Nether Ruby
        AdvancementEntry foundTheNetherRuby = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.RAW_NETHER_RUBY),
                        Text.literal("Found the Nether Ruby"),
                        Text.literal("This raw gem seems interesting, I wonder what it does???"),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                ))
                .criterion("get_raw_nether_ruby", InventoryChangedCriterion.Conditions.items(ModItems.RAW_NETHER_RUBY))
                .parent(rootAdvancement) // <--- added parent
                .build(consumer, InfernumMod.MOD_ID + ":raw_nether_ruby");

        // Nether Ruby Sword
        AdvancementEntry theNetherRubySword = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.NETHER_RUBY_SWORD),
                        Text.literal("The Sword of the Nether"),
                        Text.literal("You have now discovered the Nether Ruby Sword, use it wisely"),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.GOAL,
                        true, true, false
                ))
                .criterion("has_nether_ruby_sword", InventoryChangedCriterion.Conditions.items(ModItems.NETHER_RUBY_SWORD))
                .parent(foundTheNetherRuby)
                .build(consumer, InfernumMod.MOD_ID + ":nether_ruby_sword");


        // Nether Ruby Armor Set

        AdvancementEntry fullNetherRubyArmor = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.NETHER_RUBY_CHESTPLATE),
                        Text.literal("Full Nether Ruby Armor"),
                        Text.literal("You are now clad in the full power of Nether Ruby!"),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.GOAL,
                        true, true, false
                ))
                .criterion("has_full_nether_ruby_armor",
                        InventoryChangedCriterion.Conditions.items(
                                ModItems.NETHER_RUBY_HELMET,
                                ModItems.NETHER_RUBY_CHESTPLATE,
                                ModItems.NETHER_RUBY_LEGGINGS,
                                ModItems.NETHER_RUBY_BOOTS
                        )
                )
                .parent(theNetherRubySword) // stays under Sword in the tree
                .build(consumer, InfernumMod.MOD_ID + ":full_nether_ruby_armor");


        // Get The Blazing Heart


        AdvancementEntry gotBlazingHeart = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.BLAZE_HEART),
                        Text.literal("Blazing Heart"),
                        Text.literal("You’ve claimed the fiery heart of a Blaze."),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                ))
                .criterion("has_blazing_heart",
                        InventoryChangedCriterion.Conditions.items(ModItems.BLAZE_HEART)
                )
                .parent(rootAdvancement) // attach under root or wherever you want
                .build(consumer, InfernumMod.MOD_ID + ":got_blazing_heart");

        // Create the Blazing Heart Potion

        AdvancementEntry brewBlazingHeartPotion = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(Items.POTION),
                        Text.literal("Potion of Blazing Heart"),
                        Text.literal("Brew the mystical Potion of Blazing Heart."),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.GOAL,
                        true, true, false
                ))
                .criterion("has_potion_blazing_heart",
                        InventoryChangedCriterion.Conditions.items(
                                ItemPredicate.Builder.create()
                                        .items(Items.POTION)
                                        .nbt(createNbt("{Potion:\"infernummod:blazing_heart_potion\"}"))
                                        .build()
                        )
                )
                .parent(gotBlazingHeart)                           // parent is another AdvancementEntry
                .build(consumer, InfernumMod.MOD_ID + ":brew_potion_blazing_heart");


        // Entered Infernal Biome
        AdvancementEntry enteredIntoInfernal = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModBlocks.INFERNAL_OBSIDIAN_BLOCK),
                        Text.literal("Entered Into the Infernal"),
                        Text.literal("You have entered into the Soul of Fire! Welcome to Infernal."),
                        new Identifier("minecraft","textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                ))
                .criterion("entered_infernal", EnterBlockCriterion.Conditions.block(ModBlocks.INFERNAL_GRASS_BLOCK))
                .parent(gotBlazingHeart)
                .rewards(AdvancementRewards.Builder.experience(20))
                .build(consumer, InfernumMod.MOD_ID + ":entered_infernal");

        // Silenced the Obsidian Wail
        AdvancementEntry silencedTheObsidianWail = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.INFERNO_FANG),
                        Text.literal("Silenced the Obsidian Wail"),
                        Text.literal("You defeated the Obsidian Ghast, and obtained the Inferno Fang."),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                ))
                .criterion("silenced_the_obsidian_wail",
                        OnKilledCriterion.Conditions.createPlayerKilledEntity(
                                Optional.of(EntityPredicate.Builder.create().type(ModEntities.OBSIDIAN_GHAST).build())
                        ))
                .parent(enteredIntoInfernal)
                .rewards(AdvancementRewards.Builder.experience(12))
                .build(consumer, InfernumMod.MOD_ID + ":silenced_the_obsidian_wail");

        // Echo of Damnation
        AdvancementEntry theEchoOfDamnation = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.ECHO_OF_DAMNATION),
                        Text.literal("The Echo of Damnation"),
                        Text.literal("You've found the key to summon the king himself, use it wisely."),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.GOAL,
                        true, true, false
                ))
                .criterion("has_echo_damnation", InventoryChangedCriterion.Conditions.items(ModItems.ECHO_OF_DAMNATION))
                .parent(silencedTheObsidianWail)
                .build(consumer, InfernumMod.MOD_ID + ":echo_damnation");

        // Silenced the Eye of the Fire
        AdvancementEntry silencedTheEyeOfTheFire = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.CURSED_FLINT),
                        Text.literal("Silenced the Eye of the Fire"),
                        Text.literal("You defeated the Infernum Herobrine, and saved the world."),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                ))
                .criterion("silenced_the_eye_of_the_fire",
                        OnKilledCriterion.Conditions.createPlayerKilledEntity(
                                Optional.of(EntityPredicate.Builder.create().type(ModEntities.INFERNUM_HEROBRINE).build())
                        ))
                .parent(theEchoOfDamnation)
                .rewards(AdvancementRewards.Builder.experience(23))
                .build(consumer, InfernumMod.MOD_ID + ":silenced_the_eye_of_the_fire");

        // Silenced the King of the Fire
        AdvancementEntry silencedTheKingOfTheFire = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModBlocks.INFERNAL_OBSIDIAN_BLOCK),
                        Text.literal("Silenced the King of the Fire"),
                        Text.literal("You defeated the Infernum Threat, and saved the world once again!"),
                        new Identifier("minecraft", "textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                ))
                .criterion("silenced_the_king_of_the_fire",
                        OnKilledCriterion.Conditions.createPlayerKilledEntity(
                                Optional.of(EntityPredicate.Builder.create().type(ModEntities.INFERNUM).build())
                        ))
                .parent(silencedTheEyeOfTheFire)
                .rewards(AdvancementRewards.Builder.experience(43))
                .build(consumer, InfernumMod.MOD_ID + ":silenced_the_king_of_the_fire");


    }

    private static NbtCompound createNbt(String nbtString) {
        try {
            return StringNbtReader.parse(nbtString);
        } catch (Exception e) {
            throw new RuntimeException("Invalid NBT: " + nbtString, e);
        }
    }
}
