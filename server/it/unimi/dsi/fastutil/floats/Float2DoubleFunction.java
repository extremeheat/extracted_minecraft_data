package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Float2DoubleFunction extends Function<Float, Double>, DoubleUnaryOperator {
   /** @deprecated */
   @Deprecated
   default double applyAsDouble(double var1) {
      return this.get(SafeMath.safeDoubleToFloat(var1));
   }

   default double put(float var1, double var2) {
      throw new UnsupportedOperationException();
   }

   double get(float var1);

   default double remove(float var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Double put(Float var1, Double var2) {
      float var3 = var1;
      boolean var4 = this.containsKey(var3);
      double var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Double get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = (Float)var1;
         double var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Double remove(Object var1) {
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

   default void defaultReturnValue(double var1) {
      throw new UnsupportedOperationException();
   }

   default double defaultReturnValue() {
      return 0.0D;
   }
}
