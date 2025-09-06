package net.ronm19.infernummod.api.interfaces;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface LavaItemConverted {
    boolean onEntityItemUpdate( ItemStack stack, ItemEntity entity );
}
