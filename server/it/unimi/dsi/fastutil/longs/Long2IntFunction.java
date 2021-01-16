package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface Long2IntFunction extends Function<Long, Integer>, LongToIntFunction {
   default int applyAsInt(long var1) {
      return this.get(var1);
   }

   default int put(long var1, int var3) {
      throw new UnsupportedOperationException();
   }

   int get(long var1);

   default int remove(long var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(Long var1, Integer var2) {
      long var3 = var1;
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
         long var2 = (Long)var1;
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

   default void defaultReturnValue(int var1) {
      throw new UnsupportedOperationException();
   }

   default int defaultReturnValue() {
      return 0;
   }
}
