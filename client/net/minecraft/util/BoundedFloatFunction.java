package net.minecraft.util;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.Objects;
import java.util.function.Function;

public interface BoundedFloatFunction<C> {
   BoundedFloatFunction<Float> IDENTITY = createUnlimited((input) -> input);

   float apply(final C c);

   float minValue();

   float maxValue();

   static BoundedFloatFunction<Float> createUnlimited(final Float2FloatFunction function) {
      return new BoundedFloatFunction<Float>() {
         public float apply(final Float aFloat) {
            return (Float)function.apply(aFloat);
         }

         public float minValue() {
            return -1.0F / 0.0F;
         }

         public float maxValue() {
            return 1.0F / 0.0F;
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
