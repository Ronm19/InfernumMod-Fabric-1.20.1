package net.ronm19.infernummod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.client.DemonRenderer;
import net.ronm19.infernummod.entity.client.MalfuryxRenderer;
import net.ronm19.infernummod.entity.custom.DemonEntity;
import net.ronm19.infernummod.item.ModItemGroups;
import net.ronm19.infernummod.item.ModItems;
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

        EntityRendererRegistry.register(ModEntities.DEMON, DemonRenderer::new);
        EntityRendererRegistry.register(ModEntities.MALFURYX, MalfuryxRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.DEMON, DemonEntity.createDemonAttributes());

    }



}