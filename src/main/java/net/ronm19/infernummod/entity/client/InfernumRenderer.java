package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernumEntity;
import net.ronm19.infernummod.entity.custom.MalfuryxEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernumRenderer extends MobEntityRenderer<InfernumEntity, InfernumModel<InfernumEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/infernum.png");

    public InfernumRenderer(EntityRendererFactory.Context context) {
        super(context, new InfernumModel<>(context.getPart(ModModelLayers.INFERNUM)), 0.7f);
    }

    @Override
    public Identifier getTexture(InfernumEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render(InfernumEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
