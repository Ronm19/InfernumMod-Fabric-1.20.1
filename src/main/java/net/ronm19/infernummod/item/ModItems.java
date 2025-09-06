package net.ronm19.infernummod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.custom.EmberstoneSwordItem;
import net.ronm19.infernummod.item.custom.ModArmorItem;
import net.ronm19.infernummod.item.custom.NetherRubyItem;

public class ModItems {
    public static final Item NETHER_RUBY = registerItem("nether_ruby", new NetherRubyItem(new FabricItemSettings().fireproof()));
    public static final Item RAW_NETHER_RUBY = registerItem("raw_nether_ruby", new Item(new FabricItemSettings()));

    public static final Item FIRERITE = registerItem("firerite", new Item(new FabricItemSettings().fireproof()));
    public static final Item RAW_FIRERITE = registerItem("raw_firerite", new Item(new FabricItemSettings().fireproof()));

    public static final Item INFERNIUM = registerItem("infernium", new Item(new FabricItemSettings().fireproof()));
    public static final Item RAW_INFERNIUM = registerItem("raw_infernium", new Item(new FabricItemSettings().fireproof()));
    public static final Item PYROCLAST = registerItem("pyroclast", new Item(new FabricItemSettings().fireproof()));
    public static final Item RAW_PYROCLAST = registerItem("raw_pyroclast", new Item(new FabricItemSettings().fireproof()));
    public static final Item RAW_CINDESTONE = registerItem("raw_cinderstone", new Item(new FabricItemSettings().fireproof()));
    public static final Item CINDERTSONE = registerItem("cinderstone", new Item(new FabricItemSettings().fireproof()));
    public static final Item RAW_EMBERSTONE = registerItem("raw_emberstone", new Item(new FabricItemSettings().fireproof()));
    public static final Item EMBERSTONE = registerItem("emberstone", new Item(new FabricItemSettings().fireproof()));

    public static final Item NETHER_RUBY_PICKAXE = registerItem("nether_ruby_pickaxe",
            new PickaxeItem(ModToolMaterial.NETHER_RUBY, 3, 2.0f, new FabricItemSettings().fireproof()));
    public static final Item NETHER_RUBY_AXE = registerItem("nether_ruby_axe",
            new AxeItem(ModToolMaterial.NETHER_RUBY, 9, 3.0f, new FabricItemSettings().fireproof()));
    public static final Item NETHER_RUBY_SHOVEL = registerItem("nether_ruby_shovel",
            new ShovelItem(ModToolMaterial.NETHER_RUBY, 3, 2.8f, new FabricItemSettings().fireproof()));
    public static final Item NETHER_RUBY_SWORD = registerItem("nether_ruby_sword",
            new SwordItem(ModToolMaterial.NETHER_RUBY, 9, 2.2f, new FabricItemSettings().fireproof()));
    public static final Item NETHER_RUBY_HOE = registerItem("nether_ruby_hoe",
            new HoeItem(ModToolMaterial.NETHER_RUBY, 0, 0f, new FabricItemSettings().fireproof()));

    public static final Item EMBERSTONE_PICKAXE = registerItem("emberstone_pickaxe",
            new PickaxeItem(ModToolMaterial.EMBERSTONE, 2, 2.3f, new FabricItemSettings().fireproof()));
    public static final Item EMBERSTONE_AXE = registerItem("emberstone_axe",
            new AxeItem(ModToolMaterial.EMBERSTONE, 7, 3.2f, new FabricItemSettings().fireproof()));
    public static final Item EMBERSTONE_SHOVEL = registerItem("emberstone_shovel",
            new ShovelItem(ModToolMaterial.EMBERSTONE, 2, 2.9f, new FabricItemSettings().fireproof()));
    public static final Item EMBERSTONE_SWORD = registerItem("emberstone_sword",
            new EmberstoneSwordItem(ModToolMaterial.EMBERSTONE, 7, 2.1f, new FabricItemSettings().fireproof()));
    public static final Item EMBERSTONE_HOE = registerItem("emberstone_hoe",
            new HoeItem(ModToolMaterial.EMBERSTONE, 0, 0f, new FabricItemSettings().fireproof()));

    public static final Item CINDERSTONE_PICKAXE = registerItem("cinderstone_pickaxe",
            new PickaxeItem(ModToolMaterial.CINDERSTONE, 2, 2.5f, new FabricItemSettings().fireproof()));
    public static final Item CINDERSTONE_AXE = registerItem("cinderstone_axe",
            new AxeItem(ModToolMaterial.CINDERSTONE, 10, 3.0f, new FabricItemSettings().fireproof()));
    public static final Item CINDERSTONE_SHOVEL = registerItem("cinderstone_shovel",
            new ShovelItem(ModToolMaterial.CINDERSTONE, 5, 2.3f, new FabricItemSettings().fireproof()));
    public static final Item CINDERSTONE_SWORD = registerItem("cinderstone_sword",
            new SwordItem(ModToolMaterial.CINDERSTONE, 10, 2.4f, new FabricItemSettings().fireproof()));
    public static final Item CINDERSTONE_HOE = registerItem("cinderstone_hoe",
            new HoeItem(ModToolMaterial.CINDERSTONE, 0, 0f, new FabricItemSettings().fireproof()));

    public static final Item INFERNIUM_PICKAXE = registerItem("infernium_pickaxe",
            new PickaxeItem(ModToolMaterial.INFERNIUM, 2, 2.3f, new FabricItemSettings().fireproof()));
    public static final Item INFERNIUM_AXE = registerItem("infernium_axe",
            new AxeItem(ModToolMaterial.INFERNIUM, 10.5f, 3.0f, new FabricItemSettings().fireproof()));
    public static final Item INFERNIUM_SHOVEL = registerItem("infernium_shovel",
            new ShovelItem(ModToolMaterial.INFERNIUM, 4, 2.2f, new FabricItemSettings().fireproof()));
    public static final Item INFERNIUM_SWORD = registerItem("infernium_sword",
            new SwordItem(ModToolMaterial.INFERNIUM, 11, 2.1f, new FabricItemSettings().fireproof()));
    public static final Item INFERNIUM_HOE = registerItem("infernium_hoe",
            new HoeItem(ModToolMaterial.INFERNIUM, 0, 0f, new FabricItemSettings().fireproof()));

    public static final Item PYROCLAST_PICKAXE = registerItem("pyroclast_pickaxe",
            new PickaxeItem(ModToolMaterial.PYROCLAST, 3, 2.2f, new FabricItemSettings().fireproof()));
    public static final Item PYROCLAST_AXE = registerItem("pyroclast_axe",
            new AxeItem(ModToolMaterial.PYROCLAST, 11.5f, 2.8f, new FabricItemSettings().fireproof()));
    public static final Item PYROCLAST_SHOVEL = registerItem("pyroclast_shovel",
            new ShovelItem(ModToolMaterial.PYROCLAST, 2, 2.6f, new FabricItemSettings().fireproof()));
    public static final Item PYROCLAST_SWORD = registerItem("pyroclast_sword",
            new SwordItem(ModToolMaterial.PYROCLAST, 12, 2.2f, new FabricItemSettings().fireproof()));
    public static final Item PYROCLAST_HOE = registerItem("pyroclast_hoe",
            new HoeItem(ModToolMaterial.PYROCLAST, 0, 0f, new FabricItemSettings().fireproof()));

    public static final Item FIRERITE_PICKAXE = registerItem("firerite_pickaxe",
            new PickaxeItem(ModToolMaterial.FIRERITE, 4, 2.1f, new FabricItemSettings().fireproof()));
    public static final Item FIRERITE_AXE = registerItem("firerite_axe",
            new AxeItem(ModToolMaterial.FIRERITE, 12.5f, 2.6f, new FabricItemSettings().fireproof()));
    public static final Item FIRERITE_SHOVEL = registerItem("firerite_shovel",
            new ShovelItem(ModToolMaterial.FIRERITE, 3, 2.4f, new FabricItemSettings().fireproof()));
    public static final Item FIRERITE_SWORD = registerItem("firerite_sword",
            new EmberstoneSwordItem(ModToolMaterial.FIRERITE, 14, 2.1f, new FabricItemSettings().fireproof()));
    public static final Item FIRERITE_HOE = registerItem("firerite_hoe",
            new HoeItem(ModToolMaterial.FIRERITE, 0, 0f, new FabricItemSettings().fireproof()));


    public static final Item NETHER_RUBY_HELMET = registerItem("nether_ruby_helmet",
            new ModArmorItem(ModArmorMaterials.NETHER_RUBY, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));
    public static final Item NETHER_RUBY_CHESTPLATE = registerItem("nether_ruby_chestplate",
            new ArmorItem(ModArmorMaterials.NETHER_RUBY, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().fireproof()));
    public static final Item NETHER_RUBY_LEGGINGS = registerItem("nether_ruby_leggings",
            new ArmorItem(ModArmorMaterials.NETHER_RUBY, ArmorItem.Type.LEGGINGS, new FabricItemSettings().fireproof()));
    public static final Item NETHER_RUBY_BOOTS = registerItem("nether_ruby_boots",
            new ArmorItem(ModArmorMaterials.NETHER_RUBY, ArmorItem.Type.BOOTS, new FabricItemSettings().fireproof()));

    public static final Item CINDERSTONE_HELMET = registerItem("cinderstone_helmet",
            new ModArmorItem(ModArmorMaterials.CINDERSTONE, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));
    public static final Item CINDERSTONE_CHESTPLATE = registerItem("cinderstone_chestplate",
            new ArmorItem(ModArmorMaterials.CINDERSTONE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().fireproof()));
    public static final Item CINDERSTONE_LEGGINGS = registerItem("cinderstone_leggings",
            new ArmorItem(ModArmorMaterials.CINDERSTONE, ArmorItem.Type.LEGGINGS, new FabricItemSettings().fireproof()));
    public static final Item CINDERSTONE_BOOTS = registerItem("cinderstone_boots",
            new ArmorItem(ModArmorMaterials.CINDERSTONE, ArmorItem.Type.BOOTS, new FabricItemSettings().fireproof()));

    public static final Item EMBERSTONE_HELMET = registerItem("emberstone_helmet",
            new ModArmorItem(ModArmorMaterials.EMBERSTONE, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));
    public static final Item EMBERSTONE_CHESTPLATE = registerItem("emberstone_chestplate",
            new ArmorItem(ModArmorMaterials.EMBERSTONE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().fireproof()));
    public static final Item EMBERSTONE_LEGGINGS = registerItem("emberstone_leggings",
            new ArmorItem(ModArmorMaterials.EMBERSTONE, ArmorItem.Type.LEGGINGS, new FabricItemSettings().fireproof()));
    public static final Item EMBERSTONE_BOOTS = registerItem("emberstone_boots",
            new ArmorItem(ModArmorMaterials.EMBERSTONE, ArmorItem.Type.BOOTS, new FabricItemSettings().fireproof()));

    public static final Item INFERNIUM_HELMET = registerItem("infernium_helmet",
            new ModArmorItem(ModArmorMaterials.INFERNIUM, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));
    public static final Item INFERNIUM_CHESTPLATE = registerItem("infernium_chestplate",
            new ArmorItem(ModArmorMaterials.INFERNIUM, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().fireproof()));
    public static final Item INFERNIUM_LEGGINGS = registerItem("infernium_leggings",
            new ArmorItem(ModArmorMaterials.INFERNIUM, ArmorItem.Type.LEGGINGS, new FabricItemSettings().fireproof()));
    public static final Item INFERNIUM_BOOTS = registerItem("infernium_boots",
            new ArmorItem(ModArmorMaterials.INFERNIUM, ArmorItem.Type.BOOTS, new FabricItemSettings().fireproof()));

    public static final Item PYROCLAST_HELMET = registerItem("pyroclast_helmet",
            new ModArmorItem(ModArmorMaterials.PYROCLAST, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));
    public static final Item PYROCLAST_CHESTPLATE = registerItem("pyroclast_chestplate",
            new ArmorItem(ModArmorMaterials.PYROCLAST, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().fireproof()));
    public static final Item PYROCLAST_LEGGINGS = registerItem("pyroclast_leggings",
            new ArmorItem(ModArmorMaterials.PYROCLAST, ArmorItem.Type.LEGGINGS, new FabricItemSettings().fireproof()));
    public static final Item PYROCLAST_BOOTS = registerItem("pyroclast_boots",
            new ArmorItem(ModArmorMaterials.PYROCLAST, ArmorItem.Type.BOOTS, new FabricItemSettings().fireproof()));

    public static final Item FIRERITE_HELMET = registerItem("firerite_helmet",
            new ModArmorItem(ModArmorMaterials.FIRERITE, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));
    public static final Item FIRERITE_CHESTPLATE = registerItem("firerite_chestplate",
            new ArmorItem(ModArmorMaterials.FIRERITE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().fireproof()));
    public static final Item FIRERITE_LEGGINGS = registerItem("firerite_leggings",
            new ArmorItem(ModArmorMaterials.FIRERITE, ArmorItem.Type.LEGGINGS, new FabricItemSettings().fireproof()));
    public static final Item FIRERITE_BOOTS = registerItem("firerite_boots",
            new ArmorItem(ModArmorMaterials.FIRERITE, ArmorItem.Type.BOOTS, new FabricItemSettings().fireproof()));

    public static final Item DEMON_SPAWN_EGG = registerItem("demon_spawn_egg",
            new SpawnEggItem(ModEntities.DEMON, 0xFF4500, 0x1C1C1C, new FabricItemSettings()));

    public static final Item MALFURYX_SPAWN_EGG = registerItem("malfuryx_spawn_egg",
            new SpawnEggItem(ModEntities.MALFURYX, 0x0A0A0A, 0x1F1F1F, new FabricItemSettings()));





    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(InfernumMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        InfernumMod.LOGGER.info("Registering Mod Items for " + InfernumMod.MOD_ID);

    }
}
