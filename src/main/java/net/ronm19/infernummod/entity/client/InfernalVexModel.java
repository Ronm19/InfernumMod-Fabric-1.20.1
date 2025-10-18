package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.custom.InfernalVexEntity;

@Environment(EnvType.CLIENT)
public class InfernalVexModel extends SinglePartEntityModel<InfernalVexEntity> implements ModelWithArms {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart head;

    public InfernalVexModel(ModelPart root) {
        super(RenderLayer::getEntityTranslucent);
        this.root = root;
        this.body = root.getChild("body");
        this.rightArm = body.getChild("right_arm");
        this.leftArm = body.getChild("left_arm");
        this.rightWing = body.getChild("right_wing");
        this.leftWing = body.getChild("left_wing");
        this.head = root.getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Root and body
        ModelPartData body = modelPartData.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 10).cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F)
                        .uv(0, 16).cuboid(-1.5F, 1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.0F, 20.0F, 0.0F));

        // Head
        modelPartData.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F),
                ModelTransform.pivot(0.0F, 20.0F, 0.0F));

        // Arms and Wings
        body.addChild("right_arm",
                ModelPartBuilder.create().uv(23, 0).cuboid(-1.25F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F),
                ModelTransform.pivot(-1.75F, 0.25F, 0.0F));

        body.addChild("left_arm",
                ModelPartBuilder.create().uv(23, 6).cuboid(-0.75F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F),
                ModelTransform.pivot(1.75F, 0.25F, 0.0F));

        body.addChild("left_wing",
                ModelPartBuilder.create().uv(16, 14).mirrored().cuboid(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F),
                ModelTransform.pivot(0.5F, 1.0F, 1.0F));

        body.addChild("right_wing",
                ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F),
                ModelTransform.pivot(-0.5F, 1.0F, 1.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(InfernalVexEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        this.head.yaw = netHeadYaw * ((float)Math.PI / 180F);
        this.head.pitch = headPitch * ((float)Math.PI / 180F);
        float k = MathHelper.cos(ageInTicks * 5.5F * ((float)Math.PI / 180F)) * 0.1F;
        this.rightArm.roll = ((float)Math.PI / 5F) + k;
        this.leftArm.roll = -(((float)Math.PI / 5F) + k);

        if (entity.isCharging()) {
            this.body.pitch = 0.0F;
            this.setChargingArmAngles(entity.getMainHandStack(), entity.getOffHandStack(), k);
        } else {
            this.body.pitch = 0.15707964F;
        }

        this.leftWing.yaw = 1.0995574F + MathHelper.cos(ageInTicks * 45.836624F * ((float)Math.PI / 180F)) * ((float)Math.PI / 180F) * 16.2F;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.leftWing.pitch = 0.47123888F;
        this.leftWing.roll = -0.47123888F;
        this.rightWing.pitch = 0.47123888F;
        this.rightWing.roll = 0.47123888F;
    }

    private void setChargingArmAngles(ItemStack mainHandStack, ItemStack offHandStack, float f) {
        if (mainHandStack.isEmpty() && offHandStack.isEmpty()) {
            this.rightWing.pitch = -1.2217305F;
            this.rightWing.yaw = 0.2617994F;
            this.rightWing.roll = -0.47123888F - f;
            this.leftWing.pitch = -1.2217305F;
            this.leftWing.yaw = -0.2617994F;
            this.leftWing.roll = 0.47123888F + f;
        } else {
            if (!mainHandStack.isEmpty()) {
                this.rightWing.pitch = 3.6651914F;
                this.rightWing.yaw = 0.2617994F;
                this.rightWing.roll = -0.47123888F - f;
            }
            if (!offHandStack.isEmpty()) {
                this.leftWing.pitch = 3.6651914F;
                this.leftWing.yaw = -0.2617994F;
                this.leftWing.roll = 0.47123888F + f;
            }
        }
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        boolean right = arm == Arm.RIGHT;
        ModelPart part = right ? this.rightWing : this.leftWing;
        this.root.rotate(matrices);
        this.body.rotate(matrices);
        part.rotate(matrices);
        matrices.scale(0.55F, 0.55F, 0.55F);
        this.translateForHand(matrices, right);
    }

    private void translateForHand(MatrixStack matrices, boolean mainHand) {
        matrices.translate(mainHand ? 0.046875F : -0.046875F, -0.15625F, 0.078125F);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
