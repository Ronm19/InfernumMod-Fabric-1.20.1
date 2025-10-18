package net.ronm19.infernummod.entity.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.InfernalHorseEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class InfernalHorseRenderer extends AbstractHorseEntityRenderer<
        AbstractHorseEntity, HorseEntityModel<AbstractHorseEntity>> {

    private static final Map<EntityType<?>, Identifier> TEXTURE =
            Map.of(ModEntities.INFERNAL_HORSE, new Identifier("infernummod", "textures/entity/infernal_horse.png"));

    public InfernalHorseRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new HorseEntityModel<>(ctx.getPart(EntityModelLayers.HORSE)), 1.0F);
    }

    @Override
    public Identifier getTexture(AbstractHorseEntity entity) {
        return TEXTURE.get(entity.getType());
    }
}



