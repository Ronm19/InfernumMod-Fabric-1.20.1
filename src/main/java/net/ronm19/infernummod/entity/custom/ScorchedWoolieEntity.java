package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.ai.goals.socrchedwoolie.EatInfernalGrassGoal;
import net.ronm19.infernummod.item.ModItems;
import org.joml.Vector3f;

public class ScorchedWoolieEntity extends SheepEntity {
    private static final int MAX_GRASS_TIMER = 40;
    private int eatInfernalGrassTimer;
    private EatInfernalGrassGoal eatInfernalGrassGoal = null;


    public ScorchedWoolieEntity( EntityType<? extends SheepEntity> entityType, World world ) {
        super(entityType, world);
        this.isFireImmune(); // cannot burn
    }

    // -------------------- ATTRIBUTES --------------------
    public static DefaultAttributeContainer.Builder createScorchedWoolieAttributes() {
        return SheepEntity.createSheepAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.22D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 25.0D);
    }

    // --------------------- GOALs ----------------------

    @Override
    protected void initGoals() {
        // Initialize vanilla sheep goals first (important!)
        super.initGoals();

        // Replace or augment with your own goals
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25D));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.1D, Ingredient.ofItems(ModItems.ASH_DUST), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.add(5, new EatInfernalGrassGoal(this)); // your custom goal
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }


    // -------------------- BEHAVIOR --------------------
    @Override
    public void tick() {
        super.tick();

        // Spawn smoke & ember particles around body
        if (this.getWorld().isClient && this.age % 8 == 0) {
            double offsetX = (this.random.nextDouble() - 0.5) * this.getWidth();
            double offsetZ = (this.random.nextDouble() - 0.5) * this.getWidth();
            this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX() + offsetX, this.getY() + 0.8D, this.getZ() + offsetZ, 0.0, 0.02, 0.0);
            this.getWorld().addParticle(ParticleTypes.FLAME, this.getX() + offsetX, this.getY() + 0.6D, this.getZ() + offsetZ, 0.0, 0.01, 0.0);
        }

        // Small damage if standing in water
        if (this.isTouchingWater()) {
            this.damage(this.getDamageSources().drown(), 0.5F);
        }
    }

    // -------------------- INTERACTIONS --------------------
    public ActionResult interactMob( PlayerEntity player, Hand hand ) {

        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.SHEARS)) {
            if (!this.getWorld().isClient && this.isShearable()) {
                this.sheared(SoundCategory.PLAYERS);
                this.emitGameEvent(GameEvent.SHEAR, player);
                itemStack.damage(1, player, ( playerx ) -> playerx.sendToolBreakStatus(hand));
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.CONSUME;
            }
        } else {
            return super.interactMob(player, hand);
        }
    }

    public void sheared( SoundCategory shearedSoundCategory ) {
        this.getWorld().playSoundFromEntity((PlayerEntity) null, this, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
        this.setSheared(true);
        int i = 1 + this.random.nextInt(3);
    }

    // -------------------- SOUNDS --------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    protected void playStepSound( BlockPos pos, BlockState state ) {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    // -------------------- DROPS --------------------
    @Override
    protected void dropLoot( DamageSource source, boolean causedByPlayer ) {
        super.dropLoot(source, causedByPlayer);

        // Chance to drop Infernum Wool or Charred Wool
        if (!this.getWorld().isClient && this.random.nextFloat() < 0.3F) {
            this.dropStack(new ItemStack(ModBlocks.CHARRED_WOOL_BLOCK));
        }
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    // ------------------- OTHER ------------------------------

    public void handleStatus( byte status ) {
        if (status == 10) {
            this.eatInfernalGrassTimer = 40;
        } else {
            super.handleStatus(status);
        }

    }

    public float getNeckAngle( float delta ) {
        if (this.eatInfernalGrassTimer <= 0) {
            return 0.0F;
        } else if (this.eatInfernalGrassTimer >= 4 && this.eatInfernalGrassTimer <= 36) {
            return 1.0F;
        } else {
            return this.eatInfernalGrassTimer < 4 ? ((float) this.eatInfernalGrassTimer - delta) / 4.0F : -((float) (this.eatInfernalGrassTimer - 40) - delta) / 4.0F;
        }

    }

    public float getHeadAngle( float delta ) {
        if (this.eatInfernalGrassTimer > 4 && this.eatInfernalGrassTimer <= 36) {
            float f = ((float) (this.eatInfernalGrassTimer - 4) - delta) / 32.0F;
            return ((float) Math.PI / 5F) + 0.21991149F * MathHelper.sin(f * 28.7F);
        } else {
            return this.eatInfernalGrassTimer > 0 ? ((float) Math.PI / 5F) : this.getPitch() * ((float) Math.PI / 180F);
        }
    }

    public boolean isShearable() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    public void writeCustomDataToNbt( NbtCompound nbt ) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sheared", this.isSheared());

    }

    public void readCustomDataFromNbt( NbtCompound nbt ) {
        super.readCustomDataFromNbt(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));

    }

    public void onEatingInfernalGrass() {
        this.setSheared(false); // Wool regrows
        if (this.isBaby()) {
            this.growUp(60);
        }

        // 🔥 Visual feedback when eating Infernal Grass
        if (this.getWorld().isClient) {
            for (int i = 0; i < 8; i++) {
                double ox = (this.random.nextDouble() - 0.5) * 0.5;
                double oz = (this.random.nextDouble() - 0.5) * 0.5;
                this.getWorld().addParticle(ParticleTypes.LAVA, this.getX() + ox, this.getY() + 0.6D, this.getZ() + oz, 0.0, 0.02, 0.0);
            }
        }

        // 🔥 Sound feedback — infernal regrowth
        this.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 0.6F, 1.2F + this.random.nextFloat() * 0.3F);

    // 🔥 Infernal particles when eating grass
        if (this.getWorld().isClient) {
            for (int i = 0; i < 8; i++) {
                double ox = (this.random.nextDouble() - 0.5) * 0.5;
                double oz = (this.random.nextDouble() - 0.5) * 0.5;
                this.getWorld().addParticle(ParticleTypes.LAVA, this.getX() + ox, this.getY() + 0.6D, this.getZ() + oz, 0.0, 0.02, 0.0);
            }
        }


}
    protected float getActiveEyeHeight( EntityPose pose, EntityDimensions dimensions) {
        return 0.95F * dimensions.height;
    }

    protected Vector3f getPassengerAttachmentPos( Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, dimensions.height - 0.0625F * scaleFactor, 0.0F);
    }

    @Override
    public ScorchedWoolieEntity createChild( ServerWorld world, PassiveEntity entity ) {
        return ModEntities.SCORCHED_WOOLIE.create(world);
    }
}