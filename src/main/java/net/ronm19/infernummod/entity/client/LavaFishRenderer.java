package net.ronm19.infernummod.entity.client;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.LavaFishEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class LavaFishRenderer extends MobEntityRenderer<LavaFishEntity, LavaFishModel<LavaFishEntity>> {

    // Path to your texture
    private static final Identifier TEXTURE =
            new Identifier(InfernumMod.MOD_ID, "textures/entity/lava_fish.png");

    public LavaFishRenderer(EntityRendererFactory.Context context) {
        // Pass in the model with its baked layer and shadow size
        super(context, new LavaFishModel<>(context.getPart(ModModelLayers.LAVA_FISH)), 0.2f);
    }

    @Override
    public Identifier getTexture(LavaFishEntity entity) {
        return TEXTURE;
    }
}
