package net.ronm19.infernummod.api.interfaces;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ronm19.infernummod.enchantment.ModEnchantments;

public interface StrikingFire {

    TypedActionResult<ItemStack> use( World world, PlayerEntity user, Hand hand );

    public default boolean postHit( ItemStack stack, LivingEntity target, LivingEntity attacker ) {
        if (!(attacker instanceof PlayerEntity player)) return false;

        int level = EnchantmentHelper.getLevel(ModEnchantments.FIREBALL_STRIKER, stack);
        if (level <= 0 || !(player.getWorld() instanceof ServerWorld world)) return false;

        // Spawn fireballs based on enchantment level
        Vec3d look = player.getRotationVec(1.0F);
        int fireballCount = MathHelper.clamp(level, 1, 3);

        for (int i = 0; i < fireballCount; i++) {
            SmallFireballEntity fireball = new SmallFireballEntity(world, player, look.x, look.y, look.z);
            fireball.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            world.spawnEntity(fireball);
        }

        // Optional: swing hand for visual effect
        player.swingHand(player.getActiveHand(), true);
        return false;
    }
}