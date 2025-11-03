package net.ronm19.infernummod.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HellCrownItem extends ArmorItem {
    public HellCrownItem( ArmorMaterial material, Type type, Settings settings ) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof net.minecraft.entity.LivingEntity living)) return;

        boolean worn = living.getEquippedStack(EquipmentSlot.HEAD) == stack;
        if (worn) {
            if (!world.isClient) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 220, 2, true, false));
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 220, 0, true, false));

                // Corruption every 60 s
                if (world.getTime() % 1200 == 0) {
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200, 0));
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
                }
            } else {
                // Soul-fire particles around head
                // inside inventoryTick() or client tick
                if (world.isClient && worn && world.random.nextFloat() < 0.5F) {
                    for (int i = 0; i < 8; i++) { // more particles = bigger flame
                        world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                                living.getX(),
                                living.getBodyY(1.0 + (i * 0.05)), // raises the fire upward
                                living.getZ(),
                                0.0, 0.03, 0.0);
                    }
                }

            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public boolean hasGlint(ItemStack stack) { return true; }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Crown of the Infernal Tyrant.").formatted(Formatting.DARK_RED));
        tooltip.add(Text.literal("Wearing it grants power... and damnation.").formatted(Formatting.GRAY, Formatting.ITALIC));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
