package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.PyerlingWyrnEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class PyerlingWyrnRenderer extends MobEntityRenderer<PyerlingWyrnEntity, PyerlingWyrnModel<PyerlingWyrnEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/pyerling_wyrn.png");

    public PyerlingWyrnRenderer( EntityRendererFactory.Context context) {
        super(context, new PyerlingWyrnModel<>(context.getPart(ModModelLayers.PYERLING_WYRN)), 0.7f);
    }

    @Override
    public Identifier getTexture(PyerlingWyrnEntity entity ) {
        return TEXTURE;
    }


    @Override
    public void render( PyerlingWyrnEntity livingEntity, float f, float g, MatrixStack matrixStack,
                        VertexConsumerProvider vertexConsumerProvider, int i ) {

        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}