package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.Mth;

public record DistancePredicate(MinMaxBounds.Doubles b, MinMaxBounds.Doubles c, MinMaxBounds.Doubles d, MinMaxBounds.Doubles e, MinMaxBounds.Doubles f) {
   private final MinMaxBounds.Doubles x;
   private final MinMaxBounds.Doubles y;
   private final MinMaxBounds.Doubles z;
   private final MinMaxBounds.Doubles horizontal;
   private final MinMaxBounds.Doubles absolute;
   public static final Codec<DistancePredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::x),
               MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::y),
               MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::z),
               MinMaxBounds.Doubles.CODEC.optionalFieldOf("horizontal", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::horizontal),
               MinMaxBounds.Doubles.CODEC.optionalFieldOf("absolute", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::absolute)
            )
            .apply(var0, DistancePredicate::new)
   );

   public DistancePredicate(
      MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2, MinMaxBounds.Doubles var3, MinMaxBounds.Doubles var4, MinMaxBounds.Doubles var5
   ) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.horizontal = var4;
      this.absolute = var5;
   }

   public static DistancePredicate horizontal(MinMaxBounds.Doubles var0) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY);
   }

   public static DistancePredicate vertical(MinMaxBounds.Doubles var0) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
   }

   public static DistancePredicate absolute(MinMaxBounds.Doubles var0) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0);
   }

   public boolean matches(double var1, double var3, double var5, double var7, double var9, double var11) {
      float var13 = (float)(var1 - var7);
      float var14 = (float)(var3 - var9);
      float var15 = (float)(var5 - var11);
      if (!this.x.matches((double)Mth.abs(var13)) || !this.y.matches((double)Mth.abs(var14)) || !this.z.matches((double)Mth.abs(var15))) {
         return false;
      } else if (!this.horizontal.matchesSqr((double)(var13 * var13 + var15 * var15))) {
         return false;
      } else {
         return this.absolute.matchesSqr((double)(var13 * var13 + var14 * var14 + var15 * var15));
      }
   }
}
