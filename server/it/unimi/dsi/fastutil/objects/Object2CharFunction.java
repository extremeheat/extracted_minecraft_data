package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2CharFunction<K> extends Function<K, Character>, ToIntFunction<K> {
   default int applyAsInt(K var1) {
      return this.getChar(var1);
   }

   default char put(K var1, char var2) {
      throw new UnsupportedOperationException();
   }

   char getChar(Object var1);

   default char removeChar(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default Character put(K var1, Character var2) {
      boolean var4 = this.containsKey(var1);
      char var5 = this.put(var1, var2);
      return var4 ? var5 : null;
   }

   /** @deprecated */
   @Deprecated
   default Character get(Object var1) {
      char var3 = this.getChar(var1);
      return var3 == this.defaultReturnValue() && !this.containsKey(var1) ? null : var3;
   }

   /** @deprecated */
   @Deprecated
   default Character remove(Object var1) {
      return this.containsKey(var1) ? this.removeChar(var1) : null;
   }

   default void defaultReturnValue(char var1) {
      throw new UnsupportedOperationException();
   }

   default char defaultReturnValue() {
      return '\u0000';
   }
}
