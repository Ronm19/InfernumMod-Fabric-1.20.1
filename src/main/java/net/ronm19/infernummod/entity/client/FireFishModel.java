// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.custom.FireFishEntity;

public class FireFishModel <T extends FireFishEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body_front;
    private final ModelPart body_back;
    private final ModelPart fin_left;
    private final ModelPart fin_right;
    private final ModelPart fin_back_1;
    private final ModelPart fin_back_2;
    private final ModelPart tail;

    public FireFishModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body_front = root.getChild("body_front");
        this.body_back = root.getChild("body_back");
        this.fin_left = root.getChild("fin_left");
        this.fin_right = root.getChild("fin_right");
        this.fin_back_1 = root.getChild("fin_back_1");
        this.fin_back_2 = root.getChild("fin_back_2");
        this.tail = root.getChild("tail");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(22, 0).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 21.5F, -8.0F));

        ModelPartData body_front = modelPartData.addChild("body_front", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 21.5F, -8.0F));

        ModelPartData body_back = modelPartData.addChild("body_back", ModelPartBuilder.create().uv(0, 13).cuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 21.5F, 0.0F));

        ModelPartData fin_left = modelPartData.addChild("fin_left", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.5F, 23.0F, -8.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData fin_right = modelPartData.addChild("fin_right", ModelPartBuilder.create().uv(-4, 0).cuboid(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.5F, 23.0F, -8.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData fin_back_1 = modelPartData.addChild("fin_back_1", ModelPartBuilder.create().uv(2, 1).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 17.0F, -3.0F));

        ModelPartData fin_back_2 = modelPartData.addChild("fin_back_2", ModelPartBuilder.create().uv(0, 2).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 17.0F, -1.0F));

        ModelPartData tail = modelPartData.addChild("tail", ModelPartBuilder.create().uv(20, 10).cuboid(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 21.5F, 8.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public void setAngles( FireFishEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            float speed = entity.isTouchingWater() ? 1.0F : 1.5F;
            this.tail.yaw = -speed * 0.45F * MathHelper.sin(0.6F * ageInTicks);
        }

    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body_front.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body_back.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        fin_left.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        fin_right.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        fin_back_1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        fin_back_2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tail.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}