package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.InfernalZombilagerEntity;

public class InfernalZombilagerRenderer extends BipedEntityRenderer<InfernalZombilagerEntity, ZombieVillagerEntityModel<InfernalZombilagerEntity>> {
    private static final Identifier TEXTURE = new Identifier("infernummod", "textures/entity/infernal_zombilager.png");

    public InfernalZombilagerRenderer(EntityRendererFactory.Context context) {
        super(context, new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), 0.5F);

        // 🧱 Adds zombie villager armor and clothing layers
        this.addFeature(new ArmorFeatureRenderer<>(
                this,
                new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)),
                new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)),
                context.getModelManager()));

        this.addFeature(new VillagerClothingFeatureRenderer<>(this, context.getResourceManager(), "zombie_villager"));
    }

    @Override
    public Identifier getTexture(InfernalZombilagerEntity entity) {
        return TEXTURE;
    }

    @Override
    public boolean isShaking(InfernalZombilagerEntity entity) {
        return super.isShaking(entity) || entity.isConverting();
    }
}
