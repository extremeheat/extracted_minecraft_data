package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public record DistancePredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z, MinMaxBounds.Doubles horizontal, MinMaxBounds.Doubles absolute) {
   public static final Codec<DistancePredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::x), MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::y), MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::z), MinMaxBounds.Doubles.CODEC.optionalFieldOf("horizontal", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::horizontal), MinMaxBounds.Doubles.CODEC.optionalFieldOf("absolute", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::absolute)).apply(var0, DistancePredicate::new);
   });

   public DistancePredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z, MinMaxBounds.Doubles horizontal, MinMaxBounds.Doubles absolute) {
      super();
      this.x = x;
      this.y = y;
      this.z = z;
      this.horizontal = horizontal;
      this.absolute = absolute;
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
      if (this.x.matches((double)Mth.abs(var13)) && this.y.matches((double)Mth.abs(var14)) && this.z.matches((double)Mth.abs(var15))) {
         if (!this.horizontal.matchesSqr((double)(var13 * var13 + var15 * var15))) {
            return false;
         } else {
            return this.absolute.matchesSqr((double)(var13 * var13 + var14 * var14 + var15 * var15));
         }
      } else {
         return false;
      }
   }

   public MinMaxBounds.Doubles x() {
      return this.x;
   }

   public MinMaxBounds.Doubles y() {
      return this.y;
   }

   public MinMaxBounds.Doubles z() {
      return this.z;
   }

   public MinMaxBounds.Doubles horizontal() {
      return this.horizontal;
   }

   public MinMaxBounds.Doubles absolute() {
      return this.absolute;
   }
}
