package net.ronm19.infernummod.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public enum ModToolMaterial implements ToolMaterial {
    NETHER_RUBY(5, 2400, 3.5f, 3.5f, 26, () -> Ingredient.ofItems(ModItems.NETHER_RUBY)),
    EMBERSTONE(6, 2600, 3.4f, 3.7f, 26, () -> Ingredient.ofItems(ModItems.EMBERSTONE)),
    CINDERSTONE(7, 2800, 3.3f, 3.9f, 26, () -> Ingredient.ofItems(ModItems.CINDERTSONE)),
    INFERNIUM(8, 3000, 3.2f, 4.1f, 28, () -> Ingredient.ofItems(ModItems.INFERNIUM)),
    PYROCLAST(9, 3200, 3.1f, 4.3f, 30, () -> Ingredient.ofItems(ModItems.PYROCLAST)),
    FIRERITE(10, 3400, 3.0f, 4.5f, 32, () -> Ingredient.ofItems(ModItems.FIRERITE));

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterial( int miningLevel, int itemDurability, float miningSpeedl, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient ) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeedl;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }


    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
