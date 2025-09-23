package net.ronm19.infernummod.util;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.item.ModItems;

public class ModModelPredicateProvider {
    public static void registerModModel() {

        ModelPredicateProviderRegistry.register(ModItems.INFERNO_SHIELD, new Identifier("blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
}