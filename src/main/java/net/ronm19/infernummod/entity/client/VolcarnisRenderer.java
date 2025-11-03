package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.VolcarnisEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class VolcarnisRenderer extends MobEntityRenderer<VolcarnisEntity, VolcarnisModel<VolcarnisEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/volcarnis.png");

    public VolcarnisRenderer( EntityRendererFactory.Context context ) {
        super(context, new VolcarnisModel<>(context.getPart(ModModelLayers.VOLCARNIS)), 0.4F);

    }

    @Override
    public Identifier getTexture( VolcarnisEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( VolcarnisEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    protected void scale( VolcarnisEntity volcarnisEntity, MatrixStack matrixStack, float f ) {
        super.scale(volcarnisEntity, matrixStack, f);
        matrixStack.scale(0.8F, 0.8F, 0.8F);
    }

    protected void setupTransforms( VolcarnisEntity volcarnisEntity, MatrixStack matrixStack, float f, float g, float h ) {
        super.setupTransforms(volcarnisEntity, matrixStack, f, g, h);
        float i = volcarnisEntity.getSleepAnimation(h);
        if (i > 0.0F) {
            matrixStack.translate(0.4F * i, 0.15F * i, 0.1F * i);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerpAngleDegrees(i, 0.0F, 90.0F)));
            BlockPos blockPos = volcarnisEntity.getBlockPos();

            for (PlayerEntity playerEntity : volcarnisEntity.getWorld().getNonSpectatingEntities(PlayerEntity.class, (new Box(blockPos)).expand((double) 2.0F, (double) 2.0F, (double) 2.0F))) {
                if (playerEntity.isSleeping()) {
                    matrixStack.translate(0.15F * i, 0.0F, 0.0F);
                    break;
                }
            }
        }
    }
}
