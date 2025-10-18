package net.ronm19.infernummod.entity.ai.goals;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.ronm19.infernummod.entity.ai.custom.ModEntity;

public class ModEntityMateGoal extends Goal {
    private final ModEntity entity;

    public ModEntityMateGoal(ModEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return entity.isInLove();
    }

    @Override
    public void tick() {
        for (Entity nearby : entity.getWorld().getEntitiesByClass(ModEntity.class, entity.getBoundingBox().expand(8), e -> e != entity)) {
            ModEntity mate = (ModEntity) nearby;
            if (entity.canBreedWith(mate)) {
                entity.breed((ServerWorld) entity.getWorld(), mate);
                break;
            }
        }
    }
}
