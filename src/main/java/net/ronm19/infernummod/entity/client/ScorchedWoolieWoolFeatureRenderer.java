package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.ScorchedWoolieEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class ScorchedWoolieWoolFeatureRenderer extends FeatureRenderer<ScorchedWoolieEntity, SheepEntityModel<ScorchedWoolieEntity>> {
    private static final Identifier FUR_TEXTURE = new Identifier("infernummod", "textures/entity/scorchedwoolie_fur.png");
    private final SheepEntityModel<ScorchedWoolieEntity> woolModel;

    public ScorchedWoolieWoolFeatureRenderer(
            FeatureRendererContext<ScorchedWoolieEntity, SheepEntityModel<ScorchedWoolieEntity>> context,
            EntityModelLoader loader) {
        super(context);
        this.woolModel = new SheepEntityModel<>(loader.getModelPart(EntityModelLayers.SHEEP_FUR));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light,
                       ScorchedWoolieEntity woolie, float limbAngle, float limbDistance,
                       float tickDelta, float animationProgress, float headYaw, float headPitch) {

        if (!woolie.isSheared() && !woolie.isInvisible()) {
            // Copy the current model state to the wool layer
            this.getContextModel().copyStateTo(this.woolModel);
            this.woolModel.animateModel(woolie, limbAngle, limbDistance, tickDelta);
            this.woolModel.setAngles(woolie, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(FUR_TEXTURE));
            this.woolModel.render(matrixStack, vertexConsumer, light,
                    LivingEntityRenderer.getOverlay(woolie, 0.0F),
                    1.0F, 1.0F, 1.0F, 1.0F);
        } else if (woolie.isInvisible()) {
            // Outline render for glowing effect (optional)
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.hasOutline(woolie)) {
                this.getContextModel().copyStateTo(this.woolModel);
                VertexConsumer outline = vertexConsumers.getBuffer(RenderLayer.getOutline(FUR_TEXTURE));
                this.woolModel.render(matrixStack, outline, light,
                        LivingEntityRenderer.getOverlay(woolie, 0.0F),
                        0.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }
}
