package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.DemonAnimations;
import net.ronm19.infernummod.entity.animation.EmberBoarAnimations;
import net.ronm19.infernummod.entity.animation.InfernoEndermanAnimations;
import net.ronm19.infernummod.entity.custom.EmberBoarEntity;

@Environment(EnvType.CLIENT)
public class EmberBoarModel <T extends EmberBoarEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public EmberBoarModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(16, 16).cuboid(-2.0F, -0.75F, -2.75F, 4.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-4.0F, -4.75F, -1.75F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 12.75F, -12.25F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 14.0F, 1.0F));

        ModelPartData body_r1 = body.addChild("body_r1", ModelPartBuilder.create().uv(28, 8).cuboid(-5.0F, -8.0F, -4.0F, 10.0F, 16.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData leg1 = modelPartData.addChild("leg1", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 21.0F, 7.0F));

        ModelPartData leg2 = modelPartData.addChild("leg2", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 21.0F, 7.0F));

        ModelPartData leg3 = modelPartData.addChild("leg3", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 21.0F, -5.0F));

        ModelPartData leg4 = modelPartData.addChild("leg4", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 21.0F, -5.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(EmberBoarEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart :: resetTransform);
        this.setHeadAngles(headYaw, headPitch);


        this.animateMovement(EmberBoarAnimations.EMBER_BOAR_WALKING, limbSwing, limbSwingAmount, 1f, 1f);
        this.updateAnimation(entity.idleAnimationState, EmberBoarAnimations.EMBER_BOAR_IDLE, ageInTicks, 1f);
    }

    private void setHeadAngles( float headYaw, float headPitch ) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * ((float) Math.PI / 180F);
        this.head.pitch = headPitch * ((float) Math.PI / 180F);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        leg1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        leg2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        leg3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        leg4.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
