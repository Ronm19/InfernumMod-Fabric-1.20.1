package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.EmberHundEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class EmberHundRenderer extends MobEntityRenderer<EmberHundEntity, EmberHundModel<EmberHundEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/ember_hund.png");

    public EmberHundRenderer( EntityRendererFactory.Context context) {
        super(context, new EmberHundModel<>(context.getPart(ModModelLayers.EMBER_HUND)), 0.4f);
    }

    @Override
    public Identifier getTexture(EmberHundEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render(EmberHundEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        if(mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}

