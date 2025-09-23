package net.ronm19.infernummod.util;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.potion.Potions;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.DemonEntity;
import net.ronm19.infernummod.entity.custom.EmberHundEntity;
import net.ronm19.infernummod.entity.custom.MalfuryxEntity;
import net.ronm19.infernummod.entity.custom.PyerlingWyrnEntity;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.mixin.BrewingRecipeRegistryMixin;
import net.ronm19.infernummod.potion.ModPotions;

public class ModRegistries {
    public static void registerModStuff() {
        registerFuels();
        registerCommands();
        registerModCompostables();
        registerEvents();
        registerPotionRecipes();
        registerAttributes();
    }

    private static void registerFuels() {
        FuelRegistry registry = FuelRegistry.INSTANCE;


    }

    private static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ModEntities.DEMON, DemonEntity.createDemonAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MALFURYX, MalfuryxEntity.createMalfuryxAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EMBER_HUND, EmberHundEntity.createEmberHundAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PYERLING_WYRN, PyerlingWyrnEntity.createPyerlingWyrnAttributes());
    }


    private static void registerModCompostables() {


    }

    private static void registerCommands() {


    }

    private static void registerEvents() {


    }

    private static void registerPotionRecipes() {
        BrewingRecipeRegistryMixin.InvokeRegisterPotionRecipe(Potions.AWKWARD, ModItems.PYROCLAST, ModPotions.BLAZING_HEART_POTION);
    }
}
