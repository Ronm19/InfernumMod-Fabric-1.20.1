package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.PyerlingWyrnAnimations;
import net.ronm19.infernummod.entity.custom.PyerlingWyrnEntity;

public class PyerlingWyrnModel <T extends PyerlingWyrnEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;

    public PyerlingWyrnModel( ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
        this.tail = root.getChild("tail");
        this.tail2 = root.getChild("tail2");
        this.tail3 = root.getChild("tail3");
        this.tail4 = root.getChild("tail4");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(76, 65).cuboid(-3.0F, -2.0F, -5.1667F, 6.0F, 4.0F, 11.0F, new Dilation(0.0F))
                .uv(76, 80).cuboid(-4.0F, -2.0F, -4.1667F, 1.0F, 4.0F, 8.0F, new Dilation(0.0F))
                .uv(38, 84).cuboid(3.0F, -2.0F, -4.1667F, 1.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(2.2F, 14.1F, -18.9333F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-5.5F, -3.25F, -10.0F, 11.0F, 5.0F, 20.0F, new Dilation(0.0F))
                .uv(62, 0).cuboid(5.5F, -3.25F, -9.0F, 1.0F, 5.0F, 18.0F, new Dilation(0.0F))
                .uv(0, 65).cuboid(-6.5F, -3.25F, -9.0F, 1.0F, 5.0F, 18.0F, new Dilation(0.0F))
                .uv(0, 25).cuboid(-5.5F, 1.75F, -9.0F, 11.0F, 1.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(2.1F, 15.05F, -3.9F));

        ModelPartData leg1 = modelPartData.addChild("leg1", ModelPartBuilder.create().uv(56, 84).cuboid(-1.5F, -3.5F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.6F, 20.5F, 3.5F));

        ModelPartData leg2 = modelPartData.addChild("leg2", ModelPartBuilder.create().uv(0, 88).cuboid(-1.5F, -3.5F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 20.5F, 3.5F));

        ModelPartData leg3 = modelPartData.addChild("leg3", ModelPartBuilder.create().uv(12, 88).cuboid(-1.5F, -3.5F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 20.5F, -10.5F));

        ModelPartData leg4 = modelPartData.addChild("leg4", ModelPartBuilder.create().uv(24, 88).cuboid(-1.5F, -3.5F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.3F, 20.5F, -10.5F));

        ModelPartData tail = modelPartData.addChild("tail", ModelPartBuilder.create().uv(0, 44).cuboid(-3.0F, -2.0F, -8.5F, 6.0F, 4.0F, 17.0F, new Dilation(0.0F)), ModelTransform.pivot(2.2F, 13.8F, 14.4F));

        ModelPartData tail2 = modelPartData.addChild("tail2", ModelPartBuilder.create().uv(62, 23).cuboid(-1.0F, -2.0F, -8.5F, 2.0F, 4.0F, 17.0F, new Dilation(0.0F)), ModelTransform.pivot(2.2F, 13.8F, 48.4F));

        ModelPartData tail3 = modelPartData.addChild("tail3", ModelPartBuilder.create().uv(46, 44).cuboid(-2.0F, -2.0F, -8.5F, 4.0F, 4.0F, 17.0F, new Dilation(0.0F)), ModelTransform.pivot(2.2F, 13.8F, 31.4F));

        ModelPartData tail4 = modelPartData.addChild("tail4", ModelPartBuilder.create().uv(38, 65).cuboid(-1.0F, -1.0F, -8.5F, 2.0F, 2.0F, 17.0F, new Dilation(0.0F)), ModelTransform.pivot(2.2F, 13.8F, 65.4F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(PyerlingWyrnEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(headYaw, headPitch);

        this.animateMovement(PyerlingWyrnAnimations.PYERLING_WYRN_WALK, limbSwing, limbSwingAmount, 1f, 1f);
        this.updateAnimation(entity.idleAnimationState, PyerlingWyrnAnimations.PYERLING_WYRN_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.attackAnimationState, PyerlingWyrnAnimations.PYERLING_WYRN_ATTACK, ageInTicks, 1f);

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
        leg1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        leg2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        leg3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        leg4.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail4.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
