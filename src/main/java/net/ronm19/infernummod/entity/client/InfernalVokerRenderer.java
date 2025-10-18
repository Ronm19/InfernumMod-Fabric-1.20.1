package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.InfernalVokerEntity;
import net.ronm19.infernummod.entity.custom.InfernalWraithEntity;

public class InfernalVokerRenderer extends MobEntityRenderer<InfernalVokerEntity, IllagerEntityModel<InfernalVokerEntity>> {

    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/infernal_voker.png");

    public InfernalVokerRenderer( EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.EVOKER)), 0.5F);
    }

    @Override
    public Identifier getTexture(InfernalVokerEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render( InfernalVokerEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
