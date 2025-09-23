package net.ronm19.infernummod.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> INFERNUM_PAXEL_MINEABLE =
                createBlockTag("mineable/paxel");

        private static TagKey<Block> createBlockTag( String name ) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(InfernumMod.MOD_ID, name));
        }

        private static TagKey<Block> createCommonBlockTag( String name ) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier("c", name));
        }
    }



    public static class Items {

        private static TagKey<Item> createItemTag( String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(InfernumMod.MOD_ID, name));
        }

        private static TagKey<Item> createCommonItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier("c", name));
        }
    }
}

