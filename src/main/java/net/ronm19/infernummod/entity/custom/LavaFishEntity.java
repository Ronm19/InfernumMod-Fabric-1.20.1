package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.RandomLookAroundTask;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowGroupLeaderGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.goal.TemptGoal;

import net.minecraft.entity.passive.FishEntity;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.util.ModTags;

public class LavaFishEntity extends SchoolingFishEntity {

    public LavaFishEntity( EntityType<? extends LavaFishEntity> type, World world ) {
        super(type, world);
        // optional: make lava free for pathfinding
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
    }

    // ---------- ATTRIBUTES ----------
    public static DefaultAttributeContainer.Builder createLavaFishAttributes() {
        return FishEntity.createFishAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0)       // 3❤
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.2)   // base swim speed
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0)
                .add(EntityAttributes.GENERIC_LUCK, 0.1);
    }

    // ---------- GOALS ----------
    @Override
    protected void initGoals() {
        // wander gently around lava lakes
        this.goalSelector.add(0, new SwimAroundGoal(this, 1.0, 50));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 4.0F, 1.2, 1.5));
        this.goalSelector.add(3, new FollowGroupLeaderGoal(this));
        this.goalSelector.add(4, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.MAGMA_CREAM), false));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    // ---------- BEHAVIOUR ----------
    @Override
    public int getMaxGroupSize() {
        return 6; // fits 4–7 fish shoals
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
    }

    @Override
    public boolean canBreatheInWater() {
        return true; // can survive in water too
    }

    @Override
    public boolean isTouchingWater() {
        return super.isTouchingWater() || this.isInLava();
    }


    @Override
    public boolean isFireImmune() {
        return true; // survives lava & fire
    }

    // slower movement in water than in lava
    @Override
    public void tick() {
        super.tick();
        if (this.isTouchingWater() && !this.isInLava()) {
            this.setVelocity(this.getVelocity().multiply(0.5));
        }
    }

    @Override
    public void onDeath( DamageSource source) {
        super.onDeath(source);

        if (!this.getWorld().isClient) {
            this.dropStack(new ItemStack(ModItems.LAVA_FISH, 1));
        }
    }

    // bucket interaction (replace later with a custom bucket item)
    @Override
    public ItemStack getBucketItem() {
        return Items.BUCKET.getDefaultStack();
    }
}