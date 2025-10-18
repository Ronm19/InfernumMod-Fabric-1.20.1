package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.custom.LavacatorEntity;

public class LavacatorModel<T extends LavacatorEntity> extends SinglePartEntityModel<T> implements ModelWithArms {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart nose;
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public LavacatorModel(ModelPart root) {
        this.root = root;

        this.head = root.getChild("head");
        this.hat = root.getChild("hat");
        this.nose = root.getChild("nose");
        this.body = root.getChild("body");
        this.left_arm = root.getChild("left_arm");
        this.right_arm = root.getChild("right_arm");
        this.left_leg = root.getChild("left_leg");
        this.right_leg = root.getChild("right_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), ModelTransform.NONE);

        root.addChild("hat", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        root.addChild("nose", ModelPartBuilder.create()
                .uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        root.addChild("body", ModelPartBuilder.create()
                .uv(16, 20).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
                .uv(0, 38).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new Dilation(0.25F)), ModelTransform.NONE);

        root.addChild("left_arm", ModelPartBuilder.create()
                        .uv(40, 46).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.pivot(5.0F, 2.0F, 0.0F));

        root.addChild("right_arm", ModelPartBuilder.create()
                        .uv(40, 46).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

        root.addChild("left_leg", ModelPartBuilder.create()
                        .uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.pivot(2.0F, 12.0F, 0.0F));

        root.addChild("right_leg", ModelPartBuilder.create()
                        .uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.pivot(-2.0F, 12.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {

        // --- Riding pose check ---
        if (entity.hasVehicle()) {
            this.right_arm.pitch = -(float)Math.PI / 5F;
            this.left_arm.pitch = -(float)Math.PI / 5F;
            this.right_leg.pitch = -1.4137F;
            this.left_leg.pitch = -1.4137F;
            this.right_leg.yaw = (float)Math.PI / 10F;
            this.left_leg.yaw = -(float)Math.PI / 10F;
        } else {
            // --- Walking animation ---
            this.right_arm.pitch = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.left_arm.pitch = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.right_leg.pitch = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.left_leg.pitch = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        }

        // --- Attack swing animation ---

        float attackProgress = entity.getAttackProgress(0.0F);

        if (entity.isAttacking()) {
            attackProgress = entity.getAttackProgress(0.0F);
            float swing = MathHelper.sin(MathHelper.sqrt(attackProgress) * (float)Math.PI);

            // Strong, downward swing like a Vindicator
            this.right_arm.pitch = -2.0F + swing;
            this.right_arm.yaw = 0.1F;
            this.right_arm.roll = MathHelper.cos(ageInTicks * 0.3F) * 0.15F;

            // Counterbalance left arm
            this.left_arm.pitch = -0.4F;
            this.left_arm.yaw = 0.2F;
        }
    }

    private ModelPart getAttackingArm(Arm arm) {
        return arm == Arm.LEFT ? this.left_arm : this.right_arm;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.getAttackingArm(arm).rotate(matrices);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
