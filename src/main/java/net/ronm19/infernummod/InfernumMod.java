package net.ronm19.infernummod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.effect.ModEffects;
import net.ronm19.infernummod.enchantment.ModEnchantments;
import net.ronm19.infernummod.entity.ModBoats;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.client.DemonRenderer;
import net.ronm19.infernummod.entity.client.MalfuryxRenderer;
import net.ronm19.infernummod.entity.custom.DemonEntity;
import net.ronm19.infernummod.entity.custom.MalfuryxEntity;
import net.ronm19.infernummod.item.ModItemGroups;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.potion.ModPotions;
import net.ronm19.infernummod.sound.ModSounds;
import net.ronm19.infernummod.util.ModRegistries;
import net.ronm19.infernummod.world.gen.ModWorldGeneration;
import net.ronm19.infernummod.world.tree.ModFoliagePlacerTypes;
import net.ronm19.infernummod.world.tree.ModTrunkPlacerTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernumMod implements ModInitializer {
	public static final String MOD_ID = "infernummod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

        ModItemGroups.registerItemGroups();

        ModItems.registerModItems();
        ModBlocks.registerModBlock();
        ModRegistries.registerModStuff();

        ModBoats.registerBoats();

        ModEntities.registerModEntites();

        ModEnchantments.registerModEnchantments();
        ModEffects.registerEffects();
        ModPotions.registerPotions();

        ModTrunkPlacerTypes.register();
        ModFoliagePlacerTypes.register();

        ModSounds.registerSounds();


        StrippableBlockRegistry.register(ModBlocks.INFERNO_ESSENCE_LOG, ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG);
        StrippableBlockRegistry.register(ModBlocks.INFERNO_ESSENCE_WOOD, ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD);

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.INFERNO_ESSENCE_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.INFERNO_ESSENCE_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD, 5, 5);

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.INFERNO_ESSENCE_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.INFERNO_ESSENCE_LEAVES, 30, 5);

        ModWorldGeneration.generateModWorldGen();

        CustomPortalBuilder.beginPortal()
                .frameBlock(ModBlocks.ABYSSIUM_STONE)
                .lightWithItem(ModItems.FLAME_STAFF)
                .destDimID(new Identifier(InfernumMod.MOD_ID, "abyssium"))
                .tintColor(0x3b0f5c)
                .registerPortal();

    }



}