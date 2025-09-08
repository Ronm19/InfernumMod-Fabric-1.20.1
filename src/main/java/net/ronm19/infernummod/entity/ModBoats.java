package net.ronm19.infernummod.entity;

import com.terraformersmc.terraform.boat.api.TerraformBoatType;
import com.terraformersmc.terraform.boat.api.TerraformBoatTypeRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.item.ModItems;

public class ModBoats {
    public static final Identifier INFERNO_BOAT_ID = new Identifier(InfernumMod.MOD_ID, "inferno_boat");
    public static final Identifier INFERNO_CHEST_BOAT_ID = new Identifier(InfernumMod.MOD_ID, "inferno_chest_boat");

    public static final RegistryKey<TerraformBoatType> INFERNO_BOAT_KEY = TerraformBoatTypeRegistry.createKey(INFERNO_BOAT_ID);

    public static void registerBoats() {
        TerraformBoatType infernoBoat = new TerraformBoatType.Builder()
                .item(ModItems.INFERNO_BOAT)
                .chestItem(ModItems.INFERNO_CHEST_BOAT)
                .planks(ModBlocks.INFERNO_ESSENCE_PLANKS.asItem())
                .build();


        Registry.register(TerraformBoatTypeRegistry.INSTANCE, INFERNO_BOAT_KEY, infernoBoat);
    }
}
