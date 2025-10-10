package net.ronm19.infernummod.client.render;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import net.ronm19.infernummod.effect.ModEffects;

/**
 * Lava Vision visual handler:
 *  - Makes lava semi-transparent near player
 *  - Adds angle-based transparency
 *  - Adds subtle shimmer / heat-wave effect
 */
public class LavaVisionFluidHandler implements FluidRenderHandler {
    private final FluidRenderHandler vanillaLava = FluidRenderHandlerRegistry.INSTANCE.get(Fluids.LAVA);

    @Override
    public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        return vanillaLava.getFluidSprites(view, pos, state);
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        if (view == null || pos == null) return 0xFFFF5500;

        if (view instanceof net.minecraft.world.World world) {
            var player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
            if (player != null && player.hasStatusEffect(ModEffects.LAVA_VISION)) {

                // Distance fade (closer = more transparent)
                double dist = player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ());
                double fadeDist = Math.min(Math.sqrt(dist) / 6.0, 1.0);

                // Angle fade (looking down = clearer)
                Vec3d look = player.getRotationVecClient().normalize();
                double downness = Math.max(0.0, look.dotProduct(new Vec3d(0, -1, 0)));
                double angleFade = 1.0 - downness;

                double combined = Math.min(1.0, (fadeDist * 0.6 + angleFade * 0.4));
                int alpha = (int) (0x55 + (0xAA * combined));
                if (alpha > 0xFF) alpha = 0xFF;

                // --- 🔥 SHIMMER EFFECT ---
                // slight oscillation based on time and position
                long time = world.getTime();
                double wave = Math.sin((pos.getX() * 0.35 + pos.getZ() * 0.4 + time * 0.15)) * 0.1;
                double brightness = 1.0 + wave; // between ~0.9 and 1.1 brightness

                // Clamp brightness safely
                if (brightness < 0.85) brightness = 0.85;
                if (brightness > 1.15) brightness = 1.15;

                // Slightly dynamic orange hue (heat glow)
                int r = (int) (0xEE * brightness);
                int g = (int) (0x44 * brightness);
                int b = (int) (0x11 * brightness);

                int color = (r << 16) | (g << 8) | b;
                return (alpha << 24) | color;
            }
        }



        return 0xFFFF5500; // default lava
    }
}
