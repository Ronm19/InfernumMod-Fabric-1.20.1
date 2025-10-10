package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.MagmaDolphinEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class MagmaDolphinRenderer extends MobEntityRenderer<MagmaDolphinEntity, MagmaDolphinModel<MagmaDolphinEntity>> {

    private static final Identifier TEXTURE =
            new Identifier(InfernumMod.MOD_ID, "textures/entity/magma_dolphin.png");

    public MagmaDolphinRenderer( EntityRendererFactory.Context context) {
        super(context, new MagmaDolphinModel<>(context.getPart(ModModelLayers.MAGMA_DOLPHIN)), 0.7F);
    }

    @Override
    public Identifier getTexture( MagmaDolphinEntity entity ) {
        return TEXTURE;
    }
}
