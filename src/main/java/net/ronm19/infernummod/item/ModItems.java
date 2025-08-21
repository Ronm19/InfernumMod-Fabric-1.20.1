package net.ronm19.infernummod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;

public class ModItems {
    public static final Item NETHER_RUBY = registerItem("nether_ruby", new Item(new FabricItemSettings()));
    public static final Item RAW_NETHER_RUBY = registerItem("raw_nether_ruby", new Item(new FabricItemSettings()));

    public static final Item FIRERITE = registerItem("firerite", new Item(new FabricItemSettings()));
    public static final Item RAW_FIRERITE = registerItem("raw_firerite", new Item(new FabricItemSettings()));

    public static final Item INFERNIUM = registerItem("infernium", new Item(new FabricItemSettings()));
    public static final Item RAW_INFERNIUM = registerItem("raw_infernium", new Item(new FabricItemSettings()));
    public static final Item PYROCLAST = registerItem("pyroclast", new Item(new FabricItemSettings()));
    public static final Item RAW_PYROCLAST = registerItem("raw_pyroclast", new Item(new FabricItemSettings()));
    public static final Item RAW_CINDESTONE = registerItem("raw_cinderstone", new Item(new FabricItemSettings()));
    public static final Item CINDERTSONE = registerItem("cinderstone", new Item(new FabricItemSettings()));
    public static final Item RAW_EMBERSTONE = registerItem("raw_emberstone", new Item(new FabricItemSettings()));
    public static final Item EMBERSTONE = registerItem("emberstone", new Item(new FabricItemSettings()));



    private static void addItemsToIngredientItemGroup( FabricItemGroupEntries entries) {
        entries.add(NETHER_RUBY);
        entries.add(RAW_NETHER_RUBY);
        entries.add(FIRERITE);
        entries.add(RAW_FIRERITE);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(InfernumMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        InfernumMod.LOGGER.info("Registering Mod Items for " + InfernumMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
