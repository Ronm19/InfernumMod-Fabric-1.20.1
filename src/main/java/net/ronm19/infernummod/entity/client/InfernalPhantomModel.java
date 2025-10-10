package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.InfernalPhantomAnimations;
import net.ronm19.infernummod.entity.animation.InfernumAnimations;
import net.ronm19.infernummod.entity.custom.InfernalPhantomEntity;

public class InfernalPhantomModel <T extends InfernalPhantomEntity> extends SinglePartEntityModel<T> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart left_wing;
    private final ModelPart left_wing_tip;
    private final ModelPart right_wing;
    private final ModelPart right_wing_tip;
    private final ModelPart tail;
    private final ModelPart tail2;

    public InfernalPhantomModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.left_wing = root.getChild("left_wing");
        this.left_wing_tip = root.getChild("left_wing_tip");
        this.right_wing = root.getChild("right_wing");
        this.right_wing_tip = root.getChild("right_wing_tip");
        this.tail = root.getChild("tail");
        this.tail2 = root.getChild("tail2");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 31).cuboid(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 19.0F, 0.0F));

        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(28, 31).cuboid(-4.0F, -3.3F, -5.0F, 7.0F, 3.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 20.25F, -8.0F));

        ModelPartData left_wing = modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(1, 20).cuboid(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(2.5F, 17.0F, -8.0F));

        ModelPartData left_wing_tip = modelPartData.addChild("left_wing_tip", ModelPartBuilder.create().uv(0, 10).cuboid(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(8.5F, 17.0F, -8.0F));

        ModelPartData right_wing = modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(1, 20).mirrored().cuboid(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-2.5F, 17.0F, -8.0F));

        ModelPartData right_wing_tip = modelPartData.addChild("right_wing_tip", ModelPartBuilder.create().uv(0, 10).mirrored().cuboid(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-8.5F, 17.0F, -8.0F));

        ModelPartData tail = modelPartData.addChild("tail", ModelPartBuilder.create().uv(28, 39).cuboid(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 17.0F, 1.0F));

        ModelPartData tail2 = modelPartData.addChild("tail2", ModelPartBuilder.create().uv(0, 43).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 17.5F, 7.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(InfernalPhantomEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart :: resetTransform);

        this.animateMovement(InfernalPhantomAnimations.INFERNAL_PHANTOM_WALKING, limbSwing, limbSwingAmount, 1f, 1f);
        this.updateAnimation(entity.idleAnimationState, InfernalPhantomAnimations.INFERNAL_PHANTOM_IDLE, ageInTicks, 1f);

    }

    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_wing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_wing_tip.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_wing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_wing_tip.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
