package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernoEndermanEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernoEndermanRenderer extends MobEntityRenderer<InfernoEndermanEntity, EndermanEntityModel<InfernoEndermanEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/inferno_enderman.png");


    public InfernoEndermanRenderer( EntityRendererFactory.Context context) {
        super(context, new EndermanEntityModel<>(context.getPart(EntityModelLayers.ENDERMAN)), 0.7F);
    }

    @Override
    public Identifier getTexture(InfernoEndermanEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render(InfernoEndermanEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
