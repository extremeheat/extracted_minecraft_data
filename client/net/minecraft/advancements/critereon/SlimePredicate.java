package net.minecraft.advancements.critereon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;

public record SlimePredicate(MinMaxBounds.Ints size) implements EntitySubPredicate {
   public static final MapCodec<SlimePredicate> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("size", MinMaxBounds.Ints.ANY).forGetter(SlimePredicate::size)).apply(var0, SlimePredicate::new);
   });

   public SlimePredicate(MinMaxBounds.Ints size) {
      super();
      this.size = size;
   }

   public static SlimePredicate sized(MinMaxBounds.Ints var0) {
      return new SlimePredicate(var0);
   }

   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (var1 instanceof Slime var4) {
         return this.size.matches(var4.getSize());
      } else {
         return false;
      }
   }

   public MapCodec<SlimePredicate> codec() {
      return EntitySubPredicates.SLIME;
   }

   public MinMaxBounds.Ints size() {
      return this.size;
   }
}
