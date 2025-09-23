package net.ronm19.infernummod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.EmberHundEntity;

public class InfernalRuneBlock extends Block {
    public InfernalRuneBlock( Settings settings ) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient) {
            ItemStack stack = player.getStackInHand(hand);

            // Summon the Infernal Hound
            EmberHundEntity hound = ModEntities.EMBER_HUND.create(world);
            if (hound != null) {
                hound.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                        player.getYaw(), 0);
                world.spawnEntity(hound);

                // Optional: play summoning sound
                world.playSound(null, pos, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);

                // Optional: consume the rune if not in creative
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
        }

        return ActionResult.SUCCESS;
    }
}
