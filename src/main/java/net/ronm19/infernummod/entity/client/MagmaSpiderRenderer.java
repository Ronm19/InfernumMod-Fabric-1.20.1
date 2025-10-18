package net.ronm19.infernummod.entity.client;


import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.MagmaSpiderEntity;

public class MagmaSpiderRenderer extends SpiderEntityRenderer<MagmaSpiderEntity> {

    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/magma_spider.png");

    public MagmaSpiderRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.SPIDER);
    }

    @Override
    public Identifier getTexture(MagmaSpiderEntity entity) {
        return TEXTURE;
    }
}
