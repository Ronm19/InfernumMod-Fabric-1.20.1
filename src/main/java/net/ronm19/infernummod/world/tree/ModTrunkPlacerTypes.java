package net.ronm19.infernummod.world.tree;

import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.mixin.TrunkPlacerTypeInvoker;
import net.ronm19.infernummod.world.tree.custom.InfernoEssenceTrunkPlacer;

public class ModTrunkPlacerTypes {
    public static final TrunkPlacerType<?> INFERNO_ESSENCE_TRUNK_PLACER = TrunkPlacerTypeInvoker.callRegister("inferno_essence_trunk_placer", InfernoEssenceTrunkPlacer.CODEC);

    public static void register() {
        InfernumMod.LOGGER.info("Registering Trunk Placers for"  + InfernumMod.MOD_ID);
    }
}
