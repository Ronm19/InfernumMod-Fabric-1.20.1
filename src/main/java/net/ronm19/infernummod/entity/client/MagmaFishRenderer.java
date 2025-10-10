package net.ronm19.infernummod.entity.client;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.LavaFishEntity;
import net.ronm19.infernummod.entity.custom.MagmaFishEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class MagmaFishRenderer extends MobEntityRenderer<MagmaFishEntity, MagmaFishModel<MagmaFishEntity>> {

    // Path to your texture
    private static final Identifier TEXTURE =
            new Identifier(InfernumMod.MOD_ID, "textures/entity/magma_fish.png");

    public MagmaFishRenderer(EntityRendererFactory.Context context) {
        // Pass in the model with its baked layer and shadow size
        super(context, new MagmaFishModel<>(context.getPart(ModModelLayers.MAGMA_FISH)), 0.2f);
    }

    @Override
    public Identifier getTexture(MagmaFishEntity entity) {
        return TEXTURE;
    }
}
