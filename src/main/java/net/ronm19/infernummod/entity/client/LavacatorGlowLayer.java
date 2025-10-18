package net.ronm19.infernummod.entity.client;


import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.LavacatorEntity;

public class LavacatorGlowLayer extends FeatureRenderer<LavacatorEntity, IllagerEntityModel<LavacatorEntity>> {
    private static final Identifier GLOW_TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/lavacator_glow.png");

    public LavacatorGlowLayer(FeatureRendererContext<LavacatorEntity, IllagerEntityModel<LavacatorEntity>> context) {
        super(context);
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            LavacatorEntity entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        // Only glow when the Lavacator is enraged
        if (!entity.isEnraged()) return;

        // Use the "eyes" render layer for emissive glowing parts
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(GLOW_TEXTURE));

        // Optional: pulsating glow intensity
        float pulse = 0.8F + 0.2F * (float)Math.sin((entity.age + tickDelta) * 0.25F);

        // Reddish glow tone with subtle pulsing
        this.getContextModel().render(
                matrices,
                vertexConsumer,
                0xF000F0, // full brightness for glow
                OverlayTexture.DEFAULT_UV,
                1.0F, // Red
                0.25F * pulse, // Green (dim)
                0.05F * pulse, // Blue (dim)
                1.0F  // Alpha
        );
    }
}
