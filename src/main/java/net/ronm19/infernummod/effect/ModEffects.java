package net.ronm19.infernummod.effect;

import jdk.dynalink.Operation;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;

import java.util.UUID;

public class ModEffects {

    public static final StatusEffect BLAZING_HEART = registerStatusEffect("blazing_heart",
            new BlazingHeartEffect(StatusEffectCategory.BENEFICIAL, 0xFF4500))
            .addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH,
                    "7107DE5E-7CE8-4030-940E-514C1F160890", 4.0, EntityAttributeModifier.Operation.ADDITION)
            .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    "7107DE5E-7CE8-4030-940E-514C1F160890", 2.0, EntityAttributeModifier.Operation.ADDITION)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    "7107DE5E-7CE8-4030-940E-514C1F160890", 0.1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    public static final StatusEffect LAVA_VISION = registerStatusEffect("lava_vision",
            new LavaVisionEffect(StatusEffectCategory.BENEFICIAL, 0xFF4C00)
                    .addAttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            "b8b7c95f-81cb-4e69-9cc4-778a2db87456", // random UUID
                            0.15D, // +15% movement
                            EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    ));


    private static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(InfernumMod.MOD_ID, name), statusEffect);
    }

    public static void registerEffects() {
        InfernumMod.LOGGER.info("Registering Mod Effects for " + InfernumMod.MOD_ID);
    }

}
