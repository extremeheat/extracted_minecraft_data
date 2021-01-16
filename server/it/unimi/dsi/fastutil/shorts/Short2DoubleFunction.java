package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Short2DoubleFunction extends Function<Short, Double>, IntToDoubleFunction {
   /** @deprecated */
   @Deprecated
   default double applyAsDouble(int var1) {
      return this.get(SafeMath.safeIntToShort(var1));
   }

   default double put(short var1, double var2) {
      throw new UnsupportedOperationException();
   }

   double get(short var1);

   default double remove(short var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Double put(Short var1, Double var2) {
      short var3 = var1;
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
         short var2 = (Short)var1;
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

   default void defaultReturnValue(double var1) {
      throw new UnsupportedOperationException();
   }

   default double defaultReturnValue() {
      return 0.0D;
   }
}
