package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Object2BooleanFunction<K> extends Function<K, Boolean>, Predicate<K> {
   default boolean test(K var1) {
      return this.getBoolean(var1);
   }

   default boolean put(K var1, boolean var2) {
      throw new UnsupportedOperationException();
   }

   boolean getBoolean(Object var1);

   default boolean removeBoolean(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Boolean put(K var1, Boolean var2) {
      boolean var4 = this.containsKey(var1);
      boolean var5 = this.put(var1, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Boolean get(Object var1) {
      boolean var3 = this.getBoolean(var1);
      return var3 == this.defaultReturnValue() && !this.containsKey(var1) ? null : var3;
   }

   /** @deprecated */
   @Deprecated
   default Boolean remove(Object var1) {
      return this.containsKey(var1) ? this.removeBoolean(var1) : null;
   }

   default void defaultReturnValue(boolean var1) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }
}
