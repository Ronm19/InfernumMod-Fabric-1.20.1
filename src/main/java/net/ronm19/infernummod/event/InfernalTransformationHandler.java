package net.ronm19.infernummod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.event.GameEvent;
import net.ronm19.infernummod.entity.ModEntities;

import java.util.HashMap;
import java.util.Map;

public class InfernalTransformationHandler {

    private static final Map<EntityType<?>, EntityType<? extends LivingEntity>> TRANSFORM_MAP = new HashMap<>();

    public static void registerDefaultTransformations() {
        TRANSFORM_MAP.put(EntityType.PIG, ModEntities.EMBER_BOAR);
        TRANSFORM_MAP.put(EntityType.ZOMBIE_VILLAGER, ModEntities.INFERNAL_ZOMBILAGER);
        TRANSFORM_MAP.put(EntityType.ZOMBIE, ModEntities.INFERNO_ZOMBIE);
        TRANSFORM_MAP.put(EntityType.ENDERMAN, ModEntities.INFERNO_ENDERMAN);
        TRANSFORM_MAP.put(EntityType.GHAST, ModEntities.OBSIDIAN_GHAST);
        TRANSFORM_MAP.put(EntityType.SKELETON, ModEntities.FLAME_SKELETON);
        TRANSFORM_MAP.put(EntityType.VEX, ModEntities.INFERNAL_VEX);
        TRANSFORM_MAP.put(EntityType.BLAZE, ModEntities.INFERNAL_WRAITH);
        TRANSFORM_MAP.put(EntityType.EVOKER, ModEntities.INFERNAL_VOKER);
        TRANSFORM_MAP.put(EntityType.PILLAGER, ModEntities.LAVAGER);
        TRANSFORM_MAP.put(EntityType.VINDICATOR, ModEntities.LAVACATOR);
        TRANSFORM_MAP.put(EntityType.WITCH, ModEntities.LAVA_WITCH);
        TRANSFORM_MAP.put(EntityType.SLIME, ModEntities.LAVA_SLIME);
        TRANSFORM_MAP.put(EntityType.CREEPER, ModEntities.MAGMA_CREEPER);
        TRANSFORM_MAP.put(EntityType.STRIDER, ModEntities.MAGMA_STRIDER);
        TRANSFORM_MAP.put(EntityType.DOLPHIN, ModEntities.MAGMA_DOLPHIN);
        TRANSFORM_MAP.put(EntityType.SALMON, ModEntities.FIRE_FISH);
        TRANSFORM_MAP.put(EntityType.TROPICAL_FISH, ModEntities.LAVA_FISH);
        TRANSFORM_MAP.put(EntityType.COD, ModEntities.MAGMA_FISH);
        TRANSFORM_MAP.put(EntityType.SPIDER, ModEntities.MAGMA_SPIDER);
        TRANSFORM_MAP.put(EntityType.WARDEN, ModEntities.INFERNUM);
        TRANSFORM_MAP.put(EntityType.WITHER, ModEntities.INFERNUM_HEROBRINE);
        TRANSFORM_MAP.put(EntityType.WITHER_SKELETON, ModEntities.MALFURYX);
        TRANSFORM_MAP.put(EntityType.SKELETON_HORSE, ModEntities.ASHBONE_HORSE);
        TRANSFORM_MAP.put(EntityType.ZOMBIE_HORSE, ModEntities.INFERNAL_HORSE);
        TRANSFORM_MAP.put(EntityType.HORSE, ModEntities.FLAME_HORSE);
        TRANSFORM_MAP.put(EntityType.WOLF, ModEntities.EMBER_HUND);
        TRANSFORM_MAP.put(EntityType.CAT, ModEntities.VOLCARNIS);
        TRANSFORM_MAP.put(EntityType.SHEEP, ModEntities.SCORCHED_WOOLIE);
        TRANSFORM_MAP.put(EntityType.CHICKEN, ModEntities.ASH_CHICKEN);
        TRANSFORM_MAP.put(EntityType.PHANTOM, ModEntities.INFERNAL_PHANTOM);
        TRANSFORM_MAP.put(ModEntities.EMBER_BOAR, ModEntities.DEMON);
        TRANSFORM_MAP.put(EntityType.POLAR_BEAR, ModEntities.MAGMA_BEAR);
        TRANSFORM_MAP.put(EntityType.IRON_GOLEM, ModEntities.MAGMA_GOLEM);
        TRANSFORM_MAP.put(EntityType.HUSK, ModEntities.INFERNAL_HOARDE);
        TRANSFORM_MAP.put(EntityType.COW, ModEntities.LAVA_COW);
        TRANSFORM_MAP.put(EntityType.RABBIT, ModEntities.INFERNAL_RABBIT);
    }

    public static void onEntityStruckByInfernalLightning( Entity entity, ServerWorld world ) {
        if (!(entity instanceof LivingEntity living)) return;


        EntityType<? extends LivingEntity> newType = TRANSFORM_MAP.get(living.getType());
        if (newType == null) return;

        transformEntity(living, newType, world);

        world.spawnParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY() + 1.0, entity.getZ(),
                15, 0.4, 0.3, 0.4, 0.02);
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 0.7F);
    }


   public static void transformEntity( LivingEntity oldEntity, EntityType<? extends LivingEntity> newType, ServerWorld world ) {
        LivingEntity newEntity = newType.create(world);
        if (newEntity == null) return;

        newEntity.refreshPositionAndAngles(oldEntity.getX(), oldEntity.getY(), oldEntity.getZ(), oldEntity.getYaw(), oldEntity.getPitch());
        world.spawnEntity(newEntity);

        // ↓↓↓ mark so lightning ignores it this tick
        newEntity.addCommandTag("infernal_just_transformed");

        if (oldEntity.hasCustomName()) newEntity.setCustomName(oldEntity.getCustomName());
        if (oldEntity.isOnFire()) newEntity.setOnFireFor(8);

        world.spawnParticles(ParticleTypes.LAVA, newEntity.getX(), newEntity.getY() + 1.0, newEntity.getZ(), 20, 0.3, 0.3, 0.3, 0.05);
        world.playSound(null, newEntity.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.HOSTILE, 1.0F, 0.8F);

        oldEntity.discard();
        newEntity.emitGameEvent(GameEvent.ENTITY_PLACE);
    }

    public static void tryTransform(LivingEntity entity, ServerWorld world) {
        if (entity == null || world == null) return;

        EntityType<? extends LivingEntity> newType = TRANSFORM_MAP.get(entity.getType());
        if (newType == null) return; // Not a valid transform target

        transformEntity(entity, newType, world);
    }
}