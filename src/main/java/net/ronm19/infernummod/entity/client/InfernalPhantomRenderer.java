package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernalEyeEntity;
import net.ronm19.infernummod.entity.custom.InfernalPhantomEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernalPhantomRenderer extends MobEntityRenderer<InfernalPhantomEntity, InfernalPhantomModel<InfernalPhantomEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/infernal_phantom.png");

    public InfernalPhantomRenderer( EntityRendererFactory.Context context) {
        super(context, new InfernalPhantomModel<>(context.getPart(ModModelLayers.INFERNAL_PHANTOM)), 0.4f);
    }

    @Override
    public Identifier getTexture( InfernalPhantomEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( InfernalPhantomEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i ) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
