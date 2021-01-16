package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Byte2DoubleFunction extends Function<Byte, Double>, IntToDoubleFunction {
   /** @deprecated */
   @Deprecated
   default double applyAsDouble(int var1) {
      return this.get(SafeMath.safeIntToByte(var1));
   }

   default double put(byte var1, double var2) {
      throw new UnsupportedOperationException();
   }

   double get(byte var1);

   default double remove(byte var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Double put(Byte var1, Double var2) {
      byte var3 = var1;
      boolean var4 = this.containsKey(var3);
      double var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Double get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         byte var2 = (Byte)var1;
         double var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Double remove(Object var1) {
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

   default void defaultReturnValue(double var1) {
      throw new UnsupportedOperationException();
   }

   default double defaultReturnValue() {
      return 0.0D;
   }
}
