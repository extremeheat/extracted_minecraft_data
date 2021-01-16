package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface Long2ShortFunction extends Function<Long, Short>, LongToIntFunction {
   default int applyAsInt(long var1) {
      return this.get(var1);
   }

   default short put(long var1, short var3) {
      throw new UnsupportedOperationException();
   }

   short get(long var1);

   default short remove(long var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Short put(Long var1, Short var2) {
      long var3 = var1;
      boolean var5 = this.containsKey(var3);
      short var6 = this.put(var3, var2);
      return var5 ? var6 : null;
   }

   /** @deprecated */
   @Deprecated
   default Short get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         long var2 = (Long)var1;
         short var4 = this.get(var2);
         return var4 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var4;
      }
   }

   /** @deprecated */
   @Deprecated
   default Short remove(Object var1) {
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

   default void defaultReturnValue(short var1) {
      throw new UnsupportedOperationException();
   }

   default short defaultReturnValue() {
      return 0;
   }
}
