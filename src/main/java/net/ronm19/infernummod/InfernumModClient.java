package net.ronm19.infernummod;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.entity.ModBoats;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.client.*;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

public class InfernumModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.NETHER_RUBY_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.NETHER_RUBY_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FIRERITE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FIRERITE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFERNO_ESSENCE_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFERNO_ESSENCE_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_INFERNO_ESSENCE_SAPLING, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLAZE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLAZE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EMBERSTONE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EMBERSTONE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFERNAL_BRICKS_STONE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFERNAL_BRICKS_STONE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOLTEN_BRICKS_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOLTEN_BRICKS_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOLTEN_STONE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOLTEN_STONE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFERNAL_STONE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFERNAL_STONE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFERNO_ESSENCE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFERNO_ESSENCE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLAZEBLOOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_BLAZEBLOOM, RenderLayer.getCutout());

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.DEMON, DemonModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MALFURYX, MalfuryxModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PYERLING_WYRN, PyerlingWyrnModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.OBSIDIAN_GHAST, ObsidianGhastModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNAL_HOARDE, InfernalHoardeModel::getTexturedModelData);

        
        EntityRendererRegistry.register(ModEntities.DEMON, DemonRenderer ::new);
        EntityRendererRegistry.register(ModEntities.MALFURYX, MalfuryxRenderer ::new);
        EntityRendererRegistry.register(ModEntities.PYERLING_WYRN, PyerlingWyrnRenderer ::new);
        EntityRendererRegistry.register(ModEntities.OBSIDIAN_GHAST, ObsidianGhastRenderer ::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_HOARDE, InfernalHoardeRenderer ::new);

        TerraformBoatClientHelper.registerModelLayers(ModBoats.INFERNO_BOAT_ID, false);

        EntityRendererRegistry.register(ModEntities.ASH_DUST_PROJECTILE, FlyingItemEntityRenderer ::new);



    }
}
