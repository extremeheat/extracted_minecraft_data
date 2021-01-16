package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Double2IntFunction extends Function<Double, Integer>, DoubleToIntFunction {
   default int applyAsInt(double var1) {
      return this.get(var1);
   }

   default int put(double var1, int var3) {
      throw new UnsupportedOperationException();
   }

   int get(double var1);

   default int remove(double var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(Double var1, Integer var2) {
      double var3 = var1;
      boolean var5 = this.containsKey(var3);
      int var6 = this.put(var3, var2);
      return var5 ? var6 : null;
   }

   /** @deprecated */
   @Deprecated
   default Integer get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         double var2 = (Double)var1;
         int var4 = this.get(var2);
         return var4 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var4;
      }
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(Object var1) {
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

   default void defaultReturnValue(int var1) {
      throw new UnsupportedOperationException();
   }

   default int defaultReturnValue() {
      return 0;
   }
}
