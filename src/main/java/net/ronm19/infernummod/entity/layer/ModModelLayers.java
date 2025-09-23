package net.ronm19.infernummod.entity.layer;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;

public class ModModelLayers {
    public static final EntityModelLayer DEMON =
            new EntityModelLayer(new Identifier(InfernumMod.MOD_ID, "demon"), "main");
    public static final EntityModelLayer MALFURYX =
            new EntityModelLayer(new Identifier(InfernumMod.MOD_ID, "malfuryx"), "main");
    public static final EntityModelLayer EMBER_HUND =
            new EntityModelLayer(new Identifier(InfernumMod.MOD_ID, "ember_hund"), "main");
    public static final EntityModelLayer PYERLING_WYRN =
            new EntityModelLayer(new Identifier(InfernumMod.MOD_ID, "pyerling_wyrn"), "main");
}
