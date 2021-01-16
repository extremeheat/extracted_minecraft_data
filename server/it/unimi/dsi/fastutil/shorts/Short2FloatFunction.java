package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Short2FloatFunction extends Function<Short, Float>, IntToDoubleFunction {
   /** @deprecated */
   @Deprecated
   default double applyAsDouble(int var1) {
      return (double)this.get(SafeMath.safeIntToShort(var1));
   }

   default float put(short var1, float var2) {
      throw new UnsupportedOperationException();
   }

   float get(short var1);

   default float remove(short var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Float put(Short var1, Float var2) {
      short var3 = var1;
      boolean var4 = this.containsKey(var3);
      float var5 = this.put(var3, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Float get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         short var2 = (Short)var1;
         float var3 = this.get(var2);
         return var3 == this.defaultReturnValue() && !this.containsKey(var2) ? null : var3;
      }
   }

   /** @deprecated */
   @Deprecated
   default Float remove(Object var1) {
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

   default void defaultReturnValue(float var1) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }
}
