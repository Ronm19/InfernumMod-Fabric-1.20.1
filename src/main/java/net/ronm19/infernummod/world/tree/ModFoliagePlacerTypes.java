package net.ronm19.infernummod.world.tree;

import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.mixin.FoliagePlacerTypeInvoker;
import net.ronm19.infernummod.world.tree.custom.InfernoEssenceFoliagePlacer;

public class ModFoliagePlacerTypes {
    public static final FoliagePlacerType<?> INFERNO_ESSENCE_FOLIAGE_PLACER = FoliagePlacerTypeInvoker.callRegister("inferno_essence_foliage_placer", InfernoEssenceFoliagePlacer.CODEC);

    public static void register() {
        InfernumMod.LOGGER.info("Registering Foliage Placer for " + InfernumMod.MOD_ID);
    }
}
