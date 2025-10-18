package net.ronm19.infernummod.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;
import net.minecraft.datafixer.fix.VillagerXpRebuildFix;

import java.util.Optional;

public class InfernalZombilagerXpRebuildFix extends ChoiceFix {
    public InfernalZombilagerXpRebuildFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "Infernal Zombilager XP rebuild", TypeReferences.ENTITY, "infernummod:infernal_zombilager");
    }

    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), (dynamic) -> {
            Optional<Number> optional = dynamic.get("Xp").asNumber().result();
            if (optional.isEmpty()) {
                int i = dynamic.get("VillagerData").get("level").asInt(1);
                return dynamic.set("Xp", dynamic.createInt(VillagerXpRebuildFix.levelToXp(i)));
            } else {
                return dynamic;
            }
        });
    }
}

