package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.MagmaCreeperEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class MagmaCreeperRenderer extends MobEntityRenderer<MagmaCreeperEntity, MagmaCreeperModel<MagmaCreeperEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/magma_creeper.png");

    public MagmaCreeperRenderer( EntityRendererFactory.Context context) {
        super(context, new MagmaCreeperModel<>(context.getPart(ModModelLayers.MAGMA_CREEPER)), 0.6F);
    }

    @Override
    public Identifier getTexture( MagmaCreeperEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( MagmaCreeperEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
