package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntPredicate;

@FunctionalInterface
public interface Short2BooleanFunction extends Function<Short, Boolean>, IntPredicate {
   /** @deprecated */
   @Deprecated
   default boolean test(int var1) {
      return this.get(SafeMath.safeIntToShort(var1));
   }

   default boolean put(short var1, boolean var2) {
      throw new UnsupportedOperationException();
   }

   boolean get(short var1);

   default boolean remove(short var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Boolean put(Short var1, Boolean var2) {
      short var3 = var1;
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
         short var2 = (Short)var1;
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
         short var2 = (Short)var1;
         return this.containsKey(var2) ? this.remove(var2) : null;
      }
   }

   default boolean containsKey(short var1) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return var1 == null ? false : this.containsKey((Short)var1);
   }

   default void defaultReturnValue(boolean var1) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }
}
