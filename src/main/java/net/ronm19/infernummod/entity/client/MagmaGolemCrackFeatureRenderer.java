package net.ronm19.infernummod.entity.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.entity.custom.MagmaGolemEntity;

import java.util.Map;

public class MagmaGolemCrackFeatureRenderer extends FeatureRenderer<MagmaGolemEntity, IronGolemEntityModel<MagmaGolemEntity>> {
    private static final Map<IronGolemEntity.Crack, Identifier> DAMAGE_TO_TEXTURE;

    public MagmaGolemCrackFeatureRenderer( FeatureRendererContext<MagmaGolemEntity, IronGolemEntityModel<MagmaGolemEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render( MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, MagmaGolemEntity magmaGolemEntity, float f, float g, float h, float j, float k, float l) {
        if (!magmaGolemEntity.isInvisible()) {
            IronGolemEntity.Crack crack = magmaGolemEntity.getCrack();
            if (crack != IronGolemEntity.Crack.NONE) {
                Identifier identifier = (Identifier)DAMAGE_TO_TEXTURE.get(crack);
                renderModel(this.getContextModel(), identifier, matrixStack, vertexConsumerProvider, i, magmaGolemEntity, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    static {
        DAMAGE_TO_TEXTURE = ImmutableMap.of(IronGolemEntity.Crack.LOW, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_low.png"), IronGolemEntity.Crack.MEDIUM, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), IronGolemEntity.Crack.HIGH, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_high.png"));
    }
}
