package net.ronm19.infernummod.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

/**
 * Gives the player perfect vision inside lava — removing the orange fog
 * and letting them see as if underwater, with light-based clarity.
 */
public class LavaVisionEffect extends StatusEffect {
    public LavaVisionEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // updates every tick
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        World world = entity.getWorld();

        // Server logic — prevents damage
        if (!world.isClient) {
            if (entity.isInLava()) {
                entity.extinguish(); // removes fire
                entity.setAir(entity.getMaxAir()); // resets air bar
            }
        }
        // Client-side rendering logic
        else if (entity instanceof PlayerEntity player) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.gameRenderer != null && player.isInLava()) {
                // Give a clean lava vision effect
                GameRenderer renderer = client.gameRenderer;

                // Increase gamma and remove fog while in lava
                client.options.getGamma().setValue(10.0); // full brightness
                renderer.getLightmapTextureManager().update(1.0f);
            } else {
                // Reset gamma when not in lava
                MinecraftClient.getInstance().options.getGamma().setValue(1.0);
            }

        } else {
            // Client-side visual touch
            if (entity.isInLava() && world.random.nextFloat() < 0.05F) {
                world.addParticle(ParticleTypes.FLAME,
                        entity.getX() + (world.random.nextDouble() - 0.5D) * entity.getWidth(),
                        entity.getY() + world.random.nextDouble() * entity.getHeight(),
                        entity.getZ() + (world.random.nextDouble() - 0.5D) * entity.getWidth(),
                        0.0D, 0.01D, 0.0D);
            }
        }
    }

    public void onRemoved(LivingEntity entity, net.minecraft.entity.attribute.AttributeContainer attributes, int amplifier) {
        // Reset brightness once effect ends
        if (entity.getWorld().isClient) {
            MinecraftClient.getInstance().options.getGamma().setValue(1.0);
        }
        super.onRemoved(attributes);
    }
}