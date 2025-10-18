package net.ronm19.infernummod.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LightningBoltPredicate;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.ronm19.infernummod.entity.custom.InfernalLightningEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record InfernalLightningBoltPredicate(NumberRange.IntRange blocksSetOnFire, Optional<EntityPredicate> entityStruck) implements TypeSpecificPredicate {
    public static final MapCodec<InfernalLightningBoltPredicate> CODEC = RecordCodecBuilder.mapCodec(( instance) -> instance.group(Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "blocks_set_on_fire", NumberRange.IntRange.ANY).forGetter(InfernalLightningBoltPredicate::blocksSetOnFire), Codecs.createStrictOptionalFieldCodec(EntityPredicate.CODEC, "entity_struck").forGetter(InfernalLightningBoltPredicate::entityStruck)).apply(instance, InfernalLightningBoltPredicate::new));

    public static InfernalLightningBoltPredicate of(NumberRange.IntRange blocksSetOnFire) {
        return new InfernalLightningBoltPredicate(blocksSetOnFire, Optional.empty());
    }

    public TypeSpecificPredicate.Type type() {
        return Deserializers.LIGHTNING;
    }

    public boolean test( Entity entity, ServerWorld world, @Nullable Vec3d pos) {
        if (!(entity instanceof InfernalLightningEntity infernalLightningEntity)) {
            return false;
        } else {
            return this.blocksSetOnFire.test(infernalLightningEntity.getBlocksSetOnFire()) && (this.entityStruck.isEmpty() || infernalLightningEntity.getStruckEntities().anyMatch((struckEntity) -> ((EntityPredicate)this.entityStruck.get()).test(world, pos, struckEntity)));
        }
    }
}
