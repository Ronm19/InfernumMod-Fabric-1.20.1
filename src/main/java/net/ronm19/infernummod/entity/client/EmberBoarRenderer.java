package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.EmberBoarEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class EmberBoarRenderer extends MobEntityRenderer<EmberBoarEntity, EmberBoarModel<EmberBoarEntity>> {
    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/ember_boar.png");

    public EmberBoarRenderer( EntityRendererFactory.Context context) {
        super(context, new EmberBoarModel<>(context.getPart(ModModelLayers.EMBER_BOAR)), 0.7F);
    }

    @Override
    public Identifier getTexture(EmberBoarEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( EmberBoarEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {

        if (mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
