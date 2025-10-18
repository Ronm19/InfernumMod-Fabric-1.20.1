package net.ronm19.infernummod.entity.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.custom.AshChickenEntity;

public class AshChickenModel <T extends AshChickenEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart bill;
    private final ModelPart chin;
    private final ModelPart body;
    private final ModelPart left_wing;
    private final ModelPart right_wing;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public AshChickenModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.bill = root.getChild("bill");
        this.chin = root.getChild("chin");
        this.body = root.getChild("body");
        this.left_wing = root.getChild("left_wing");
        this.right_wing = root.getChild("right_wing");
        this.left_leg = root.getChild("left_leg");
        this.right_leg = root.getChild("right_leg");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.0F, -4.0F));

        ModelPartData bill = modelPartData.addChild("bill", ModelPartBuilder.create().uv(14, 0).cuboid(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.0F, -4.0F));

        ModelPartData chin = modelPartData.addChild("chin", ModelPartBuilder.create().uv(14, 4).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.0F, -4.0F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 9).cuboid(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 16.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData left_wing = modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(24, 13).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, 13.0F, 0.0F));

        ModelPartData right_wing = modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(24, 13).cuboid(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 13.0F, 0.0F));

        ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(26, 0).cuboid(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 19.0F, 1.0F));

        ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(26, 0).cuboid(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 19.0F, 1.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head, this.bill, this.chin);
    }

    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.right_leg, this.left_leg, this.right_wing, this.left_wing);
    }

    @Override
    public void setAngles(AshChickenEntity entity, float limbAngle, float limbDistance, float ageInTicks, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180F);
        this.head.yaw = headYaw * ((float)Math.PI / 180F);
        this.bill.pitch = this.head.pitch;
        this.bill.yaw = this.head.yaw;
        this.chin.pitch = this.head.pitch;
        this.chin.yaw = this.head.yaw;
        this.right_leg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.left_leg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float)Math.PI) * 1.4F * limbDistance;
    }

    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        bill.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        chin.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_wing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_wing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
