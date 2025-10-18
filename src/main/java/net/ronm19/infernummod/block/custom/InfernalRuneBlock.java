package net.ronm19.infernummod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.InfernalVokerEntity;
import net.ronm19.infernummod.item.ModItems;

import java.util.List;

public class InfernalRuneBlock extends net.minecraft.block.Block {

    public InfernalRuneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world,
                              BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {

        if (world.isClient) return ActionResult.SUCCESS;

        ItemStack held = player.getStackInHand(hand);

        // Determine which ritual to run based on held item
        if (held.isOf(ModItems.ECHO_OF_DAMNATION)) {
            return trySummonInfernalVoker(world, pos, player, hand);
        } else if (held.isOf(ModItems.INFERNUM_HEROBRINE_RELIC)) { // Your Herobrine catalyst item
            return trySummonHerobrine(world, pos, player, hand);
        }

        return ActionResult.PASS;
    }

    // ============================================================
    // 🔥 INFERNAL VOKER SUMMONING RITUAL
    // ============================================================

    private ActionResult trySummonInfernalVoker(World world, BlockPos pos,
                                                PlayerEntity player, Hand hand) {

        // 1) Check Raw Firerite circle pattern
        if (!isValidFireriteCircle(world, pos)) {
            player.sendMessage(Text.literal("The fiery circle is incomplete!"), true);
            return ActionResult.FAIL;
        }

        // 2) Check for Infernal Beast Horn nearby
        List<ItemEntity> horns = world.getEntitiesByClass(ItemEntity.class,
                new Box(pos).expand(3.0),
                e -> e.getStack().isOf(ModItems.INFERNAL_BEAST_HORN));

        if (horns.isEmpty()) {
            player.sendMessage(Text.literal("The Infernal Beast Horn is missing!"), true);
            return ActionResult.FAIL;
        }

        // 3) Prevent multiple Vokers near
        boolean exists = !world.getEntitiesByClass(InfernalVokerEntity.class,
                new Box(pos).expand(40.0),
                entity -> true // this just means "include all InfernalVokers"
        ).isEmpty();

        if (exists) {
            player.sendMessage(Text.literal("The Infernal Voker already roams nearby..."), true);
            return ActionResult.FAIL;
        }

        // Consume catalyst and horn
        if (!player.getAbilities().creativeMode) player.getStackInHand(hand).decrement(1);
        horns.get(0).discard();

        performInfernalVokerEffects((ServerWorld) world, pos);

        // Spawn Voker
        InfernalVokerEntity voker = ModEntities.INFERNAL_VOKER.create(world);
        if (voker != null) {
            voker.refreshPositionAndAngles(
                    pos.getX() + 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5,
                    world.random.nextFloat() * 360F,
                    0.0F
            );
            world.spawnEntity(voker);
        }

        player.sendMessage(Text.literal("🔥 The Infernal Voker rises! 🔥"), true);
        return ActionResult.SUCCESS;
    }

    private void performInfernalVokerEffects(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 5f, 0.8f);
        world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 2f, 1f);
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                2, 0.0, 0.0, 0.0, 0.0);
        world.spawnParticles(ParticleTypes.LAVA,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                150, 1.5, 0.5, 1.5, 0.01);
        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                100, 1.0, 0.5, 1.0, 0.02);
        world.spawnParticles(ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                50, 0.5, 0.3, 0.5, 0.01);
        world.spawnParticles(ParticleTypes.EXPLOSION,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                10, 0.0, 0.0, 0.0, 0.1);
    }

    private boolean isValidFireriteCircle(World world, BlockPos center) {
        int[][] offsets = {
                {2, 0}, {-2, 0}, {0, 2}, {0, -2},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] off : offsets) {
            BlockPos check = center.add(off[0], 0, off[1]);
            if (!world.getBlockState(check).isOf(ModBlocks.RAW_FIRERITE_BLOCK)) {
                return false;
            }
        }
        return true;
    }

    // ============================================================
    // 👁️‍🗨️ HEROBRINE SUMMONING RITUAL
    // ============================================================

    private ActionResult trySummonHerobrine(World world, BlockPos pos,
                                            PlayerEntity player, Hand hand) {

        // 1) Check the Infernal Grass 3×3 pattern
        if (!checkInfernalGrassPattern(world, pos)) {
            player.sendMessage(Text.literal("The ritual circle is incomplete!"), true);
            return ActionResult.FAIL;
        }

        // 2) Check for 4 Infernum Bones dropped on the Rune
        List<ItemEntity> bones = world.getEntitiesByClass(ItemEntity.class,
                new Box(pos).expand(1.0),
                e -> e.getStack().isOf(ModItems.INFERNUM_BONE));

        if (bones.size() < 4) {
            player.sendMessage(Text.literal("The offering is insufficient!"), true);
            return ActionResult.FAIL;
        }

        // Consume 4 bones
        int bonesToConsume = 4;
        for (ItemEntity item : bones) {
            ItemStack stack = item.getStack();
            int take = Math.min(stack.getCount(), bonesToConsume);
            stack.decrement(take);
            bonesToConsume -= take;
            if (bonesToConsume <= 0) break;
        }

        // Consume catalyst
        if (!player.getAbilities().creativeMode)
            player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(hand));

        // Ritual FX
        performHerobrineEffects((ServerWorld) world, pos);

        // Spawn Herobrine
        var herobrine = ModEntities.INFERNUM_HEROBRINE.create(world);
        if (herobrine != null) {
            herobrine.refreshPositionAndAngles(pos.getX() + 0.5,
                    pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
            world.spawnEntity(herobrine);
        }

        player.sendMessage(Text.literal("⚡ Herobrine has been summoned... ⚡"), true);
        return ActionResult.SUCCESS;
    }

    private void performHerobrineEffects(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.WEATHER, 4f, 0.8f);
        world.spawnParticles(ParticleTypes.FLAME,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                150, 1.5, 0.5, 1.5, 0.01);
        world.spawnParticles(ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                100, 1.0, 0.5, 1.0, 0.02);
    }

    private boolean checkInfernalGrassPattern(World world, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos check = center.add(dx, 0, dz);
                if (!world.getBlockState(check).isOf(ModBlocks.INFERNAL_GRASS_BLOCK)) {
                    return false;
                }
            }
        }
        return true;
    }
}
