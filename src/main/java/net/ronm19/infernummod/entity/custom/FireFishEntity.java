package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.util.ModTags;

public class FireFishEntity extends SchoolingFishEntity {

    public FireFishEntity(EntityType<? extends FireFishEntity> type, World world) {
        super(type, world);
        // allow lava swimming for pathfinding
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
    }

    // ---------- ATTRIBUTES ----------
    public static DefaultAttributeContainer.Builder createFireFishAttributes() {
        return FishEntity.createFishAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5.0)        // 2.5❤ – fragile
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.3)    // faster
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0)
                .add(EntityAttributes.GENERIC_LUCK, 0.1);
    }

    // ---------- GOALS ----------
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimAroundGoal(this, 1.2, 50));          // slightly faster wander
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.5));            // panic if attacked
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 4.0F, 1.3, 1.6));
        this.goalSelector.add(3, new TemptGoal(this, 1.15, Ingredient.ofItems(Items.BLAZE_POWDER), false));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    // ---------- BEHAVIOUR ----------
    @Override
    public int getMaxGroupSize() {
        return 2;   // prefers to be solitary or in a pair
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;    // can still survive in water
    }

    @Override
    public boolean isFireImmune() {
        return true;    // survives lava & fire
    }

    // treat lava as water for swim AI
    @Override
    public boolean isTouchingWater() {
        return super.isTouchingWater() || this.isInLava();
    }

    // moves slower in water vs lava
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
            this.dropStack(new ItemStack(ModItems.FIRE_FISH, 1));
        }
    }

    @Override
    public ItemStack getBucketItem() {
        return Items.BUCKET.getDefaultStack();   // replace later with custom fire_fish_bucket
    }
}
