package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.ModEntities;

import java.util.Map;
@Environment(EnvType.CLIENT)
public class AshboneHorseRenderer extends AbstractHorseEntityRenderer<
        AbstractHorseEntity, HorseEntityModel<AbstractHorseEntity>> {

    private static final Map<EntityType<?>, Identifier> TEXTURE =
            Map.of(ModEntities.ASHBONE_HORSE, new Identifier("infernummod", "textures/entity/ashbone_horse.png"));

    public AshboneHorseRenderer(EntityRendererFactory.Context ctx) {
        // use SKELETON_HORSE model layer for bone structure
        super(ctx, new HorseEntityModel<>(ctx.getPart(EntityModelLayers.SKELETON_HORSE)), 1.0F);
    }

    @Override
    public Identifier getTexture(AbstractHorseEntity entity) {
        // Fallback protection just in case something goes wrong with the map
        return TEXTURE.getOrDefault(entity.getType(),
                new Identifier("infernummod", "textures/entity/ashbone_horse.png"));
    }
}
