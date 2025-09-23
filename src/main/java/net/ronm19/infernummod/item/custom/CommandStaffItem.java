package net.ronm19.infernummod.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.ICommandableEntity;
import net.ronm19.infernummod.entity.ai.custom.CommandState;

public class CommandStaffItem extends Item {

    public CommandStaffItem(Settings settings) {
        super(settings);
    }

    /**
     * Right-click an entity to make all nearby tameable commandable entities attack it.
     */
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity target, Hand hand) {
        if (user.getWorld().isClient) return ActionResult.PASS;

        user.getWorld().getEntitiesByClass(LivingEntity.class,
                user.getBoundingBox().expand(20),
                e -> e instanceof ICommandableEntity cmdEntity &&
                        (e instanceof TameableEntity tame && tame.isTamed() && tame.getOwner() == user)
        ).forEach(e -> {
            ICommandableEntity cmdEntity = (ICommandableEntity) e;
            cmdEntity.setCommandState(CommandState.ATTACK);

            if (e instanceof MobEntity mob) {
                mob.setTarget(target); // entity will attack this target
            }
        });

        return ActionResult.SUCCESS;
    }

    /**
     * Right-click in air to cycle command state for all nearby tameable commandable entities.
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            world.getEntitiesByClass(LivingEntity.class,
                    user.getBoundingBox().expand(20),
                    e -> e instanceof ICommandableEntity cmdEntity &&
                            (e instanceof TameableEntity tame && tame.isTamed() && tame.getOwner() == user)
            ).forEach(e -> {
                ICommandableEntity cmdEntity = (ICommandableEntity) e;

                CommandState newState = switch (cmdEntity.getCommandState()) {
                    case FOLLOW -> CommandState.HOLD;
                    case HOLD -> CommandState.PATROL;
                    case PATROL -> CommandState.ATTACK;
                    case ATTACK -> CommandState.FOLLOW;
                };

                cmdEntity.setCommandState(newState);

                user.sendMessage(Text.literal(e.getName().getString() + " Command: " + newState), true);
            });
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
