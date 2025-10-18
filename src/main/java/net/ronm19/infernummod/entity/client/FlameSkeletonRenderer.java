package net.ronm19.infernummod.entity.client;


import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.FlameSkeletonEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class FlameSkeletonRenderer extends MobEntityRenderer<FlameSkeletonEntity, SkeletonEntityModel<FlameSkeletonEntity>> {
    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/flame_skeleton.png");

    public FlameSkeletonRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SkeletonEntityModel<>(ctx.getPart(EntityModelLayers.WITHER_SKELETON)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));

    }

    @Override
    public Identifier getTexture(FlameSkeletonEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(FlameSkeletonEntity entity, MatrixStack matrices, float amount) {
        // Optional: slightly scale to look more imposing
        float scale = 1.05F;
        matrices.scale(scale, scale, scale);
    }
}
