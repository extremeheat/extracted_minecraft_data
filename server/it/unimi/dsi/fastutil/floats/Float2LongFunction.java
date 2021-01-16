package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleToLongFunction;

@FunctionalInterface
public interface Float2LongFunction extends Function<Float, Long>, DoubleToLongFunction {
   /** @deprecated */
   @Deprecated
   default long applyAsLong(double var1) {
      return this.get(SafeMath.safeDoubleToFloat(var1));
   }

   default long put(float var1, long var2) {
      throw new UnsupportedOperationException();
   }

   long get(float var1);

   default long remove(float var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Long put(Float var1, Long var2) {
      float var3 = var1;
      boolean var4 = this.containsKey(var3);
      long var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Long get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = (Float)var1;
         long var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Long remove(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = (Float)var1;
         return this.containsKey(var2) ? this.remove(var2) : null;
      }
   }

   default boolean containsKey(float var1) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return var1 == null ? false : this.containsKey((Float)var1);
   }

   default void defaultReturnValue(long var1) {
      throw new UnsupportedOperationException();
   }

   default long defaultReturnValue() {
      return 0L;
   }
}
