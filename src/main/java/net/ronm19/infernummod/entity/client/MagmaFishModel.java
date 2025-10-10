package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.custom.MagmaFishEntity;

public class MagmaFishModel<T extends MagmaFishEntity> extends SinglePartEntityModel<T> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightFin;
    private final ModelPart leftFin;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart finBack;
    private final ModelPart tail;

    public MagmaFishModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.rightFin = body.getChild("right_fin");
        this.leftFin = body.getChild("left_fin");
        this.head = root.getChild("head");
        this.nose = root.getChild("nose");
        this.finBack = root.getChild("fin_back");
        this.tail = root.getChild("tail");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // Body
        ModelPartData body = root.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-1.0F, -2.0F, 0.0F,
                                2.0F, 4.0F, 7.0F),
                ModelTransform.pivot(0.0F, 22.0F, 0.0F));

        // Side fins attached to body
        body.addChild("right_fin",
                ModelPartBuilder.create()
                        .uv(24, 1)
                        .cuboid(-2.0F, 0.0F, -1.0F,
                                2.0F, 0.0F, 2.0F),
                ModelTransform.of(-1.0F, 1.0F, 0.0F,
                        0.0F, 0.0F, -0.7854F));

        body.addChild("left_fin",
                ModelPartBuilder.create()
                        .uv(24, 4)
                        .cuboid(0.0F, 0.0F, -1.0F,
                                2.0F, 0.0F, 2.0F),
                ModelTransform.of(1.0F, 1.0F, 0.0F,
                        0.0F, 0.0F, 0.7854F));

        // Head section
        root.addChild("head",
                ModelPartBuilder.create()
                        .uv(11, 0)
                        .cuboid(-1.0F, -2.0F, -3.0F,
                                2.0F, 4.0F, 3.0F),
                ModelTransform.pivot(0.0F, 22.0F, 0.0F));

        // Nose tip
        root.addChild("nose",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-1.0F, -2.0F, -1.0F,
                                2.0F, 3.0F, 1.0F),
                ModelTransform.pivot(0.0F, 22.0F, -3.0F));

        // Top/back fin
        root.addChild("fin_back",
                ModelPartBuilder.create()
                        .uv(20, -6)
                        .cuboid(0.0F, -1.0F, -1.0F,
                                0.0F, 1.0F, 6.0F),
                ModelTransform.pivot(0.0F, 20.0F, 0.0F));

        // Tail
        root.addChild("tail",
                ModelPartBuilder.create()
                        .uv(22, 3)
                        .cuboid(0.0F, -2.0F, 0.0F,
                                0.0F, 4.0F, 4.0F),
                ModelTransform.pivot(0.0F, 22.0F, 7.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(MagmaFishEntity entity, float limbSwing, float limbSwingAmount,
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
        return root; // return the root so renderer can render all children
    }
}
