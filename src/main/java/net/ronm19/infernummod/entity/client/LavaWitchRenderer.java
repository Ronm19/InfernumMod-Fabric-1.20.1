package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.LavaWitchEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class LavaWitchRenderer extends MobEntityRenderer<LavaWitchEntity, WitchEntityModel<LavaWitchEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/lava_witch.png");

    public LavaWitchRenderer( EntityRendererFactory.Context context) {
        super(context, new WitchEntityModel<>(context.getPart(ModModelLayers.LAVA_WITCH)), 0.5F);
        this.addFeature(new WitchHeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    public void render( LavaWitchEntity lavaWitchEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        ((WitchEntityModel<LavaWitchEntity>)this.model).setLiftingNose(!lavaWitchEntity.getMainHandStack().isEmpty());
        super.render(lavaWitchEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture( LavaWitchEntity entity ) {
        return TEXTURE;
    }

    protected void scale(LavaWitchEntity lavaWitchEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
