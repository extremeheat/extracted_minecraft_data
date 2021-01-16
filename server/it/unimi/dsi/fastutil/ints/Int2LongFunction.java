package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface Int2LongFunction extends Function<Integer, Long>, IntToLongFunction {
   default long applyAsLong(int var1) {
      return this.get(var1);
   }

   default long put(int var1, long var2) {
      throw new UnsupportedOperationException();
   }

   long get(int var1);

   default long remove(int var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Long put(Integer var1, Long var2) {
      int var3 = var1;
      boolean var4 = this.containsKey(var3);
      long var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Long get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = (Integer)var1;
         long var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Long remove(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = (Integer)var1;
         return this.containsKey(var2) ? this.remove(var2) : null;
      }
   }

   default boolean containsKey(int var1) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return var1 == null ? false : this.containsKey((Integer)var1);
   }

   default void defaultReturnValue(long var1) {
      throw new UnsupportedOperationException();
   }

   default long defaultReturnValue() {
      return 0L;
   }
}
