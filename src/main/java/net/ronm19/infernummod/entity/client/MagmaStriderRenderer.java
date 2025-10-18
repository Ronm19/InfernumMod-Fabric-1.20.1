package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.MagmaStriderEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class MagmaStriderRenderer extends MobEntityRenderer<MagmaStriderEntity, MagmaStriderModel<MagmaStriderEntity>> {

    private static final Identifier TEXTURE =
            new Identifier(InfernumMod.MOD_ID, "textures/entity/magma_strider.png");

    public MagmaStriderRenderer( EntityRendererFactory.Context context) {
        super(context, new MagmaStriderModel<>(context.getPart(ModModelLayers.MAGMA_STRIDER)), 0.7F);
    }

    @Override
    public Identifier getTexture( MagmaStriderEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render(MagmaStriderEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
