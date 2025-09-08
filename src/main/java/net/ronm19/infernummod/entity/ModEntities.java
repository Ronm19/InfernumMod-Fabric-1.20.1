package net.ronm19.infernummod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.impl.object.builder.FabricEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.entity.custom.AshDustProjectileEntity;
import net.ronm19.infernummod.entity.custom.DemonEntity;
import net.ronm19.infernummod.entity.custom.MalfuryxEntity;

import static net.minecraft.entity.SpawnGroup.*;

public class ModEntities {
    public static final EntityType<DemonEntity> DEMON = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(InfernumMod.MOD_ID, "demon"),
            FabricEntityTypeBuilder.create(MONSTER, DemonEntity::new).dimensions(EntityDimensions.fixed(1f, 2.5f)).build());

    public static final EntityType<MalfuryxEntity> MALFURYX = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(InfernumMod.MOD_ID, "malfuryx"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MalfuryxEntity::new).dimensions(EntityDimensions.fixed(1f, 3.5f)).build());

    public static final EntityType<AshDustProjectileEntity> ASH_DUST_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(InfernumMod.MOD_ID, "ash_dust_projectile"),
            FabricEntityTypeBuilder.<AshDustProjectileEntity>create(SpawnGroup.MISC, AshDustProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
}
