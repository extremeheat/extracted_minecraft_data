package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Float2FloatFunction extends Function<Float, Float>, DoubleUnaryOperator {
   /** @deprecated */
   @Deprecated
   default double applyAsDouble(double var1) {
      return (double)this.get(SafeMath.safeDoubleToFloat(var1));
   }

   default float put(float var1, float var2) {
      throw new UnsupportedOperationException();
   }

   float get(float var1);

   default float remove(float var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Float put(Float var1, Float var2) {
      float var3 = var1;
      boolean var4 = this.containsKey(var3);
      float var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Float get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = (Float)var1;
         float var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Float remove(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = (Float)var1;
         return this.containsKey(var2) ? this.remove(var2) : null;
      }
   }

   default boolean containsKey(float var1) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return var1 == null ? false : this.containsKey((Float)var1);
   }

   default void defaultReturnValue(float var1) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }
}
