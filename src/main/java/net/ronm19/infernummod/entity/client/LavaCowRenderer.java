package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.LavaCowEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class LavaCowRenderer extends MobEntityRenderer<LavaCowEntity, LavaCowModel<LavaCowEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/lava_cow.png");


    public LavaCowRenderer( EntityRendererFactory.Context context) {
        super(context, new LavaCowModel<>(context.getPart(ModModelLayers.LAVA_COW)), 0.7F);
    }

    @Override
    public Identifier getTexture( LavaCowEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( LavaCowEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        if(mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
