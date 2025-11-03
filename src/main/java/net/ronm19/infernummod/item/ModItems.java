package net.ronm19.infernummod.item;

import com.terraformersmc.terraform.boat.api.item.TerraformBoatItemHelper;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.ModBoats;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.custom.*;

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

    public static final Item BLAZE_HEART = registerItem("blaze_heart", new Item(new FabricItemSettings().fireproof().maxCount(16)));
    public static final Item INFERNAL_BEAST_HORN = registerItem("infernal_beast_horn", new Item(new FabricItemSettings().fireproof()));
    public static final Item ECHO_OF_DAMNATION = registerItem("echo_of_damnation", new Item(new FabricItemSettings().maxCount(1).fireproof().rarity(Rarity.EPIC)));

    public static final Item INFERNUM_BONE = registerItem("infernum_bone", new Item(new FabricItemSettings().fireproof()));

    public static final Item INFERNUM_HEROBRINE_RELIC = registerItem("infernum_herobrine_relic", new Item(new FabricItemSettings().fireproof().maxCount(1)));
    public static final Item INFERNAL_GEM = registerItem("infernal_gem", new Item(new FabricItemSettings().fireproof()));

    public static final Item ASH_DUST = registerItem("ash_dust", new AshDustItem(new FabricItemSettings().fireproof()));
    public static final Item ASH_EGG = registerItem("ash_egg", new AshEggItem(new FabricItemSettings().fireproof().maxCount(16)));

    public static final Item EMBER_ASH = registerItem("ember_ash", new Item(new FabricItemSettings().fireproof()));

    public static final Item HELL_CROWN = registerItem("hell_crown",
            new HellCrownItem(ModArmorMaterials.HELL_CROWN, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));

    public static final Item CURSED_FLINT = registerItem("cursed_flint", new Item(new FabricItemSettings().fireproof()));
    public static final Item INFERNUM_STAFF = registerItem("infernum_staff", new InfernalStaffItem(new FabricItemSettings().maxDamage(2500).fireproof().rarity(Rarity.UNCOMMON)));
    public static final Item INFERNAL_ROYAL_STAFF = registerItem("infernal_royal_staff", new InfernalRoyalStaffItem(new FabricItemSettings().maxDamage(512).fireproof().rarity(Rarity.UNCOMMON)));

    public static final Item INFERNUM_PAXEL = registerItem("infernum_paxel",
            new InfernumPaxelItem(ModToolMaterial.FIRERITE, 8, 2.3f, new FabricItemSettings().fireproof()));
    public static final Item INFERNUM_DAGGER = registerItem("infernum_dagger",
            new ModInfernumWeaknessSwordItem(ModToolMaterial.NETHER_RUBY, 9, 2.2f, new FabricItemSettings().fireproof()));
    public static final Item INFERNUM_SWORD = registerItem("infernum_sword",
            new InfernumSwordItem(ModToolMaterial.FIRERITE, 20, 2.3f, new FabricItemSettings().fireproof().rarity(Rarity.UNCOMMON)));
    public static final Item ABYSSAL_BLADE = registerItem("abyssal_blade",
            new AbyssalBladeItem(ModToolMaterial.FIRERITE, 17, 2.2f, new FabricItemSettings().fireproof().rarity(Rarity.RARE)));

    public static final Item LAVAGER_ARROW = registerItem("lavager_arrow",
            new LavagerArrowItem(new FabricItemSettings().fireproof()));

    public static final Item INFERNO_FANG = registerItem("inferno_fang",
            new FireballStrikerSwordItem(ModToolMaterial.FIRERITE, 7, 2.2f, new FabricItemSettings().fireproof().rarity(Rarity.UNCOMMON)));
    public static final Item INFERNO_SHIELD = registerItem("inferno_shield",
            new ShieldItem(new FabricItemSettings().fireproof()));

    public static final Item LAVA_FISH = registerItem("lava_fish",
            new Item(new FabricItemSettings().food(ModFoodComponents.LAVA_FISH).fireproof().maxCount(16)));
    public static final Item MAGMA_FISH = registerItem("magma_fish",
            new Item(new FabricItemSettings().food(ModFoodComponents.MAGMA_FISH).fireproof().maxCount(16)));
    public static final Item FIRE_FISH = registerItem("fire_fish",
            new Item(new FabricItemSettings().food(ModFoodComponents.FIRE_FISH).fireproof().maxCount(16)));
    public static final Item INFERNAL_APPLE = registerItem("infernal_apple",
            new Item(new FabricItemSettings().fireproof().food(ModFoodComponents.INFERNAL_APPLE)));

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


    public static final Item INFERNAL_KNIGHT_SPAWN_EGG = registerItem("infernal_knight_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_KNIGHT, 0x3C0F00, 0xFF8C00, new FabricItemSettings()));
    public static final Item INFERNAL_EYE_SPAWN_EGG = registerItem("infernal_eye_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_EYE, 0x400000, 0xF3E000, new FabricItemSettings()));
    public static final Item INFERNAL_HORSE_SPAWN_EGG = registerItem("infernal_horse_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_HORSE, 0x3B0C02, 0xFF4500, new FabricItemSettings()));
    public static final Item FLAME_HORSE_SPAWN_EGG = registerItem("flame_horse_spawn_egg",
            new SpawnEggItem(ModEntities.FLAME_HORSE,  0x1A0000, 0xFF6600, new FabricItemSettings()));
    public static final Item ASHBONE_HORSE_SPAWN_EGG = registerItem("ashbone_horse_spawn_egg",
            new SpawnEggItem(ModEntities.ASHBONE_HORSE, 0x4A403A, 0xC97E3A, new FabricItemSettings()));
    public static final Item PYERLING_WYRN_SPAWN_EGG = registerItem("pyerling_wyrn_spawn_egg",
            new SpawnEggItem(ModEntities.PYERLING_WYRN, 0xC1440A, 0xFFD12C, new FabricItemSettings()));
    public static final Item INFERNAL_PHANTOM_SPAWN_EGG = registerItem("infernal_phantom_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_PHANTOM, 0x3A0D0D, 0xE65C2C, new FabricItemSettings()));
    public static final Item EMBER_HUND_SPAWN_EGG = registerItem("ember_hund_spawn_egg",
            new SpawnEggItem(ModEntities.EMBER_HUND, 0x5C1C00, 0xFF6B00, new FabricItemSettings()));
    public static final Item VOLCARNIS_SPAWN_EGG = registerItem("volcarnis_spawn_egg",
            new SpawnEggItem(ModEntities.VOLCARNIS, 0xE25822, 0x8B0000, new FabricItemSettings()));
    public static final Item SCORCHED_WOOLIE_SPAWN_EGG = registerItem("scorched_woolie_spawn_egg",
            new SpawnEggItem(ModEntities.SCORCHED_WOOLIE, 0x4B2A24, 0xD67B5D, new FabricItemSettings()));
    public static final Item INFERNAL_RABBIT_SPAWN_EGG = registerItem("infernal_rabbit_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_RABBIT, 0x2B0900, 0xFF3C00, new FabricItemSettings()));
    public static final Item ASH_CHICKEN_SPAWN_EGG = registerItem("ash_chicken_spawn_egg",
            new SpawnEggItem(ModEntities.ASH_CHICKEN, 0x2B1B18, 0xD96500, new FabricItemSettings()));
    public static final Item EMBER_BOAR_SPAWN_EGG = registerItem("ember_boar_spawn_egg",
            new SpawnEggItem(ModEntities.EMBER_BOAR, 0x2A0E00, 0xFF5500, new FabricItemSettings()));
    public static final Item LAVA_COW_SPAWN_EGG = registerItem("lava_cow_spawn_egg",
            new SpawnEggItem(ModEntities.LAVA_COW, 0x3A0D00, 0xFF4B00, new FabricItemSettings()));
    public static final Item MAGMA_BEAR_SPAWN_EGG = registerItem("magma_bear_spawn_egg",
            new SpawnEggItem(ModEntities.MAGMA_BEAR, 0xA0211C, 0xF4511E, new FabricItemSettings()));
    public static final Item MAGMA_GOLEM_SPAWN_EGG = registerItem("magma_golem_spawn_egg",
            new SpawnEggItem(ModEntities.MAGMA_GOLEM, 0x3C0D04, 0xFF5A00, new FabricItemSettings()));




    public static final Item LAVA_FISH_SPAWN_EGG = registerItem("lava_fish_spawn_egg",
            new SpawnEggItem(ModEntities.LAVA_FISH, 0xD32F2F, 0xFF9800, new FabricItemSettings()));
    public static final Item MAGMA_FISH_SPAWN_EGG = registerItem("magma_fish_spawn_egg",
            new SpawnEggItem(ModEntities.MAGMA_FISH,  0x4B2E24, 0xFF6F00, new FabricItemSettings()));
    public static final Item FIRE_FISH_SPAWN_EGG = registerItem("fire_fish_spawn_egg",
            new SpawnEggItem(ModEntities.FIRE_FISH,  0xFF6F00, 0xFFD54F, new FabricItemSettings()));
    public static final Item MAGMA_DOLPHIN_SPAWN_EGG = registerItem("magma_dolphin_spawn_egg",
            new SpawnEggItem(ModEntities.MAGMA_DOLPHIN,  0xFF3C00, 0x7A1A00, new FabricItemSettings()));


    public static final Item DEMON_SPAWN_EGG = registerItem("demon_spawn_egg",
            new SpawnEggItem(ModEntities.DEMON, 0x7A0000, 0xFF3C00, new FabricItemSettings()));
    public static final Item MALFURYX_SPAWN_EGG = registerItem("malfuryx_spawn_egg",
            new SpawnEggItem(ModEntities.MALFURYX, 0x101010, 0x7A0B7A, new FabricItemSettings()));
    public static final Item OBSIDIAN_GHAST_SPAWN_EGG = registerItem("obsidian_ghast_spawn_egg",
            new SpawnEggItem(ModEntities.OBSIDIAN_GHAST, 0x2B2B2B, 0x8A0000, new FabricItemSettings()));
    public static final Item INFERNAL_HOARDE_SPAWN_EGG = registerItem("infernal_hoarde_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_HOARDE, 0xA52C0A, 0xE3B800, new FabricItemSettings()));
    public static final Item INFERNO_ZOMBIE_SPAWN_EGG = registerItem("inferno_zombie_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNO_ZOMBIE, 0x331C1C, 0x552000, new FabricItemSettings()));
    public static final Item INFERNAL_ZOMBILAGER_SPAWN_EGG = registerItem("infernal_zombilager_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_ZOMBILAGER, 0x5A1E16, 0xD35400, new FabricItemSettings()));
    public static final Item FLAME_SKELETON_SPAWN_EGG = registerItem("flame_skeleton_spawn_egg",
            new SpawnEggItem(ModEntities.FLAME_SKELETON, 0xB32C00, 0xFF8C1A, new FabricItemSettings()));
    public static final Item MAGMA_SPIDER_SPAWN_EGG = registerItem("magma_spider_spawn_egg",
            new SpawnEggItem(ModEntities.MAGMA_SPIDER, 0x3B1B00, 0xFF6F00, new FabricItemSettings()));
    public static final Item INFERNAL_VEX_SPAWN_EGG = registerItem("infernal_vex_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_VEX, 0x2B0B0B, 0xE14A00, new FabricItemSettings()));
    public static final Item INFERNAL_WRAITH_SPAWN_EGG = registerItem("infernal_wraith_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_WRAITH, 0x1A0A00, 0xFF5E00, new FabricItemSettings()));
    public static final Item LAVACATOR_SPAWN_EGG = registerItem("lavacator_spawn_egg",
            new SpawnEggItem(ModEntities.LAVACATOR, 0x2C0F08, 0xFF4500, new FabricItemSettings()));
    public static final Item LAVAGER_SPAWN_EGG = registerItem("lavager_spawn_egg",
            new SpawnEggItem(ModEntities.LAVAGER, 0x3B0C04, 0xFF4E0D, new FabricItemSettings()));
    public static final Item INFERNAL_VOKER_SPAWN_EGG = registerItem("infernal_voker_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_VOKER, 0x2B0B0B, 0xE14A00, new FabricItemSettings()));
    public static final Item LAVA_WITCH_SPAWN_EGG = registerItem("lava_witch_spawn_egg",
            new SpawnEggItem(ModEntities.LAVA_WITCH, 0x4B0D09, 0xFF5E00, new FabricItemSettings()));
    public static final Item LAVA_SLIME_SPAWN_EGG = registerItem("lava_slime_spawn_egg",
            new SpawnEggItem(ModEntities.LAVA_SLIME, 0x7A1F00, 0xFF6600, new FabricItemSettings()));
    public static final Item MAGMA_CREEPER_SPAWN_EGG = registerItem("magma_creeper_spawn_egg",
            new SpawnEggItem(ModEntities.MAGMA_CREEPER, 0x4A1A0B, 0xFF4500, new FabricItemSettings()));
    public static final Item INFERNO_ENDERMAN_SPAWN_EGG = registerItem("inferno_enderman_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNO_ENDERMAN, 0x1A0B0B, 0xFF3C00, new FabricItemSettings()));
    public static final Item MAGMA_STRIDER_SPAWN_EGG = registerItem("magma_strider_spawn_egg",
            new SpawnEggItem(ModEntities.MAGMA_STRIDER, 0x4A1B00, 0xFF3C00, new FabricItemSettings()));
    public static final Item EMBER_SERPENT_SPAWN_EGG = registerItem("ember_serpent_spawn_egg",
            new SpawnEggItem(ModEntities.EMBER_SERPENT,  0x8B0000, 0xFF6600, new FabricItemSettings()));
    public static final Item INFERNUM_HEROBRINE_SPAWN_EGG = registerItem("infernum_herobrine_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNUM_HEROBRINE, 0x3A0D0D, 0xC90000, new FabricItemSettings()));
    public static final Item INFERNAL_BEAST_SPAWN_EGG = registerItem("infernal_beast_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_BEAST, 0x8B1A1A, 0xFF8200, new FabricItemSettings()));
    public static final Item INFERNUM_SPAWN_EGG = registerItem("infernum_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNUM, 0x0D0D0D, 0xFF2400, new FabricItemSettings()));






    public static final Item INFERNO_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.INFERNO_BOAT_ID, ModBoats.INFERNO_BOAT_KEY, false);
    public static final Item INFERNO_CHEST_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.INFERNO_CHEST_BOAT_ID, ModBoats.INFERNO_BOAT_KEY, true);


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(InfernumMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        InfernumMod.LOGGER.info("Registering Mod Items for " + InfernumMod.MOD_ID);

    }
}
