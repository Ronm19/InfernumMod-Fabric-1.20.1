package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.FlameSkeletonAnimations;
import net.ronm19.infernummod.entity.animation.InfernumAnimations;
import net.ronm19.infernummod.entity.custom.FlameSkeletonEntity;

public class FlameSkeletonModel <T extends FlameSkeletonEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart right_leg;
    private final ModelPart right_arm;
    private final ModelPart left_leg;
    private final ModelPart left_arm;
    private final ModelPart body;
    private final ModelPart head;

    public FlameSkeletonModel( ModelPart root ) {
        this.root = root;
        this.right_leg = root.getChild("right_leg");
        this.right_arm = root.getChild("right_arm");
        this.left_leg = root.getChild("left_leg");
        this.left_arm = root.getChild("left_arm");
        this.body = root.getChild("body");
        this.head = root.getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        data = new ModelData();
        ModelPartData modelPartData = data.getRoot();
        ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, 0.0F, -1.1F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 12.0F, 0.1F));

        ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

        ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-1.0F, 0.0F, -1.1F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(2.0F, 12.0F, 0.1F));

        ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(data, 64, 32);
    }

    @Override
    public void setAngles( FlameSkeletonEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart :: resetTransform);
        this.setHeadAngles(headYaw, headPitch);

        this.animateMovement(FlameSkeletonAnimations.FLAME_SKELETON_WALKING, limbSwing, limbSwingAmount, 1f, 1f);
        this.updateAnimation(entity.idleAnimationState, FlameSkeletonAnimations.FLAME_SKELETON_IDLE, ageInTicks, 1f);
    }

        private void setHeadAngles( float headYaw, float headPitch ) {
            headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
            headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

            this.head.yaw = headYaw * ((float) Math.PI / 180F);
            this.head.pitch = headPitch * ((float) Math.PI / 180F);
        }


    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha ) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

