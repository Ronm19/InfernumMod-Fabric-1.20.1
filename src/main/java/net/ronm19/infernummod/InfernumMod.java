package net.ronm19.infernummod;

import net.fabricmc.api.ModInitializer;

import net.ronm19.infernummod.block.ModBlocks;
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
	}
}