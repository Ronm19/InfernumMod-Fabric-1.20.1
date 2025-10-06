package net.ronm19.infernummod.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.custom.InfernalSkullEntity;

public class InfernalStaffItem extends Item {

    private static final int    EXPLOSION_POWER = 5;       // balanced blast power
    private static final float  VELOCITY        = 6.5F;    // stable speed
    private static final float  INACCURACY      = 0.0F;    // 100% accurate

    public InfernalStaffItem(Settings settings) {
        super(settings);
    }

    // === Right-click in air ===
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && world instanceof ServerWorld serverWorld) {

            int skullCount = MathHelper.clamp(1, 1, 3);
            float spreadDegrees = 12f;
            Vec3d look = user.getRotationVec(1.0F);

            for (int i = 0; i < skullCount; i++) {
                float angleOffset = 0f;
                if (skullCount == 2) angleOffset = (i == 0) ? -spreadDegrees : spreadDegrees;
                else if (skullCount == 3) angleOffset = (i - 1) * spreadDegrees;

                double rad = Math.toRadians(angleOffset);
                double cos = Math.cos(rad);
                double sin = Math.sin(rad);
                double rotatedX = look.x * cos - look.z * sin;
                double rotatedZ = look.x * sin + look.z * cos;

                // --- Vanilla Wither Skull, accurate and safe ---
                WitherSkullEntity skull = new WitherSkullEntity(serverWorld, user, rotatedX, look.y, rotatedZ);

                // Spawn slightly forward to avoid hitting self
                Vec3d forward = look.normalize().multiply(1.5);
                skull.setPos(user.getX() + forward.x,
                        user.getEyeY() - 0.1 + forward.y,
                        user.getZ() + forward.z);

                // Perfect accuracy, stable velocity
                skull.setVelocity(rotatedX, look.y, rotatedZ, VELOCITY, INACCURACY);
                skull.setCharged(false);
                serverWorld.spawnEntity(skull);

                if (!user.isCreative()) {
                    stack.damage(1, user, e -> e.sendToolBreakStatus(hand));
                }
            }

            // Sound + cooldown
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_WITHER_SHOOT,
                    SoundCategory.PLAYERS,
                    1.0f,
                    0.9f + world.random.nextFloat() * 0.2f);

            user.swingHand(hand, true);
            user.getItemCooldownManager().set(this, 8); // ~0.4 s cooldown
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    // === Right-click on entity (targeted shot) ===
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity target, Hand hand) {
        if (!user.getWorld().isClient && user.getWorld() instanceof ServerWorld serverWorld) {

            double dx = target.getX() - user.getX();
            double dy = target.getBodyY(0.5) - user.getEyeY();
            double dz = target.getZ() - user.getZ();

            // --- Stronger Infernal Skull for targeted mode ---
            InfernalSkullEntity skull = new InfernalSkullEntity(serverWorld, user, dx, dy, dz);
            skull.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());
            skull.setInfernalVelocity(dx, dy, dz, VELOCITY, 0.0F);
            skull.setExplosionPower(EXPLOSION_POWER);
            skull.setCharged(true);
            serverWorld.spawnEntity(skull);

            serverWorld.playSound(null, user.getBlockPos(),
                    SoundEvents.ENTITY_WITHER_SHOOT,
                    SoundCategory.PLAYERS, 1.0F, 1.0F);

            if (!user.isCreative()) {
                stack.damage(1, user, e -> e.sendToolBreakStatus(hand));
            }

            user.getItemCooldownManager().set(this, 8);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}