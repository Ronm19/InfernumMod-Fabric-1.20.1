package net.ronm19.infernummod.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ronm19.infernummod.effect.ModEffects;

/**
 * 🔥 Infernum Lava Vision Fog Renderer 🔥
 * Adds a faint red/orange glow around lava when Lava Vision is active.
 */
@Environment(EnvType.CLIENT)
public class LavaFogRenderer {

    public static void applyLavaGlow(Camera camera, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || !player.hasStatusEffect(ModEffects.LAVA_VISION)) return;

        World world = player.getWorld();
        Vec3d playerPos = player.getPos();
        BlockPos.Mutable pos = new BlockPos.Mutable();

        boolean nearLava = false;
        int radius = 4; // glow radius
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    pos.set(playerPos.x + x, playerPos.y + y, playerPos.z + z);
                    if (world.getBlockState(pos).getFluidState().isIn(net.minecraft.registry.tag.FluidTags.LAVA)) {
                        nearLava = true;
                        break;
                    }
                }
            }
        }

        if (nearLava) {
            float time = (world.getTime() + tickDelta) * 0.02f;
            float intensity = 0.6f + (float)Math.sin(time) * 0.15f; // breathing pulse

            float r = 1.0f;
            float g = 0.4f + (float)Math.sin(time * 1.5) * 0.05f;
            float b = 0.1f;
            float a = 0.25f + intensity * 0.15f;

            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(18.0F + (float)Math.sin(time) * 2.0f);
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
            RenderSystem.setShaderFogColor(r * intensity, g * intensity, b * intensity, a);
        }
    }
}
