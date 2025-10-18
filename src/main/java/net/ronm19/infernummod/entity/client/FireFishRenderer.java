package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.FireFishEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class FireFishRenderer extends MobEntityRenderer<FireFishEntity, SalmonEntityModel<FireFishEntity>> {

    // Path to your texture
    private static final Identifier TEXTURE =
            new Identifier(InfernumMod.MOD_ID, "textures/entity/fire_fish.png");

    public FireFishRenderer( EntityRendererFactory.Context context) {
        // Pass in the model with its baked layer and shadow size
        super(context, new SalmonEntityModel<>(context.getPart(EntityModelLayers.SALMON)), 0.2f);
    }

    @Override
    public Identifier getTexture(FireFishEntity entity) {
        return TEXTURE;
    }
}