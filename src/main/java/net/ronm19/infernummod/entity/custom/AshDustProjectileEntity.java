package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.ronm19.infernummod.block.ModBlocks;
import net.ronm19.infernummod.block.custom.AshBlock;
import net.ronm19.infernummod.entity.ModEntities;
import net.ronm19.infernummod.item.ModItems;

public class AshDustProjectileEntity extends ThrownItemEntity {
    public AshDustProjectileEntity( EntityType<? extends ThrownItemEntity> entityType, World world ) {
        super(entityType, world);
    }

    public AshDustProjectileEntity( LivingEntity livingEntity, World world ) {
        super(ModEntities.ASH_DUST_PROJECTILE, livingEntity, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ASH_DUST;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    protected void onBlockHit( BlockHitResult blockHitResult ) {
        if (!this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.getWorld().setBlockState(getBlockPos(), ((AshBlock) ModBlocks.ASH_BLOCK).getRandomBlockState(), 3);
        }

        this.discard();
        super.onBlockHit(blockHitResult);
    }
}
