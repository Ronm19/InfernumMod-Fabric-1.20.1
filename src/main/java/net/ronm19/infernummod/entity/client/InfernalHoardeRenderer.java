package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernalHoardeEntity;
import net.ronm19.infernummod.entity.custom.MalfuryxEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernalHoardeRenderer extends MobEntityRenderer<InfernalHoardeEntity, InfernalHoardeModel<InfernalHoardeEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/infernal_hoarde.png");

    public InfernalHoardeRenderer( EntityRendererFactory.Context context) {
        super(context, new InfernalHoardeModel<>(context.getPart(ModModelLayers.INFERNAL_HOARDE)), 0.7f);
    }

    @Override
    public Identifier getTexture(InfernalHoardeEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(InfernalHoardeEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
