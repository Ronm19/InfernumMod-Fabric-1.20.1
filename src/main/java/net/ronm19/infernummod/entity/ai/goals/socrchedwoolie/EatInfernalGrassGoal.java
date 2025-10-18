package net.ronm19.infernummod.entity.ai.goals.socrchedwoolie;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.custom.ScorchedWoolieEntity;

import java.util.EnumSet;
import java.util.function.Predicate;

public class EatInfernalGrassGoal extends Goal {
    private static final int MAX_INFERNAL_GRASS_TIMER = 40;
    private static final Predicate<BlockState> INFERNAL_GRASS_PREDICATE;
    private final MobEntity mob;
    private final World world;
    private int timer;

    public EatInfernalGrassGoal(MobEntity mob) {
        this.mob = mob;
        this.world = mob.getWorld();
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    public boolean canStart() {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPos blockPos = this.mob.getBlockPos();
            if (INFERNAL_GRASS_PREDICATE.test(this.world.getBlockState(blockPos))) {
                return true;
            } else {
                return this.world.getBlockState(blockPos.down()).isOf(Blocks.GRASS_BLOCK);
            }
        }
    }

    public void start() {
        this.timer = this.getTickCount(40);
        this.world.sendEntityStatus(this.mob, (byte)10);
        this.mob.getNavigation().stop();
    }

    public void stop() {
        this.timer = 0;
    }

    public boolean shouldContinue() {
        return this.timer > 0;
    }

    public int getTimer() {
        return this.timer;
    }

    public void tick() {
        this.timer = Math.max(0, this.timer - 1);
        if (this.timer == this.getTickCount(4)) {
            BlockPos pos = this.mob.getBlockPos();

            boolean ateInfernalGrass = false;

            if (INFERNAL_GRASS_PREDICATE.test(this.world.getBlockState(pos))) {
                if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    this.world.breakBlock(pos, false);
                }
                ateInfernalGrass = true;
            } else {
                BlockPos below = pos.down();
                if (this.world.getBlockState(below).isOf(ModBlocks.INFERNAL_GRASS_BLOCK)) {
                    if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                        this.world.syncWorldEvent(2001, below, Block.getRawIdFromState(ModBlocks.INFERNAL_GRASS_BLOCK.getDefaultState()));
                        this.world.setBlockState(below, ModBlocks.INFERNAL_DIRT_BLOCK.getDefaultState(), 2);
                    }
                    ateInfernalGrass = true;
                }
            }

            // ✅ Notify the Scorched Woolie that it ate infernal grass
            if (ateInfernalGrass && this.mob instanceof ScorchedWoolieEntity woolie) {
                woolie.onEatingInfernalGrass();
            }
        }
    }

    static {
        INFERNAL_GRASS_PREDICATE = BlockStatePredicate.forBlock(ModBlocks.INFERNAL_GRASS_BLOCK);
    }
}
