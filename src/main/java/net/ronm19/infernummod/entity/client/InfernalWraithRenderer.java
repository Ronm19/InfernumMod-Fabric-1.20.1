package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.InfernalWraithEntity;

public class InfernalWraithRenderer extends MobEntityRenderer<InfernalWraithEntity, BlazeEntityModel<InfernalWraithEntity>> {

    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/infernal_wraith.png");

    public InfernalWraithRenderer(EntityRendererFactory.Context context) {
        super(context, new BlazeEntityModel<>(context.getPart(EntityModelLayers.BLAZE)), 0.5F);
    }

    @Override
    public Identifier getTexture(InfernalWraithEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render( InfernalWraithEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
