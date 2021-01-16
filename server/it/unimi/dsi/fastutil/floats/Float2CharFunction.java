package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Float2CharFunction extends Function<Float, Character>, DoubleToIntFunction {
   /** @deprecated */
   @Deprecated
   default int applyAsInt(double var1) {
      return this.get(SafeMath.safeDoubleToFloat(var1));
   }

   default char put(float var1, char var2) {
      throw new UnsupportedOperationException();
   }

   char get(float var1);

   default char remove(float var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Character put(Float var1, Character var2) {
      float var3 = var1;
      boolean var4 = this.containsKey(var3);
      char var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Character get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = (Float)var1;
         char var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Character remove(Object var1) {
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

   default void defaultReturnValue(char var1) {
      throw new UnsupportedOperationException();
   }

   default char defaultReturnValue() {
      return '\u0000';
   }
}
