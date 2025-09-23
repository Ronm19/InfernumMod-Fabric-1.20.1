package net.ronm19.infernummod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.StrikingFire;

public class FireballStrikerEnchantment extends Enchantment implements StrikingFire {

    protected FireballStrikerEnchantment( Rarity rarity, EnchantmentTarget target, EquipmentSlot... slotTypes ) {
        super(rarity, target, slotTypes);
    }


    public void shootFireballsInAir( PlayerEntity player, int level ) {
        if (!(player.getWorld() instanceof ServerWorld world)) return;

        Vec3d look = player.getRotationVec(1.0F);

        // Base offsets for fireball spread (in radians)
        float[] offsets;
        if (level == 1) {
            offsets = new float[]{0f}; // Single fireball, straight
        } else if (level == 2) {
            offsets = new float[]{-0.05f, 0.05f}; // Two fireballs, slightly left & right
        } else { // level 3
            offsets = new float[]{-0.08f, 0f, 0.08f}; // Three fireballs, small fan
        }

        for (float yawOffset : offsets) {
            // Rotate the look vector around Y axis for spread
            double sin = Math.sin(yawOffset);
            double cos = Math.cos(yawOffset);
            Vec3d direction = new Vec3d(
                    look.x * cos - look.z * sin,
                    look.y,
                    look.x * sin + look.z * cos
            );

            SmallFireballEntity fireball = new SmallFireballEntity(world, player, direction.x, direction.y, direction.z);
            fireball.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            world.spawnEntity(fireball);
        }

        player.swingHand(player.getActiveHand(), true);
    }


    @Override
    public TypedActionResult<ItemStack> use( World world, PlayerEntity user, Hand hand ) {
        ItemStack stack = user.getStackInHand(hand);
        int level = EnchantmentHelper.getLevel(ModEnchantments.FIREBALL_STRIKER, stack);

        if (level > 0 && world instanceof ServerWorld serverWorld) {
            Vec3d look = user.getRotationVec(1.0F);

            for (int i = 0; i < level; i++) {
                float angleOffset = 0f;
                if (level == 2) angleOffset = (i == 0) ? -15f : 15f;
                else if (level == 3) angleOffset = (i - 1) * 15f;

                double rad = Math.toRadians(angleOffset);
                double cos = Math.cos(rad);
                double sin = Math.sin(rad);
                double rotatedX = look.x * cos - look.z * sin;
                double rotatedZ = look.x * sin + look.z * cos;

                SmallFireballEntity fireball = new SmallFireballEntity(serverWorld, user, rotatedX, look.y, rotatedZ);
                fireball.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());
                serverWorld.spawnEntity(fireball);

                // Decrease durability **per fireball**
                if (!user.isCreative()) {
                    stack.damage(1, user, e -> e.sendToolBreakStatus(hand));
                }
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_BLAZE_SHOOT,
                    SoundCategory.PLAYERS,
                    1.0f, 1.0f);

            user.swingHand(hand, true);
        }

        return TypedActionResult.success(stack, world.isClient());
    }


    @Override
    public boolean postHit( ItemStack stack, LivingEntity target, LivingEntity attacker ) {
        if (attacker instanceof PlayerEntity player) {
            int level = EnchantmentHelper.getLevel(ModEnchantments.FIREBALL_STRIKER, stack);

            if (level > 0 && player.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d look = player.getRotationVec(1.0F);

                int fireballCount = MathHelper.clamp(level, 1, 3);
                for (int i = 0; i < fireballCount; i++) {
                    SmallFireballEntity fireball = new SmallFireballEntity(serverWorld, player, look.x, look.y, look.z);
                    fireball.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                    serverWorld.spawnEntity(fireball);
                }

                player.swingHand(player.getActiveHand(), true);

                stack.damage(1, player, e -> e.sendToolBreakStatus(player.getActiveHand()));
            }
            target.setOnFireFor(5);
        }
        return postHit(stack, target, attacker);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
