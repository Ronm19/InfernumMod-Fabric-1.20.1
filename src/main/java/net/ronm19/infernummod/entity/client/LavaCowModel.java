package net.ronm19.infernummod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import net.ronm19.infernummod.entity.custom.LavaCowEntity;

public class LavaCowModel <T extends LavaCowEntity> extends QuadrupedEntityModel<T> {
    public LavaCowModel( ModelPart root) {
        super(root, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 12;
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new Dilation(0.0F)).uv(22, 0).cuboid(4.0F, -5.0F, -5.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(22, 0).cuboid(-5.0F, -5.0F, -5.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(1, 33).cuboid(-3.0F, 1.0F, -7.0F, 6.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.0F, -8.0F));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(18, 4).cuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F).uv(52, 0).cuboid(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F), ModelTransform.of(0.0F, 5.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 12.0F, 7.0F));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(4.0F, 12.0F, 7.0F));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 12.0F, -6.0F));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(4.0F, 12.0F, -6.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public ModelPart getHead() {
        return this.head;
    }
}
