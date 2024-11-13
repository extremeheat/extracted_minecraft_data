package net.minecraft.util;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Function;

public interface ToFloatFunction<C> {
   ToFloatFunction<Float> IDENTITY = createUnlimited((var0) -> var0);

   float apply(C var1);

   float minValue();

   float maxValue();

   static ToFloatFunction<Float> createUnlimited(final Float2FloatFunction var0) {
      return new ToFloatFunction<Float>() {
         public float apply(Float var1) {
            return (Float)var0.apply(var1);
         }

         public float minValue() {
            return -1.0F / 0.0F;
         }

         public float maxValue() {
            return 1.0F / 0.0F;
         }
      };
   }

   default <C2> ToFloatFunction<C2> comap(final Function<C2, C> var1) {
      return new ToFloatFunction<C2>() {
         public float apply(C2 var1x) {
            return ToFloatFunction.this.apply(var1.apply(var1x));
         }

         public float minValue() {
            return ToFloatFunction.this.minValue();
         }

         public float maxValue() {
            return ToFloatFunction.this.maxValue();
         }
      };
   }
}
