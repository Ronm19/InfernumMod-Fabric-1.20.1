package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.ObsidianGhastEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class ObsidianGhastRenderer extends MobEntityRenderer<ObsidianGhastEntity, ObsidianGhastModel<ObsidianGhastEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/obsidian_ghast.png");

    public ObsidianGhastRenderer( EntityRendererFactory.Context context) {
        super(context, new ObsidianGhastModel<>(context.getPart(ModModelLayers.OBSIDIAN_GHAST)), 1.5F);
    }

    @Override
    public Identifier getTexture( ObsidianGhastEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( ObsidianGhastEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    protected void scale( ObsidianGhastEntity obsidianGhastEntity, MatrixStack matrixStack, float f) {
        float g = 1.0F;
        float h = 4.5F;
        float i = 4.5F;
        matrixStack.scale(4.5F, 4.5F, 4.5F);
    }
}
