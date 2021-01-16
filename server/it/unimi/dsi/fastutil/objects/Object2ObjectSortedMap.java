package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;
import java.util.SortedMap;

public interface Object2ObjectSortedMap<K, V> extends Object2ObjectMap<K, V>, SortedMap<K, V> {
   Object2ObjectSortedMap<K, V> subMap(K var1, K var2);

   Object2ObjectSortedMap<K, V> headMap(K var1);

   Object2ObjectSortedMap<K, V> tailMap(K var1);

   default ObjectSortedSet<java.util.Map.Entry<K, V>> entrySet() {
      return this.object2ObjectEntrySet();
   }

   ObjectSortedSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet();

   ObjectSortedSet<K> keySet();

   ObjectCollection<V> values();

   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K, V> extends ObjectSortedSet<Object2ObjectMap.Entry<K, V>>, Object2ObjectMap.FastEntrySet<K, V> {
      ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> fastIterator();

      ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> fastIterator(Object2ObjectMap.Entry<K, V> var1);
   }
}
