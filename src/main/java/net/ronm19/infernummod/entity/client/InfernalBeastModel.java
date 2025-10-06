package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.ronm19.infernummod.entity.animation.InfernalBeastAnimations;
import net.ronm19.infernummod.entity.animation.MalfuryxAnimations;
import net.ronm19.infernummod.entity.custom.InfernalBeastEntity;
import net.ronm19.infernummod.entity.custom.InfernalEyeEntity;

public class InfernalBeastModel <T extends InfernalBeastEntity> extends SinglePartEntityModel<T> {
    private final ModelPart Root;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart Tail;
    private final ModelPart LeftHand;
    private final ModelPart RightHand;
    private final ModelPart Neck;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LowerTorso;

    public InfernalBeastModel(ModelPart root) {
        this.Root = root;
        this.LeftLeg = root.getChild("LeftLeg");
        this.RightLeg = root.getChild("RightLeg");
        this.Tail = root.getChild("Tail");
        this.LeftHand = root.getChild("LeftHand");
        this.RightHand = root.getChild("RightHand");
        this.Neck = root.getChild("Neck");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.LowerTorso = root.getChild("LowerTorso");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData LeftLeg = modelPartData.addChild("LeftLeg", ModelPartBuilder.create().uv(24, 88).cuboid(1.0F, 11.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(68, 81).cuboid(-3.0F, 11.0F, 2.0F, 5.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(38, 72).cuboid(-2.0F, 0.0F, 2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(50, 58).cuboid(-3.0F, 0.0F, 1.0F, 5.0F, 8.0F, 6.0F, new Dilation(0.0F))
                .uv(34, 88).cuboid(-1.0F, 11.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(88, 54).cuboid(-3.0F, 11.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 12.0F, 1.0F));

        ModelPartData RightLeg = modelPartData.addChild("RightLeg", ModelPartBuilder.create().uv(86, 16).cuboid(2.0F, 10.0F, -3.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(78, 86).cuboid(0.0F, 10.0F, -3.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(86, 81).cuboid(-2.0F, 10.0F, -3.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(74, 31).cuboid(-2.0F, 10.0F, 1.0F, 5.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(24, 72).cuboid(-1.0F, -1.0F, 1.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(28, 58).cuboid(-2.0F, -1.0F, 0.0F, 5.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 13.0F, 2.0F));

        ModelPartData Tail = modelPartData.addChild("Tail", ModelPartBuilder.create().uv(72, 37).cuboid(-2.0F, -2.0F, -3.0F, 2.0F, 3.0F, 7.0F, new Dilation(0.0F))
                .uv(16, 77).cuboid(-2.0F, -9.0F, 4.0F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F))
                .uv(84, 72).cuboid(-2.0F, -9.0F, 6.0F, 2.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 11.0F, 11.0F));

        ModelPartData LeftHand = modelPartData.addChild("LeftHand", ModelPartBuilder.create().uv(1, 55).cuboid(-3.0F, -11.0F, 0.0F, 7.0F, 9.0F, 6.0F, new Dilation(0.0F))
                .uv(56, 11).cuboid(-3.0F, -2.0F, 0.0F, 7.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(74, 22).cuboid(-3.0F, 3.0F, 0.0F, 2.0F, 3.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 77).cuboid(-0.6F, 3.0F, 0.0F, 2.0F, 3.0F, 6.0F, new Dilation(0.0F))
                .uv(68, 72).cuboid(2.0F, 3.0F, 0.0F, 2.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.0F, 9.0F, 2.0F));

        ModelPartData RightHand = modelPartData.addChild("RightHand", ModelPartBuilder.create().uv(46, 22).cuboid(-4.0F, -11.0F, 0.0F, 7.0F, 9.0F, 6.0F, new Dilation(0.0F))
                .uv(35, 47).cuboid(-4.0F, -2.0F, 0.0F, 7.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(52, 72).cuboid(-4.0F, 3.0F, 0.0F, 2.0F, 3.0F, 6.0F, new Dilation(0.0F))
                .uv(72, 54).cuboid(-1.4F, 3.0F, 0.0F, 2.0F, 3.0F, 6.0F, new Dilation(0.0F))
                .uv(72, 63).cuboid(1.0F, 3.0F, 0.0F, 2.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(13.0F, 9.0F, 2.0F));

        ModelPartData Neck = modelPartData.addChild("Neck", ModelPartBuilder.create().uv(66, 47).cuboid(-4.0F, -1.0F, 0.0F, 8.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 2.0F));

        ModelPartData Head = modelPartData.addChild("Head", ModelPartBuilder.create().uv(0, 39).cuboid(-4.5F, -2.0F, 0.0F, 10.0F, 9.0F, 7.0F, new Dilation(0.0F))
                .uv(-1, 85).cuboid(-4.5F, -2.0F, -6.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(52, 81).cuboid(3.5F, -2.0F, -6.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(68, 86).cuboid(3.5F, -8.0F, -6.0F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(86, 7).cuboid(-4.5F, -8.0F, -6.0F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(88, 62).cuboid(1.5F, 0.0F, -1.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(88, 59).cuboid(-4.5F, 0.0F, -1.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(82, 0).cuboid(-0.5F, 3.0F, -1.0F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 70).cuboid(-2.5F, 5.0F, -1.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -14.0F, 1.0F));

        ModelPartData Torso = modelPartData.addChild("Torso", ModelPartBuilder.create().uv(0, 22).cuboid(-7.5F, 3.0F, 0.0F, 15.0F, 9.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-16.5F, -3.0F, 0.0F, 33.0F, 3.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 11).cuboid(-9.5F, 0.0F, 0.0F, 19.0F, 3.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -2.0F, 1.0F));

        ModelPartData LowerTorso = modelPartData.addChild("LowerTorso", ModelPartBuilder.create().uv(34, 39).cuboid(-6.5F, -1.0F, 0.0F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 11.0F, 2.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(InfernalBeastEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        this.animateMovement(InfernalBeastAnimations.INFERNAL_BEAST_WALK, limbSwing, limbSwingAmount, 1.75f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, InfernalBeastAnimations.INFERNAL_BEAST_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.attackAnimationState, InfernalBeastAnimations.INFERNAL_BEAST_ATTACK, ageInTicks, 1f);
    }

    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        LeftLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        RightLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        Tail.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        LeftHand.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        RightHand.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        Neck.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        Head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        Torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        LowerTorso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.Root;
    }
}
