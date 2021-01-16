package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface Double2ObjectFunction<V> extends Function<Double, V>, DoubleFunction<V> {
   default V apply(double var1) {
      return this.get(var1);
   }

   default V put(double var1, V var3) {
      throw new UnsupportedOperationException();
   }

   V get(double var1);

   default V remove(double var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default V put(Double var1, V var2) {
      double var3 = var1;
      boolean var5 = this.containsKey(var3);
      Object var6 = this.put(var3, var2);
      return var5 ? var6 : null;
   }

   /** @deprecated */
   @Deprecated
   default V get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         double var2 = (Double)var1;
         Object var4 = this.get(var2);
         return var4 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var4;
      }
   }

   /** @deprecated */
   @Deprecated
   default V remove(Object var1) {
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

   default void defaultReturnValue(V var1) {
      throw new UnsupportedOperationException();
   }

   default V defaultReturnValue() {
      return null;
   }
}
