package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.MagmaGolemEntity;

public class MagmaGolemRenderer extends MobEntityRenderer<MagmaGolemEntity, IronGolemEntityModel<MagmaGolemEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/magma_golem.png");


    public MagmaGolemRenderer( EntityRendererFactory.Context context) {
        super(context, new IronGolemEntityModel<>(context.getPart(EntityModelLayers.IRON_GOLEM)), 0.7f);
        this.addFeature(new MagmaGolemCrackFeatureRenderer(this));
        this.addFeature(new MagmaGolemFlowerFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture( MagmaGolemEntity entity ) {
        return TEXTURE;
    }

    protected void setupTransforms( MagmaGolemEntity magmaGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(magmaGolemEntity, matrixStack, f, g, h);
        if (!((double)magmaGolemEntity.limbAnimator.getSpeed() < 0.01)) {
            float i = 13.0F;
            float j = magmaGolemEntity.limbAnimator.getPos(h) + 6.0F;
            float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5F * k));
        }
    }
}
