package it.unimi.dsi.fastutil;

@FunctionalInterface
public interface Function<K, V> extends java.util.function.Function<K, V> {
   default V apply(K var1) {
      return this.get(var1);
   }

   default V put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   V get(Object var1);

   default boolean containsKey(Object var1) {
      return true;
   }

   default V remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   default int size() {
      return -1;
   }

   default void clear() {
      throw new UnsupportedOperationException();
   }
}
