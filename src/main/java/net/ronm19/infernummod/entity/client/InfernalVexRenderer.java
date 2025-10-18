package net.ronm19.infernummod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.InfernalVexEntity;
import net.ronm19.infernummod.entity.layer.ModModelLayers;

@Environment(EnvType.CLIENT)
public class InfernalVexRenderer extends MobEntityRenderer<InfernalVexEntity, InfernalVexModel> {
    private static final Identifier TEXTURE = new Identifier(InfernumMod.MOD_ID, "textures/entity/infernal_vex.png");
    private static final Identifier CHARGING_TEXTURE =  new Identifier(InfernumMod.MOD_ID, "textures/entity/infernal_vex_charging.png");

    public InfernalVexRenderer( EntityRendererFactory.Context context) {
        super(context, new InfernalVexModel(context.getPart(ModModelLayers.INFERNAL_VEX)), 0.3F);
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    protected int getBlockLight(InfernalVexEntity infernalVexEntity, BlockPos blockPos) {
        return 15;
    }

    public Identifier getTexture(InfernalVexEntity infernalVexEntity) {
        return infernalVexEntity.isCharging() ? CHARGING_TEXTURE : TEXTURE;
    }
}
