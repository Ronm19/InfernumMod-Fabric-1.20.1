package net.ronm19.infernummod.mixin;

import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.server.world.ServerWorld;
import net.ronm19.infernummod.entity.custom.InfernalLightningEntity;
import net.ronm19.infernummod.event.InfernalTransformationHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PigEntity.class)
public class PigEntityMixin {

    @Inject(method = "onStruckByLightning", at = @At("HEAD"), cancellable = true)
    private void onInfernalLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        if (lightning instanceof InfernalLightningEntity infernal) {
            InfernalTransformationHandler.onEntityStruckByInfernalLightning((PigEntity)(Object)this, world);
            ci.cancel(); // stop vanilla zombified piglin conversion
        }
    }

}
