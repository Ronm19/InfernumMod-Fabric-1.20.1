package net.ronm19.infernummod.item.custom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.custom.LavagerArrowEntity;

public class LavagerArrowItem extends ArrowItem {

    public LavagerArrowItem(Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        LavagerArrowEntity arrow = new LavagerArrowEntity(world, shooter);
        arrow.setOnFireFor(100);   // Keep the flaming property consistent
        return arrow;
    }
}
