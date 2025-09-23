package net.ronm19.infernummod.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class BlazingHeartEffect extends StatusEffect {
    protected BlazingHeartEffect( StatusEffectCategory category, int color ) {
            super(category, color);
    }

        @Override
        public void applyUpdateEffect( LivingEntity entity, int amplifier) {
            World world = entity.getWorld();


            if (!world.isClient) {
                // Fire resistance effect
                entity.setFireTicks(0);

                // Set mobs around player on fire
                world.getEntitiesByClass(LivingEntity.class,
                        entity.getBoundingBox().expand(3.0D), // radius
                        target -> target != entity && target.isAlive()
                ).forEach(target -> target.setOnFireFor(2 + amplifier));

                // Hunger drain for players
                if (entity instanceof PlayerEntity player) {
                    if (world.getTime() % 40 == 0) { // every 2 seconds
                        player.addExhaustion(0.5f + amplifier * 0.2f);
                    }
                }
            } else {
                // Client-side particles
                for (int i = 0; i < 3; i++) {
                    double offsetX = (world.random.nextDouble() - 0.5) * 0.5;
                    double offsetY = world.random.nextDouble() * 1.5;
                    double offsetZ = (world.random.nextDouble() - 0.5) * 0.5;
                    world.addParticle(ParticleTypes.FLAME,
                            entity.getX() + offsetX,
                            entity.getY() + offsetY,
                            entity.getZ() + offsetZ,
                            0, 0.02, 0);
                }
            }
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return true; // makes it tick every tick
        }
    }

