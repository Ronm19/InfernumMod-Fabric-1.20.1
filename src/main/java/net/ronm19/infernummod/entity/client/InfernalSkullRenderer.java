package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernalSkullEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class InfernalSkullRenderer extends EntityRenderer<InfernalSkullEntity> {
    private static final Identifier TEXTURE =
            new Identifier(InfernumMod.MOD_ID, "textures/entity/projectiles/infernal_skull.png");

    public InfernalSkullRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(InfernalSkullEntity entity, float entityYaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Billboard towards camera
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(1.0f, 1.0f, 1.0f);

        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f mat = entry.getPositionMatrix();
        Matrix3f normal = entry.getNormalMatrix();

        // Flat quad
        vc.vertex(mat, -0.5f, -0.5f, 0).color(255, 255, 255, 255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        vc.vertex(mat,  0.5f, -0.5f, 0).color(255, 255, 255, 255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        vc.vertex(mat,  0.5f,  0.5f, 0).color(255, 255, 255, 255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        vc.vertex(mat, -0.5f,  0.5f, 0).color(255, 255, 255, 255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();

        matrices.pop();
        super.render(entity, entityYaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(InfernalSkullEntity entity) {
        return TEXTURE;
    }
}

