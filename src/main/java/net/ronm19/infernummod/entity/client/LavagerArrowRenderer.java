package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.LavagerArrowEntity;

public class LavagerArrowRenderer extends ArrowEntityRenderer {
    private static final Identifier TEXTURE =
            new Identifier("infernummod", "textures/entity/projectiles/lavager_arrow.png");

    public LavagerArrowRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(LavagerArrowEntity entity) {
        // Cast because ArrowEntityRenderer is non-generic now
        return TEXTURE;
    }
}