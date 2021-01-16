package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2ByteFunction<K> extends Function<K, Byte>, ToIntFunction<K> {
   default int applyAsInt(K var1) {
      return this.getByte(var1);
   }

   default byte put(K var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   byte getByte(Object var1);

   default byte removeByte(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Byte put(K var1, Byte var2) {
      boolean var4 = this.containsKey(var1);
      byte var5 = this.put(var1, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Byte get(Object var1) {
      byte var3 = this.getByte(var1);
      return var3 == this.defaultReturnValue() && !this.containsKey(var1) ? null : var3;
   }

   /** @deprecated */
   @Deprecated
   default Byte remove(Object var1) {
      return this.containsKey(var1) ? this.removeByte(var1) : null;
   }

   default void defaultReturnValue(byte var1) {
      throw new UnsupportedOperationException();
   }

   default byte defaultReturnValue() {
      return 0;
   }
}
