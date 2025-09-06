package net.ronm19.infernummod.item.custom;

import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.LavaItemConverted;
import net.ronm19.infernummod.item.ModItems;

public class NetherRubyItem extends Item implements LavaItemConverted {

    public NetherRubyItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        World world = entity.getWorld();

        if (!world.isClient) {
            BlockPos pos = entity.getBlockPos();

            // Check if the item is in lava
            if (world.getFluidState(pos).isIn(FluidTags.LAVA)) {
                // Remove Nether Ruby
                entity.discard();

                // Spawn Firerite item
                ItemEntity fireriteEntity = new ItemEntity(
                        world,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        new ItemStack(ModItems.FIRERITE)
                );
                world.spawnEntity(fireriteEntity);

                // Play lava pop sound
                world.playSound(null, pos, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);

                return true;
            }
        }

        return false;
    }
}