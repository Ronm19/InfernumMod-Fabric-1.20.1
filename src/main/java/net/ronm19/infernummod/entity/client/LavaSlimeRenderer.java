package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.LavaSlimeEntity;

public class LavaSlimeRenderer extends MobEntityRenderer<LavaSlimeEntity, SlimeEntityModel<LavaSlimeEntity>> {
    private static final Identifier INNER_TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/lava_slime/lava_slime.png");
    private static final Identifier OUTER_TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/lava_slime/lava_slime_outer.png");

    public LavaSlimeRenderer( EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 0.25F);
        this.addFeature(new SlimeOverlayFeatureRenderer<>(this, context.getModelLoader()));
    }

    @Override
    public Identifier getTexture(LavaSlimeEntity entity) {
        return INNER_TEXTURE;
    }

    public static class SlimeOverlayFeatureRenderer<T extends LavaSlimeEntity> extends FeatureRenderer<T, SlimeEntityModel<T>> {
        private final SlimeEntityModel<T> overlayModel;

        public SlimeOverlayFeatureRenderer( FeatureRendererContext<T, SlimeEntityModel<T>> context, EntityModelLoader loader) {
            super(context);
            this.overlayModel = new SlimeEntityModel<>(loader.getModelPart(EntityModelLayers.SLIME_OUTER));
        }

        @Override
        public void render( MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
           Identifier texture = OUTER_TEXTURE;
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
            this.getContextModel().copyStateTo(this.overlayModel);
            this.overlayModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        protected void scale( LavaSlimeEntity lavaSlimeEntity, MatrixStack matrixStack, float f) {
            float g = 0.999F;
            matrixStack.scale(0.999F, 0.999F, 0.999F);
            matrixStack.translate(0.0F, 0.001F, 0.0F);
            float h = (float)lavaSlimeEntity.getSize();
            float i = MathHelper.lerp(f, lavaSlimeEntity.lastStretch, lavaSlimeEntity.stretch) / (h * 0.5F + 1.0F);
            float j = 1.0F / (i + 1.0F);
            matrixStack.scale(j * h, 1.0F / j * h, j * h);
        }

    }
}