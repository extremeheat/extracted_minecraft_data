package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;

public interface BoundedFloatFunction<C> {
   BoundedFloatFunction<Float> IDENTITY = new BoundedFloatFunction<Float>() {
      public float apply(final Float value) {
         return value;
      }

      public Interval range() {
         return Interval.INFINITE;
      }
   };

   float apply(final C c);

   Interval range();

   static <C> BoundedFloatFunction<C> constant(final float value) {
      final Interval range = Interval.ofExact((double)value);
      return new BoundedFloatFunction<C>() {
         public float apply(final C c) {
            return value;
         }

         public Interval range() {
            return range;
         }
      };
   }

   default <C2> BoundedFloatFunction<C2> comap(final Function<C2, C> function) {
      return new BoundedFloatFunction<C2>() {
         {
            Objects.requireNonNull(BoundedFloatFunction.this);
         }

         public float apply(final C2 c2) {
            return BoundedFloatFunction.this.apply(function.apply(c2));
         }

         public Interval range() {
            return BoundedFloatFunction.this.range();
         }
      };
   }
}
