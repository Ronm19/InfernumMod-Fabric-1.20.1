package net.ronm19.infernummod.item;

import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent LAVA_FISH = new FoodComponent.Builder().hunger(5).saturationModifier(0.7F).build();
    public static final FoodComponent MAGMA_FISH = new FoodComponent.Builder().hunger(4).saturationModifier(0.6F).build();
    public static final FoodComponent FIRE_FISH = new FoodComponent.Builder().hunger(6).saturationModifier(0.5F).build();
}
