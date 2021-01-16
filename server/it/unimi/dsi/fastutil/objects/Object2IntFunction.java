package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2IntFunction<K> extends Function<K, Integer>, ToIntFunction<K> {
   default int applyAsInt(K var1) {
      return this.getInt(var1);
   }

   default int put(K var1, int var2) {
      throw new UnsupportedOperationException();
   }

   int getInt(Object var1);

   default int removeInt(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(K var1, Integer var2) {
      boolean var4 = this.containsKey(var1);
      int var5 = this.put(var1, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Integer get(Object var1) {
      int var3 = this.getInt(var1);
      return var3 == this.defaultReturnValue() && !this.containsKey(var1) ? null : var3;
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(Object var1) {
      return this.containsKey(var1) ? this.removeInt(var1) : null;
   }

   default void defaultReturnValue(int var1) {
      throw new UnsupportedOperationException();
   }

   default int defaultReturnValue() {
      return 0;
   }
}
