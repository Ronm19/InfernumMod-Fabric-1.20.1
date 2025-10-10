package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.InfernalHoardeAnimations;
import net.ronm19.infernummod.entity.animation.InfernumAnimations;
import net.ronm19.infernummod.entity.custom.InfernumEntity;

public class InfernumModel <T extends InfernumEntity> extends SinglePartEntityModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart left_ribcage;
    private final ModelPart right_ribcage;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public InfernumModel(ModelPart root) {

        this.root = root;
        this.head = root.getChild("head");
        this.torso = root.getChild("torso");
        this.left_ribcage = root.getChild("left_ribcage");
        this.right_ribcage = root.getChild("right_ribcage");
        this.left_arm = root.getChild("left_arm");
        this.right_arm = root.getChild("right_arm");
        this.left_leg = root.getChild("left_leg");
        this.right_leg = root.getChild("right_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F, new Dilation(0.0F))
                .uv(32, 58).cuboid(-8.0F, -16.0F, -10.9F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F))
                .uv(82, 0).cuboid(-8.0F, -23.0F, -14.9F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F))
                .uv(82, 15).cuboid(3.9F, -23.0F, -14.9F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F))
                .uv(58, 19).cuboid(4.0F, -16.0F, -10.9F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -10.0F, 0.1F));

        ModelPartData torso = modelPartData.addChild("torso", ModelPartBuilder.create().uv(0, 0).cuboid(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F, 11.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 3.0F, -1.0F));

        ModelPartData left_ribcage = modelPartData.addChild("left_ribcage", ModelPartBuilder.create().uv(56, 68).cuboid(-7.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(7.0F, 1.0F, -5.0F));

        ModelPartData right_ribcage = modelPartData.addChild("right_ribcage", ModelPartBuilder.create().uv(74, 68).cuboid(-2.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.0F, 1.0F, -5.0F));

        ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(52, 32).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(13.0F, -10.0F, 0.0F));

        ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(0, 58).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-13.0F, -10.0F, 0.0F));

        ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(58, 0).cuboid(-2.9F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 11.0F, -1.0F));

        ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(32, 68).cuboid(-3.1F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 11.0F, -1.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(InfernumEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart :: resetTransform);
        this.setHeadAngles(headYaw, headPitch);

        this.animateMovement(InfernumAnimations.INFERNUM_WALK, limbSwing, limbSwingAmount, 1f, 1f);
        this.updateAnimation(entity.idleAnimationState, InfernumAnimations.INFERNUM_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.attackAnimationState, InfernumAnimations.INFERNUM_ATTACK, ageInTicks, 1f);

    }

    private void setHeadAngles( float headYaw, float headPitch ) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * ((float) Math.PI / 180F);
        this.head.pitch = headPitch * ((float) Math.PI / 180F);
    }

    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_ribcage.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_ribcage.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
