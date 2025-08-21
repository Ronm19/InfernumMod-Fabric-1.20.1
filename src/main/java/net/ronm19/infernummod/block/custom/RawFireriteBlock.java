package net.ronm19.infernummod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class RawFireriteBlock extends Block {
    private static final int SCHEDULED_TICK_DELAY = 20;

    public RawFireriteBlock( Settings settings ) {
        super(FabricBlockSettings.copyOf(Blocks.MAGMA_BLOCK)
                .sounds(BlockSoundGroup.GRAVEL)
                .luminance(10)
                .strength(3.0f, 6.0f)
        );
    }

    public void onSteppedOn( World world, BlockPos pos, BlockState state, Entity entity ) {
        if (!entity.bypassesSteppingEffects() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            entity.damage(world.getDamageSources().hotFloor(), 1.0F);
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    public void scheduledTick( BlockState state, ServerWorld world, BlockPos pos, Random random ) {
        BubbleColumnBlock.update(world, pos.up(), state);
    }

    public BlockState getStateForNeighborUpdate( BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos ) {
        if (direction == Direction.UP && neighborState.isOf(Blocks.WATER)) {
            world.scheduleBlockTick(pos, this, 20);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public void onBlockAdded( BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify ) {
        world.scheduleBlockTick(pos, this, 20);
    }

    @Override
    public void onEntityCollision( BlockState state, World world, BlockPos pos, Entity entity ) {
        if (entity instanceof LivingEntity living) {
            // Exclude certain mobs if needed (optional)
            if (entity.getType() == EntityType.BLAZE || entity.getType() == EntityType.STRIDER) {
                return;
            }

            // Skip if the entity has Fire Resistance
            if (living.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                return;
            }

            // Damage/make entity catch fire
            if (!world.isClient) {
                living.setOnFireFor(4); // 4 seconds fire
            }
        }
    }
}