package net.ronm19.infernummod.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.ronm19.infernummod.InfernumMod;

public class ModSounds {


    public static final SoundEvent INFERNAL_BEAST_AMBIENT = registerSoundEvent("infernal_beast_ambient");
    public static final SoundEvent INFERNAL_BEAST_STEP = registerSoundEvent("infernal_beast_step");
    public static final SoundEvent INFERNAL_BEAST_DEATH = registerSoundEvent("infernal_beast_death");
    public static final SoundEvent INFERNAL_BEAST_ROAR = registerSoundEvent("infernal_beast_roar");

    public static final SoundEvent INFERNAL_KNIGHT_SUMMON = registerSoundEvent("infernal_knight_summon");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier identifier = new Identifier(InfernumMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void registerSounds() {
        InfernumMod.LOGGER.info("Registering Mod Sounds for " + InfernumMod.MOD_ID);
    }
}
