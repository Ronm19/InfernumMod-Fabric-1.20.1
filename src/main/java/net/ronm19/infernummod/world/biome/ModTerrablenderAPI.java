package net.ronm19.infernummod.world.biome;

import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.world.biome.surface.ModMaterialRules;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

public class ModTerrablenderAPI implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new ModOverworldRegion(new Identifier(InfernumMod.MOD_ID, "overworld"), 20));

        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, InfernumMod.MOD_ID, ModMaterialRules.makeAshlandRules());
    }
}
