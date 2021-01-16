package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Double2DoubleFunction extends Function<Double, Double>, DoubleUnaryOperator {
   default double applyAsDouble(double var1) {
      return this.get(var1);
   }

   default double put(double var1, double var3) {
      throw new UnsupportedOperationException();
   }

   double get(double var1);

   default double remove(double var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Double put(Double var1, Double var2) {
      double var3 = var1;
      boolean var5 = this.containsKey(var3);
      double var6 = this.put(var3, var2);
      return var5 ? var6 : null;
   }

   /** @deprecated */
   @Deprecated
   default Double get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         double var2 = (Double)var1;
         double var4 = this.get(var2);
         return var4 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var4;
      }
   }

   /** @deprecated */
   @Deprecated
   default Double remove(Object var1) {
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

   default void defaultReturnValue(double var1) {
      throw new UnsupportedOperationException();
   }

   default double defaultReturnValue() {
      return 0.0D;
   }
}
