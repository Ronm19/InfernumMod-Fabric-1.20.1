package net.ronm19.infernummod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.client.particle.ModParticles;
import net.ronm19.infernummod.effect.ModEffects;
import net.ronm19.infernummod.enchantment.ModEnchantments;
import net.ronm19.infernummod.entity.ModBoats;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.InfernalKnightEntity;
import net.ronm19.infernummod.event.InfernalTransformationHandler;
import net.ronm19.infernummod.event.InfernalWeatherHandler;
import net.ronm19.infernummod.item.ModItemGroups;
import net.ronm19.infernummod.item.ModItems;
import net.ronm19.infernummod.item.custom.InfernalRoyalStaffItem;
import net.ronm19.infernummod.potion.ModPotions;
import net.ronm19.infernummod.sound.ModSounds;
import net.ronm19.infernummod.util.ModLootTableModifiers;
import net.ronm19.infernummod.util.ModRegistries;
import net.ronm19.infernummod.villager.ModVillagers;
import net.ronm19.infernummod.world.gen.ModWorldGeneration;
import net.ronm19.infernummod.world.tree.ModFoliagePlacerTypes;
import net.ronm19.infernummod.world.tree.ModTrunkPlacerTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class InfernumMod implements ModInitializer {
	public static final String MOD_ID = "infernummod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

        ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
            // Only run logic on server worlds
            if (serverWorld instanceof ServerWorld) {
                InfernalWeatherHandler.tickServerWorld((ServerWorld) serverWorld);
            }
        });

        ModItemGroups.registerItemGroups();

        ModItems.registerModItems();
        ModBlocks.registerModBlock();
        ModRegistries.registerModStuff();

        ModBoats.registerBoats();

        ModEntities.registerModEntites();
        ModVillagers.registerVillagers();

        ModEnchantments.registerModEnchantments();
        ModEffects.registerEffects();
        ModPotions.registerPotions();

        ModParticles.registerParticles();

        ModLootTableModifiers.modifyLootTables();

        ModTrunkPlacerTypes.register();
        ModFoliagePlacerTypes.register();

        ModSounds.registerSounds();

        InfernalTransformationHandler.registerDefaultTransformations();

        registerAttackListener();



        StrippableBlockRegistry.register(ModBlocks.INFERNO_ESSENCE_LOG, ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG);
        StrippableBlockRegistry.register(ModBlocks.INFERNO_ESSENCE_WOOD, ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD);

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.INFERNO_ESSENCE_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.INFERNO_ESSENCE_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_INFERNO_ESSENCE_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_INFERNO_ESSENCE_WOOD, 5, 5);

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.INFERNO_ESSENCE_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.INFERNO_ESSENCE_LEAVES, 30, 5);

        ModWorldGeneration.generateModWorldGen();

        CustomPortalBuilder.beginPortal()
                .frameBlock(ModBlocks.INFERNAL_OBSIDIAN_BLOCK)
                .lightWithItem(ModItems.FLAME_STAFF)
                .destDimID(new Identifier(InfernumMod.MOD_ID, "infernal"))
                .tintColor(0xFF4500)
                .registerPortal();

    }

    private static final HashMap<UUID, Integer> STAFF_ATTACK_COOLDOWN = new HashMap<>();

    private void registerAttackListener() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return ActionResult.PASS;

            // ----- ⏳ cooldown check -----
            int currentTick = (int) world.getTime();
            UUID id = player.getUuid();
            int last = STAFF_ATTACK_COOLDOWN.getOrDefault(id, 0);
            if (currentTick - last < 10) { // 10 ticks ≈ 0.5s
                return ActionResult.PASS; // still cooling down
            }
            STAFF_ATTACK_COOLDOWN.put(id, currentTick);
            // -----------------------------

            boolean hasKnightsInAttack = !world.getEntitiesByClass(
                    InfernalKnightEntity.class,
                    new Box(player.getBlockPos()).expand(InfernalRoyalStaffItem.COMMAND_RANGE),
                    k -> k.isTamed()
                            && player.getUuid().equals(k.getOwnerUuid())
                            && k.getCommand() == InfernalKnightEntity.CommandMode.ATTACK
            ).isEmpty();

            if (!hasKnightsInAttack) return ActionResult.PASS;
            if (target instanceof InfernalKnightEntity || target == player) return ActionResult.PASS;

            InfernalRoyalStaffItem.commandOwnedKnightsAttack(player, target, world, InfernalRoyalStaffItem.COMMAND_RANGE);

            // give the staff a slight hand animation feedback
            if (player instanceof ServerPlayerEntity sp)
                sp.swingHand(Hand.MAIN_HAND, true);

            return ActionResult.PASS;
        });
    }


}