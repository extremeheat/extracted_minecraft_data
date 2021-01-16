package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToLongFunction;

@FunctionalInterface
public interface Object2LongFunction<K> extends Function<K, Long>, ToLongFunction<K> {
   default long applyAsLong(K var1) {
      return this.getLong(var1);
   }

   default long put(K var1, long var2) {
      throw new UnsupportedOperationException();
   }

   long getLong(Object var1);

   default long removeLong(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Long put(K var1, Long var2) {
      boolean var4 = this.containsKey(var1);
      long var5 = this.put(var1, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Long get(Object var1) {
      long var3 = this.getLong(var1);
      return var3 == this.defaultReturnValue() && !this.containsKey(var1) ? null : var3;
   }

   /** @deprecated */
   @Deprecated
   default Long remove(Object var1) {
      return this.containsKey(var1) ? this.removeLong(var1) : null;
   }

   default void defaultReturnValue(long var1) {
      throw new UnsupportedOperationException();
   }

   default long defaultReturnValue() {
      return 0L;
   }
}
