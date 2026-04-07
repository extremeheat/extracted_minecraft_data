package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;

public interface BoundedFloatFunction<C> {
   BoundedFloatFunction<Float> IDENTITY = new BoundedFloatFunction<Float>() {
      public float apply(final Float value) {
         return value;
      }

      public float minValue() {
         return -1.0F / 0.0F;
      }

      public float maxValue() {
         return 1.0F / 0.0F;
      }
   };

   float apply(final C c);

   float minValue();

   float maxValue();

   static <C> BoundedFloatFunction<C> constant(final float value) {
      return new BoundedFloatFunction<C>() {
         public float apply(final C c) {
            return value;
         }

         public float minValue() {
            return value;
         }

         public float maxValue() {
            return value;
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

         public float minValue() {
            return BoundedFloatFunction.this.minValue();
         }

         public float maxValue() {
            return BoundedFloatFunction.this.maxValue();
         }
      };
   }
}
