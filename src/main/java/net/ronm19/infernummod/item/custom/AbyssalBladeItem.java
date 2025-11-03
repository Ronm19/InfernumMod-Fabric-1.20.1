package net.ronm19.infernummod.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbyssalBladeItem extends SwordItem {

    public AbyssalBladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    // ---------------------- COMBAT EFFECT ----------------------
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = target.getWorld();

        // Fire damage & wither
        target.setOnFireFor(3);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));

        // Lifesteal: small heal to attacker
        if (!world.isClient) {
            attacker.heal(2.0F); // heals 1 heart
        }

        // Play infernal sound
        world.playSound(null, target.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.PLAYERS, 1.0F, 0.6F + world.random.nextFloat() * 0.4F);

        // Show flame particles
        if (world.isClient) {
            for (int i = 0; i < 6; i++) {
                world.addParticle(ParticleTypes.FLAME,
                        target.getParticleX(0.4), target.getBodyY(0.5), target.getParticleZ(0.4),
                        0.0, 0.02, 0.0);
            }
        }

        return super.postHit(stack, target, attacker);
    }

    // ---------------------- PARTICLE EFFECT WHILE HELD ----------------------
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient && selected) {
            double x = entity.getX();
            double y = entity.getBodyY(0.6);
            double z = entity.getZ();
            if (world.random.nextFloat() < 0.25F) {
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.01, 0);
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.01, 0);
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    // ---------------------- TOOLTIP ----------------------
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Forged from the core of the Infernum.").formatted(Formatting.DARK_RED));
        tooltip.add(Text.literal("Illegal under Malfuryx’s law.").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.literal("§4☠ §7Strikes burn and corrupt souls."));
        super.appendTooltip(stack, world, tooltip, context);
    }

    // ---------------------- VISUAL EFFECT ----------------------
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // gives enchanted shimmer even without enchantments
    }
}
