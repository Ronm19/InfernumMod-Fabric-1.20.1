package net.ronm19.infernummod.entity.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;

public class ModModelLayers {
    public static final EntityModelLayer DEMON =
            new EntityModelLayer(new Identifier(InfernumMod.MOD_ID, "demon"), "main");
    public static final EntityModelLayer MALFURYX =
            new EntityModelLayer(new Identifier(InfernumMod.MOD_ID, "malfuryx"), "main");
}
