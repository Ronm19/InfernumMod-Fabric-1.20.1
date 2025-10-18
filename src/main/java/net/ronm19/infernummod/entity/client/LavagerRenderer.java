package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.LavagerEntity;

@Environment(EnvType.CLIENT)
public class LavagerRenderer extends IllagerEntityRenderer<LavagerEntity> {
    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/lavager.png");

    public LavagerRenderer( EntityRendererFactory.Context context ) {
        super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.PILLAGER)), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    public Identifier getTexture( LavagerEntity lavagerEntity ) {
        return TEXTURE;
    }
}