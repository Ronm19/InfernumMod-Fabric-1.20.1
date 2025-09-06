package net.ronm19.infernummod.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.ronm19.infernummod.item.ModArmorMaterials;

import java.util.List;
import java.util.Map;

public class ModArmorItem extends ArmorItem {
    private static final Map<ArmorMaterial, List<StatusEffectInstance>> MATERIAL_TO_EFFECTS_MAP =
            new ImmutableMap.Builder<ArmorMaterial, List<StatusEffectInstance>>()
                    .put(ModArmorMaterials.NETHER_RUBY, List.of(
                            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 0, false, false, true),
                            new StatusEffectInstance(StatusEffects.STRENGTH, 900, 0, false, false, true)
                    ))
                    .put(ModArmorMaterials.EMBERSTONE, List.of(
                            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 0, false, false, true),
                            new StatusEffectInstance(StatusEffects.STRENGTH, 900, 0, false, false, true)
                    ))
                    .put(ModArmorMaterials.CINDERSTONE, List.of(
                            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 999999, 0, false, false, true),
                            new StatusEffectInstance(StatusEffects.STRENGTH, 900, 0, false, false, true)
                    ))
                    .put(ModArmorMaterials.INFERNIUM, List.of(
                            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 1, false, false, true),
                            new StatusEffectInstance(StatusEffects.STRENGTH, 900, 0, false, false, true),
                            new StatusEffectInstance(StatusEffects.RESISTANCE, 900, 0, false, false, true)
                    ))
                    .put(ModArmorMaterials.PYROCLAST, List.of(
                            new StatusEffectInstance(StatusEffects.STRENGTH, 900, 1, false, false, true),
                            new StatusEffectInstance(StatusEffects.RESISTANCE, 900, 1, false, false, true),
                            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 1, false, false, true)
                    ))
                    .put(ModArmorMaterials.FIRERITE, List.of(
                            new StatusEffectInstance(StatusEffects.RESISTANCE, 900, 1, false, false, true),
                            new StatusEffectInstance(StatusEffects.STRENGTH, 900, 1, false, false, true),
                            new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1, false, false, true),
                            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 1, false, false, true)
                    ))
                    .build();

    public ModArmorItem( ArmorMaterial material, Type type, Settings settings ) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick( ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient()) {
            if(entity instanceof PlayerEntity player && hasFullSuitOfArmorOn(player)) {
                evaluateArmorEffects(player);
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void evaluateArmorEffects(PlayerEntity player) {
        for (Map.Entry<ArmorMaterial, List<StatusEffectInstance>> entry : MATERIAL_TO_EFFECTS_MAP.entrySet()) {
            ArmorMaterial mapArmorMaterial = entry.getKey();
            List<StatusEffectInstance> effects = entry.getValue();

            if (hasCorrectArmorOn(mapArmorMaterial, player)) {
                addStatusEffectsForMaterial(player, mapArmorMaterial, effects);
            }
        }
    }

    private void addStatusEffectsForMaterial(PlayerEntity player, ArmorMaterial mapArmorMaterial, List<StatusEffectInstance> effects) {
        for (StatusEffectInstance effect : effects) {
            boolean hasPlayerEffect = player.hasStatusEffect(effect.getEffectType());

            if (hasCorrectArmorOn(mapArmorMaterial, player) && !hasPlayerEffect) {
                player.addStatusEffect(new StatusEffectInstance(effect));
            }
        }
    }

    private boolean hasFullSuitOfArmorOn(PlayerEntity player) {
        ItemStack boots = player.getInventory().getArmorStack(0);
        ItemStack leggings = player.getInventory().getArmorStack(1);
        ItemStack breastplate = player.getInventory().getArmorStack(2);
        ItemStack helmet = player.getInventory().getArmorStack(3);

        return !helmet.isEmpty() && !breastplate.isEmpty()
                && !leggings.isEmpty() && !boots.isEmpty();
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, PlayerEntity player) {
        for (ItemStack armorStack: player.getInventory().armor) {
            if(!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }

        ArmorItem boots = ((ArmorItem)player.getInventory().getArmorStack(0).getItem());
        ArmorItem leggings = ((ArmorItem)player.getInventory().getArmorStack(1).getItem());
        ArmorItem breastplate = ((ArmorItem)player.getInventory().getArmorStack(2).getItem());
        ArmorItem helmet = ((ArmorItem)player.getInventory().getArmorStack(3).getItem());

        return helmet.getMaterial() == material && breastplate.getMaterial() == material &&
                leggings.getMaterial() == material && boots.getMaterial() == material;
    }
}
