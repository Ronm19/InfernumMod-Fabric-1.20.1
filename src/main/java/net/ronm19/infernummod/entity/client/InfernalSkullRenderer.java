package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.WitherSkullEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernalSkullEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class InfernalSkullRenderer extends EntityRenderer<InfernalSkullEntity> {
    private static final Identifier INVULNERABLE_TEXTURE = new Identifier("textures/entity/wither/wither_invulnerable.png");
    private static final Identifier TEXTURE = new Identifier("infernummod","textures/entity/projectiles/infernal_skull.png");
    private final SkullEntityModel model;

    public InfernalSkullRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new SkullEntityModel(context.getPart(EntityModelLayers.WITHER_SKULL));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 35).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    protected int getBlockLight(InfernalSkullEntity witherSkullEntity, BlockPos blockPos) {
        return 15;
    }

    public void render(InfernalSkullEntity witherSkullEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        float h = MathHelper.lerpAngleDegrees(g, witherSkullEntity.prevYaw, witherSkullEntity.getYaw());
        float j = MathHelper.lerp(g, witherSkullEntity.prevPitch, witherSkullEntity.getPitch());
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(witherSkullEntity)));
        this.model.setHeadRotation(0.0F, h, j);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
        super.render(witherSkullEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Identifier getTexture(InfernalSkullEntity witherSkullEntity) {
        return witherSkullEntity.isCharged() ? INVULNERABLE_TEXTURE : TEXTURE;
    }
}