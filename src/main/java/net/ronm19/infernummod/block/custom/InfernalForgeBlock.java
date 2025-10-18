package net.ronm19.infernummod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
public class InfernalForgeBlock extends Block {

    public InfernalForgeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextFloat() < 0.3F) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5);
            double y = pos.getY() + 1.1;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5);

            // Ember + smoke particles
            world.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0, 0.02, 0.0);
            world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.01, 0.0);

            // Occasional lava drip
            if (random.nextInt(30) == 0) {
                world.addParticle(ParticleTypes.DRIPPING_LAVA, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            world.playSound(null, pos, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE,
                    SoundCategory.BLOCKS, 0.7F, 1.0F + (world.random.nextFloat() - 0.5F) * 0.2F);
            player.sendMessage(Text.literal("§6The forge radiates infernal heat..."), true);
        }
        return ActionResult.SUCCESS;
    }
}
