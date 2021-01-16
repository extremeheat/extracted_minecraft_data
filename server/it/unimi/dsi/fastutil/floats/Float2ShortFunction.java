package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Float2ShortFunction extends Function<Float, Short>, DoubleToIntFunction {
   /** @deprecated */
   @Deprecated
   default int applyAsInt(double var1) {
      return this.get(SafeMath.safeDoubleToFloat(var1));
   }

   default short put(float var1, short var2) {
      throw new UnsupportedOperationException();
   }

   short get(float var1);

   default short remove(float var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Short put(Float var1, Short var2) {
      float var3 = var1;
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
         float var2 = (Float)var1;
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

   default void defaultReturnValue(short var1) {
      throw new UnsupportedOperationException();
   }

   default short defaultReturnValue() {
      return 0;
   }
}
