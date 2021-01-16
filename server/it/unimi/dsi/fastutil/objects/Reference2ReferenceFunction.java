package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;

@FunctionalInterface
public interface Reference2ReferenceFunction<K, V> extends Function<K, V> {
   default V put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   V get(Object var1);

   default V remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   default void defaultReturnValue(V var1) {
      throw new UnsupportedOperationException();
   }

   default V defaultReturnValue() {
      return null;
   }
}
