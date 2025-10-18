package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;
import net.ronm19.infernummod.entity.custom.LavaWitchEntity;

public class LavaWitchItemFeatureRenderer extends VillagerHeldItemFeatureRenderer<LavaWitchEntity, LavaWitchModel<LavaWitchEntity>> {

    public LavaWitchItemFeatureRenderer(
            FeatureRendererContext<LavaWitchEntity, LavaWitchModel<LavaWitchEntity>> context,
            HeldItemRenderer heldItemRenderer) {
        super(context, heldItemRenderer);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LavaWitchEntity entity, float limbAngle,
                       float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {

        ItemStack heldItem = entity.getMainHandStack();
        matrices.push();
        if (heldItem.isOf(Items.POTION)) {
            // Align with head/nose like vanilla Witch
            this.getContextModel().getHead().rotate(matrices);
            this.getContextModel().getNose().rotate(matrices);
            matrices.translate(0.0625F, 0.25F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(140.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(10.0F));
            matrices.translate(0.0F, -0.4F, 0.4F);
        }

        super.render(matrices, vertexConsumers, light, entity,
                limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);

        matrices.pop();
    }
}
