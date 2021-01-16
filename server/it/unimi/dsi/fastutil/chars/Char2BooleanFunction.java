package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntPredicate;

@FunctionalInterface
public interface Char2BooleanFunction extends Function<Character, Boolean>, IntPredicate {
   /** @deprecated */
   @Deprecated
   default boolean test(int var1) {
      return this.get(SafeMath.safeIntToChar(var1));
   }

   default boolean put(char var1, boolean var2) {
      throw new UnsupportedOperationException();
   }

   boolean get(char var1);

   default boolean remove(char var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Boolean put(Character var1, Boolean var2) {
      char var3 = var1;
      boolean var4 = this.containsKey(var3);
      boolean var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Boolean get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         char var2 = (Character)var1;
         boolean var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Boolean remove(Object var1) {
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

   default void defaultReturnValue(boolean var1) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }
}
