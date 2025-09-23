package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.MalfuryxEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class MalfuryxRenderer extends MobEntityRenderer<MalfuryxEntity, MalfuryxModel<MalfuryxEntity>> {
   private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/malfuryx.png");

    public MalfuryxRenderer( EntityRendererFactory.Context context) {
        super(context, new MalfuryxModel<>(context.getPart(ModModelLayers.MALFURYX)), 0.7f);
    }

    @Override
    public Identifier getTexture( MalfuryxEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( MalfuryxEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
