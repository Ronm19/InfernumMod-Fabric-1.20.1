package net.ronm19.infernummod.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.ronm19.infernummod.InfernumMod;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
   NETHER_RUBY("nether_ruby_carapace", 40, new int[] {9, 12, 8, 6}, 23,
           SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3f, 0.4f, () -> Ingredient.ofItems(ModItems.NETHER_RUBY)),
   CINDERSTONE("cinderstone", 32, new int[] {8, 10, 7, 5}, 23,
           SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 2f, 0.3f, () -> Ingredient.ofItems(ModItems.CINDERTSONE)),
    EMBERSTONE("emberstone", 33, new int[] {8, 11, 9, 7}, 23,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3f, 0.3f, () -> Ingredient.ofItems(ModItems.EMBERSTONE)),
    INFERNIUM("infernium", 34, new int[] {9, 12, 10, 9}, 24,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3f, 0.4f, () -> Ingredient.ofItems(ModItems.INFERNIUM)),
    PYROCLAST("pyroclast", 35, new int[] {10, 13, 11, 10}, 25,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3f, 0.4f, () -> Ingredient.ofItems(ModItems.PYROCLAST)),
    FIRERITE("firerite", 38, new int[] {11, 13, 13, 11}, 26,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 4f, 0.5f, () -> Ingredient.ofItems(ModItems.FIRERITE));



    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    private static int[] BASE_DURABILITY = {17, 19, 18, 16};

    ModArmorMaterials( String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient ) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability( ArmorItem.Type type ) {
        return BASE_DURABILITY[type.ordinal()] * this.durabilityMultiplier;
    }

    @Override
    public int getProtection( ArmorItem.Type type ) {
        return protectionAmounts[type.ordinal()];
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return InfernumMod.MOD_ID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
