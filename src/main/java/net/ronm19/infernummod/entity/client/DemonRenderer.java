package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.DemonEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class DemonRenderer extends MobEntityRenderer<DemonEntity, DemonModel<DemonEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/demon.png");

    public DemonRenderer( EntityRendererFactory.Context context) {
        super(context, new DemonModel<>(context.getPart(ModModelLayers.DEMON)), 0.7f);
    }

    @Override
    public Identifier getTexture( DemonEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( DemonEntity mobEntity, float f, float g, MatrixStack matrixStack,
                        VertexConsumerProvider vertexConsumerProvider, int i ) {


        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
