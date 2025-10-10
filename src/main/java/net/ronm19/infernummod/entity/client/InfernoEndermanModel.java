package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.InfernoEndermanAnimations;
import net.ronm19.infernummod.entity.animation.InfernoZombieAnimations;
import net.ronm19.infernummod.entity.custom.InfernoEndermanEntity;

public class InfernoEndermanModel <T extends InfernoEndermanEntity> extends SinglePartEntityModel<T> {
    public boolean carryingBlock;
    public boolean angry;

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart headwear;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_leg;
    private final ModelPart left_leg;

    public InfernoEndermanModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.headwear = root.getChild("headwear");
        this.right_arm = root.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(32, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -15.0F, 0.0F));

        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -15.0F, 0.0F));

        ModelPartData headwear = modelPartData.addChild("headwear", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(-0.5F)), ModelTransform.pivot(0.0F, -15.0F, 0.0F));

        ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, -13.0F, 0.0F));

        ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(5.0F, -13.0F, 0.0F));

        ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -6.0F, 0.0F));

        ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(2.0F, -6.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(InfernoEndermanEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.head.visible = true;
        int k = -14;
        this.body.pitch = 0.0F;
        this.body.pivotY = -14.0F;
        this.body.pivotZ = -0.0F;
        ModelPart var10000 = this.right_leg;
        var10000.pitch -= 0.0F;
        var10000 = this.left_leg;
        var10000.pitch -= 0.0F;
        var10000 = this.right_arm;
        var10000.pitch *= 0.5F;
        var10000 = this.left_arm;
        var10000.pitch *= 0.5F;
        var10000 = this.right_leg;
        var10000.pitch *= 0.5F;
        var10000 = this.left_leg;
        var10000.pitch *= 0.5F;
        float l = 0.4F;
        if (this.right_arm.pitch > 0.4F) {
            this.right_arm.pitch = 0.4F;
        }

        if (this.left_arm.pitch > 0.4F) {
            this.left_arm.pitch = 0.4F;
        }

        if (this.right_arm.pitch < -0.4F) {
            this.right_arm.pitch = -0.4F;
        }

        if (this.left_arm.pitch < -0.4F) {
            this.left_arm.pitch = -0.4F;
        }

        if (this.right_leg.pitch > 0.4F) {
            this.right_leg.pitch = 0.4F;
        }

        if (this.left_leg.pitch > 0.4F) {
            this.left_leg.pitch = 0.4F;
        }

        if (this.right_leg.pitch < -0.4F) {
            this.right_leg.pitch = -0.4F;
        }

        if (this.left_leg.pitch < -0.4F) {
            this.left_leg.pitch = -0.4F;
        }

        if (this.carryingBlock) {
            this.right_arm.pitch = -0.5F;
            this.left_arm.pitch = -0.5F;
            this.right_arm.roll = 0.05F;
            this.left_arm.roll = -0.05F;
        }

        this.right_leg.pivotZ = 0.0F;
        this.left_leg.pivotZ = 0.0F;
        this.right_leg.pivotY = -5.0F;
        this.left_leg.pivotY = -5.0F;
        this.head.pivotZ = -0.0F;
        this.head.pivotY = -13.0F;
        this.headwear.pivotX = this.head.pivotX;
        this.headwear.pivotY = this.head.pivotY;
        this.headwear.pivotZ = this.head.pivotZ;
        this.headwear.pitch = this.head.pitch;
        this.headwear.yaw = this.head.yaw;
        this.headwear.roll = this.head.roll;
        if (this.angry) {
            float m = 1.0F;
            var10000 = this.head;
            var10000.pivotY -= 5.0F;
        }

        int n = -14;
        this.right_arm.setPivot(-5.0F, -12.0F, 0.0F);
        this.left_arm.setPivot(5.0F, -12.0F, 0.0F);


        this.getPart().traverse().forEach(ModelPart :: resetTransform);

        this.animateMovement(InfernoEndermanAnimations.INFERNO_ENDERMAN_WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, InfernoEndermanAnimations.INFERNO_ENDERMAN_IDLE, ageInTicks, 1f);



    }

    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        headwear.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
