package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Double2FloatFunction extends Function<Double, Float>, DoubleUnaryOperator {
   default double applyAsDouble(double var1) {
      return (double)this.get(var1);
   }

   default float put(double var1, float var3) {
      throw new UnsupportedOperationException();
   }

   float get(double var1);

   default float remove(double var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Float put(Double var1, Float var2) {
      double var3 = var1;
      boolean var5 = this.containsKey(var3);
      float var6 = this.put(var3, var2);
      return var5 ? var6 : null;
   }

   /** @deprecated */
   @Deprecated
   default Float get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         double var2 = (Double)var1;
         float var4 = this.get(var2);
         return var4 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var4;
      }
   }

   /** @deprecated */
   @Deprecated
   default Float remove(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         double var2 = (Double)var1;
         return this.containsKey(var2) ? this.remove(var2) : null;
      }
   }

   default boolean containsKey(double var1) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return var1 == null ? false : this.containsKey((Double)var1);
   }

   default void defaultReturnValue(float var1) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }
}
