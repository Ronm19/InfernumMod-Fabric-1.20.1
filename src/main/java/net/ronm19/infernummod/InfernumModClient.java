package net.ronm19.infernummod;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import net.minecraft.fluid.Fluids;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.client.render.LavaVisionFluidHandler;
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
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.OBSIDIAN_GHAST, ObsidianGhastModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNAL_HOARDE, InfernalHoardeModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNO_ZOMBIE, InfernoZombieModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.FLAME_SKELETON, FlameSkeletonModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNAL_VEX, InfernalVexModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LAVACATOR, LavacatorModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LAVA_WITCH, LavaWitchModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MAGMA_CREEPER, MagmaCreeperModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MAGMA_STRIDER, MagmaStriderModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.EMBER_SERPENT, EmberSerpentModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNO_ENDERMAN, InfernoEndermanModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNAL_BEAST, InfernalBeastModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNUM_HEROBRINE, InfernumHerobrineModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNUM, InfernumModel::getTexturedModelData);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNAL_KNIGHT, InfernalKnightModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PYERLING_WYRN, PyerlingWyrnModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNAL_PHANTOM, InfernalPhantomModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.INFERNAL_EYE, InfernalEyeModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.ASH_CHICKEN, AshChickenModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.EMBER_HUND, EmberHundModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.EMBER_BOAR, EmberBoarModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LAVA_COW, LavaCowModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.VOLCARNIS, VolcarnisModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SCORCHED_WOOLIE, ScorchedWoolieModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SCORCHED_WOOLIE_FUR, ScorchedWoolieWoolModel::getTexturedModelData);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LAVA_FISH, LavaFishModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MAGMA_FISH, MagmaFishModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.FIRE_FISH, FireFishModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MAGMA_DOLPHIN, MagmaDolphinModel::getTexturedModelData);


        EntityRendererRegistry.register(ModEntities.DEMON, DemonRenderer ::new);
        EntityRendererRegistry.register(ModEntities.MALFURYX, MalfuryxRenderer ::new);
        EntityRendererRegistry.register(ModEntities.OBSIDIAN_GHAST, ObsidianGhastRenderer ::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_HOARDE, InfernalHoardeRenderer ::new);
        EntityRendererRegistry.register(ModEntities.INFERNO_ZOMBIE, InfernoZombieRenderer::new);
        EntityRendererRegistry.register(ModEntities.FLAME_SKELETON, FlameSkeletonRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_VEX, InfernalVexRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_WRAITH, InfernalWraithRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_ZOMBILAGER, InfernalZombilagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVACATOR, LavacatorRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVAGER, LavagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_VOKER, InfernalVokerRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVA_WITCH, LavaWitchRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVA_SLIME, LavaSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGMA_CREEPER, MagmaCreeperRenderer ::new);
        EntityRendererRegistry.register(ModEntities.INFERNO_ENDERMAN, InfernoEndermanRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGMA_STRIDER, MagmaStriderRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGMA_SPIDER, MagmaSpiderRenderer::new);
        EntityRendererRegistry.register(ModEntities.EMBER_SERPENT, EmberSerpentRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_BEAST, InfernalBeastRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNUM_HEROBRINE, InfernumHerobrineRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNUM, InfernumRenderer::new);

        EntityRendererRegistry.register(ModEntities.INFERNAL_KNIGHT, InfernalKnightRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_EYE, InfernalEyeRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_HORSE, InfernalHorseRenderer::new);
        EntityRendererRegistry.register(ModEntities.ASHBONE_HORSE, AshboneHorseRenderer::new);
        EntityRendererRegistry.register(ModEntities.FLAME_HORSE, FlameHorseRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGMA_BEAR, MagmaBearRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_PHANTOM, InfernalPhantomRenderer::new);
        EntityRendererRegistry.register(ModEntities.EMBER_HUND, EmberHundRenderer::new);
        EntityRendererRegistry.register(ModEntities.VOLCARNIS, VolcarnisRenderer::new);
        EntityRendererRegistry.register(ModEntities.PYERLING_WYRN, PyerlingWyrnRenderer ::new);
        EntityRendererRegistry.register(ModEntities.SCORCHED_WOOLIE, ScorchedWoolieRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_RABBIT, InfernalRabbitRenderer::new);
        EntityRendererRegistry.register(ModEntities.ASH_CHICKEN, AshChickenRenderer::new);
        EntityRendererRegistry.register(ModEntities.EMBER_BOAR, EmberBoarRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVA_COW, LavaCowRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVA_FISH, LavaFishRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGMA_FISH, MagmaFishRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIRE_FISH, FireFishRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGMA_DOLPHIN, MagmaDolphinRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGMA_GOLEM, MagmaGolemRenderer::new);



        TerraformBoatClientHelper.registerModelLayers(ModBoats.INFERNO_BOAT_ID, false);

        EntityRendererRegistry.register(ModEntities.ASH_DUST_PROJECTILE, FlyingItemEntityRenderer ::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_SKULL, InfernalSkullRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVAGER_ARROW, LavagerArrowRenderer::new);
        EntityRendererRegistry.register(ModEntities.ASH_EGG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_LIGHTNING, LightningEntityRenderer::new);

        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.LAVA, new LavaVisionFluidHandler());


    }
}
