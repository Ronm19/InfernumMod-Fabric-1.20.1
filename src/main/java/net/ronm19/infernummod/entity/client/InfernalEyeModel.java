package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.ronm19.infernummod.entity.animation.InfernalEyeAnimations;
import net.ronm19.infernummod.entity.animation.MalfuryxAnimations;
import net.ronm19.infernummod.entity.custom.InfernalEyeEntity;

public class InfernalEyeModel <T extends InfernalEyeEntity> extends SinglePartEntityModel<T> {

    private final ModelPart head;
    private final ModelPart halo;
    private final ModelPart root;

    public InfernalEyeModel(ModelPart root) {
        this.head = root.getChild("head");
        this.halo = root.getChild("halo");
        this.root = root;
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -16.0F, -1.0F, 9.0F, 9.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData halo = modelPartData.addChild("halo", ModelPartBuilder.create().uv(0, 18).cuboid(-3.0F, -17.0F, 5.8F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(12, 20).cuboid(-3.0F, -17.1F, 0.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 22.1F, 0.0F));

        ModelPartData halo_r1 = halo.addChild("halo_r1", ModelPartBuilder.create().uv(0, 20).cuboid(-4.0F, -2.0F, 0.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -15.0F, 4.9F, 0.0F, -1.5708F, 0.0F));

        ModelPartData halo_r2 = halo.addChild("halo_r2", ModelPartBuilder.create().uv(12, 18).cuboid(-4.0F, -2.0F, 0.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, -15.0F, 4.9F, 0.0F, -1.5708F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles( InfernalEyeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        this.animateMovement(InfernalEyeAnimations.INFERNAL_EYE_WALKING, limbSwing, limbSwingAmount, 6.75f, 1.5f);
        this.updateAnimation(entity.idleAnimationState, InfernalEyeAnimations.INFERNAL_EYE_IDLE, ageInTicks, 1f);
    }

    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        halo.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

