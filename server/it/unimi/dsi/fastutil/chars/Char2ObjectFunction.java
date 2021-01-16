package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntFunction;

@FunctionalInterface
public interface Char2ObjectFunction<V> extends Function<Character, V>, IntFunction<V> {
   /** @deprecated */
   @Deprecated
   default V apply(int var1) {
      return this.get(SafeMath.safeIntToChar(var1));
   }

   default V put(char var1, V var2) {
      throw new UnsupportedOperationException();
   }

   V get(char var1);

   default V remove(char var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default V put(Character var1, V var2) {
      char var3 = var1;
      boolean var4 = this.containsKey(var3);
      Object var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default V get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         char var2 = (Character)var1;
         Object var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default V remove(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         char var2 = (Character)var1;
         return this.containsKey(var2) ? this.remove(var2) : null;
      }
   }

   default boolean containsKey(char var1) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return var1 == null ? false : this.containsKey((Character)var1);
   }

   default void defaultReturnValue(V var1) {
      throw new UnsupportedOperationException();
   }

   default V defaultReturnValue() {
      return null;
   }
}
