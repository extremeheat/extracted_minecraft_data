package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Int2IntFunction extends Function<Integer, Integer>, IntUnaryOperator {
   default int applyAsInt(int var1) {
      return this.get(var1);
   }

   default int put(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   int get(int var1);

   default int remove(int var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(Integer var1, Integer var2) {
      int var3 = var1;
      boolean var4 = this.containsKey(var3);
      int var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Integer get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = (Integer)var1;
         int var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(Object var1) {
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

   default void defaultReturnValue(int var1) {
      throw new UnsupportedOperationException();
   }

   default int defaultReturnValue() {
      return 0;
   }
}
