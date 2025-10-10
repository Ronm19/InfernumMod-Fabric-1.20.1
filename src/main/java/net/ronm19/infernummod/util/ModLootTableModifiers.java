package net.ronm19.infernummod.util;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.item.ModItems;

public class ModLootTableModifiers {
    private static final Identifier BLAZE_ID
            = new Identifier("minecraft", "entities/blaze");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register(( resourceManager, lootManager, id, tableBuilder, source ) -> {
            if(BLAZE_ID.equals(id)) {
                // Adds Peat Brick to the Creeper Loot table.
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.85f)) // Drops 85% of the time
                        .with(ItemEntry.builder(ModItems.BLAZE_HEART))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}
