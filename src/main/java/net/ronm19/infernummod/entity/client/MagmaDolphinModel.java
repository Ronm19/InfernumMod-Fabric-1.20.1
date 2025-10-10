package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.custom.MagmaDolphinEntity;

public class MagmaDolphinModel<T extends MagmaDolphinEntity> extends SinglePartEntityModel<T> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart left_fin;
    private final ModelPart right_fin;
    private final ModelPart back_fin;
    private final ModelPart tail;
    private final ModelPart tail_fin;

    public MagmaDolphinModel(ModelPart root) {
        // root of the whole model
        this.root = root.getChild("root");
        // children inside the root/body hierarchy
        this.body      = this.root.getChild("body");
        this.head      = this.body.getChild("head");
        this.left_fin  = this.body.getChild("left_fin");
        this.right_fin = this.body.getChild("right_fin");
        this.back_fin  = this.body.getChild("back_fin");
        this.tail      = this.body.getChild("tail");
        this.tail_fin  = this.tail.getChild("tail_fin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelRoot = modelData.getRoot();

        // Main root
        ModelPartData root = modelRoot.addChild("root",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        // Body (main torso)
        ModelPartData body = root.addChild("body",
                ModelPartBuilder.create()
                        .uv(42, 0)
                        .cuboid(-1.0F, -2.0F, -10.0F, 2.0F, 2.0F, 4.0F)      // spine ridge
                        .uv(0, 0)
                        .cuboid(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F),      // torso
                ModelTransform.pivot(0.0F, 0.0F, -3.0F));

        // Head
        body.addChild("head",
                ModelPartBuilder.create()
                        .uv(30, 20)
                        .cuboid(-4.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F),
                ModelTransform.pivot(0.0F, -4.0F, -3.0F));

        // Side fins
        body.addChild("left_fin",
                ModelPartBuilder.create()
                        .uv(16, 40)
                        .cuboid(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F),
                ModelTransform.pivot(4.5F, 0.0F, 1.0F));

        body.addChild("right_fin",
                ModelPartBuilder.create()
                        .uv(0, 36)
                        .cuboid(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F),
                ModelTransform.pivot(-4.5F, 0.0F, 1.0F));

        // Dorsal fin
        body.addChild("back_fin",
                ModelPartBuilder.create()
                        .uv(32, 40)
                        .cuboid(-0.5F, 0.0F, 8.0F, 1.0F, 4.0F, 5.0F),
                ModelTransform.pivot(0.0F, -11.0F, -2.0F));

        // Tail
        ModelPartData tail = body.addChild("tail",
                ModelPartBuilder.create()
                        .uv(0, 20)
                        .cuboid(-2.0F, -2.5F, -1.0F, 4.0F, 5.0F, 11.0F),
                ModelTransform.pivot(0.0F, -2.5F, 14.0F));

        // Tail fin
        tail.addChild("tail_fin",
                ModelPartBuilder.create()
                        .uv(30, 33)
                        .cuboid(-5.0F, -0.5F, -1.0F, 10.0F, 1.0F, 6.0F),
                ModelTransform.pivot(0.0F, 0.0F, 9.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(MagmaDolphinEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        // head/body direction control
        this.body.pitch = headPitch * ((float)Math.PI / 180F);
        this.body.yaw   = netHeadYaw * ((float)Math.PI / 180F);

        float swimSpeed  = 0.6F;      // wave frequency
        float swimDegree = 0.4F;      // tail bend amount

        // Tail goes up/down
        this.tail.pitch = MathHelper.cos(ageInTicks * swimSpeed) * swimDegree;
        this.tail_fin.pitch = MathHelper.cos(ageInTicks * swimSpeed) * swimDegree * 1.2F;

        // Pectoral fins gently flap
        this.left_fin.roll = MathHelper.cos(ageInTicks * 0.3F) * 0.15F;
        this.right_fin.roll = -this.left_fin.roll;
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices,
                       int light, int overlay,
                       float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}