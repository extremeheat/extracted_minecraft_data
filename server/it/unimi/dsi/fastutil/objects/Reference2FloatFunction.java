package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Reference2FloatFunction<K> extends Function<K, Float>, ToDoubleFunction<K> {
   default double applyAsDouble(K var1) {
      return (double)this.getFloat(var1);
   }

   default float put(K var1, float var2) {
      throw new UnsupportedOperationException();
   }

   float getFloat(Object var1);

   default float removeFloat(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Float put(K var1, Float var2) {
      boolean var4 = this.containsKey(var1);
      float var5 = this.put(var1, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Float get(Object var1) {
      float var3 = this.getFloat(var1);
      return var3 == this.defaultReturnValue() && !this.containsKey(var1) ? null : var3;
   }

   /** @deprecated */
   @Deprecated
   default Float remove(Object var1) {
      return this.containsKey(var1) ? this.removeFloat(var1) : null;
   }

   default void defaultReturnValue(float var1) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }
}
