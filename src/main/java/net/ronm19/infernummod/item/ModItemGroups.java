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

                        entries.add(ModItems.CINDERTSONE);
                        entries.add(ModItems.RAW_CINDESTONE);
                        entries.add(ModItems.RAW_INFERNIUM);
                        entries.add(ModItems.INFERNIUM);
                        entries.add(ModItems.RAW_PYROCLAST);
                        entries.add(ModItems.PYROCLAST);
                        entries.add(ModItems.RAW_EMBERSTONE);
                        entries.add(ModItems.EMBERSTONE);

                        entries.add(ModBlocks.NETHER_RUBY_BLOCK);
                        entries.add(ModBlocks.RAW_NETHER_RUBY_BLOCK);
                        entries.add(ModBlocks.NETHER_RUBY_ORE);
                        entries.add(ModBlocks.DEEPSLATE_NETHER_RUBY_ORE);

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

                    }).build());

    public static void  registerItemGroups() {
        InfernumMod.LOGGER.info("Registering Item Groups for " + InfernumMod.MOD_ID);
    }
}
