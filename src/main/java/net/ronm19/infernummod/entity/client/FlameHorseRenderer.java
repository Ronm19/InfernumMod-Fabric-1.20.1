package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.FlameHorseEntity;

public class FlameHorseRenderer extends MobEntityRenderer<FlameHorseEntity, HorseEntityModel<FlameHorseEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/flame_horse.png");

    public FlameHorseRenderer( EntityRendererFactory.Context context) {
        super(context, new HorseEntityModel<>(context.getPart(EntityModelLayers.HORSE)), 1.1F);
    }

    @Override
    public Identifier getTexture(FlameHorseEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render( FlameHorseEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
