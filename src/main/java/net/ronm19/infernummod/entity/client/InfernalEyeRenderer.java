package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernalEyeEntity;
import net.ronm19.infernummod.entity.custom.MalfuryxEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernalEyeRenderer extends MobEntityRenderer<InfernalEyeEntity, InfernalEyeModel<InfernalEyeEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/infernal_eye.png");

    public InfernalEyeRenderer( EntityRendererFactory.Context context) {
        super(context, new InfernalEyeModel<>(context.getPart(ModModelLayers.INFERNAL_EYE)), 0.4f);
    }

    @Override
    public Identifier getTexture( InfernalEyeEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( InfernalEyeEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
