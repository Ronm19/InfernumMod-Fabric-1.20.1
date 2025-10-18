package net.ronm19.infernummod.entity.ai.goals.lava_witch;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.custom.LavaWitchEntity;

import java.util.EnumSet;

public class ThrowLavaPotionGoal extends Goal {
    private final LavaWitchEntity witch;
    private int cooldown;

    public ThrowLavaPotionGoal(LavaWitchEntity witch) {
        this.witch = witch;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.witch.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = this.witch.getTarget();
        if (target == null) return;

        World world = witch.getWorld();
        this.witch.getLookControl().lookAt(target, 30.0F, 30.0F);

        if (--cooldown <= 0 && this.witch.canSee(target)) {
            cooldown = 60 + witch.getRandom().nextInt(40); // 3–5 sec cooldown
            this.throwLavaPotion(target);
        }
    }

    private void throwLavaPotion(LivingEntity target) {
        ItemStack potion = new ItemStack(Items.SPLASH_POTION);
        PotionEntity potionEntity = new PotionEntity(this.witch.getWorld(), this.witch);
        potionEntity.setItem(potion);

        // Aim
        Vec3d vec = target.getEyePos().subtract(this.witch.getPos());
        potionEntity.setVelocity(vec.x, vec.y + 0.15D, vec.z, 0.75F, 8.0F);

        // Random fiery effects
        int choice = this.witch.getRandom().nextInt(3);
        switch (choice) {
            case 0 -> {
                // Flame Brew: ignite
                potionEntity.getWorld().createExplosion(this.witch, potionEntity.getX(),
                        potionEntity.getY(), potionEntity.getZ(), 0.5F, World.ExplosionSourceType.MOB);
            }
            case 1 -> {
                // Magma Splash: spawn fire block
                potionEntity.getWorld().setBlockState(
                        target.getBlockPos(),
                        net.minecraft.block.Blocks.FIRE.getDefaultState()
                );
            }
            case 2 -> {
                // Ash Cloud: weaken and blind
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
            }
        }

        witch.playSound(SoundEvents.ENTITY_WITCH_THROW, 1.0F, 0.8F + witch.getRandom().nextFloat() * 0.4F);
        witch.getWorld().spawnEntity(potionEntity);
    }
}
