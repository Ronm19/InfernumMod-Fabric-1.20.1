package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.AshChickenEntity;
import net.ronm19.infernummod.entity.custom.InfernalWraithEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class AshChickenRenderer extends MobEntityRenderer<AshChickenEntity, AshChickenModel<AshChickenEntity>> {
    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/ash_chicken.png");

    public AshChickenRenderer( EntityRendererFactory.Context context ) {
        super(context, new AshChickenModel<>(context.getPart(ModModelLayers.ASH_CHICKEN)), 0.5F);
    }

    @Override
    public Identifier getTexture( AshChickenEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( AshChickenEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {

    if (mobEntity.isBaby()) {
        matrixStack.scale(0.5f, 0.5f, 0.5f);
    } else {
        matrixStack.scale(1f, 1f, 1f);
    }

        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}



