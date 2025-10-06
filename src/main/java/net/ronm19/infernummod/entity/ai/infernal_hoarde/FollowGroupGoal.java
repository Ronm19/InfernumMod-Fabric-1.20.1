package net.ronm19.infernummod.entity.ai.infernal_hoarde;

import net.minecraft.entity.ai.goal.Goal;
import net.ronm19.infernummod.entity.custom.InfernalHoardeEntity;

import java.util.List;

public class FollowGroupGoal extends Goal {
    private final InfernalHoardeEntity mob;
    private final double speed;
    private final float minDist;
    private final float maxDist;
    private InfernalHoardeEntity leader;

    public FollowGroupGoal(InfernalHoardeEntity mob, double speed, float minDist, float maxDist) {
        this.mob = mob;
        this.speed = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
    }

    @Override
    public boolean canStart() {
        // Don’t run if mob has target (so it can fight normally)
        if (mob.getTarget() != null) return false;

        // Find nearest other Infernal Horde
        List<InfernalHoardeEntity> nearby = mob.getWorld().getEntitiesByClass(
                InfernalHoardeEntity.class,
                mob.getBoundingBox().expand(maxDist),
                e -> e != mob && e.isAlive()
        );

        if (!nearby.isEmpty()) {
            // Pick the closest one
            leader = nearby.get(0);
            for (InfernalHoardeEntity other : nearby) {
                if (mob.distanceTo(other) < mob.distanceTo(leader)) {
                    leader = other;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldContinue() {
        return leader != null && leader.isAlive()
                && mob.squaredDistanceTo(leader) > (minDist * minDist)
                && mob.squaredDistanceTo(leader) < (maxDist * maxDist);
    }

    @Override
    public void tick() {
        if (leader != null) {
            mob.getNavigation().startMovingTo(leader, speed);
        }
    }

    @Override
    public void stop() {
        leader = null;
    }
}
