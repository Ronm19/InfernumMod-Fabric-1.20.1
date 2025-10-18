package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernoZombieEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernoZombieRenderer  extends MobEntityRenderer<InfernoZombieEntity, InfernoZombieModel<InfernoZombieEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/inferno_zombie.png");

    public InfernoZombieRenderer( EntityRendererFactory.Context context) {
        super(context, new InfernoZombieModel<>(context.getPart(ModModelLayers.INFERNO_ZOMBIE)), 0.7f);
    }

    @Override
    public Identifier getTexture( InfernoZombieEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render(InfernoZombieEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
