package net.ronm19.infernummod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.ModBlocks;

public class ModItemGroups {
    public static final ItemGroup INFERNUM_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(InfernumMod.MOD_ID, "nether_ruby"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.nether_ruby"))
                    .icon(() -> new ItemStack(ModItems.NETHER_RUBY)).entries(( displayContext, entries ) -> {
                        entries.add(ModItems.NETHER_RUBY);
                        entries.add(ModItems.RAW_NETHER_RUBY);

                        entries.add(ModItems.FIRERITE);
                        entries.add(ModItems.RAW_FIRERITE);

                        entries.add(ModItems.CURSED_FLINT);
                        entries.add(ModItems.INFERNUM_BONE);
                        entries.add(ModItems.ASH_DUST);
                        entries.add(ModItems.ASH_EGG);
                        entries.add(ModItems.INFERNUM_STAFF);
                        entries.add(ModItems.INFERNUM_HEROBRINE_RELIC);
                        entries.add(ModItems.INFERNAL_GEM);
                        entries.add(ModItems.EMBER_ASH);
                        entries.add(ModItems.INFERNAL_ROYAL_STAFF);
                        entries.add(ModItems.INFERNUM_SWORD);
                        entries.add(ModItems.ABYSSAL_BLADE);



                        entries.add(ModItems.LAVAGER_ARROW);

                        entries.add(ModItems.INFERNAL_BEAST_HORN);
                        entries.add(ModItems.ECHO_OF_DAMNATION);
                        entries.add(ModItems.BLAZE_HEART);

                        entries.add(ModItems.LAVA_FISH);
                        entries.add(ModItems.MAGMA_FISH);
                        entries.add(ModItems.FIRE_FISH);
                        entries.add(ModItems.INFERNAL_APPLE);

                        entries.add(ModItems.INFERNUM_PAXEL);
                        entries.add(ModItems.INFERNUM_DAGGER);
                        entries.add(ModItems.INFERNO_FANG);
                        entries.add(ModItems.INFERNO_SHIELD);

                        entries.add(ModItems.CINDERTSONE);
                        entries.add(ModItems.RAW_CINDESTONE);
                        entries.add(ModItems.RAW_INFERNIUM);
                        entries.add(ModItems.INFERNIUM);
                        entries.add(ModItems.RAW_PYROCLAST);
                        entries.add(ModItems.PYROCLAST);
                        entries.add(ModItems.RAW_EMBERSTONE);
                        entries.add(ModItems.EMBERSTONE);

                        entries.add(ModItems.NETHER_RUBY_PICKAXE);
                        entries.add(ModItems.NETHER_RUBY_AXE);
                        entries.add(ModItems.NETHER_RUBY_SHOVEL);
                        entries.add(ModItems.NETHER_RUBY_SWORD);
                        entries.add(ModItems.NETHER_RUBY_HOE);

                        entries.add(ModItems.EMBERSTONE_PICKAXE);
                        entries.add(ModItems.EMBERSTONE_AXE);
                        entries.add(ModItems.EMBERSTONE_SHOVEL);
                        entries.add(ModItems.EMBERSTONE_SWORD);
                        entries.add(ModItems.EMBERSTONE_HOE);

                        entries.add(ModItems.CINDERSTONE_PICKAXE);
                        entries.add(ModItems.CINDERSTONE_AXE);
                        entries.add(ModItems.CINDERSTONE_SHOVEL);
                        entries.add(ModItems.CINDERSTONE_SWORD);
                        entries.add(ModItems.CINDERSTONE_HOE);

                        entries.add(ModItems.INFERNIUM_PICKAXE);
                        entries.add(ModItems.INFERNIUM_AXE);
                        entries.add(ModItems.INFERNIUM_SHOVEL);
                        entries.add(ModItems.INFERNIUM_SWORD);
                        entries.add(ModItems.INFERNIUM_HOE);

                        entries.add(ModItems.PYROCLAST_PICKAXE);
                        entries.add(ModItems.PYROCLAST_AXE);
                        entries.add(ModItems.PYROCLAST_SHOVEL);
                        entries.add(ModItems.PYROCLAST_SWORD);
                        entries.add(ModItems.PYROCLAST_HOE);

                        entries.add(ModItems.FIRERITE_PICKAXE);
                        entries.add(ModItems.FIRERITE_AXE);
                        entries.add(ModItems.FIRERITE_SHOVEL);
                        entries.add(ModItems.FIRERITE_SWORD);
                        entries.add(ModItems.FIRERITE_HOE);

                        entries.add(ModItems.NETHER_RUBY_HELMET);
                        entries.add(ModItems.NETHER_RUBY_CHESTPLATE);
                        entries.add(ModItems.NETHER_RUBY_LEGGINGS);
                        entries.add(ModItems.NETHER_RUBY_BOOTS);

                        entries.add(ModItems.CINDERSTONE_HELMET);
                        entries.add(ModItems.CINDERSTONE_CHESTPLATE);
                        entries.add(ModItems.CINDERSTONE_LEGGINGS);
                        entries.add(ModItems.CINDERSTONE_BOOTS);


                        entries.add(ModItems.EMBERSTONE_HELMET);
                        entries.add(ModItems.EMBERSTONE_CHESTPLATE);
                        entries.add(ModItems.EMBERSTONE_LEGGINGS);
                        entries.add(ModItems.EMBERSTONE_BOOTS);

                        entries.add(ModItems.INFERNIUM_HELMET);
                        entries.add(ModItems.INFERNIUM_CHESTPLATE);
                        entries.add(ModItems.INFERNIUM_LEGGINGS);
                        entries.add(ModItems.INFERNIUM_BOOTS);

                        entries.add(ModItems.FIRERITE_HELMET);
                        entries.add(ModItems.FIRERITE_CHESTPLATE);
                        entries.add(ModItems.FIRERITE_LEGGINGS);
                        entries.add(ModItems.FIRERITE_BOOTS);


                        entries.add(ModItems.PYROCLAST_HELMET);
                        entries.add(ModItems.PYROCLAST_CHESTPLATE);
                        entries.add(ModItems.PYROCLAST_LEGGINGS);
                        entries.add(ModItems.PYROCLAST_BOOTS);

                        entries.add(ModItems.HELL_CROWN);

                        entries.add(ModItems.INFERNO_CHEST_BOAT);
                        entries.add(ModItems.INFERNO_BOAT);

                        entries.add(ModItems.DEMON_SPAWN_EGG);
                        entries.add(ModItems.MALFURYX_SPAWN_EGG);
                        entries.add(ModItems.OBSIDIAN_GHAST_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_HOARDE_SPAWN_EGG);
                        entries.add(ModItems.INFERNO_ZOMBIE_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_ZOMBILAGER_SPAWN_EGG);
                        entries.add(ModItems.FLAME_SKELETON_SPAWN_EGG);
                        entries.add(ModItems.LAVA_WITCH_SPAWN_EGG);
                        entries.add(ModItems.LAVA_SLIME_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_VEX_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_VOKER_SPAWN_EGG);
                        entries.add(ModItems.LAVACATOR_SPAWN_EGG);
                        entries.add(ModItems.LAVAGER_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_WRAITH_SPAWN_EGG);
                        entries.add(ModItems.MAGMA_SPIDER_SPAWN_EGG);
                        entries.add(ModItems.INFERNO_ENDERMAN_SPAWN_EGG);
                        entries.add(ModItems.MAGMA_STRIDER_SPAWN_EGG);
                        entries.add(ModItems.EMBER_SERPENT_SPAWN_EGG);
                        entries.add(ModItems.MAGMA_CREEPER_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_BEAST_SPAWN_EGG);
                        entries.add(ModItems.INFERNUM_HEROBRINE_SPAWN_EGG);
                        entries.add(ModItems.INFERNUM_SPAWN_EGG);

                        entries.add(ModItems.INFERNAL_KNIGHT_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_EYE_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_HORSE_SPAWN_EGG);
                        entries.add(ModItems.FLAME_HORSE_SPAWN_EGG);
                        entries.add(ModItems.ASHBONE_HORSE_SPAWN_EGG);
                        entries.add(ModItems.PYERLING_WYRN_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_PHANTOM_SPAWN_EGG);
                        entries.add(ModItems.EMBER_HUND_SPAWN_EGG);
                        entries.add(ModItems.VOLCARNIS_SPAWN_EGG);
                        entries.add(ModItems.SCORCHED_WOOLIE_SPAWN_EGG);
                        entries.add(ModItems.ASH_CHICKEN_SPAWN_EGG);
                        entries.add(ModItems.EMBER_BOAR_SPAWN_EGG);
                        entries.add(ModItems.LAVA_COW_SPAWN_EGG);
                        entries.add(ModItems.INFERNAL_RABBIT_SPAWN_EGG);
                        entries.add(ModItems.MAGMA_BEAR_SPAWN_EGG);
                        entries.add(ModItems.MAGMA_GOLEM_SPAWN_EGG);

                        entries.add(ModItems.LAVA_FISH_SPAWN_EGG);
                        entries.add(ModItems.MAGMA_FISH_SPAWN_EGG);
                        entries.add(ModItems.FIRE_FISH_SPAWN_EGG);
                        entries.add(ModItems.MAGMA_DOLPHIN_SPAWN_EGG);


                        // ---------------------------- BLOCKS ------------------------------------

                        entries.add(ModBlocks.NETHER_RUBY_BLOCK);
                        entries.add(ModBlocks.RAW_NETHER_RUBY_BLOCK);
                        entries.add(ModBlocks.NETHER_RUBY_ORE);
                        entries.add(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE);

                        entries.add(ModBlocks.INFERNO_ESSENCE_LOG);
                        entries.add(ModBlocks.INFERNO_ESSENCE_WOOD);
                        entries.add(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG);
                        entries.add(ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD);
                        entries.add(ModBlocks.INFERNO_ESSENCE_PLANKS);
                        entries.add(ModBlocks.INFERNO_ESSENCE_LEAVES);
                        entries.add(ModBlocks.INFERNO_ESSENCE_SAPLING);

                        entries.add(ModBlocks.NETHER_RUBY_BUTTON);
                        entries.add(ModBlocks.NETHER_RUBY_SLAB);
                        entries.add(ModBlocks.NETHER_RUBY_DOOR);
                        entries.add(ModBlocks.NETHER_RUBY_TRAPDOOR);
                        entries.add(ModBlocks.NETHER_RUBY_WALL);
                        entries.add(ModBlocks.NETHER_RUBY_PRESSURE_PLATE);
                        entries.add(ModBlocks.NETHER_RUBY_FENCE);
                        entries.add(ModBlocks.NETHER_RUBY_FENCE_GATE);
                        entries.add(ModBlocks.NETHER_RUBY_STAIRS);

                        entries.add(ModBlocks.FIRERITE_BUTTON);
                        entries.add(ModBlocks.FIRERITE_SLAB);
                        entries.add(ModBlocks.FIRERITE_DOOR);
                        entries.add(ModBlocks.FIRERITE_TRAPDOOR);
                        entries.add(ModBlocks.FIRERITE_WALL);
                        entries.add(ModBlocks.FIRERITE_PRESSURE_PLATE);
                        entries.add(ModBlocks.FIRERITE_FENCE);
                        entries.add(ModBlocks.FIRERITE_FENCE_GATE);
                        entries.add(ModBlocks.FIRERITE_STAIRS);

                        entries.add(ModBlocks.BLAZE_BUTTON);
                        entries.add(ModBlocks.BLAZE_SLAB);
                        entries.add(ModBlocks.BLAZE_DOOR);
                        entries.add(ModBlocks.BLAZE_TRAPDOOR);
                        entries.add(ModBlocks.BLAZE_WALL);
                        entries.add(ModBlocks.BLAZE_PRESSURE_PLATE);
                        entries.add(ModBlocks.BLAZE_FENCE);
                        entries.add(ModBlocks.BLAZE_FENCE_GATE);
                        entries.add(ModBlocks.BLAZE_STAIRS);

                        entries.add(ModBlocks.EMBERSTONE_BUTTON);
                        entries.add(ModBlocks.EMBERSTONE_SLAB);
                        entries.add(ModBlocks.EMBERSTONE_DOOR);
                        entries.add(ModBlocks.EMBERSTONE_TRAPDOOR);
                        entries.add(ModBlocks.EMBERSTONE_WALL);
                        entries.add(ModBlocks.EMBERSTONE_PRESSURE_PLATE);
                        entries.add(ModBlocks.EMBERSTONE_FENCE);
                        entries.add(ModBlocks.EMBERSTONE_FENCE_GATE);
                        entries.add(ModBlocks.EMBERSTONE_STAIRS);

                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_BUTTON);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_SLAB);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_DOOR);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_TRAPDOOR);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_WALL);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_PRESSURE_PLATE);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_FENCE);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_FENCE_GATE);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_STAIRS);

                        entries.add(ModBlocks.MOLTEN_BRICKS_BUTTON);
                        entries.add(ModBlocks.MOLTEN_BRICKS_SLAB);
                        entries.add(ModBlocks.MOLTEN_BRICKS_DOOR);
                        entries.add(ModBlocks.MOLTEN_BRICKS_TRAPDOOR);
                        entries.add(ModBlocks.MOLTEN_BRICKS_WALL);
                        entries.add(ModBlocks.MOLTEN_BRICKS_PRESSURE_PLATE);
                        entries.add(ModBlocks.MOLTEN_BRICKS_FENCE);
                        entries.add(ModBlocks.MOLTEN_BRICKS_FENCE_GATE);
                        entries.add(ModBlocks.MOLTEN_BRICKS_STAIRS);

                        entries.add(ModBlocks.MOLTEN_STONE_BUTTON);
                        entries.add(ModBlocks.MOLTEN_STONE_SLAB);
                        entries.add(ModBlocks.MOLTEN_STONE_DOOR);
                        entries.add(ModBlocks.MOLTEN_STONE_TRAPDOOR);
                        entries.add(ModBlocks.MOLTEN_STONE_WALL);
                        entries.add(ModBlocks.MOLTEN_STONE_PRESSURE_PLATE);
                        entries.add(ModBlocks.MOLTEN_STONE_FENCE);
                        entries.add(ModBlocks.MOLTEN_STONE_FENCE_GATE);
                        entries.add(ModBlocks.MOLTEN_STONE_STAIRS);

                        entries.add(ModBlocks.INFERNAL_STONE_BUTTON);
                        entries.add(ModBlocks.INFERNAL_STONE_SLAB);
                        entries.add(ModBlocks.INFERNAL_STONE_DOOR);
                        entries.add(ModBlocks.INFERNAL_STONE_TRAPDOOR);
                        entries.add(ModBlocks.INFERNAL_STONE_WALL);
                        entries.add(ModBlocks.INFERNAL_STONE_PRESSURE_PLATE);
                        entries.add(ModBlocks.INFERNAL_STONE_FENCE);
                        entries.add(ModBlocks.INFERNAL_STONE_FENCE_GATE);
                        entries.add(ModBlocks.INFERNAL_STONE_STAIRS);

                        entries.add(ModBlocks.INFERNO_ESSENCE_BUTTON);
                        entries.add(ModBlocks.INFERNO_ESSENCE_SLAB);
                        entries.add(ModBlocks.INFERNO_ESSENCE_DOOR);
                        entries.add(ModBlocks.INFERNO_ESSENCE_TRAPDOOR);
                        entries.add(ModBlocks.INFERNO_ESSENCE_WALL);
                        entries.add(ModBlocks.INFERNO_ESSENCE_PRESSURE_PLATE);
                        entries.add(ModBlocks.INFERNO_ESSENCE_FENCE);
                        entries.add(ModBlocks.INFERNO_ESSENCE_FENCE_GATE);
                        entries.add(ModBlocks.INFERNO_ESSENCE_STAIRS);

                        entries.add(ModBlocks.RAW_FIRERITE_BLOCK);
                        entries.add(ModBlocks.FIRERITE_BLOCK);
                        entries.add(ModBlocks.FIRERITE_ORE);
                        entries.add(ModBlocks.DEEPSLATE_FIRERITE_ORE);

                        entries.add(ModBlocks.BLAZE_BLOCK);
                        entries.add(ModBlocks.BLAZE_STONE_BLOCK);
                        entries.add(ModBlocks.EMBERSTONE_BLOCK);
                        entries.add(ModBlocks.RAW_EMBERSTONE_BLOCK);
                        entries.add(ModBlocks.DEEPSLATE_CINDERSTONE_ORE);
                        entries.add(ModBlocks.INFERNAL_BRICKS_STONE_BLOCK);
                        entries.add(ModBlocks.MOLTEN_BRICKS_BLOCK);
                        entries.add(ModBlocks.MOLTEN_GRANITE_BLOCK);
                        entries.add(ModBlocks.MOLTEN_STONE_BLOCK);
                        entries.add(ModBlocks.STONE_INFERNIUM_ORE);
                        entries.add(ModBlocks.INFERNAL_STONE_BLOCK);
                        entries.add(ModBlocks.EMBERSTONE_ORE);
                        entries.add(ModBlocks.NETHER_PYROCLAST_ORE);

                        entries.add(ModBlocks.ASH_BLOCK);
                        entries.add(ModBlocks.ABYSSIUM_STONE_BLOCK);
                        entries.add(ModBlocks.INFERNAL_RUNE_BLOCK);
                        entries.add(ModBlocks.CHARRED_WOOL_BLOCK);
                        entries.add(ModBlocks.INFERNAL_FORGE_BLOCK);
                        entries.add(ModBlocks.ROYAL_FIRE_BLOCK);

                        entries.add(ModBlocks.BLAZEBLOOM);

                        entries.add(ModBlocks.INFERNAL_DIRT_BLOCK);
                        entries.add(ModBlocks.INFERNAL_GRASS_BLOCK);

                        entries.add(ModBlocks.INFERNAL_OBSIDIAN_BLOCK);
                        entries.add(ModBlocks.INFERNAL_EYE_STATUE_BLOCK);

                    }).build());

    public static void  registerItemGroups() {
        InfernumMod.LOGGER.info("Registering Item Groups for " + InfernumMod.MOD_ID);
    }
}
