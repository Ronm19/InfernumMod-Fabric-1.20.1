package net.ronm19.infernummod.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.effect.ModEffects;

public class ModPotions {

    public static final Potion BLAZING_HEART_POTION = registerPotion("blazing_heart_potion",
            new Potion(new StatusEffectInstance(ModEffects.BLAZING_HEART, 600, 0)));

    public static final Potion LAVA_VISION_POTION = registerPotion("lava_vision_potion",
            new Potion(new StatusEffectInstance(ModEffects.LAVA_VISION, 600, 0)));

    public static Potion registerPotion(String name, Potion potion) {
        return Registry.register(Registries.POTION, new Identifier(InfernumMod.MOD_ID, name), potion);
    }

    public static void registerPotions() {
        InfernumMod.LOGGER.info("Registering Potions for " + InfernumMod.MOD_ID);
    }
}
