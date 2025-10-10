package net.ronm19.infernummod.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.ronm19.infernummod.entity.custom.PyerlingWyrnEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityExplosionProtectionMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void infernum$preventExplosionWhileRiding( DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.hasVehicle() && self.getVehicle() instanceof PyerlingWyrnEntity) {
            if (source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.PLAYER_EXPLOSION)) {
                cir.setReturnValue(false); // cancel explosion damage entirely
            }
        }
    }
}
