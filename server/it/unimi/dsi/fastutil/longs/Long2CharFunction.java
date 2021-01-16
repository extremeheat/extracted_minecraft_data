package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface Long2CharFunction extends Function<Long, Character>, LongToIntFunction {
   default int applyAsInt(long var1) {
      return this.get(var1);
   }

   default char put(long var1, char var3) {
      throw new UnsupportedOperationException();
   }

   char get(long var1);

   default char remove(long var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Character put(Long var1, Character var2) {
      long var3 = var1;
      boolean var5 = this.containsKey(var3);
      char var6 = this.put(var3, var2);
      return var5 ? var6 : null;
   }

   /** @deprecated */
   @Deprecated
   default Character get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         long var2 = (Long)var1;
         char var4 = this.get(var2);
         return var4 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var4;
      }
   }

   /** @deprecated */
   @Deprecated
   default Character remove(Object var1) {
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

   default void defaultReturnValue(char var1) {
      throw new UnsupportedOperationException();
   }

   default char defaultReturnValue() {
      return '\u0000';
   }
}
