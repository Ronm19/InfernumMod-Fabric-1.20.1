package net.ronm19.infernummod.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ronm19.infernummod.sound.ModSounds;

import java.util.List;

public class InfernumSwordItem extends SwordItem {

    private static final double REACH_DISTANCE = 5.5D;   // Attack reach radius
    private static final float KNOCKBACK_STRENGTH = 2.8F;
    private static final int COOLDOWN_TICKS = 25;        // 1.25 seconds cooldown

    public InfernumSwordItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();

        if (!world.isClient && attacker instanceof PlayerEntity player) {

            // Knockback and flame hit
            Vec3d dir = player.getRotationVec(1.0F);
            target.takeKnockback(KNOCKBACK_STRENGTH, -dir.x, -dir.z);
            target.setOnFireFor(4);

            // Add cooldown
            player.getItemCooldownManager().set(this, COOLDOWN_TICKS);

            // AoE impact wave
            createShockwave(world, player);

            // Handle flying entities (extra reach zone)
            handleFlyingEntities(world, player);

            if (world instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.LAVA, target.getX(), target.getY() + 0.6, target.getZ(),
                        15, 0.3, 0.3, 0.3, 0.02);
                sw.spawnParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(),
                        2, 0.2, 0.2, 0.2, 0.02);
            }

            // Durability loss
            stack.damage(1, player, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
        }

        return super.postHit(stack, target, attacker);
    }

    // --- Shockwave logic ---
    private void createShockwave(World world, PlayerEntity player) {
        Vec3d pos = player.getPos();
        double radius = 3.5;

        List<LivingEntity> nearby = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(pos.add(-radius, -1, -radius), pos.add(radius, 1.5, radius)),
                e -> e.isAlive() && e != player
        );

        for (LivingEntity e : nearby) {
            if (player.canSee(e)) {
                double dx = e.getX() - player.getX();
                double dz = e.getZ() - player.getZ();
                e.takeKnockback(KNOCKBACK_STRENGTH * 0.8, -dx, -dz);
                e.damage(world.getDamageSources().playerAttack(player), 6.0F);
            }
        }

        if (world instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y + 1, pos.z, 25, 1, 0.3, 1, 0.04);
            sw.spawnParticles(ParticleTypes.SMOKE, pos.x, pos.y + 1, pos.z, 10, 0.6, 0.4, 0.6, 0.02);
        }
    }

    // --- Handles flying entities above player ---
    private void handleFlyingEntities(World world, PlayerEntity player) {
        Vec3d look = player.getRotationVec(1.0F);
        Vec3d center = player.getPos().add(look.multiply(REACH_DISTANCE));

        List<LivingEntity> flyingTargets = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(center.add(-2, -2, -2), center.add(2, 2, 2)),
                e -> e.isAlive() && e != player && e.getY() > player.getY() + 1.0
        );

        for (LivingEntity mob : flyingTargets) {
            mob.damage(world.getDamageSources().playerAttack(player), 10.0F);
            mob.addVelocity(0, -0.6, 0); // Slam downward
            mob.takeKnockback(1.5F, -look.x, -look.z);

            if (world instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        mob.getX(), mob.getY(), mob.getZ(), 10, 0.3, 0.3, 0.3, 0.01);
            }

        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Always glows
    }
}
