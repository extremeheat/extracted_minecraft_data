package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongUnaryOperator;

@FunctionalInterface
public interface Long2LongFunction extends Function<Long, Long>, LongUnaryOperator {
   default long applyAsLong(long var1) {
      return this.get(var1);
   }

   default long put(long var1, long var3) {
      throw new UnsupportedOperationException();
   }

   long get(long var1);

   default long remove(long var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Long put(Long var1, Long var2) {
      long var3 = var1;
      boolean var5 = this.containsKey(var3);
      long var6 = this.put(var3, var2);
      return var5 ? var6 : null;
   }

   /** @deprecated */
   @Deprecated
   default Long get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         long var2 = (Long)var1;
         long var4 = this.get(var2);
         return var4 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var4;
      }
   }

   /** @deprecated */
   @Deprecated
   default Long remove(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         long var2 = (Long)var1;
         return this.containsKey(var2) ? this.remove(var2) : null;
      }
   }

   default boolean containsKey(long var1) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return var1 == null ? false : this.containsKey((Long)var1);
   }

   default void defaultReturnValue(long var1) {
      throw new UnsupportedOperationException();
   }

   default long defaultReturnValue() {
      return 0L;
   }
}
