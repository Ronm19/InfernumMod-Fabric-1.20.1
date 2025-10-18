package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.EmberHundAnimations;
import net.ronm19.infernummod.entity.animation.EmberSerpentAnimations;
import net.ronm19.infernummod.entity.animation.InfernumAnimations;
import net.ronm19.infernummod.entity.custom.EmberSerpentEntity;

public class EmberSerpentModel <T extends EmberSerpentEntity> extends SinglePartEntityModel<T> {
    private final ModelPart[] tailSegments;
    private final ModelPart root;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;
    private final ModelPart tail6;
    private final ModelPart tail7;
    private final ModelPart tail8;
    private final ModelPart tail9;
    private final ModelPart tail10;
    private final ModelPart tail11;
    private final ModelPart tail12;
    private final ModelPart body;
    private final ModelPart head;

    public EmberSerpentModel(ModelPart root) {
        this.root = root;
        this.tailSegments = new ModelPart[]{
                this.tail1 = root.getChild("tail1"),
                this.tail2 = root.getChild("tail2"),
                this.tail3 = root.getChild("tail3"),
                this.tail4 = root.getChild("tail4"),
                this.tail5 = root.getChild("tail5"),
                this.tail6 = root.getChild("tail6"),
                this.tail7 = root.getChild("tail7"),
                this.tail8 = root.getChild("tail8"),
                this.tail9 = root.getChild("tail9"),
                this.tail10 = root.getChild("tail10"),
                this.tail11 = root.getChild("tail11"),
                this.tail12 = root.getChild("tail12"),

        };
                this.body = root.getChild("body");
                this.head = root.getChild("head");

    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData tail1 = modelPartData.addChild("tail1", ModelPartBuilder.create().uv(106, 20).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(96, 126).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 27.3F));

        ModelPartData tail2 = modelPartData.addChild("tail2", ModelPartBuilder.create().uv(80, 70).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(120, 80).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 37.3F));

        ModelPartData tail3 = modelPartData.addChild("tail3", ModelPartBuilder.create().uv(0, 69).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(120, 90).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 47.3F));

        ModelPartData tail4 = modelPartData.addChild("tail4", ModelPartBuilder.create().uv(84, 50).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(120, 100).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 57.3F));

        ModelPartData tail5 = modelPartData.addChild("tail5", ModelPartBuilder.create().uv(40, 70).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(120, 70).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 67.3F));

        ModelPartData tail6 = modelPartData.addChild("tail6", ModelPartBuilder.create().uv(106, 40).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F))
                .uv(44, 50).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 77.3F));

        ModelPartData tail7 = modelPartData.addChild("tail7", ModelPartBuilder.create().uv(122, 40).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 89).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 87.3F));

        ModelPartData tail8 = modelPartData.addChild("tail8", ModelPartBuilder.create().uv(124, 50).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F))
                .uv(40, 90).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 97.3F));

        ModelPartData tail9 = modelPartData.addChild("tail9", ModelPartBuilder.create().uv(124, 60).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F))
                .uv(80, 90).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 107.3F));

        ModelPartData tail10 = modelPartData.addChild("tail10", ModelPartBuilder.create().uv(80, 126).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F))
                .uv(106, 0).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 117.3F));

        ModelPartData tail11 = modelPartData.addChild("tail11", ModelPartBuilder.create().uv(128, 110).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F))
                .uv(40, 110).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 127.3F));

        ModelPartData tail12 = modelPartData.addChild("tail12", ModelPartBuilder.create().uv(112, 126).cuboid(-1.0F, -5.5F, -3.0F, 2.0F, 4.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 109).cuboid(-5.0F, -1.5F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.5F, 137.3F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0F, -11.0F, -17.0F, 14.0F, 11.0F, 39.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 50).cuboid(-5.0F, -8.0F, -28.8F, 10.0F, 7.0F, 12.0F, new Dilation(0.0F))
                .uv(80, 110).cuboid(-5.9F, -7.0F, -27.8F, 1.0F, 5.0F, 11.0F, new Dilation(0.0F))
                .uv(104, 110).cuboid(5.0F, -7.0F, -27.8F, 1.0F, 5.0F, 11.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }
    @Override
    public void setAngles(EmberSerpentEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart :: resetTransform);

        this.animateMovement(EmberSerpentAnimations.EMBER_SERPENT_WALKING, limbSwing, limbSwingAmount, 1f, 1f);
        this.updateAnimation(entity.idleAnimationState, EmberSerpentAnimations.EMBER_SERPENT_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.attackAnimationState, EmberSerpentAnimations.EMBER_SERPENT_ATTACK, ageInTicks, 1f);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail4.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail5.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail6.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail7.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail8.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail9.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail10.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail11.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail12.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}