package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2ShortFunction<K> extends Function<K, Short>, ToIntFunction<K> {
   default int applyAsInt(K var1) {
      return this.getShort(var1);
   }

   default short put(K var1, short var2) {
      throw new UnsupportedOperationException();
   }

   short getShort(Object var1);

   default short removeShort(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Short put(K var1, Short var2) {
      boolean var4 = this.containsKey(var1);
      short var5 = this.put(var1, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Short get(Object var1) {
      short var3 = this.getShort(var1);
      return var3 == this.defaultReturnValue() && !this.containsKey(var1) ? null : var3;
   }

   /** @deprecated */
   @Deprecated
   default Short remove(Object var1) {
      return this.containsKey(var1) ? this.removeShort(var1) : null;
   }

   default void defaultReturnValue(short var1) {
      throw new UnsupportedOperationException();
   }

   default short defaultReturnValue() {
      return 0;
   }
}
