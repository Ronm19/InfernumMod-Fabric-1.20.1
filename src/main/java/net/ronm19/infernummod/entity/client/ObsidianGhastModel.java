package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.ronm19.infernummod.entity.animation.DemonAnimations;
import net.ronm19.infernummod.entity.animation.ObsidianGhastAnimations;
import net.ronm19.infernummod.entity.custom.ObsidianGhastEntity;

public class ObsidianGhastModel <T extends ObsidianGhastEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart tentacle1;
    private final ModelPart tentacle2;
    private final ModelPart tentacle3;
    private final ModelPart tentacle4;
    private final ModelPart tentacle5;
    private final ModelPart tentacle6;
    private final ModelPart tentacle7;
    private final ModelPart tentacle8;
    private final ModelPart tentacle9;

    public ObsidianGhastModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.tentacle1 = root.getChild("tentacle1");
        this.tentacle2 = root.getChild("tentacle2");
        this.tentacle3 = root.getChild("tentacle3");
        this.tentacle4 = root.getChild("tentacle4");
        this.tentacle5 = root.getChild("tentacle5");
        this.tentacle6 = root.getChild("tentacle6");
        this.tentacle7 = root.getChild("tentacle7");
        this.tentacle8 = root.getChild("tentacle8");
        this.tentacle9 = root.getChild("tentacle9");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData tentacle1 = modelPartData.addChild("tentacle1", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.7F, 11.0F, -5.0F));

        ModelPartData tentacle2 = modelPartData.addChild("tentacle2", ModelPartBuilder.create().uv(0, 0).cuboid(4.0F, 0.0F, -1.0F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.3F, 11.0F, -5.0F));

        ModelPartData tentacle3 = modelPartData.addChild("tentacle3", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(6.3F, 11.0F, -5.0F));

        ModelPartData tentacle4 = modelPartData.addChild("tentacle4", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.3F, 11.0F, 0.0F));

        ModelPartData tentacle5 = modelPartData.addChild("tentacle5", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.3F, 11.0F, 0.0F));

        ModelPartData tentacle6 = modelPartData.addChild("tentacle6", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(3.7F, 11.0F, 0.0F));

        ModelPartData tentacle7 = modelPartData.addChild("tentacle7", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.7F, 11.0F, 5.0F));

        ModelPartData tentacle8 = modelPartData.addChild("tentacle8", ModelPartBuilder.create().uv(0, 0).cuboid(4.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.3F, 11.0F, 5.0F));

        ModelPartData tentacle9 = modelPartData.addChild("tentacle9", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, 0.0F, -1.0F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(6.3F, 11.0F, 5.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }
    @Override
    public void setAngles( ObsidianGhastEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float bodyYaw, float bodyPitch) {
        this.getPart().traverse().forEach(ModelPart :: resetTransform);

        this.animateMovement(ObsidianGhastAnimations.OBSIDIAN_GHAST_WALK, limbSwing, limbSwingAmount, 2f, 0.5f);
        this.updateAnimation(entity.idleAnimationState, ObsidianGhastAnimations.OBSIDIAN_GHAST_IDLE, ageInTicks, 1f);

    }

    @Override
    public void render( MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle4.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle5.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle6.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle7.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle8.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        tentacle9.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
