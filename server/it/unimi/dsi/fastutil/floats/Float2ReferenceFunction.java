package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface Float2ReferenceFunction<V> extends Function<Float, V>, DoubleFunction<V> {
   /** @deprecated */
   @Deprecated
   default V apply(double var1) {
      return this.get(SafeMath.safeDoubleToFloat(var1));
   }

   default V put(float var1, V var2) {
      throw new UnsupportedOperationException();
   }

   V get(float var1);

   default V remove(float var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default V put(Float var1, V var2) {
      float var3 = var1;
      boolean var4 = this.containsKey(var3);
      Object var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default V get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = (Float)var1;
         Object var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default V remove(Object var1) {
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

   default void defaultReturnValue(V var1) {
      throw new UnsupportedOperationException();
   }

   default V defaultReturnValue() {
      return null;
   }
}
