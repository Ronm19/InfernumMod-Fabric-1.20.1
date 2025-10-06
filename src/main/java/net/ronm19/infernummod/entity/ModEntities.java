package net.ronm19.infernummod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.*;

import static net.minecraft.entity.SpawnGroup.*;
import static net.ronm19.infernummod.InfernumMod.MOD_ID;

public class ModEntities {
    public static final EntityType<DemonEntity> DEMON = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "demon"),
            FabricEntityTypeBuilder.create(MONSTER, DemonEntity::new).dimensions(EntityDimensions.fixed(1f, 2.5f)).build());

    public static final EntityType<MalfuryxEntity> MALFURYX = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "malfuryx"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MalfuryxEntity::new).dimensions(EntityDimensions.fixed(1f, 3.5f)).build());

    public static final EntityType<PyerlingWyrnEntity> PYERLING_WYRN = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "pyerling_wyrn"),
            FabricEntityTypeBuilder.create(CREATURE, PyerlingWyrnEntity ::new).dimensions(EntityDimensions.fixed(1f, 3.5f)).build());

    public static final EntityType<ObsidianGhastEntity> OBSIDIAN_GHAST = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "obsidian_ghast"),
            FabricEntityTypeBuilder.create(MONSTER, ObsidianGhastEntity ::new).dimensions(EntityDimensions.fixed(2f, 4.5f)).build());

    public static final EntityType<InfernalHoardeEntity> INFERNAL_HOARDE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "infernal_hoarde"),
            FabricEntityTypeBuilder.create(MONSTER, InfernalHoardeEntity::new).dimensions(EntityDimensions.fixed(1f, 4.5f)).build());

    public static final EntityType<InfernalEyeEntity> INFERNAL_EYE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "infernal_eye"),
            FabricEntityTypeBuilder.create(CREATURE, InfernalEyeEntity::new).dimensions(EntityDimensions.fixed(2f, 5.5f)).build());

    public static final EntityType<EmberHundEntity> EMBER_HUND = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "ember_hund"),
            FabricEntityTypeBuilder.create(CREATURE, EmberHundEntity::new).dimensions(EntityDimensions.fixed(2f, 5.5f)).build());

    public static final EntityType<InfernalBeastEntity> INFERNAL_BEAST = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "infernal_beast"),
            FabricEntityTypeBuilder.create(MONSTER, InfernalBeastEntity::new).dimensions(EntityDimensions.fixed(2f, 5.5f)).build());

    public static final EntityType<InfernumHerobrineEntity> INFERNUM_HEROBRINE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "infernum_herobrine"),
            FabricEntityTypeBuilder.create(MONSTER, InfernumHerobrineEntity::new).dimensions(EntityDimensions.fixed(2f, 5.5f)).build());

    public static final EntityType<InfernumEntity> INFERNUM = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "infernum"),
            FabricEntityTypeBuilder.create(MONSTER, InfernumEntity::new).dimensions(EntityDimensions.fixed(2f, 5.5f)).build());


    public static final EntityType<AshDustProjectileEntity> ASH_DUST_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "ash_dust_projectile"),
            FabricEntityTypeBuilder.<AshDustProjectileEntity>create(SpawnGroup.MISC, AshDustProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

    public static final EntityType<InfernalSkullEntity> INFERNAL_SKULL = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "infernal_skull"),
            FabricEntityTypeBuilder.<InfernalSkullEntity>create(SpawnGroup.MISC, InfernalSkullEntity::new)
                            .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeBlocks(64).trackedUpdateRate(10).build());



    public static void registerModEntites() {
        InfernumMod.LOGGER.info("Registering Mod Entities for " + MOD_ID);
    }
}
