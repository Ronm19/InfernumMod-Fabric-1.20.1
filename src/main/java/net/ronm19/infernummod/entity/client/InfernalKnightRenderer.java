package net.ronm19.infernummod.entity.client;

import com.google.common.collect.Maps;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.ronm19.infernummod.entity.custom.InfernalKnightEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;
import net.ronm19.infernummod.entity.variant.InfernalKnightVariant;

import java.util.Map;

public class InfernalKnightRenderer extends MobEntityRenderer<InfernalKnightEntity, InfernalKnightModel<InfernalKnightEntity>> {
    private static final Map<InfernalKnightVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(InfernalKnightVariant.class), infernalKnightVariantIdentifierEnumMap -> {
                infernalKnightVariantIdentifierEnumMap.put(InfernalKnightVariant.DEFAULT,
                        new Identifier("infernummod", "textures/entity/infernal_knight.png"));
                infernalKnightVariantIdentifierEnumMap.put(InfernalKnightVariant.ELITE,
                        new Identifier("infernummod", "textures/entity/infernal_knight_elite.png"));
            });

    public InfernalKnightRenderer( EntityRendererFactory.Context context) {
        super(context, new InfernalKnightModel<>(context.getPart(ModModelLayers.INFERNAL_KNIGHT)), 0.6F);
        this.addFeature(new ArmorFeatureRenderer<>(
                this,
                new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
                new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()   // ✅ Correct for Fabric 1.20.2
        ));
    }

    @Override
    public Identifier getTexture(InfernalKnightEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    public void render( InfernalKnightEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    protected void scale( InfernalKnightEntity entity, MatrixStack matrices, float amount ) {
        float scale = 1.05F;
        matrices.scale(scale, scale, scale);
        super.scale(entity, matrices, amount);
    }
}
