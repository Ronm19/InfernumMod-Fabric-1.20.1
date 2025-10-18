package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.ronm19.infernummod.entity.custom.InfernalKnightEntity;

@Environment(EnvType.CLIENT)
public class InfernalKnightModel<T extends InfernalKnightEntity> extends BipedEntityModel<T> {
    public InfernalKnightModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // Head
        root.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 16)
                        .cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        // Hat
        root.addChild("hat",
                ModelPartBuilder.create(), // empty part (no geometry)
                ModelTransform.NONE);


        // Body
        root.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 32)
                        .cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        // Arms
        root.addChild("right_arm",
                ModelPartBuilder.create()
                        .uv(32, 16)
                        .cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

        root.addChild("left_arm",
                ModelPartBuilder.create()
                        .uv(32, 0)
                        .cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.pivot(5.0F, 2.0F, 0.0F));

        // Legs
        root.addChild("right_leg",
                ModelPartBuilder.create()
                        .uv(40, 32)
                        .cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

        root.addChild("left_leg",
                ModelPartBuilder.create()
                        .uv(24, 32)
                        .cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.pivot(1.9F, 12.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Default walking/head rotation animation
        super.setAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Custom shield or sword pose logic can go here
        if (entity.isShielding()) {
            this.leftArm.pitch = -1.2F;  // shield up
            this.leftArm.yaw = 0.3F;
        }
        if (entity.isAttacking()) {
            this.rightArm.pitch = -1.8F; // sword swing
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        // Let BipedEntityModel handle the part ordering and layers
        super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
