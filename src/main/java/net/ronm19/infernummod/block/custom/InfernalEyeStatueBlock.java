package net.ronm19.infernummod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.InfernumEntity;
import net.ronm19.infernummod.item.ModItems;

import java.util.List;

public class InfernalEyeStatueBlock extends Block {

    public InfernalEyeStatueBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world,
                              BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {

        if (world.isClient) return ActionResult.SUCCESS;

        // ---- Catalyst check ----
        if (!player.getStackInHand(hand).isOf(ModItems.ECHO_OF_DAMNATION)) {
            player.sendMessage(Text.literal("The statue remains dormant..."), true);
            return ActionResult.PASS;
        }

        // ---- Obsidian ring check ----
        if (!checkObsidianRing(world, pos)) {
            player.sendMessage(Text.literal("The Obsidian ring is incomplete!"), true);
            return ActionResult.FAIL;
        }

        // ---- Skull check ----
        BlockPos skullPos = pos.up();
        if (!world.getBlockState(skullPos).isOf(Blocks.WITHER_SKELETON_SKULL)
                && !world.getBlockState(skullPos).isOf(Blocks.WITHER_SKELETON_WALL_SKULL)) {
            player.sendMessage(Text.literal("A Wither Skeleton Skull is required atop the statue!"), true);
            return ActionResult.FAIL;
        }

        // ---- Offerings check ----
        List<ItemEntity> offerings = world.getEntitiesByClass(ItemEntity.class,
                new net.minecraft.util.math.Box(pos).expand(2.0),
                e -> e.getStack().isOf(ModItems.BLAZE_HEART) ||
                        e.getStack().isOf(ModItems.INFERNAL_BEAST_HORN));

        int blazeHearts = 0;
        int beastHorns = 0;
        for (ItemEntity item : offerings) {
            if (item.getStack().isOf(ModItems.BLAZE_HEART))
                blazeHearts += item.getStack().getCount();
            else if (item.getStack().isOf(ModItems.INFERNAL_BEAST_HORN))
                beastHorns += item.getStack().getCount();
        }

        if (blazeHearts < 4 || beastHorns < 2) {
            player.sendMessage(Text.literal("The offering is insufficient!"), true);
            return ActionResult.FAIL;
        }

        // ---- Consume offerings ----
        int heartsNeeded = 4;
        int hornsNeeded = 2;
        for (ItemEntity item : offerings) {
            ItemStack stack = item.getStack();
            if (stack.isOf(ModItems.BLAZE_HEART) && heartsNeeded > 0) {
                int take = Math.min(stack.getCount(), heartsNeeded);
                stack.decrement(take);
                heartsNeeded -= take;
            }
            if (stack.isOf(ModItems.INFERNAL_BEAST_HORN) && hornsNeeded > 0) {
                int take = Math.min(stack.getCount(), hornsNeeded);
                stack.decrement(take);
                hornsNeeded -= take;
            }
            if (heartsNeeded <= 0 && hornsNeeded <= 0) break;
        }

        // ---- Consume catalyst ----
        if (!player.getAbilities().creativeMode) {
            player.getStackInHand(hand).decrement(1);
        }

        // ---- Remove skull ----
        world.breakBlock(skullPos, false);

        // ---- Ritual FX ----
        world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.WEATHER, 5f, 0.6f);
        world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE,
                SoundCategory.BLOCKS, 3f, 0.8f);

        if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
            // Lava bursts on each Obsidian in ring
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                        BlockPos ringPos = pos.add(dx, 0, dz);
                        if (world.getBlockState(ringPos).isOf(ModBlocks.INFERNAL_OBSIDIAN_BLOCK)) {
                            serverWorld.spawnParticles(
                                    net.minecraft.particle.ParticleTypes.LAVA,
                                    ringPos.getX() + 0.5, ringPos.getY() + 1, ringPos.getZ() + 0.5,
                                    20, 0.4, 0.2, 0.4, 0.05);
                        }
                    }
                }
            }

            // Central flame burst
            serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    300, 2.0, 1.0, 2.0, 0.03);

            // ---- Spawn Infernum instantly ----
            InfernumEntity infernum = ModEntities.INFERNUM.create(serverWorld);
            if (infernum != null) {
                infernum.refreshPositionAndAngles(
                        pos.getX() + 0.5,
                        pos.getY() + 1,
                        pos.getZ() + 0.5,
                        0, 0);
                serverWorld.spawnEntity(infernum);
            }
        }

        player.sendMessage(Text.literal("🔥 The Lord of Fire has awoken!"), true);
        return ActionResult.SUCCESS;
    }

    private boolean checkObsidianRing(World world, BlockPos center) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                    BlockPos check = center.add(dx, 0, dz);
                    if (!world.getBlockState(check).isOf(ModBlocks.INFERNAL_OBSIDIAN_BLOCK)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
