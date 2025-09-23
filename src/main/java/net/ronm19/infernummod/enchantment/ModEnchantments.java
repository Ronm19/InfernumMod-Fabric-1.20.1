package net.ronm19.infernummod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;

public class ModEnchantments {

    public static final Enchantment FIREBALL_STRIKER = register("fireball_striker",
            new FireballStrikerEnchantment(Enchantment.Rarity.COMMON,
                    EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND));

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(InfernumMod.MOD_ID, name), enchantment);
    }


    public static void registerModEnchantments() {
        InfernumMod.LOGGER.info("Registering ModEnchantments for " + InfernumMod.MOD_ID);
    }
}
