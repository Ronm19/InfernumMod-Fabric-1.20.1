package net.ronm19.infernummod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.InfernalKnightEntity;
import net.ronm19.infernummod.entity.variant.InfernalKnightVariant;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.sound.ModSounds;

public class RoyalFireBlock extends Block {
    public static final BooleanProperty COOLDOWN = BooleanProperty.of("cooldown");
    private static final int COOLDOWN_TICKS = 20 * 20; // 20 seconds

    public RoyalFireBlock(Settings settings) {
        super(settings.nonOpaque().luminance(s -> 12).strength(3.5f, 1200f).requiresTool());
        this.setDefaultState(getStateManager().getDefaultState().with(COOLDOWN, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COOLDOWN);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ItemStack held = player.getStackInHand(hand);

        // Check if using the Echo of Domination
        if (held.isOf(ModItems.ECHO_OF_DAMNATION)) {
            if (state.get(COOLDOWN)) {
                player.sendMessage(Text.literal("§cThe Royal Fire is still recovering its power..."), true);
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7f, 1.2f);
                return ActionResult.CONSUME;
            }

            if (world instanceof ServerWorld sw) {
                for (int i = 0; i < 3; i++) {
                    double offsetX = (world.random.nextDouble() - 0.5) * 3.0;
                    double offsetZ = (world.random.nextDouble() - 0.5) * 3.0;
                    BlockPos spawnPos = pos.add((int) offsetX, 1, (int) offsetZ);

                    var golem = ModEntities.MAGMA_GOLEM.create(sw);
                    if (golem != null) {
                        golem.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                                world.random.nextFloat() * 360F, 0.0F);
                        sw.spawnEntity(golem);

                        // Particles & effects
                        sw.spawnParticles(ParticleTypes.LAVA,
                                spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5,
                                30, 0.5, 0.4, 0.5, 0.02);
                        sw.playSound(null, spawnPos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 1.3F, 0.8F);
                    }
                }

                // Big visual + sound effect
                sw.spawnParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 100, 0.6, 0.4, 0.6, 0.02);
                world.playSound(null, pos, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.4F, 0.9F);
                player.sendMessage(Text.literal("§c🔥 The Royal Fire unleashes three Magma Golems!"), true);
            }

            // Apply cooldown & damage
            world.setBlockState(pos, state.with(COOLDOWN, true), Block.NOTIFY_ALL);
            if (world instanceof ServerWorld sw) sw.scheduleBlockTick(pos, this, COOLDOWN_TICKS);
            held.damage(1, player, p -> p.sendToolBreakStatus(hand));
            return ActionResult.CONSUME;
        }

        // Infernal Royal Staff (existing logic)
        if (!held.isOf(ModItems.INFERNAL_ROYAL_STAFF)) return ActionResult.PASS;

        if (state.get(COOLDOWN)) {
            player.sendMessage(Text.literal("§cThe Royal Fire is still recovering its power..."), true);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7f, 1.2f);
            return ActionResult.CONSUME;
        }

        for (int i = 0; i < 2; i++) {
            InfernalKnightEntity knight = ModEntities.INFERNAL_KNIGHT.create(world);
            if (knight != null) {
                double offsetX = (i == 0 ? +1.4 : -1.4);
                boolean isElite = (i == 1);
                knight.setVariant(isElite ? InfernalKnightVariant.ELITE : InfernalKnightVariant.DEFAULT);
                knight.setupDefaultLoadout();
                knight.tameTo(player);
                knight.refreshPositionAndAngles(pos.getX() + 0.5 + offsetX, pos.getY() + 1.0, pos.getZ() + 0.5, 0f, 0f);
                world.spawnEntity(knight);

                if (isElite && world instanceof ServerWorld sw) {
                    sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, knight.getX(), knight.getY() + 1.0, knight.getZ(), 40, 0.5, 0.5, 0.5, 0.02);
                    sw.spawnParticles(ParticleTypes.ENCHANTED_HIT, knight.getX(), knight.getY() + 1.0, knight.getZ(), 15, 0.3, 0.3, 0.3, 0.01);
                }
            }
        }

        if (world instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 80, 0.6, 0.4, 0.6, 0.02);
            sw.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 25, 0.4, 0.3, 0.4, 0.02);
        }

        world.playSound(null, pos, ModSounds.INFERNAL_KNIGHT_SUMMON, SoundCategory.BLOCKS, 1.3f, 1.0f);
        world.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.RECORDS, 0.8f, 1.0f);
        player.sendMessage(Text.literal("§6🔥 Two Infernal Knights rise from the Royal Flame!"), true);

        world.setBlockState(pos, state.with(COOLDOWN, true), Block.NOTIFY_ALL);
        if (world instanceof ServerWorld sw) sw.scheduleBlockTick(pos, this, COOLDOWN_TICKS);
        held.damage(1, player, p -> p.sendToolBreakStatus(hand));

        return ActionResult.CONSUME;
    }


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (state.get(COOLDOWN)) {
            world.setBlockState(pos, state.with(COOLDOWN, false), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.7f, 1.4f);
        }
    }
}
