package net.ronm19.infernummod.util;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.raid.Raid;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.*;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.mixin.BrewingRecipeRegistryMixin;
import net.ronm19.infernummod.potion.ModPotions;
import net.ronm19.infernummod.villager.ModVillagers;

public class ModRegistries {
    public static void registerModStuff() {
        registerFuels();
        registerCommands();
        registerModCompostables();
        registerEvents();
        registerPotionRecipes();
        registerAttributes();
        registerCustomTrades();
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
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_ZOMBILAGER, InfernalZombilagerEntity.createInfernalZombilagerAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.FLAME_SKELETON, FlameSkeletonEntity.createFlameSkeletonAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_VEX, InfernalVexEntity.createInfernalVexAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_WRAITH, InfernalWraithEntity.createInfernalWraithAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNO_ENDERMAN, InfernoEndermanEntity.createInfernoEndermanAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.LAVACATOR, LavacatorEntity.createLavacatorAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.LAVAGER, LavagerEntity.createLavagerAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_VOKER, InfernalVokerEntity.createInfernalVokerAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.LAVA_WITCH, LavaWitchEntity.createLavaWitchAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.LAVA_SLIME, LavaSlimeEntity.createLavaSlimeAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MAGMA_SPIDER, MagmaSpiderEntity.createMagmaSpiderAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MAGMA_STRIDER, MagmaStriderEntity.createMagmaStriderAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MAGMA_CREEPER, MagmaCreeperEntity.createMagmaCreeperAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EMBER_SERPENT, EmberSerpentEntity.createEmberSerpentAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_BEAST, InfernalBeastEntity.createInfernalBeastAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNUM_HEROBRINE, InfernumHerobrineEntity.createInfernumHerobrineAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNUM, InfernumEntity.createInfernumAttributes());

        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_KNIGHT, InfernalKnightEntity.createKnightAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_EYE, InfernalEyeEntity.createInfernalEyeAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INFERNAL_HORSE, InfernalHorseEntity.createInfernalHorseAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.FLAME_HORSE, FlameHorseEntity.createFlameHorseAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.ASHBONE_HORSE, AshboneHorseEntity.createAshboneHorseAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EMBER_HUND, EmberHundEntity.createEmberHundAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.VOLCARNIS, VolcarnisEntity.createVolcarnisAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.SCORCHED_WOOLIE, ScorchedWoolieEntity.createScorchedWoolieAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.ASH_CHICKEN, AshChickenEntity.createAshChickenAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EMBER_BOAR, EmberBoarEntity.createEmberBoarAttributes());
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

    private static void registerCustomTrades() {
        // Tier 1 – Basic Resource Exchange
        TradeOfferHelper.registerVillagerOffers(ModVillagers.INFERNAL_MASTER, 1,
                factories -> {
                    factories.add(( entity, random ) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 12),
                            new ItemStack(ModItems.NETHER_RUBY, 5),
                            6, 2, 0.02f
                    ));
                });

        // Tier 2 – Weapon Trade
        TradeOfferHelper.registerVillagerOffers(ModVillagers.INFERNAL_MASTER, 2,
                factories -> {
                    factories.add(( entity, random ) -> new TradeOffer(
                            new ItemStack(ModItems.NETHER_RUBY, 4),
                            new ItemStack(ModItems.NETHER_RUBY_SWORD, 1),
                            6, 6, 0.05f
                    ));
                });

        // Tier 3 – Nether Ruby Armor Piece (Chestplate)
        TradeOfferHelper.registerVillagerOffers(ModVillagers.INFERNAL_MASTER, 3,
                factories -> {
                    factories.add(( entity, random ) -> new TradeOffer(
                            new ItemStack(ModItems.NETHER_RUBY, 10),
                            new ItemStack(ModItems.NETHER_RUBY_CHESTPLATE, 1),
                            6, 10, 0.08f
                    ));
                });

        // Tier 4 – Full Firerite Trade (Weapon Upgrade)
        TradeOfferHelper.registerVillagerOffers(ModVillagers.INFERNAL_MASTER, 4,
                factories -> {
                    factories.add(( entity, random ) -> new TradeOffer(
                            new ItemStack(ModItems.NETHER_RUBY_SWORD, 1),
                            new ItemStack(ModItems.FIRERITE, 6),
                            1, 15, 0.1f
                    ));
                });

        // Tier 5 – High-Value Infernal Exchange (Emberstone)
        TradeOfferHelper.registerVillagerOffers(ModVillagers.INFERNAL_MASTER, 5,
                factories -> {
                    factories.add(( entity, random ) -> new TradeOffer(
                            new ItemStack(ModItems.FIRERITE, 8),
                            new ItemStack(ModItems.EMBERSTONE, 2),
                            2, 20, 0.15f
                    ));
                });
    }
}
