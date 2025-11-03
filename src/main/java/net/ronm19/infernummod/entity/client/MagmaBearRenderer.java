package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.MagmaBearEntity;

public class MagmaBearRenderer extends MobEntityRenderer<MagmaBearEntity, PolarBearEntityModel<MagmaBearEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/magma_bear.png");


    public MagmaBearRenderer( EntityRendererFactory.Context context) {
        super(context, new PolarBearEntityModel<>(context.getPart(EntityModelLayers.POLAR_BEAR)), 0.9f);
    }

    @Override
    public Identifier getTexture( MagmaBearEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( MagmaBearEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        if (mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    protected void scale(MagmaBearEntity magmaBearEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(magmaBearEntity, matrixStack, f);
    }
}
