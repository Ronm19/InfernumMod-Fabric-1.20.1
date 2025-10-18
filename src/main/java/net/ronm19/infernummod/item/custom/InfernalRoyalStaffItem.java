package net.ronm19.infernummod.item.custom;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.enchantment.Enchantments;
import net.ronm19.infernummod.entity.custom.InfernalKnightEntity;
import net.ronm19.infernummod.entity.variant.InfernalKnightVariant;
import net.ronm19.infernummod.item.ModItems;
import com.mojang.datafixers.util.Pair;


import java.util.List;

public class InfernalRoyalStaffItem extends Item {
    public static final double COMMAND_RANGE = 200.0;

    public InfernalRoyalStaffItem(Settings settings) {
        super(settings);
    }

    // ------------------ RIGHT CLICK IN AIR ------------------
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        // Gather all knights owned by player
        List<InfernalKnightEntity> knights = world.getEntitiesByClass(
                InfernalKnightEntity.class,
                new Box(user.getBlockPos()).expand(COMMAND_RANGE),
                k -> k.isTamed() && user.getUuid().equals(k.getOwnerUuid())
        );

        if (knights.isEmpty()) {
            user.sendMessage(Text.literal("§cNo Infernal Knights nearby to command!"), true);
            return TypedActionResult.pass(stack);
        }

        // Cycle command mode
        InfernalKnightEntity.CommandMode sample = knights.get(0).getCommand();
        InfernalKnightEntity.CommandMode newMode = switch (sample) {
            case FOLLOW -> InfernalKnightEntity.CommandMode.HOLD;
            case HOLD -> InfernalKnightEntity.CommandMode.PATROL;
            case PATROL -> InfernalKnightEntity.CommandMode.ATTACK;
            default -> InfernalKnightEntity.CommandMode.FOLLOW;
        };

        for (InfernalKnightEntity k : knights) {
            k.setCommand(newMode);
        }

        // Feedback
        String modeMsg = switch (newMode) {
            case FOLLOW -> "§6⚔ Command: §eFollow";
            case HOLD -> "§6🛡 Command: §eHold Position";
            case PATROL -> "§6🚶 Command: §ePatrol Area";
            case ATTACK -> "§6🔥 Command: §eAttack Mode";
        };
        user.sendMessage(Text.literal(modeMsg), true);
        world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1f, 1.2f);

        if (world instanceof ServerWorld sw)
            sw.spawnParticles(ParticleTypes.FLAME, user.getX(), user.getEyeY(), user.getZ(), 25, 0.4, 0.4, 0.4, 0.01);

        user.getItemCooldownManager().set(this, 20);
        return TypedActionResult.success(stack);
    }

    // ------------------ RIGHT CLICK ON ENTITY ------------------
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.getWorld().isClient) return ActionResult.SUCCESS;

        if (entity instanceof InfernalKnightEntity knight) {
            if (knight.isTamed() && user.getUuid().equals(knight.getOwnerUuid())) {

                // 🔥 Promote to Elite
                if (knight.getVariant() == InfernalKnightVariant.DEFAULT) {

                    knight.setVariant(InfernalKnightVariant.ELITE);
                    knight.equipStack(EquipmentSlot.MAINHAND, ModItems.INFERNUM_SWORD.getDefaultStack());
                    knight.equipStack(EquipmentSlot.OFFHAND, ModItems.INFERNO_SHIELD.getDefaultStack());
                    knight.setStackInHand(Hand.MAIN_HAND, ModItems.INFERNUM_SWORD.getDefaultStack());

                    // Sync manually for all nearby players
                    if (user.getWorld() instanceof ServerWorld sw)
                        sw.getChunkManager().sendToNearbyPlayers(knight,
                                new EntityEquipmentUpdateS2CPacket(
                                        knight.getId(),
                                        List.of(
                                                new Pair<>(EquipmentSlot.MAINHAND, knight.getEquippedStack(EquipmentSlot.MAINHAND)),
                                                new Pair<>(EquipmentSlot.OFFHAND, knight.getEquippedStack(EquipmentSlot.OFFHAND)),
                                                new Pair<>(EquipmentSlot.HEAD, knight.getEquippedStack(EquipmentSlot.HEAD)),
                                                new Pair<>(EquipmentSlot.CHEST, knight.getEquippedStack(EquipmentSlot.CHEST)),
                                                new Pair<>(EquipmentSlot.LEGS, knight.getEquippedStack(EquipmentSlot.LEGS)),
                                                new Pair<>(EquipmentSlot.FEET, knight.getEquippedStack(EquipmentSlot.FEET))
                                        )
                                )
                        );


                    user.getWorld().playSound(null, knight.getBlockPos(),
                            SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS, 1.0f, 1.2f);
                    user.sendMessage(Text.literal("⚔ Promoted to Infernal Elite!").formatted(net.minecraft.util.Formatting.GOLD), true);

                    if (user.getWorld() instanceof ServerWorld sw)
                        sw.spawnParticles(ParticleTypes.LAVA, knight.getX(), knight.getEyeY(), knight.getZ(),
                                40, 0.5, 0.5, 0.5, 0.03);

                    // Short glow flash (3 seconds)
                    knight.setGlowing(true);
                    user.getServer().execute(() -> {
                        knight.setGlowing(false);
                    });

                    return ActionResult.SUCCESS;
                }

                // 🛡 Demote to Melee
                else {
                    knight.setVariant(InfernalKnightVariant.DEFAULT);

                    ItemStack sword = new ItemStack(ModItems.FIRERITE_SWORD);
                    ItemStack shield = new ItemStack(ModItems.INFERNO_SHIELD);

                    knight.equipStack(EquipmentSlot.MAINHAND, sword);
                    knight.equipStack(EquipmentSlot.OFFHAND, shield);
                    knight.setStackInHand(Hand.MAIN_HAND, sword);

                    if (user.getWorld() instanceof ServerWorld sw)
                        sw.getChunkManager().sendToNearbyPlayers(knight,
                                new EntityEquipmentUpdateS2CPacket(
                                        knight.getId(),
                                        List.of(
                                                new Pair<>(EquipmentSlot.MAINHAND, knight.getEquippedStack(EquipmentSlot.MAINHAND)),
                                                new Pair<>(EquipmentSlot.OFFHAND, knight.getEquippedStack(EquipmentSlot.OFFHAND)),
                                                new Pair<>(EquipmentSlot.HEAD, knight.getEquippedStack(EquipmentSlot.HEAD)),
                                                new Pair<>(EquipmentSlot.CHEST, knight.getEquippedStack(EquipmentSlot.CHEST)),
                                                new Pair<>(EquipmentSlot.LEGS, knight.getEquippedStack(EquipmentSlot.LEGS)),
                                                new Pair<>(EquipmentSlot.FEET, knight.getEquippedStack(EquipmentSlot.FEET))
                                        )
                                )
                        );


                    user.getWorld().playSound(null, knight.getBlockPos(),
                            SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    user.sendMessage(Text.literal("🛡 Demoted to Infernal Knight!").formatted(net.minecraft.util.Formatting.RED), true);

                    if (user.getWorld() instanceof ServerWorld sw)
                        sw.spawnParticles(ParticleTypes.SMOKE, knight.getX(), knight.getEyeY(), knight.getZ(),
                                40, 0.5, 0.5, 0.5, 0.01);

                    knight.setGlowing(true);
                    user.getServer().execute(() -> {
                        knight.setGlowing(false);
                    });

                    return ActionResult.SUCCESS;
                }
            }
        }

        // 🔥 Normal right-click on other entity → Attack command
        commandOwnedKnightsAttack(user, entity, user.getWorld(), COMMAND_RANGE);
        user.getWorld().playSound(null, entity.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.7f, 1.0f);
        return ActionResult.CONSUME;
    }

    // ------------------ HELPER: Create enchanted bow ------------------
    private ItemStack createInfernalBow() {
        ItemStack bow = new ItemStack(Items.BOW);
        bow.addEnchantment(Enchantments.POWER, 5);
        bow.addEnchantment(Enchantments.INFINITY, 1);
        bow.addEnchantment(Enchantments.UNBREAKING, 3);
        bow.getOrCreateNbt().putBoolean("Unbreakable", true);
        return bow;
    }

    // ------------------ SHARED HELPER ------------------
    public static void commandOwnedKnightsAttack(PlayerEntity player, LivingEntity target, World world, double range) {
        List<InfernalKnightEntity> owned = world.getEntitiesByClass(
                InfernalKnightEntity.class,
                new Box(player.getBlockPos()).expand(range),
                k -> k.isTamed() && player.getUuid().equals(k.getOwnerUuid())
        );
        if (owned.isEmpty()) return;

        for (InfernalKnightEntity k : owned) {
            k.setCommand(InfernalKnightEntity.CommandMode.ATTACK);
            k.setTarget(target);
        }

        player.sendMessage(Text.literal("§6🔥 Command: §cAttack " + target.getName().getString()), true);

        if (world instanceof ServerWorld sw)
            sw.spawnParticles(ParticleTypes.FLAME, target.getX(), target.getY() + 0.6, target.getZ(),
                    30, 0.5, 0.5, 0.5, 0.02);
    }
}
