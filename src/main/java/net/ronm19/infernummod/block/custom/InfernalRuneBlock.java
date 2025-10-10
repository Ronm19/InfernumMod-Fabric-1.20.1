package net.ronm19.infernummod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;

import java.util.List;

public class InfernalRuneBlock extends Block {

    public InfernalRuneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world,
                              BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {

        if (world.isClient) return ActionResult.SUCCESS;

        // Must hold the catalyst item
        if (!player.getStackInHand(hand).isOf(ModItems.CURSED_FLINT)) {
            return ActionResult.PASS;
        }

        // 1) Check the 3×3 Infernal Grass pattern
        if (!checkRitualPattern(world, pos)) {
            player.sendMessage(
                    net.minecraft.text.Text.literal("The ritual circle is incomplete!"), true);
            return ActionResult.FAIL;
        }

        // 2) Check for 4 Infernum Bones dropped on the Rune
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class,
                new net.minecraft.util.math.Box(pos).expand(1.0),
                e -> e.getStack().isOf(ModItems.INFERNUM_BONE));

        if (items.size() < 4) {
            player.sendMessage(
                    net.minecraft.text.Text.literal("The offering is insufficient!"), true);
            return ActionResult.FAIL;
        }

        // Consume 4 bones
        int bonesToConsume = 4;
        for (ItemEntity item : items) {
            ItemStack stack = item.getStack();
            int take = Math.min(stack.getCount(), bonesToConsume);
            stack.decrement(take);
            bonesToConsume -= take;
            if (bonesToConsume <= 0) break;
        }

        // Consume catalyst (optional)
        if (!player.getAbilities().creativeMode) {
            player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(hand));
        }

        // 3) FX
        world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                net.minecraft.sound.SoundCategory.WEATHER, 4f, 0.8f);
        ((net.minecraft.server.world.ServerWorld) world)
                .spawnParticles(net.minecraft.particle.ParticleTypes.FLAME,
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                        150, 1.5, 0.5, 1.5, 0.01);

        // 4) Spawn Herobrine Boss
        var herobrine = ModEntities.INFERNUM_HEROBRINE.create(world);
        if (herobrine != null) {
            herobrine.refreshPositionAndAngles(pos.getX() + 0.5,
                    pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
            world.spawnEntity(herobrine);
        }

        return ActionResult.SUCCESS;
    }

    private boolean checkRitualPattern(World world, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; // skip center
                BlockPos check = center.add(dx, 0, dz);
                if (!world.getBlockState(check).isOf(ModBlocks.INFERNAL_GRASS_BLOCK)) {
                    return false;
                }
            }
        }
        return true;
    }
}
