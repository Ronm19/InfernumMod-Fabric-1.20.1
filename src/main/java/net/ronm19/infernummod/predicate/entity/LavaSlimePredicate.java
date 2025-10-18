package net.ronm19.infernummod.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.ronm19.infernummod.entity.custom.LavaSlimeEntity;
import org.jetbrains.annotations.Nullable;

public record LavaSlimePredicate(NumberRange.IntRange size) implements TypeSpecificPredicate {
    public static final MapCodec<net.ronm19.infernummod.predicate.entity.LavaSlimePredicate> CODEC = RecordCodecBuilder.mapCodec(( instance) -> instance.group(Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "size", NumberRange.IntRange.ANY).forGetter(net.ronm19.infernummod.predicate.entity.LavaSlimePredicate::size)).apply(instance, net.ronm19.infernummod.predicate.entity.LavaSlimePredicate::new));

    public static net.ronm19.infernummod.predicate.entity.LavaSlimePredicate of( NumberRange.IntRange size) {
        return new net.ronm19.infernummod.predicate.entity.LavaSlimePredicate(size);
    }

    public boolean test( Entity entity, ServerWorld world, @Nullable Vec3d pos) {
        if (entity instanceof LavaSlimeEntity lavaSlimeEntity) {
            return this.size.test(lavaSlimeEntity.getSize());
        } else {
            return false;
        }
    }

    public TypeSpecificPredicate.Type type() {
        return Deserializers.SLIME;
    }
}