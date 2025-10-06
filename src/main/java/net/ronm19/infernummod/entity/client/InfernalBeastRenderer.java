package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.InfernalBeastEntity;
import net.ronm19.infernummod.entity.custom.InfernumHerobrineEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernalBeastRenderer extends MobEntityRenderer<InfernalBeastEntity, InfernalBeastModel<InfernalBeastEntity>> {
    private static final Identifier NORMAL_TEXTURE = new Identifier("infernummod", "textures/entity/infernal_beast.png");
    private static final Identifier RAGE_TEXTURE = new Identifier("infernummod", "textures/entity/infernal_beast_rage.png");

    public InfernalBeastRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new InfernalBeastModel<>(ctx.getPart(ModModelLayers.INFERNAL_BEAST)), 0.5f);
    }

    @Override
    public Identifier getTexture(InfernalBeastEntity entity) {
        return entity.isEnraged()
                ? RAGE_TEXTURE       // points to textures/entity/infernal_beast_rage.png
                : NORMAL_TEXTURE;
    }


    @Override
    public void render( InfernalBeastEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}