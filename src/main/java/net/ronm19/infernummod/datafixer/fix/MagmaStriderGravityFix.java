package net.ronm19.infernummod.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class MagmaStriderGravityFix extends ChoiceFix {
        public MagmaStriderGravityFix(Schema outputSchema, boolean changesType) {
            super(outputSchema, changesType, "MagmaStriderGravityFix", TypeReferences.ENTITY, "infernummod:magma_strider");
        }

        public Dynamic<?> updateNoGravityNbt( Dynamic<?> dynamic) {
            return dynamic.get("NoGravity").asBoolean(false) ? dynamic.set("NoGravity", dynamic.createBoolean(false)) : dynamic;
        }

        protected Typed<?> transform(Typed<?> inputType) {
            return inputType.update(DSL.remainderFinder(), this::updateNoGravityNbt);
        }
    }
