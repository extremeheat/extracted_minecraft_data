package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public record DistancePredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z, MinMaxBounds.Doubles horizontal, MinMaxBounds.Doubles absolute) {
   public static final Codec<DistancePredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::x), MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::y), MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::z), MinMaxBounds.Doubles.CODEC.optionalFieldOf("horizontal", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::horizontal), MinMaxBounds.Doubles.CODEC.optionalFieldOf("absolute", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::absolute)).apply(i, DistancePredicate::new));

   public DistancePredicate {
      super();
   }

   public static DistancePredicate horizontal(final MinMaxBounds.Doubles horizontal) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, horizontal, MinMaxBounds.Doubles.ANY);
   }

   public static DistancePredicate vertical(final MinMaxBounds.Doubles y) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, y, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
   }

   public static DistancePredicate absolute(final MinMaxBounds.Doubles absolute) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, absolute);
   }

   public boolean matches(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
      float xd = (float)(x0 - x1);
      float yd = (float)(y0 - y1);
      float zd = (float)(z0 - z1);
      if (this.x.matches((double)Mth.abs(xd)) && this.y.matches((double)Mth.abs(yd)) && this.z.matches((double)Mth.abs(zd))) {
         if (!this.horizontal.matchesSqr((double)(xd * xd + zd * zd))) {
            return false;
         } else {
            return this.absolute.matchesSqr((double)(xd * xd + yd * yd + zd * zd));
         }
      } else {
         return false;
      }
   }
}
