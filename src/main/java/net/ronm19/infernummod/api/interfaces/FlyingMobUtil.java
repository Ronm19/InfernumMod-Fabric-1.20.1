package net.ronm19.infernummod.api.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface FlyingMobUtil {
    default void handleFlyingTravel(LivingEntity self, Vec3d movementInput, BlockPos velocityAffectingPos) {
        if (self.isLogicalSideForUpdatingMovement()) {
            if (self.isTouchingWater()) {
                self.updateVelocity(0.02F, movementInput);
                self.move(MovementType.SELF, self.getVelocity());
                self.setVelocity(self.getVelocity().multiply(0.8));
            } else if (self.isInLava()) {
                self.updateVelocity(0.02F, movementInput);
                self.move(MovementType.SELF, self.getVelocity());
                self.setVelocity(self.getVelocity().multiply(0.5));
            } else {
                float f = 0.91F;
                if (self.isOnGround()) {
                    f = self.getWorld().getBlockState(velocityAffectingPos)
                            .getBlock().getSlipperiness() * 0.91F;
                }

                float g = 0.16277137F / (f * f * f);
                if (self.isOnGround()) {
                    f = self.getWorld().getBlockState(velocityAffectingPos)
                            .getBlock().getSlipperiness() * 0.91F;
                }

                self.updateVelocity(self.isOnGround() ? 0.1F * g : 0.02F, movementInput);
                self.move(MovementType.SELF, self.getVelocity());
                self.setVelocity(self.getVelocity().multiply(f));
            }
        }

        self.updateLimbs(false);
    }

    default boolean isClimbing() {
        return false;
    }
}
