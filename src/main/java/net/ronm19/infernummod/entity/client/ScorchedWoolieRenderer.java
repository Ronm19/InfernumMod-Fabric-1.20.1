package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.PyerlingWyrnEntity;
import net.ronm19.infernummod.entity.custom.ScorchedWoolieEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class ScorchedWoolieRenderer extends MobEntityRenderer<ScorchedWoolieEntity, SheepEntityModel<ScorchedWoolieEntity>> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/scorchedwoolie.png");

    public ScorchedWoolieRenderer( EntityRendererFactory.Context context) {
        super(context, new SheepEntityModel<>(context.getPart(EntityModelLayers.SHEEP)), 0.7F);
        this.addFeature(new ScorchedWoolieWoolFeatureRenderer(this, context.getModelLoader()));
    }

    @Override
    public Identifier getTexture( ScorchedWoolieEntity entity ) {
        return TEXTURE;
    }

    @Override
    public void render( ScorchedWoolieEntity livingEntity, float f, float g, MatrixStack matrixStack,
                        VertexConsumerProvider vertexConsumerProvider, int i ) {

        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
