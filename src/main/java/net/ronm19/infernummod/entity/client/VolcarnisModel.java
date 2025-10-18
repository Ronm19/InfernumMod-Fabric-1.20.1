package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.MagmaStriderAnimations;
import net.ronm19.infernummod.entity.animation.VolcarnisAnimations;
import net.ronm19.infernummod.entity.custom.VolcarnisEntity;

@Environment(EnvType.CLIENT)
public class VolcarnisModel <T extends VolcarnisEntity> extends SinglePartEntityModel<T> {
    private float sleepAnimation;
    private float tailCurlAnimation;
    private float headDownAnimation;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart front_left_leg;
    private final ModelPart front_right_leg;
    private final ModelPart back_left_leg;
    private final ModelPart back_right_leg;
    private final ModelPart tail;
    private final ModelPart tail2;

    public VolcarnisModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.front_left_leg = root.getChild("front_left_leg");
        this.front_right_leg = root.getChild("front_right_leg");
        this.back_left_leg = root.getChild("back_left_leg");
        this.back_right_leg = root.getChild("back_right_leg");
        this.tail = root.getChild("tail");
        this.tail2 = root.getChild("tail2");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 24).cuboid(-1.5F, -0.02F, -4.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 10).cuboid(-2.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(6, 10).cuboid(1.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.0F, -9.0F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(20, 0).cuboid(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 12.0F, -10.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData front_left_leg = modelPartData.addChild("front_left_leg", ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.1F, 14.1F, -5.0F));

        ModelPartData front_right_leg = modelPartData.addChild("front_right_leg", ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.1F, 14.1F, -5.0F));

        ModelPartData back_left_leg = modelPartData.addChild("back_left_leg", ModelPartBuilder.create().uv(8, 13).cuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.1F, 18.0F, 5.0F));

        ModelPartData back_right_leg = modelPartData.addChild("back_right_leg", ModelPartBuilder.create().uv(8, 13).cuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.1F, 18.0F, 5.0F));

        ModelPartData tail = modelPartData.addChild("tail", ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 15.5F, 8.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData tail2 = modelPartData.addChild("tail2", ModelPartBuilder.create().uv(4, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 15.5F, 16.0F, 1.5708F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    public void animateModel(T volcarnisEntity, float f, float g, float h) {
        this.sleepAnimation = volcarnisEntity.getSleepAnimation(h);
        this.tailCurlAnimation = volcarnisEntity.getTailCurlAnimation(h);
        this.headDownAnimation = volcarnisEntity.getHeadDownAnimation(h);

        if (this.sleepAnimation <= 0.0F) {
            this.head.pitch = 0.0F;
            this.head.roll = 0.0F;
            this.front_left_leg.pitch = 0.0F;
            this.front_left_leg.roll = 0.0F;
            this.front_right_leg.pitch = 0.0F;
            this.front_right_leg.roll = 0.0F;
            this.front_right_leg.pivotX = -1.2F;
            this.back_left_leg.pitch = 0.0F;
            this.back_right_leg.pitch = 0.0F;
            this.back_right_leg.roll = 0.0F;
            this.back_right_leg.pivotX = -1.1F;
            this.back_right_leg.pivotY = 18.0F;
        }

        super.animateModel(volcarnisEntity, f, g, h);
        if (volcarnisEntity.isInSittingPose()) {
            this.body.pitch = ((float)Math.PI / 4F);
            ModelPart var10000 = this.body;
            var10000.pivotY += -4.0F;
            var10000 = this.body;
            var10000.pivotZ += 5.0F;
            var10000 = this.head;
            var10000.pivotY += -3.3F;
            ++this.head.pivotZ;
            var10000 = this.tail2;
            var10000.pivotY += 8.0F;
            var10000 = this.tail2;
            var10000.pivotZ += -2.0F;
            var10000 = this.tail;
            var10000.pivotY += 2.0F;
            var10000 = this.tail;
            var10000.pivotZ += -0.8F;
            this.tail2.pitch = 1.7278761F;
            this.tail.pitch = 2.670354F;
            this.front_left_leg.pitch = -0.15707964F;
            this.front_left_leg.pivotY = 16.1F;
            this.front_left_leg.pivotZ = -7.0F;
            this.front_right_leg.pitch = -0.15707964F;
            this.front_right_leg.pivotY = 16.1F;
            this.front_right_leg.pivotZ = -7.0F;
            this.back_left_leg.pitch = (-(float)Math.PI / 2F);
            this.back_left_leg.pivotY = 21.0F;
            this.back_left_leg.pivotZ = 1.0F;
            this.back_right_leg.pitch = (-(float)Math.PI / 2F);
            this.back_right_leg.pivotY = 21.0F;
            this.back_right_leg.pivotZ = 1.0F;
        }
    }


    @Override
    public void setAngles(VolcarnisEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        setHeadAngles(headYaw, headPitch);


        if (this.sleepAnimation > 0.0F) {
            this.head.roll = ModelUtil.interpolateAngle(this.head.roll, -1.2707963F, this.sleepAnimation);
            this.head.yaw = ModelUtil.interpolateAngle(this.head.yaw, 1.2707963F, this.sleepAnimation);
            this.front_left_leg.pitch = -1.2707963F;
            this.front_right_leg.pitch = -0.47079635F;
            this.front_right_leg.roll = -0.2F;
            this.front_right_leg.pivotX = -0.2F;
            this.back_left_leg.pitch = -0.4F;
            this.back_right_leg.pitch = 0.5F;
            this.back_right_leg.roll = -0.5F;
            this.back_right_leg.pivotX = -0.3F;
            this.back_right_leg.pivotY = 20.0F;
            this.tail2.pitch = ModelUtil.interpolateAngle(this.tail2.pitch, 0.8F, this.tailCurlAnimation);
            this.tail.pitch = ModelUtil.interpolateAngle(this.tail.pitch, -0.4F, this.tailCurlAnimation);
        }

        if (this.headDownAnimation > 0.0F) {
            this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, -0.58177644F, this.headDownAnimation);
        }

        this.tail.yaw = MathHelper.cos(ageInTicks * 0.1F) * 0.1F;
        this.tail2.yaw = MathHelper.cos(ageInTicks * 0.1F + 1.0F) * 0.15F;


        this.animateMovement(VolcarnisAnimations.VOLCARNIS_WALKING, limbSwing, limbSwingAmount, 1f, 1.5f);
        this.updateAnimation(entity.idleAnimationState, VolcarnisAnimations.VOLCARNIS_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.attackAnimationState, VolcarnisAnimations.VOLCARNIS_ATTACKING, ageInTicks, 1f);
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
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        front_left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        front_right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        back_left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        back_right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
