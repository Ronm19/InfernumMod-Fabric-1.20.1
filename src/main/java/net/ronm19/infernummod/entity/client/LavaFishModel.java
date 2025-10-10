package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.custom.LavaFishEntity;

public class LavaFishModel<T extends LavaFishEntity> extends SinglePartEntityModel<T> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart right_fin;
    private final ModelPart left_fin;
    private final ModelPart fin_top;
    private final ModelPart tail;

    public LavaFishModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.right_fin = body.getChild("right_fin");
        this.left_fin = body.getChild("left_fin");
        this.fin_top = root.getChild("fin_top");
        this.tail = root.getChild("tail");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Body
        ModelPartData body = modelPartData.addChild("body",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F),
                ModelTransform.pivot(0.0F, 22.5F, 0.0F));

        // Fins attached to body
        body.addChild("right_fin",
                ModelPartBuilder.create().uv(2, 16)
                        .cuboid(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F),
                ModelTransform.of(-1.0F, 1.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

        body.addChild("left_fin",
                ModelPartBuilder.create().uv(2, 12)
                        .cuboid(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F),
                ModelTransform.of(1.0F, 1.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

        // Top fin & tail attached to root
        modelPartData.addChild("fin_top",
                ModelPartBuilder.create().uv(10, -6)
                        .cuboid(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F),
                ModelTransform.pivot(0.0F, 21.0F, -3.0F));

        modelPartData.addChild("tail",
                ModelPartBuilder.create().uv(24, -4)
                        .cuboid(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 4.0F),
                ModelTransform.pivot(0.0F, 22.5F, 3.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(LavaFishEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float speed = entity.isTouchingWater() ? 1.0F : 1.5F;
        this.tail.yaw = -speed * 0.45F * MathHelper.sin(0.6F * ageInTicks);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices,
                       int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return root; // return the whole model tree
    }
}
