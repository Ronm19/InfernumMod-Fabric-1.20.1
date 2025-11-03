package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernalRabbitEntity;

public class InfernalRabbitRenderer extends MobEntityRenderer<InfernalRabbitEntity, RabbitEntityModel<InfernalRabbitEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/infernal_rabbit.png");

    public InfernalRabbitRenderer( EntityRendererFactory.Context context) {
        super(context, new RabbitEntityModel<>(context.getPart(EntityModelLayers.RABBIT)),0.3f);
    }

    @Override
    public Identifier getTexture( InfernalRabbitEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( InfernalRabbitEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        if(mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
