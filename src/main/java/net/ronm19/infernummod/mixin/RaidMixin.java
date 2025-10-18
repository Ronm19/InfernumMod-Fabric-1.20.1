package net.ronm19.infernummod.mixin;

import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.ronm19.infernummod.config.LavacatorRaidConfig;
import net.ronm19.infernummod.entity.custom.InfernalVokerEntity;
import net.ronm19.infernummod.entity.custom.LavacatorEntity;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.entity.custom.LavagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// NOTE: Yarn method names can vary by MC version.
// The target below is commonly named "spawnNextWave" or "spawnNextWave(BlockPos)".
// If your mappings differ, search Raid class for the method that spawns a wave and inject at @At("TAIL").
@Mixin(Raid.class)
public abstract class RaidMixin {

    // ---- Shadows for fields/methods we need from Raid ----
    @Shadow
    @Final
    private ServerWorld world;
    @Shadow
    @Final
    private BlockPos center;

    // Some Yarn versions expose this. If not present, remove it and use your own wave->count logic only.
    @Shadow
    public abstract int getGroupsSpawned();

    // Vanilla helper; we’ll call it so raid bookkeeping stays correct
    @Shadow
    public abstract void addRaider( int wave, RaiderEntity raider, BlockPos pos, boolean countTowardsRaiders );

    /**
     * Inject after vanilla spawns a wave so we can add our Lavacators too.
     * Common signature: spawnNextWave(BlockPos)
     */
    @Inject(method = "spawnNextWave", at = @At("TAIL"))
    private void infernum$spawnLavacators( BlockPos pos, CallbackInfo ci ) {
        int wave = this.getGroupsSpawned();
        int count = LavacatorRaidConfig.getCountForWave(wave);
        if (count <= 0) return;

        // Decide how many Lavacators for this wave.
        // Example distribution: 0,0,0,1,1,2,3
        count = switch (wave) {
            case 3, 4 -> 1;
            case 5 -> 2;
            default -> (wave >= 6 ? 3 : 0);
        };

        if (count <= 0) return;

        for (int i = 0; i < count; i++) {
            LavacatorEntity lav = ModEntities.LAVACATOR.create(this.world);
            if (lav == null) continue;

            // Spawn near the raid center with a small offset
            BlockPos spawn = this.center.add(
                    (i % 2 == 0 ? 6 : -6),
                    0,
                    (i < 2 ? 6 : -6)
            );

            // Init & position (do not forget initialize so attributes/equipment/enchant logic runs)
            lav.refreshPositionAndAngles(spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5, world.random.nextFloat() * 360f, 0f);

            // Important: add to the raid so it’s tracked and counted properly
            this.addRaider(wave, lav, spawn, true);


            for (i = 0; i < count; i++) {
                InfernalVokerEntity voker = ModEntities.INFERNAL_VOKER.create(this.world);
                if (voker == null) continue;

                // Spawn near the raid center with a small offset
                spawn = this.center.add(
                        (i % 2 == 0 ? 6 : -6),
                        0,
                        (i < 2 ? 6 : -6)
                );

                // Init & position (do not forget initialize so attributes/equipment/enchant logic runs)
                lav.refreshPositionAndAngles(spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5, world.random.nextFloat() * 360f, 0f);

                // Important: add to the raid so it’s tracked and counted properly
                this.addRaider(wave, lav, spawn, true);

                for (i = 0; i < count; i++) {
                    LavagerEntity lavagerEntity = ModEntities.LAVAGER.create(this.world);
                    if (lavagerEntity == null) continue;

                    // Spawn near the raid center with a small offset
                    spawn = this.center.add(
                            (i % 2 == 0 ? 6 : -6),
                            0,
                            (i < 2 ? 6 : -6)
                    );

                    // Init & position (do not forget initialize so attributes/equipment/enchant logic runs)
                    lav.refreshPositionAndAngles(spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5, world.random.nextFloat() * 360f, 0f);

                    // Important: add to the raid so it’s tracked and counted properly
                    this.addRaider(wave, lav, spawn, true);


                    for (i = 0; i < count; i++) {
                        LavagerEntity lavager = ModEntities.LAVAGER.create(this.world);
                        if (lavager == null) continue;

                        // Spawn near the raid center with a small offset
                        spawn = this.center.add(
                                (i % 2 == 0 ? 6 : -6),
                                0,
                                (i < 2 ? 6 : -6)
                        );

                        // Init & position (do not forget initialize so attributes/equipment/enchant logic runs)
                        lav.refreshPositionAndAngles(spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5, world.random.nextFloat() * 360f, 0f);

                        // Important: add to the raid so it’s tracked and counted properly
                        this.addRaider(wave, lav, spawn, true);
                    }
                }
            }
        }
    }
}
