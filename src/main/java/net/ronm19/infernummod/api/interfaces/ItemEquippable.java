package net.ronm19.infernummod.api.interfaces;

import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;

import java.util.Random;

public interface ItemEquippable {
    void initEquipment( Random random, LocalDifficulty difficulty, ServerWorld world, SpawnReason spawnReason );
}
