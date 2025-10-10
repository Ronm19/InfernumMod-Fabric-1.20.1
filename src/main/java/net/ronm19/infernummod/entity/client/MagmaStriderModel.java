package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.ronm19.infernummod.entity.animation.InfernalEyeAnimations;
import net.ronm19.infernummod.entity.animation.MagmaStriderAnimations;
import net.ronm19.infernummod.entity.custom.MagmaStriderEntity;

public class MagmaStriderModel <T extends MagmaStriderEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart left_leg;
    private final ModelPart right_leg;
    private final ModelPart hair_left_top;
    private final ModelPart hair_left_top_rotation;
    private final ModelPart hair_left_middle;
    private final ModelPart hair_left_middle_rotation;
    private final ModelPart hair_left_bottom;
    private final ModelPart hair_left_bottom_rotation;
    private final ModelPart hair_right_top;
    private final ModelPart hair_right_top_rotation;
    private final ModelPart hair_right_middle;
    private final ModelPart hair_right_middle_rotation;
    private final ModelPart hair_right_bottom;
    private final ModelPart hair_right_bottom_rotation;

    public MagmaStriderModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.left_leg = root.getChild("left_leg");
        this.right_leg = root.getChild("right_leg");

        this.hair_left_top = root.getChild("hair_left_top");
        this.hair_left_top_rotation = this.hair_left_top.getChild("hair_left_top_rotation");

        this.hair_left_middle = root.getChild("hair_left_middle");
        this.hair_left_middle_rotation = this.hair_left_middle.getChild("hair_left_middle_rotation");

        this.hair_left_bottom = root.getChild("hair_left_bottom");
        this.hair_left_bottom_rotation = this.hair_left_bottom.getChild("hair_left_bottom_rotation");

        this.hair_right_top = root.getChild("hair_right_top");
        this.hair_right_top_rotation = this.hair_right_top.getChild("hair_right_top_rotation");

        this.hair_right_middle = root.getChild("hair_right_middle");
        this.hair_right_middle_rotation = this.hair_right_middle.getChild("hair_right_middle_rotation");

        this.hair_right_bottom = root.getChild("hair_right_bottom");
        this.hair_right_bottom_rotation = this.hair_right_bottom.getChild("hair_right_bottom_rotation");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(5.9F, -6.0F, -12.8F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(3, 3).cuboid(5.9F, -13.0F, -12.8F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-7.8F, -6.0F, -12.8F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(3, 3).cuboid(-7.8F, -13.0F, -12.8F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 55).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 17.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 7.0F, 0.0F));

        ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 17.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, 7.0F, 0.0F));

        ModelPartData hair_left_top = modelPartData.addChild("hair_left_top", ModelPartBuilder.create(), ModelTransform.pivot(8.0F, -5.0F, -8.0F));

        ModelPartData hair_left_top_rotation = hair_left_top.addChild("hair_left_top_rotation", ModelPartBuilder.create().uv(4, 33).cuboid(0.0F, 0.0F, -8.0F, 12.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 1.0472F));

        ModelPartData hair_left_middle = modelPartData.addChild("hair_left_middle", ModelPartBuilder.create(), ModelTransform.pivot(8.0F, -1.0F, -8.0F));

        ModelPartData hair_left_middle_rotation = hair_left_middle.addChild("hair_left_middle_rotation", ModelPartBuilder.create().uv(4, 49).cuboid(0.0F, 0.0F, -8.0F, 12.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 1.0472F));

        ModelPartData hair_left_bottom = modelPartData.addChild("hair_left_bottom", ModelPartBuilder.create(), ModelTransform.pivot(8.0F, 4.0F, -8.0F));

        ModelPartData hair_left_bottom_rotation = hair_left_bottom.addChild("hair_left_bottom_rotation", ModelPartBuilder.create().uv(4, 65).cuboid(0.0F, 0.0F, -8.0F, 12.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 1.0472F));

        ModelPartData hair_right_top = modelPartData.addChild("hair_right_top", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, -4.0F, -8.0F));

        ModelPartData hair_right_top_rotation = hair_right_top.addChild("hair_right_top_rotation", ModelPartBuilder.create().uv(4, 33).mirrored().cuboid(-12.0F, 0.0F, -8.0F, 12.0F, 0.0F, 16.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 8.0F, 0.0F, 0.0F, -1.0472F));

        ModelPartData hair_right_middle = modelPartData.addChild("hair_right_middle", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, 0.0F, -8.0F));

        ModelPartData hair_right_middle_rotation = hair_right_middle.addChild("hair_right_middle_rotation", ModelPartBuilder.create().uv(4, 49).mirrored().cuboid(-12.0F, 0.0F, -8.0F, 12.0F, 0.0F, 16.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 8.0F, 0.0F, 0.0F, -1.0472F));

        ModelPartData hair_right_bottom = modelPartData.addChild("hair_right_bottom", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, 5.0F, -8.0F));

        ModelPartData hair_right_bottom_rotation = hair_right_bottom.addChild("hair_right_bottom_rotation", ModelPartBuilder.create().uv(4, 65).mirrored().cuboid(-12.0F, 0.0F, -8.0F, 12.0F, 0.0F, 16.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 8.0F, 0.0F, 0.0F, -1.0472F));
        return TexturedModelData.of(modelData, 64, 128);
    }
    @Override
    public void setAngles(MagmaStriderEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        this.animateMovement(MagmaStriderAnimations.MAGMA_STRIDER_WALKING, limbSwing, limbSwingAmount, 1f, 1.5f);
        this.updateAnimation(entity.idleAnimationState, MagmaStriderAnimations.MAGMA_STRIDER_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.attackAnimationState, MagmaStriderAnimations.MAGMA_STRIDER_ATTACK, ageInTicks, 1f);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        hair_left_top.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        hair_left_middle.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        hair_left_bottom.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        hair_right_top.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        hair_right_middle.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        hair_right_bottom.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
