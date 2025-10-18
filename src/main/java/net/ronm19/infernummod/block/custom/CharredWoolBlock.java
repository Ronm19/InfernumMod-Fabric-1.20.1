package net.ronm19.infernummod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.FireProofBlock;
import net.ronm19.infernummod.api.interfaces.TickedBlock;

public class CharredWoolBlock extends Block implements FireProofBlock, TickedBlock {
    public static final BooleanProperty SMOLDERING = BooleanProperty.of("smoldering");

    public CharredWoolBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SMOLDERING, false));
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder) {
        builder.add(SMOLDERING);
    }

    @Override
    public void randomDisplayTick( BlockState state, World world, BlockPos pos, java.util.Random random ) {
        super.randomDisplayTick(state, world, pos, (Random) random);
        if (state.get(SMOLDERING) && random.nextFloat() < 0.2F) {
            world.playSound(pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
                    net.minecraft.sound.SoundCategory.BLOCKS,
                    0.3F, 1.2F, false);
        }
    }


    @Override
    public boolean isFireproof() {
        return true;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, net.minecraft.entity.LivingEntity placer, net.minecraft.item.ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient) return;

        // Automatically smolders in Infernum dimension or near fire
        if (world.getDimension().effects().toString().contains("infernum") || world.isSkyVisible(pos)) {
            world.setBlockState(pos, state.with(SMOLDERING, true), 3);
        }
    }
}
