package net.ronm19.infernummod.util;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModDamageTags {
    public static final TagKey<DamageType> INFERNALLY_IMMUNE =
            TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("infernummod", "infernally_immune"));
}
