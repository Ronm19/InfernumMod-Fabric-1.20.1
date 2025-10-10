package net.ronm19.infernummod.util;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.potion.Potions;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.*;
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
        FabricDefaultAttributeRegistry.register(ModEntities.OBSIDIAN_GHAST, ObsidianGhastEntity.createObsidianGhastAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_HOARDE, InfernalHoardeEntity.createInfernalHoardeAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNO_ZOMBIE, InfernoZombieEntity.createInfernoZombieAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.FLAME_SKELETON, FlameSkeletonEntity.createFlameSkeletonAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNO_ENDERMAN, InfernoEndermanEntity.createInfernoEndermanAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MAGMA_STRIDER, MagmaStriderEntity.createMagmaStriderAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MAGMA_CREEPER, MagmaCreeperEntity.createMagmaCreeperAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EMBER_SERPENT, EmberSerpentEntity.createEmberSerpentAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_BEAST, InfernalBeastEntity.createInfernalBeastAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNUM_HEROBRINE, InfernumHerobrineEntity.createInfernumHerobrineAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNUM, InfernumEntity.createInfernumAttributes());

        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_EYE, InfernalEyeEntity.createInfernalEyeAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EMBER_HUND, EmberHundEntity.createEmberHundAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PYERLING_WYRN, PyerlingWyrnEntity.createPyerlingWyrnAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_PHANTOM, InfernalPhantomEntity.createInfernalPhantomAttributes());

        FabricDefaultAttributeRegistry.register(ModEntities.LAVA_FISH, LavaFishEntity.createLavaFishAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MAGMA_FISH, MagmaFishEntity.createMagmaFishAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.FIRE_FISH, FireFishEntity.createFireFishAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MAGMA_DOLPHIN, MagmaDolphinEntity.createMagmaDolphinAttributes());

    }


    private static void registerModCompostables() {


    }

    private static void registerCommands() {


    }

    private static void registerEvents() {


    }

    private static void registerPotionRecipes() {
        BrewingRecipeRegistryMixin.InvokeRegisterPotionRecipe(Potions.AWKWARD, ModItems.BLAZE_HEART, ModPotions.BLAZING_HEART_POTION);
        BrewingRecipeRegistryMixin.InvokeRegisterPotionRecipe(Potions.AWKWARD, ModItems.INFERNAL_BEAST_HORN, ModPotions.LAVA_VISION_POTION);
    }
}
