package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.ronm19.infernummod.entity.custom.ScorchedWoolieEntity;

public class ScorchedWoolieWoolModel<T extends ScorchedWoolieEntity> extends QuadrupedEntityModel<T> {
    private float headAngle;

    public ScorchedWoolieWoolModel(ModelPart root) {
        // Parameters: (ModelPart root, boolean scaleHead, float childYOffset, float childZOffset, float bodyYOffset, float headYOffset, int textureHeight)
        super(root, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // HEAD
        modelPartData.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.6F)),
                ModelTransform.pivot(0.0F, 6.0F, -8.0F));

        // BODY
        modelPartData.addChild("body",
                ModelPartBuilder.create()
                        .uv(28, 8)
                        .cuboid(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, new Dilation(1.75F)),
                ModelTransform.of(0.0F, 5.0F, 2.0F, (float) Math.PI / 2F, 0.0F, 0.0F));

        // LEGS
        ModelPartBuilder legBuilder = ModelPartBuilder.create()
                .uv(0, 16)
                .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.5F));

        modelPartData.addChild("right_hind_leg", legBuilder, ModelTransform.pivot(-3.0F, 12.0F, 7.0F));
        modelPartData.addChild("left_hind_leg", legBuilder, ModelTransform.pivot(3.0F, 12.0F, 7.0F));
        modelPartData.addChild("right_front_leg", legBuilder, ModelTransform.pivot(-3.0F, 12.0F, -5.0F));
        modelPartData.addChild("left_front_leg", legBuilder, ModelTransform.pivot(3.0F, 12.0F, -5.0F));

        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);

        // Ensures smooth neck & head animation (same as sheep grazing)
        this.head.pivotY = 6.0F + entity.getNeckAngle(tickDelta) * 9.0F;
        this.headAngle = entity.getHeadAngle(tickDelta);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.head.pitch = this.headAngle;
    }
}
