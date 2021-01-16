package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Byte2ShortFunction extends Function<Byte, Short>, IntUnaryOperator {
   /** @deprecated */
   @Deprecated
   default int applyAsInt(int var1) {
      return this.get(SafeMath.safeIntToByte(var1));
   }

   default short put(byte var1, short var2) {
      throw new UnsupportedOperationException();
   }

   short get(byte var1);

   default short remove(byte var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Short put(Byte var1, Short var2) {
      byte var3 = var1;
      boolean var4 = this.containsKey(var3);
      short var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Short get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         byte var2 = (Byte)var1;
         short var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Short remove(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         byte var2 = (Byte)var1;
         return this.containsKey(var2) ? this.remove(var2) : null;
      }
   }

   default boolean containsKey(byte var1) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return var1 == null ? false : this.containsKey((Byte)var1);
   }

   default void defaultReturnValue(short var1) {
      throw new UnsupportedOperationException();
   }

   default short defaultReturnValue() {
      return 0;
   }
}
