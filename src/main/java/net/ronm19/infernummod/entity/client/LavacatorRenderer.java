package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.LavacatorEntity;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.layer.ModModelLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.LavacatorEntity;

public class LavacatorRenderer extends IllagerEntityRenderer<LavacatorEntity> {

    public LavacatorRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.VINDICATOR)), 0.5F);

        // ✅ Correct constructor: only (this, heldItemRenderer)
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()) {
            @Override
            public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                               LavacatorEntity entity, float limbAngle, float limbDistance,
                               float tickDelta, float animationProgress, float headYaw, float headPitch) {

                // Only render the held item if attacking
                if (entity.isAttacking()) {
                    super.render(matrices, vertexConsumers, light, entity,
                            limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
                }
            }
        });
    }

    @Override
    public Identifier getTexture(LavacatorEntity entity) {
        return new Identifier("infernummod", "textures/entity/lavacator.png");
    }

    @Override
    public void render(LavacatorEntity mobEntity, float f, float g,
                       MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
