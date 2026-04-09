package net.minecraft.advancements.criterion;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record SlimePredicate(MinMaxBounds.Ints size) implements EntitySubPredicate {
   public static final MapCodec<SlimePredicate> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("size", MinMaxBounds.Ints.ANY).forGetter(SlimePredicate::size)).apply(i, SlimePredicate::new));

   public SlimePredicate {
      super();
   }

   public static SlimePredicate sized(final MinMaxBounds.Ints size) {
      return new SlimePredicate(size);
   }

   public boolean matches(final Entity entity, final ServerLevel level, final @Nullable Vec3 position) {
      if (entity instanceof Slime slime) {
         return this.size.matches(slime.getSize());
      } else {
         return false;
      }
   }

   public MapCodec<SlimePredicate> codec() {
      return EntitySubPredicates.SLIME;
   }
}
