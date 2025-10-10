package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.EmberSerpentEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class EmberSerpentRenderer extends MobEntityRenderer<EmberSerpentEntity, EmberSerpentModel<EmberSerpentEntity>> {
    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/ember_serpent.png");

    public EmberSerpentRenderer( EntityRendererFactory.Context context) {
        super(context, new EmberSerpentModel<>(context.getPart(ModModelLayers.EMBER_SERPENT)), 0.7F);
    }

    @Override
    public Identifier getTexture( EmberSerpentEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( EmberSerpentEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
