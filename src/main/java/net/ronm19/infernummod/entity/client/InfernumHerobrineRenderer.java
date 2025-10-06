package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernumHerobrineEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernumHerobrineRenderer extends MobEntityRenderer<InfernumHerobrineEntity, InfernumHerobrineModel<InfernumHerobrineEntity>> {
    private static final Identifier NORMAL_TEXTURE = new Identifier("infernummod", "textures/entity/infernum_herobrine.png");
    private static final Identifier RAGE_TEXTURE = new Identifier("infernummod", "textures/entity/infernum_herobrine_rage.png");

    public InfernumHerobrineRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new InfernumHerobrineModel<>(ctx.getPart(ModModelLayers.INFERNUM_HEROBRINE)), 0.5f);
    }

    @Override
    public Identifier getTexture(InfernumHerobrineEntity entity) {
        return entity.isEnraged() ? RAGE_TEXTURE : NORMAL_TEXTURE;
    }

    @Override
    public void render( InfernumHerobrineEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
