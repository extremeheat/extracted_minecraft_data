package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Object2DoubleFunction<K> extends Function<K, Double>, ToDoubleFunction<K> {
   default double applyAsDouble(K var1) {
      return this.getDouble(var1);
   }

   default double put(K var1, double var2) {
      throw new UnsupportedOperationException();
   }

   double getDouble(Object var1);

   default double removeDouble(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Double put(K var1, Double var2) {
      boolean var4 = this.containsKey(var1);
      double var5 = this.put(var1, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Double get(Object var1) {
      double var3 = this.getDouble(var1);
      return var3 == this.defaultReturnValue() && !this.containsKey(var1) ? null : var3;
   }

   /** @deprecated */
   @Deprecated
   default Double remove(Object var1) {
      return this.containsKey(var1) ? this.removeDouble(var1) : null;
   }

   default void defaultReturnValue(double var1) {
      throw new UnsupportedOperationException();
   }

   default double defaultReturnValue() {
      return 0.0D;
   }
}
