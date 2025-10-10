package net.ronm19.infernummod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ronm19.infernummod.effect.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Lava Vision - allows partial transparency when near lava, full when submerged.
 */

/**
 * 🔥 LavaFogRendererMixin
 * Makes lava semi-transparent when near it, and fully clear when submerged
 * if the player has the Lava Vision status effect.
 */
@Mixin(BackgroundRenderer.class)
public class LavaFogRendererMixin {

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void infernum$applyLavaVisionFog(
            Camera camera, FogType fogType, float viewDistance,
            boolean thickFog, float tickDelta, CallbackInfo ci) {

        if (!(camera.getFocusedEntity() instanceof PlayerEntity player)) return;
        World world = player.getWorld();

        // Only apply for players with Lava Vision
        if (!player.hasStatusEffect(ModEffects.LAVA_VISION)) return;

        // --- 1️⃣ Fully submerged in lava → total clarity ---
        if (camera.getSubmersionType() == CameraSubmersionType.LAVA) {
            RenderSystem.setShaderFogStart(1000.0F);
            RenderSystem.setShaderFogEnd(10000.0F);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            RenderSystem.setShaderFogColor(1.0F, 0.65F, 0.25F, 0.08F); // very faint warm tint
            ci.cancel();
            return;
        }


// --- 2️⃣ Near lava surface → partial transparency ---
        BlockPos camPos = camera.getBlockPos();
        boolean nearLava = false;
        int range = 6; // detection radius

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int dx = -range; dx <= range && !nearLava; dx++) {
            for (int dy = -range; dy <= range && !nearLava; dy++) {
                for (int dz = -range; dz <= range && !nearLava; dz++) {
                    mutable.set(camPos.getX() + dx, camPos.getY() + dy, camPos.getZ() + dz);
                    if (world.getBlockState(mutable).isOf(net.minecraft.block.Blocks.LAVA)) {
                        nearLava = true;
                        break;
                    }
                }
            }
        }

        // --- 3️⃣ Apply softer fog when near lava ---
        if (nearLava) {
            RenderSystem.setShaderFogStart(1.0F);
            RenderSystem.setShaderFogEnd(20.0F);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            RenderSystem.setShaderFogColor(1.0F, 0.45F, 0.1F, 0.25F); // gentle orange haze
            // Note: we do NOT cancel, so environmental fog still layers naturally
        }
    }
}

