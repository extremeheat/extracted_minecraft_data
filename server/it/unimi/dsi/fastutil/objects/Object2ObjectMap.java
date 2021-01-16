package it.unimi.dsi.fastutil.objects;

import java.util.Map;
import java.util.function.Consumer;

public interface Object2ObjectMap<K, V> extends Object2ObjectFunction<K, V>, Map<K, V> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(V var1);

   V defaultReturnValue();

   ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet();

   default ObjectSet<java.util.Map.Entry<K, V>> entrySet() {
      return this.object2ObjectEntrySet();
   }

   default V put(K var1, V var2) {
      return Object2ObjectFunction.super.put(var1, var2);
   }

   default V remove(Object var1) {
      return Object2ObjectFunction.super.remove(var1);
   }

   ObjectSet<K> keySet();

   ObjectCollection<V> values();

   boolean containsKey(Object var1);

   public interface Entry<K, V> extends java.util.Map.Entry<K, V> {
   }

   public interface FastEntrySet<K, V> extends ObjectSet<Object2ObjectMap.Entry<K, V>> {
      ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator();

      default void fastForEach(Consumer<? super Object2ObjectMap.Entry<K, V>> var1) {
         this.forEach(var1);
      }
   }
}
