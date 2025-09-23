package net.ronm19.infernummod.api.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.Vec3d;

public interface MountableEntity {
    Vec3d getPassengerAttachmentPoint( Entity passenger, EntityDimensions passengerDimensions, float tickDelta );

    double getMountedHeightOffset();
}
